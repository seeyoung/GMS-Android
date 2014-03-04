package kr.codesolutions.gms

import grails.transaction.Transactional
import grails.util.Environment
import groovy.sql.Sql
import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.time.TimeCategory
import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.SendPolicy
import kr.codesolutions.gms.constants.SendType

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Propagation

import com.google.android.gcm.server.Message
import com.google.android.gcm.server.Result
import com.google.android.gcm.server.Sender


@Transactional
class GmsMessageService {
	def grailsApplication
	def dataSource
	SessionFactory sessionFactory
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
	def gmsInstanceLockService
	GStringTemplateEngine templateEngine = new GStringTemplateEngine()
	
	Sender gcmSender
	def mailService
	
	def cleanUpGorm() {
		Session session = sessionFactory.currentSession
		session.flush()
		session.clear()
		propertyInstanceMap.get().clear()
	}

	def GmsMessage createAndSend(GmsMessage message) {
		createMessage(message)
		if (message.isFailed){
			throw new UnexpectedRollbackException(message.error)
		}
		
		def recipients = createRecipient(message)
		if (message.isFailed){
			throw new UnexpectedRollbackException(message.error)
		}
		
		try{
			recipients.each { recipient ->
				send(message, recipient)
			}
		}catch(Exception ex){
			message.isFailed = true
			message.error = ex.message
			throw new UnexpectedRollbackException(message.error)
		}
		
		new GmsBoxResend(message: message).save(flush:true)
		
		return message
	}
	
	def GmsMessage createMessage(GmsMessage message) {
		
		def sender = GmsUser.findByUserId(message.senderUserId)
		if(sender == null){
			message.isFailed = true
			message.error = 'Invalid sender'
			return message
		}
		message.senderUserId = sender.userId
		message.senderName = sender.name
		message.senderPhoneNumber = sender.phoneNumber
		message.senderRegistrationId = sender.registrationId
		message.senderEmail = sender.email
		
		message.validate()
		if (message.hasErrors()) {
			message.isFailed = true
			throw new UnexpectedRollbackException(message.errors)
		}
		message.save()
		
		// 보낸메시지함에 보관
		new GmsBoxOut(owner: sender, message: message).save(flush:true)
		
		return message
	}
			
	def List createRecipient(GmsMessage message) {
		Template subjectTemplate = templateEngine.createTemplate(message.subject)
		Template contentTemplate = templateEngine.createTemplate(message.content)
		def users = GmsUser.createCriteria().list{
			sqlRestriction(message.recipientFilter)
			order('id', 'asc')
		}
		def recipients = []
		users.each { user ->
			recipients << createRecipient(message, user, subjectTemplate, contentTemplate)
		}
		if(recipients.size() == 0) {
			message.isFailed = true
			message.error = 'No recipients'
			return message
		}
		message.recipientCount = recipients.size()
		message.save(flush:true)
		return recipients
	}
	
	/**
	 * 메시지의 수신자를 주어진 GmsUser정보로 생성한다.
	 * 수신자에게 보낼 메시지의 제목과 내용을 Template로 생성한다.
	 * @param message 메시지
	 * @param user 사용자
	 * @param subjectTemplate 제목을 생성할 Template
	 * @param contentTemplate 내용을 생성할 Template
	 * @return
	 */
	def GmsMessageRecipient createRecipient(GmsMessage message, GmsUser user, Template subjectTemplate, Template contentTemplate){
		def binding = user.properties + message.properties
		def recipient = new GmsMessageRecipient(message: message,
												userId: user.userId,
												name: user.name,
												phoneNumber: user.phoneNumber,
												registrationId: user.registrationId,
												email: user.email,
												subject: subjectTemplate.make(binding).toString(),
												content: contentTemplate.make(binding).toString()
												)
		if(message.sendPolicy == SendPolicy.ADVENCED){
			if(user.registrationId == null) recipient.sendType = SendType.SMS
			else if(user.phoneNumber == null) recipient.sendType = SendType.EMAIL
		}else{
			recipient.sendType = SendType.valueOf(message.sendPolicy.value)
		}
		recipient.save()
//		new GmsBoxIn(owner: user, recipient: recipient).save()
		
		return recipient
	}
	
	/**
	 * 
	 * @param gmsUserInstance
	 * @param gmsMessageRecipientInstance
	 * @return
	 */
	def GmsBoxIn deleteBoxIn(GmsUser gmsUserInstance, GmsMessageRecipient gmsMessageRecipientInstance){
		GmsBoxIn.where{owner == gmsUserInstance && recipient == gmsMessageRecipientInstance}.deleteAll()
	}
	
	/**
	 * 
	 * @param gmsUserInstance
	 * @param gmsMessageInstance
	 * @return
	 */
	def GmsBoxOut deleteBoxOut(GmsUser gmsUserInstance, GmsMessage gmsMessageInstance){
		GmsBoxOut.where{owner == gmsUserInstance && message == gmsMessageInstance}.deleteAll()
	}

	def GmsMessage read(GmsMessageRecipient recipient){
		if(!recipient.isRead){
			recipient.isRead = true
			recipient.save flush:true
		}
		def gmsMessageInstance = recipient.message
		if(!gmsMessageInstance.isRead){
			gmsMessageInstance.isRead = true
			gmsMessageInstance.save flush:true
		}
		GmsBoxResend.where{message == gmsMessageInstance}.deleteAll()
		return gmsMessageInstance
	}

	
	/**
	 * 생성된 메시지를 발송하기위하여 제출한다. 
	 * 제출된 메시지는 SUBMIT -> DISTRIBUTE -> PUBLISH -> COLLECT -> SEND -> SENT -> COMPLETE -> TERMINATE 과정을 거친다.
	 * 
	 * @param message 메시지
	 */
	def submit(GmsMessage message){
		def instance = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instance)
		
		def sender = GmsUser.findByUserId(message.senderUserId)
		if(sender == null){
			message.isFailed = true
			message.error = 'Invalid sender'
			return message
		}
		message.senderName = sender.name
		message.senderPhoneNumber = sender.phoneNumber
		message.senderRegistrationId = sender.registrationId
		message.senderEmail = sender.email
		if(message.recipientGroup != null){
			message.recipientFilter = message.recipientGroup.filter
		}else if(message.recipientUserId != null){
			message.recipientFilter = "user_id='${message.recipientUserId}'"
		}
		if(message.recipientFilter == null) message.recipientFilter = '1=1'  // 모든 사용자를 의미함
		
		def (offset, end, recipientCount) = [0,0,0]
		(offset, end, recipientCount) = GmsUser.createCriteria().get{
											projections {
												min('id')
												max('id')
												rowCount()
											}
											sqlRestriction(message.recipientFilter)
										}
		// 수신자가 없으면 발송실패 처리
		if(recipientCount == 0){
			message.isFailed = true
			message.error = 'No recipients'
			return message
		}
		message.recipientCount = recipientCount
		// 메시지 수신자의 수가 queueSize(1채널이 1회에 보낼수 있는 최대 메시지 수)보다 크면 대량메시지로 설정
		if(recipientCount >= gmsInstance.queueSize) message.isBulk = true
		message.save()
		// 발송요청
		new GmsQueueSubmit(message: message, offset: offset?:0, end: end?:0, recipientCount: recipientCount).save()
		// 보낸메시지함에 보관
		new GmsBoxOut(owner: sender, message: message).save()
		return message
	}
	
	/**
	 * Draft된 메시지를 Instance에 할당한다. 
	 * 일반메시지는 자신의 Instance에만 할당한다.
	 * 대량메시지는 Publish에 많은 시간이 소요되므로 모든 Instance에 본배한다.
	 * 
	 * @param instance GMS Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 메시지 할당크기
	 */
	def distribute(int instanceId, Range channels, int queueSize){
		def instances = GmsInstance.where{isRunning == true}.list(sort:'id', order:'asc')
		
		def queues = GmsQueueSubmit.where{message.reservationTime <= new Date()}.list(max:queueSize, sort:'id', order:'asc')
		queues.each{ queue ->
			def message = queue.message
			def prevQueue = new GmsQueuePublish(instance: instanceId, message: message, offset: queue.offset, end: queue.end, recipientCount: queue.recipientCount).save()
			// 대량메시지는 Publish에 많은 시간이 소요되므로 모든 Instance에 본배한다.
			if(queue.recipientCount > queueSize){
				int offset = queue.offset
				0.step(queue.recipientCount, queueSize*instances.size()-1) { step ->
					boolean hasNext = true
					instances.eachWithIndex { gmsInstance, index ->
						if(hasNext){
							def results = GmsUser.createCriteria().list{
								projections{
									property('id','id')
								}
								sqlRestriction(message.recipientFilter + " AND id > ${offset}")
								maxResults(queueSize)
								order('id', 'asc')
							}
							if(results.size() < queueSize){
								prevQueue.recipientCount = results.size() + 1
								if(results.size() > 0){
									prevQueue.end = results.last()
								}
								prevQueue.save()
								hasNext = false
							}else{
								offset = results.last()
								prevQueue.recipientCount = results.size()
								prevQueue.end = results.getAt(-2)
								prevQueue.save()
								prevQueue = new GmsQueuePublish(instance: gmsInstance.id, message: message, offset: offset, end: offset).save()
							}
						}
					}
				}
			}
			queue.delete()
		}
		if(queues.size() > 0){
			log.info "Distributed (Instance: #${instanceId}): ${queues.size()} drafts"
		}
	}
	
	/**
	 * GmsQueuePublish에 제출된 메시지를 각 Recipient에게 보낼 메시지로 생성하고
	 * queueSize개수씩 분할하여 발송대기큐(GmsQueueWait)로 이동시킨다.
	 *
	 * @param instanceId 서버 Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 메시지 할당크기
	 */
	def publish(int instanceId, Range channels, int queueSize){
		def queues = GmsQueuePublish.where{instance == instanceId && message.isBulk == false}.list(max:queueSize, offset:queueSize*(instanceId-1), sort:'id', order:'asc')
		queues += GmsQueuePublish.where{instance == instanceId && message.isBulk == true}.list(max:channels.size(), sort:'id', order:'asc')
		queues.each { queue ->
			def message = queue.message
			Template subjectTemplate = templateEngine.createTemplate(message.subject)
			Template contentTemplate = templateEngine.createTemplate(message.content)
			def users = GmsUser.createCriteria().list{
				sqlRestriction(message.recipientFilter + " AND id >= ${queue.offset}")
				maxResults(queueSize)
				order('id', 'asc')
			}
			def (offset, end) = [0, 0]
			users.eachWithIndex { user, index ->
				def recipient = createRecipient(message, user, subjectTemplate, contentTemplate)
				if(index == 0) offset = recipient.id
				else end = recipient.id
				if(index % 100 == 0) cleanUpGorm()
			}
			new GmsQueueWait(message: message, offset: offset, end: end, recipientCount: users.size()).save() // 발송대기 큐로 이동
			queue.delete()
			log.info "Published (Instance: #${instanceId}): (Message: #${message.id}) ${users.size()} recipients"
		}
	}
	
	/**
	 * 메시지 발송대기 큐(GmsQueueWait)에 Instance를 할당한다.
	 * 처리단위는 Normal메시지: queueSize, Mass메시지: channels.size()
	 *
	 * @param instanceId 서버 Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 채널의 QUEUE크기로 할당할 메시지 갯수
	 */
	def collect(int instanceId, Range channels, int queueSize){
		def queues = GmsQueueWait.where{instance == 0 && message.isBulk == false}.list(max:queueSize, sort:'id', order:'asc')
		queues += GmsQueueWait.where{instance == 0 && message.isBulk == true}.list(max:channels.size(), sort:'id', order:'asc')
		queues.each { queue -> queue.instance = instanceId }
		GmsQueueWait.saveAll(queues)
		if(queues.size() > 0){
			log.info "Collected (Instance: #${instanceId}): ${queues.size()} waiting queues"
		}
		
		// 10분이상 대기중인 재발송건은 삭제한다.
		GmsBoxResend.where{createdTime < use(TimeCategory) { new Date() - 10.minutes}}.deleteAll()
	}
	
	/**
	 * Instance가 할당된 발송대기큐(GmsQueueWait) 메시지를 실제로 발송할 단위로 분리하고 
	 * Channel을 할당하여 발송큐(GmsQueueSend)로 이동시킨다. 
	 *
	 * @param instanceId 서버 Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 채널의 QUEUE크기로 할당할 메시지 갯수
	 */
	def post(int instanceId, Range channels, int queueSize){
		def queues = GmsQueueWait.where{instance == instanceId && message.isBulk == false}.list(max:queueSize, sort:'id', order:'asc')
		queues += GmsQueueWait.where{instance == instanceId && message.isBulk == true}.list(max:channels.size(), sort:'id', order:'asc')
		queues.eachWithIndex { queue, index ->
			def channelId = channels.get(index%channels.size())
			def recipients = GmsMessageRecipient.where{
								message == queue.message && 
								(id >= queue.offset && id <= queue.end)
							}.list(sort:'id', order:'asc')
							
			def message = queue.message
			if(message.status == MessageStatus.WAITING){
				message.status = MessageStatus.SENDING
				message.save()
			}
	
			recipients.eachWithIndex{ recipient, idx ->
				recipient.status = MessageStatus.SENDING
				recipient.save()
				new GmsQueueSend(instance: instanceId, channel: channelId, message: message, recipient: recipient).save()
				if(idx % 100 == 0) cleanUpGorm()
			}
			queue.delete()
			log.info "Posted (Intance: #${instanceId}, Channel: #${channelId}): ${recipients.size()} messages"
		}
	}
	
	/**
	 * queue에 저장된 메시지를 instance, channel로 queueSize개 검색하여 발송한다.
	 * isBulk=false인 메시지는 한 Channel에서 발송이 완료되므로 GmsMessage에대한 경쟁이 발생하지않으므로 직접 sentCount, Complete처리
	 * isBulk=true인 메시지는 여러개의 Instance, Channel에서 발송이 수행되므로 별도의 Complete Job에서 sentCount, Complete처리
	 * 
	 * @param instanceId 서버 Instance번호
	 * @param channelId 메시지 발송 채널
	 * @param queueSize 채널의 QUEUE크기
	 */
	def send(int instanceId, int channelId, int queueSize){
		def queues = GmsQueueSend.where{instance == instanceId && channel == channelId}.list(max:queueSize, sort:'id', order:'asc')
		queues.each { queue ->
			def message = queue.message
			def recipient = queue.recipient
			try{
				send(message, recipient)
				queue.delete()
			}catch(Exception ex){
				recipient.error = ex.message
				log.error ex.message
			}
		}
		if(queues.size() > 0){
			log.info("Sent (Intance: #${instanceId}, Channel: #${channelId}) : ${queues.size()} messages sent")
		}
	}
	
	/**
	 * 대량메시지중 발송이 완료된 메시지를 완료처리한다.
	 * 발송수, 실패수 등을 집계하고 status='COMPLETED'로 변경
	 * 
	 * @param instance 서버 Instance번호
	 * @param queueSize 채널의 QUEUE크기로 1회 처리단위
	 */
	def complete(int instanceId, int queueSize) {
		def messages = GmsMessage.where{ status == MessageStatus.SENDING && isBulk == true }.list(sort:'id', order:'asc')
		messages.each { gmsMessageInstance ->
			def (failedCount, sentCount) = GmsMessageRecipient.executeQuery("""
									SELECT COALESCE(SUM(CASE WHEN isFailed = true THEN 1 ELSE 0 END),0) AS failedCount,
				 						   COALESCE(SUM(CASE WHEN isSent = true THEN 1 ELSE 0 END),0) AS sentCount
									  FROM GmsMessageRecipient
									 WHERE message = ? AND status ='COMPLETED'				 
									""", [gmsMessageInstance]).get(0)
			gmsMessageInstance.failedCount = failedCount
			gmsMessageInstance.sentCount = sentCount
			def count = GmsQueuePublish.where{ message == gmsMessageInstance }.count() +
						GmsQueueWait.where{ message == gmsMessageInstance }.count() +
						GmsQueueSend.where{ message == gmsMessageInstance }.count()
			if(count == 0){
				if(gmsMessageInstance.sentCount > 0) gmsMessageInstance.isSent = true
				else gmsMessageInstance.isFailed = true
			}
			gmsMessageInstance.save()
		}
		def completed = messages.grep{it.status == MessageStatus.COMPLETED}.size()
		if(completed > 0){
			log.info("Completed (Intance: #${instanceId}) : ${completed} messages completed")
		}
	}
	
	/**
	 * 보존기간이 종료된 메시지를 월별 Log 테이블로 이동한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @param terminatePreserveDays 보존기간
	 */
	def terminate(int instanceId, int terminatePreserveDays) {
		def terminateDate = use(TimeCategory) { 
			Environment.executeForCurrentEnvironment {
				production {
					new Date() - terminatePreserveDays.days
				}
				development {
					new Date() - 10.minutes
				}
			}
		}.format('yyyyMMddHHmmss')
		
		terminateRecipient(instanceId, terminateDate)
		terminateMessage(instanceId, terminateDate)
	}
	
	/**
	 * 메시지 테이블 백업(GMS_MESSAGE -> GMS_LOG_MESSAGE_YYYYMM)
	 * 
	 * @param instanceId
	 * @param terminateDate
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW) 
	def terminateMessage(int instanceId, String terminateDate){
		def sourceTable = "GMS_MESSAGE"
		def targetTable = "GMS_LOG_MESSAGE_${terminateDate.substring(0,6)}"
		
		GmsMessage.withSession { session ->
			def sql = new Sql(session.connection())
			createLogTable(sql, sourceTable, targetTable)
			
			def criteria = GmsMessage.where{ status == MessageStatus.COMPLETED && lastEventTime <= terminateDate }
			def (terminated, moved, deleted) = [0, 0, 0]
			terminated = criteria.updateAll(status: MessageStatus.TERMINATED, terminatedTime: new Date())
			if(terminated > 0){
				moved = sql.executeUpdate("INSERT INTO ${Sql.expand(targetTable)} SELECT * FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
				sql.executeUpdate("DELETE FROM gms_box_out WHERE message_id IN(SELECT id FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED')")
				deleted =  sql.executeUpdate("DELETE FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
				log.info "Terminated (Intance: #${instanceId}, terminateDate: ${terminateDate}): ${terminated} messages terminated, ${moved} moved to Log, ${deleted} deleted"
			}
		}
	}
	
	/**
	 * 수신자 테이블 백업(GMS_MESSAGE_RECIPIENT -> GMS_LOG_MESSAGE_RECIPIENT_YYYYMM)
	 * 
	 * @param instanceId
	 * @param terminateDate
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	def terminateRecipient(int instanceId, String terminateDate){
		def sourceTable = "GMS_MESSAGE_RECIPIENT"
		def targetTable = "GMS_LOG_MESSAGE_RECIPIENT_${terminateDate.substring(0,6)}"
		
		GmsMessageRecipient.withSession { session ->
			def sql = new Sql(session.connection())
			createLogTable(sql, sourceTable, targetTable)
			
			def criteria = GmsMessageRecipient.where{ status == MessageStatus.COMPLETED && lastEventTime <= terminateDate }
			def (terminated, moved, deleted) = [0, 0, 0]
			terminated = criteria.updateAll(status: MessageStatus.TERMINATED, terminatedTime: new Date())
			if(terminated > 0){
				moved = sql.executeUpdate("INSERT INTO ${Sql.expand(targetTable)} SELECT * FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
				sql.executeUpdate("DELETE FROM gms_box_in WHERE recipient_id IN(SELECT id FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED')")
				deleted =  sql.executeUpdate("DELETE FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
				log.info "Terminated (Intance: #${instanceId}, terminateDate: ${terminateDate}): ${terminated} messages terminated, ${moved} moved to Log, ${deleted} deleted"
			}
		}
	}
	
	/**
	 * 백업테이블 생성
	 * 
	 * @param sql
	 * @param sourceTable 백업 소스 테이블 명
	 * @param targetTable 백업 타겟 테이블 명
	 */
	def createLogTable(Sql sql, String sourceTable, String targetTable){
		try{
			sql.firstRow("SELECT COUNT(1) FROM ${Sql.expand(targetTable)} WHERE 1=0")
		}catch(Exception ex){
			Environment.executeForCurrentEnvironment {
				production {
					sql.execute("CREATE TABLE ${Sql.expand(targetTable)} AS (SELECT * FROM ${Sql.expand(sourceTable)} WHERE 1=0)")
				}
				development {
					sql.execute("CREATE TABLE ${Sql.expand(targetTable)} AS (SELECT * FROM ${Sql.expand(sourceTable)} WHERE 1=0) WITH NO DATA")
				}
			}
			log.info "Table ${targetTable} created."
		}
	}

	/**
	 * 메시지를 수신자 1명에게 전송
	 * 
	 * @param message
	 * @param recipient
	 * @return
	 */
	def String send(GmsMessage message, GmsMessageRecipient recipient){
		def returnCode = null
		Environment.executeForCurrentEnvironment {
			production {
				switch(recipient.sendType){
					case SendType.GCM: returnCode = sendGCM(message, recipient); break
					case SendType.SMS: returnCode = sendSMS(message, recipient); break
					case SendType.EMAIL: returnCode = sendEMAIL(message, recipient); break
				}
			}
			development {
				switch(recipient.sendType){
					case SendType.GCM: returnCode = sendGCM(message, recipient); break
					case SendType.SMS: returnCode = sendSMS(message, recipient); break
					case SendType.EMAIL: returnCode = sendEMAIL(message, recipient); break
				}
			}
		}
		
		if(returnCode == null){
			recipient.isSent = true
			if(message.isBulk == false){
				message.sentCount += 1
			}
		}else{
			recipient.isFailed = true
			recipient.error = returnCode
			if(message.isBulk == false){
				message.failedCount += 1
			}
		}
		return returnCode
	}

	/**
	 * 메시지를 수신자 1명에게 GCM 전송
	 * 
	 * @param message
	 * @param recipient
	 * @return 에러코드
	 */
	def String sendGCM(GmsMessage message, GmsMessageRecipient recipient) {
		if(recipient.registrationId == null){
			return 'Invalid registrationId'
		}
		if(gcmSender == null){
			gcmSender = new Sender(grailsApplication.config.gms.gcmApiKey)
		}
		Message gcmMessage = new Message.Builder()
				.collapseKey('GMS')
				.addData('GMS.id', recipient.id.toString())
				.addData('GMS.ownType', message.isPersonal?'1':'0')
				.addData('GMS.msgType', message.isCallback?'1':'0')
				.addData('GMS.subject', URLEncoder.encode(recipient.subject, 'UTF-8') )
				.addData('GMS.content', URLEncoder.encode(recipient.content, 'UTF-8') )
				.addData('GMS.senderId', message.senderUserId)
				.build()
		//log.debug 'GCM message: ' + gcmMessage.toString()

		Result result = gcmSender.send(gcmMessage, recipient.registrationId, 3)
		//log.debug 'Result :' + message.userId + ',' + result.messageId
		if(result.messageId == null){
			return result.errorCode
		}
	}
	
	/**
	 * 메시지를 수신자 1명에게 SMS 전송
	 * 
	 * @param message
	 * @param recipient
	 * @return 에러코드
	 */
	def String sendSMS(GmsMessage message, GmsMessageRecipient recipient) {
		if(message.senderPhoneNumber == null){
			return 'Invalid sender phoneNumber'
		}
		if(recipient.phoneNumber == null){
			return 'Invalid recipient phoneNumber'
		}
		def sms = new GmsMessageSms(message: message,
						reservationTime: message.reservationTime,
						subject: recipient.subject,
						content: recipient.content,
						userId: recipient.userId,
						name: recipient.name,
						phoneNumber: recipient.phoneNumber,
						senderPhoneNumber: message.senderPhoneNumber
						)
		sms.id = recipient.id
		sms.save()
		return null
	}
	
	/**
	 * 메시지를 수신자 1명에게 EMAIL 전송
	 * 
	 * @param message
	 * @param recipient
	 * @return 에러코드
	 */
	def String sendEMAIL(GmsMessage message, GmsMessageRecipient recipient) {
		if(message.senderEmail == null){
			return 'Invalid sender email'
		}
		if(recipient.email == null){
			return 'Invalid recipient email'
		}

		mailService.sendMail {
							from "${message.senderName}<${message.senderEmail}>"
						    to "${recipient.name}<${recipient.email}>"
						    subject recipient.subject
							html recipient.content
					}
		return null
	}
	
	/**
	 * 메시지 읽음 처리
	 * 
	 * @param message
	 */
	def GmsMessage read(GmsMessage message){
		if(!message.isRead){
			message.isRead = true
		}
		return message
	}
	

	//	def Map listUser(UserSearchCommand cmd, Map params) {
	//		def criteria = new DetachedCriteria(GmsUser).build{
	//			and {
	//				like ('userId', cmd.userId)
	//				like ('name', cmd.name)
	//				like ('phoneNumber', cmd.phoneNumber)
	//				eq ('enabled', true)
	//			}
	//		}
	//		def totalCount = criteria.count()
	//		def list = criteria.list(params)
	//
	//		return [list: list, totalCount: totalCount]
	//	}
	//
	//	/**
	 //	 * 재발송이 필요한 메시지를 SMS발송하고 완료메시지함으로 이동한다.
	 //	 *
	 //	 * @param instance 서버 Instance번호
	 //	 * @return
	 //	 */
	 //	def resendMessage(int instance, int retryPendingSeconds) {
	 //		def criteria = new DetachedCriteria(GmsMessageResendBox).build{
	 //							lt('createdTime', use(TimeCategory) { new Date() - retryPendingSeconds.seconds})
	 //		}
	 //		def results = criteria.list(sort:'createdTime', order:'asc')
	 //		if(results != null){
	 //			results.each { box ->
	 //				if(!box.isDirty()){
	 //					// 수신자가 1명이라도 확인하지 않는 메시지는 모든 수신자에게 SMS로 재발송한다.
	 //					if(!box.message.isRead){
	 //						log.info "message ${box.message.id} is resent."
	 //						sendSMS(box.message)
	 //					}
	 //					log.info "message ${box.message.id} is moved to sentBox."
	 //					new GmsBoxSent(message:box.message).save flush:true
	 //					box.delete flush:true
	 //				}
	 //			}
	 //		}
	 //	}
	 //
	 
	 
}
