package kr.codesolutions.gms

import grails.transaction.Transactional
import grails.util.Environment
import groovy.sql.Sql
import groovy.time.TimeCategory
import kr.codesolutions.gms.constants.InstanceLock
import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.MessageType
import kr.codesolutions.gms.constants.SendPolicy
import kr.codesolutions.gms.constants.SendType

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction

import com.google.android.gcm.server.Message
import com.google.android.gcm.server.Result
import com.google.android.gcm.server.Sender


@Transactional
class GmsMessageService {
	def grailsApplication
	def dataSource
	SessionFactory sessionFactory
	Transaction transaction
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
	Sender gcmSender
	def gmsInstanceLockService
	
	def cleanUpGorm() {
		Session session = sessionFactory.currentSession
		session.flush()
		session.clear()
		propertyInstanceMap.get().clear()
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
//	def GmsMessage create(GmsMessage gmsMessageInstance, Map params) {
//		params.senderId = params.senderId?:'gmsmaster'
//		gmsMessageInstance.sender = GmsUser.findByUserId(params.senderId)
//		gmsMessageInstance.owner = gmsMessageInstance.sender
//		gmsMessageInstance.sendPolicy = 'ADVENCED'
//		gmsMessageInstance.validate()
//		if (gmsMessageInstance.hasErrors()) {
//			return gmsMessageInstance
//		}
//		gmsMessageInstance.save()
//		
//		// 메시지 수신자 저장
//		if(params.recipientGroup != null && !params.recipientGroup.empty){
//			def gmsUserGroupInstance = GmsUserGroup.get(params.recipientGroup)
//			if(gmsUserGroupInstance){
//				gmsUserGroupInstance?.members.each { gmsUserInstance -> createRecipient(gmsMessageInstance, gmsUserInstance)}
//			}
//		}
//		if(params.recipientId != null && !params.recipientId.empty){
//			params.recipientId.split(';').each { userId ->
//				def gmsUserInstance = GmsUser.findByUserId(userId)
//				if(gmsUserInstance){
//					createRecipient(gmsMessageInstance, gmsUserInstance)
//				}
//			}
//		}
//		if(gmsMessageInstance.recipients == null) {
//			throw new UnexpectedRollbackException("recipients is empty")
//		}
//
//		// 예약시간이 있으면 예약메시지함으로 저장(스케쥴러가 예약시간을 확인하여 발송한다)
//		// 그외의 메시지는 발송대기함으로 저장(스케쥴러가 즉시 발송한다)
////		if(gmsMessageInstance.reservationTime){
////			new GmsMessageReserveBox(message:gmsMessageInstance).save flush:true
////		}else{
////			new GmsMessageWaitingBox(message:gmsMessageInstance).save flush:true
////		}
//		// 모든 메시지는 보낸메시지함에도 보관한다
//		new GmsBoxOut(owner:gmsMessageInstance.owner, message:gmsMessageInstance).save flush:true
//		
//		return gmsMessageInstance
//	}
//	
//	def createRecipient(GmsMessage gmsMessageInstance, GmsUser gmsUserInstance){
//		def gmsMessageRecipientInstance =
//			new GmsMessageRecipient(userId: gmsUserInstance.userId,
//									name: gmsUserInstance.name,
//									phoneNumber: gmsUserInstance.phoneNumber,
//									registrationId: gmsUserInstance.registrationId,
//									message: gmsMessageInstance).save()
//		gmsMessageInstance.addToRecipients(gmsMessageRecipientInstance)
////		new GmsMessageInBox(owner: gmsUserInstance,
////							message: gmsMessageInstance).save()
//
//	}
//	
//	def GmsMessage read(GmsMessage gmsMessageInstance, GmsMessageRecipient gmsMessageRecipientInstance){
//		if(!gmsMessageRecipientInstance.isRead){
//			gmsMessageRecipientInstance.isRead = true
//			gmsMessageRecipientInstance.save flush:true
//		}
//		if(!gmsMessageInstance.isRead){
//			gmsMessageInstance.isRead = true
//			gmsMessageInstance.save flush:true
//		}
//		def box = GmsMessageResendBox.findByMessage(gmsMessageInstance)
//		if(box != null){
//			box.delete()
//			new GmsBoxSent(message:gmsMessageInstance).save flush:true
//		}
//		return gmsMessageInstance
//	}
//	
//	def GmsBoxIn deleteInBox(GmsUser gmsUserInstance, GmsMessage gmsMessageInstance){
//		def box = GmsBoxIn.findByOwnerAndMessage(gmsUserInstance, gmsMessageInstance)
//		if(box != null){
//			box.delete flush:true
//		}
//	}
//	
//	def GmsBoxOut deleteOutBox(GmsUser gmsUserInstance, GmsMessage gmsMessageInstance){
//		def box = GmsBoxOut.findByOwnerAndMessage(gmsUserInstance, gmsMessageInstance)
//		if(box != null){
//			box.delete flush:true
//		}
//		return box
//	}
//
//	def GmsMessage createAndSend(GmsMessage gmsMessageInstance, Map params) {
//		def sentCount = 0
//		gmsMessageInstance = create(gmsMessageInstance, params)
//		if (gmsMessageInstance.hasErrors()){
//			throw new UnexpectedRollbackException(gmsMessageInstance.errors)
//		}
//		
//		GmsMessageWaitingBox.findByMessage(gmsMessageInstance).delete(flush:true)
////		new GmsMessageSendingBox(message:gmsMessageInstance).save(flush:true).delete(flush:true)
////		sentCount = sendGCM(gmsMessageInstance)
////		new GmsMessageResendBox(message:gmsMessageInstance).save(flush:true)
//		
//		return gmsMessageInstance
//	}
//	
//
//    def send(GmsMessage gmsMessageInstance) {
//		switch(gmsMessageInstance.sendPolicy){
//			case ['GCM','COMPLEX','ADVENCED']:
//				sendGCM gmsMessageInstance
//				break
//			case 'SMS':
//				sendSMS gmsMessageInstance
//				break
//		}
//    }
//
//	// GCM메시지를 모든 수신자에게 전송 발송정책에따라 GCM미수신자에게는 SMS메시지를 전송한다.
//	def sendGCM(GmsMessage gmsMessageInstance) {
//		if(gcmSender == null){
//			gcmSender = new Sender(grailsApplication.config.gms.gcmApiKey)
//		}
//		Message gcmMessage = new Message.Builder()
//				.collapseKey('GMS')
//				.addData('GMS.id', gmsMessageInstance.id.toString())
//				.addData('GMS.ownType', gmsMessageInstance.ownType)
//				.addData('GMS.msgType', gmsMessageInstance.msgType)
//				.addData('GMS.subject', URLEncoder.encode(gmsMessageInstance.subject, 'UTF-8') )
//				.addData('GMS.content', URLEncoder.encode(gmsMessageInstance.content, 'UTF-8') )
//				.addData('GMS.senderId', gmsMessageInstance.sender.userId)
//				.build()
//		log.debug 'GCM message: ' + gcmMessage.toString()
//		def gcmRecipients = gmsMessageInstance.recipients.grep{it.registrationId != null}
//		log.info 'Recipient Count : ' + gcmRecipients.size()
//		if(gcmRecipients.size() > 0){
//			try{
//				MulticastResult result = gcmSender.send(gcmMessage, gcmRecipients*.registrationId, 3)
//				gcmRecipients.eachWithIndex{gmsMessageRecipientInstance, index ->
//					log.info 'Result[' + index + '] :' + gmsMessageRecipientInstance.name + ',' + result.results[index].messageId
//					if(result.results[index].messageId){
//						gmsMessageRecipientInstance.isSent = true
//						gmsMessageRecipientInstance.sendType = 'GCM'
//					}else{
//						gmsMessageRecipientInstance.error = result.results[index].errorCode
//					}
//					gmsMessageRecipientInstance.save()
//				}
//			}catch(Exception ex){
//				gmsMessageInstance.error = ex.message
//				log.error ex.message
//			}
//		}
//		// GCM메시지를 전송하지 못하거나 RegistrationID가 없는 사용자에게 SMS메시지를 전송한다.
//		if(gmsMessageInstance.sendPolicy == 'COMPLEX'){
//			def smsRecipients = gmsMessageInstance.recipients.grep{it.registrationId == null || it.errorCode != null}
//			if(smsRecipients.size() > 0){
//				try{
//					smsRecipients.each { recipient ->
//						sendSMS(gmsMessageInstance.content, gmsMessageInstance.sender, recipient)
//					}
//				}catch(Exception ex){
//					gmsMessageInstance.error = ex.message
//					log.error ex.message
//				}
//			}
//		}
//		return gmsMessageInstance.recipients.grep{it.isSent}.size()
//    }
//	
//	// GCM메시지를 수신자 1명에게 전송
//	def sendGCM(Message gcmMessage, Sender gcmSender, GmsMessageRecipient gmsMessageRecipientInstance){
//		Result result = gcmSender.send(gcmMessage, gmsMessageRecipientInstance.registrationId, 3)
//		if(result.messageId){
//			gmsMessageRecipientInstance.isSent = true
//			gmsMessageRecipientInstance.sendType = 'GCM'
//		}else{
//			gmsMessageRecipientInstance.error = result.errorCode
//		}
//		gmsMessageRecipientInstance.save()
//	}
//
//	// SMS메시지를 모든 수신자에게 전송
//	def sendSMS(GmsMessage gmsMessageInstance) {
//		gmsMessageInstance.recipients.each{ recipient ->
//			sendSMS(gmsMessageInstance.content, gmsMessageInstance.sender, recipient)
//		}
//    }
//	
//	// SMS메시지를 수신자 1명에게 전송
//	def sendSMS(String messageContent, GmsUser sender, GmsMessageRecipient gmsMessageRecipientInstance) {
//		gmsMessageRecipientInstance.isSent = true
//		gmsMessageRecipientInstance.sendType = 'SMS'
//		gmsMessageRecipientInstance.save()
//	}
//
//	/**
//	 * 발송대기메시지를 발송메시지함으로 이동한다.
//	 * 
//	 * @param instance 서버 Instance번호
//	 * @return
//	 */
//	def sendWaitingMessage(int instance) {
//		def results = GmsMessageWaitingBox.list(sort:'createdTime', order:'asc')
//		if(results != null){
//			results.each { box ->
//				if(!box.isDirty()){
//					log.info "message ${box.message.id} is moved to sendingbox."
////					new GmsMessageSendingBox(message:box.message).save flush:true
//					box.delete flush:true
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 발송예약시간을 초과한 예약메시지를 발송대기메시지함으로 이동한다.
//	 * 
//	 * @param instance 서버 Instance번호
//	 * @return
//	 */
//	def sendReservedMessage(int instance) {
//		def criteria = new DetachedCriteria(GmsMessageReserveBox).build{
//							message{
//								lt('reservationTime', new Date())
//							}
//		}
//		def results = criteria.list(sort:'createdTime', order:'asc')
//		if(results != null){
//			results.each { box ->
//				if(!box.isDirty()){
//					log.info "message ${box.message.id} is moved to waitingbox."
////					new GmsMessageWaitingBox(message:box.message).save flush:true
//					box.delete flush:true
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 발송메시지함에서 가장먼저 들어온 메시지 하나만 발송한다. 
//	 * 
//	 * @param instance 서버 Instance번호
//	 * @return
//	 */
//	def sendMessage(int instance) {
//		def results = GmsMessageSendingBox.list(sort:'createdTime', order:'asc')
//		if(results != null){
//			results.each { box ->
//				if(!box.isDirty()){
//					send(box.message)
//					log.info "message ${box.message.id} is sent."
//					if(box.message.sendPolicy == 'ADVENCED'){
//						log.info "message ${box.message.id} is moved to resendBox."
////						new GmsMessageResendBox(message:box.message).save flush:true
//					}else{
//						log.info "message ${box.message.id} is moved to sentBox."
//						new GmsBoxSent(message:box.message).save flush:true
//					}
//					box.delete flush:true
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 재발송이 필요한 메시지를 제외하고 완료메시지함으로 이동한다.
//	 * 
//	 * @param instance 서버 Instance번호
//	 * @return
//	 */
//	def completeMessage(int instance) {
//		def results = GmsBoxSent.list(sort:'createdTime', order:'asc')
//		if(results != null){
//			results.each { box ->
//				if(!box.isDirty()){
//					// 발신오류가 발생한 수신자의 errorCount를 증가시킨다. errorCount가 3이상이면 사용불가
//					def gcmRecipients = box.message.recipients.grep{it.error != null}
//					gcmRecipients.each{ gmsMessageRecipientInstance ->
//						def gmsUserInstance = GmsUser.findByUserId(gmsMessageRecipientInstance.userId)
//						gmsUserInstance.errorCount++
//						gmsUserInstance.save()
//					}
//					log.info "message ${box.message.id} is moved to completeBox."
//					new GmsBoxComplete(message:box.message).save flush:true
//					box.delete flush:true
//				}
//			}
//		}
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
//	/**
//	 * 관리기간이 종료된 메시지를 종결메시지함으로 이동한다.
//	 * 
//	 * @param instance 서버 Instance번호
//	 * @param terminatePendingDays 관리기간
//	 * @return
//	 */
//	def terminateMessage(int instance, int terminatePendingDays) {
//		def criteria = new DetachedCriteria(GmsBoxComplete).build{
//							lt('createdTime', use(TimeCategory) { new Date() - terminatePendingDays.days})
//		}
//		def results = criteria.list(sort:'createdTime', order:'asc')
//		if(results != null){
//			results.each { box ->
//				if(!box.isDirty()){
//					log.info "message ${box.message.id} is moved to terminateBox."
//					new GmsBoxTerminate(message:box.message).save flush:true
//					box.delete flush:true
//				}
//			}
//		}
//	}

	
	
	/**
	 * 생성된 메시지를 발송하기위하여 제출한다. 
	 * 제출된 메시지는 DISTRIBUTE -> PUBLISH -> COLLECT -> SEND -> 과정을 거친다.
	 * @param message 메시지
	 */
	def draft(GmsMessage message){
		def instance = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instance)
		//def (offset, end, recipientCount) = [0, 0, 0]
		def (offset, end, recipientCount) = GmsUser.createCriteria().get{
												projections {
													min('id')
													max('id')
													rowCount()
												}
												sqlRestriction(message.recipientFilter)
											}
		message.recipientCount = recipientCount
		// 메시지 수신자의 수가 queueSize(1채널이 1회에 보낼수 있는 최대 메시지 수)보다 크면 대량메시지로 설정
		if(message.recipientCount > gmsInstance.queueSize) message.messageType = MessageType.MASS 
		new GmsQueueDraft(message: message, offset: offset?:0, end: end?:0, recipientCount: recipientCount).save()
	}
	
	/**
	 * Draft된 메시지를 Instance에 할당한다. 
	 * 일반메시지는 자신의 Instance에만 할당한다.
	 * 대량메시지는 Publish에 많은 시간이 소요되므로 모든 Instance에 본배한다.
	 * 
	 * @param instance GMS Instance번호
	 * @param queueSize 메시지 할당크기
	 * @param transactionSize 처리단위
	 */
	def distribute(int instanceId, int queueSize, int transactionSize){
		def queues = GmsQueueDraft.where{message.reservationTime <= new Date()}.list(max:queueSize, sort:'id', order:'asc')
		queues.each{ queue ->
			def message = queue.message
			def prevQueue = new GmsQueuePublish(instance: instanceId, message: message, offset: queue.offset, end: queue.end, recipientCount: queue.recipientCount).save()
			// 대량메시지는 Publish에 많은 시간이 소요되므로 모든 Instance에 본배한다.
			if(queue.recipientCount > transactionSize){
				def instances = GmsInstance.where{isRunning == true}.list(sort:'id', order:'asc')
				int offset = queue.offset
				0.step(queue.recipientCount, transactionSize*instances.size()-1) { step ->
					boolean hasNext = true
					instances.eachWithIndex { gmsInstance, index ->
						if(hasNext){
							def results = GmsUser.createCriteria().list{
								projections{
									property('id','id')
								}
								sqlRestriction(message.recipientFilter + " AND id > ${offset}")
								maxResults(transactionSize)
								order('id', 'asc')
							}
							if(results.size() < transactionSize){
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
		log.info "Distributed (Instance: #${instanceId}): ${queues.size()} drafts"
	}
	
	/**
	 * GmsQueuePublish에 제출된 메시지를 각 Recipient에게 보낼 메시지로 생성한다.
	 * 1회에 transactionSize개수만큼의 Recipient만 처리한다.
	 *
	 * @param instanceId 서버 Instance번호
	 * @param queueSize 메시지 할당크기
	 * @param transactionSize 처리단위
	 */
	def publish(int instanceId, int queueSize, int transactionSize){
		def queues = GmsQueuePublish.where{instance == instanceId && message.messageType == MessageType.NORMAL}.list(max:queueSize, offset:queueSize*(instanceId-1), sort:'id', order:'asc')
		queues += GmsQueuePublish.where{instance == instanceId && message.messageType == MessageType.MASS}.list(max:1, sort:'id', order:'asc')
		queues.each { queue ->
			def message = queue.message
			def users = GmsUser.createCriteria().list{
				sqlRestriction(message.recipientFilter + " AND id >= ${queue.offset}")
				maxResults(transactionSize)
				order('id', 'asc')
			}
			def (offset, end) = [0, 0]
			users.eachWithIndex { user, index ->
				def recipient = new GmsMessageRecipient(message: message,
														userId: user.userId,
														name: user.name,
														phoneNumber: user.phoneNumber,
														registrationId: user.registrationId,
														email: user.email,
														subject: message.subject,
														content: message.content,
														reservationTime: message.reservationTime,
														ownType: message.ownType,
														messageType: message.messageType
														)
				if(message.sendPolicy in SendType.values()){
					recipient.sendType = message.sendPolicy
				}else if(message.sendPolicy in [SendPolicy.COMPLEX, SendPolicy.ADVENCED]){
					if(user.registrationId == null) recipient.sendType = SendType.SMS
					else if(user.phoneNumber == null) recipient.sendType = SendType.EMAIL
				}
				recipient.save()
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
	 * 현재 가동중인 GMS서버 Instance에 메시지를 할당한다.
	 * 메시지 발송요청 큐(GmsQueueWait)에 instance번호를 할당한다.
	 * 처리단위는 Normal메시지: queueSize, Mass메시지: 1
	 *
	 * @param instanceId 서버 Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 채널의 QUEUE크기로 할당할 메시지 갯수
	 * @param transactionSize 처리단위
	 */
	def collect(int instanceId, Range channels, int queueSize, int transactionSize){
		def queues = GmsQueueWait.where{instance == 0 && message.messageType == MessageType.NORMAL}.list(max:queueSize, sort:'id', order:'asc')
		queues += GmsQueueWait.where{instance == 0 && message.messageType == MessageType.MASS}.list(max:1, sort:'id', order:'asc')
		queues.each { queue -> queue.instance = instanceId }
		GmsQueueWait.saveAll(queues)
		log.info "Collected (Instance: #${instanceId}): ${queues.size()} waiting queues"
	}
	
	/**
	 * 1. Request의 recipientId로 (GmsUser)에서 registrationId를 찾아 저장한다.
	 * 2. chnnel=0으로 할당된 메시지중 (GmsUser)에 recipientId가 없으면 status='9' 발송실패로 처리하여 (GmsMassMessage)로 이동시키고 Request는 삭제한다.
	 * 3. 메시지 발송요청 저장소(GmsMassMessageRequest)에서 queueSize의 메시지를 읽어와 채널별 QUEUE에 할당한다.
	 *
	 * @param instanceId 서버 Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 채널의 QUEUE크기로 할당할 메시지 갯수
	 */
	def post(int instanceId, Range channels, int queueSize){
		def queues = GmsQueueWait.where{instance == instanceId}.list(max:channels.size(), sort:'id', order:'asc')
		queues.eachWithIndex { queue, index ->
			def message = queue.message
			def channelId = channels.get(index%channels.size())
			def recipients = GmsMessageRecipient.where{
								message == queue.message && 
								(id >= queue.offset && id <= queue.end)
							}.list(sort:'id', order:'asc')
							
			recipients.eachWithIndex{ recipient, idx ->
				new GmsQueueSend(instance: instanceId, channel: channelId, message: message, recipient: recipient).save()
				if(idx % 100 == 0) cleanUpGorm()
			}
			queue.delete()
			log.info "Posted (Intance: #${instanceId}, Channel: #${channelId}): ${recipients.size()} messages"
		}
	}
	
	/**
	 * queue에 저장된 메시지를 instance, channel로 queueSize개 검색하여 발송한다.
	 *
	 * @param instanceId 서버 Instance번호
	 * @param channelId 메시지 발송 채널
	 * @param queueSize 채널의 QUEUE크기
	 */
	def send(int instanceId, int channelId, int queueSize){
		def queues = GmsQueueSend.where{instance == instanceId && channel == channelId}.list(max:queueSize, sort:'id', order:'asc')
		queues.each { queue ->
			try{
				Environment.executeForCurrentEnvironment {
					production {
						sendGCM(queue.recipient)
					}
					development {
						sendTest(queue.recipient)
					}
				}
				queue.delete()
			}catch(Exception ex){
				queue.recipient.error = ex.message
				log.error ex.message
			}
		}
		log.info("Sent (Intance: #${instanceId}, Channel: #${channelId}) : ${queues.size()} messages sent")
	}

	/**
	 * 보존기간이 종료된 메시지를 월별 Log 테이블로 이동한다.
	 * @param instance 서버 Instance번호
	 * @param transactionSize 처리단위(channelRange.size()*queueSize)
	 * @param terminatePreserveDays 보존기간
	 */
	def terminate(int instanceId, int transactionSize, int terminatePreserveDays) {
		def terminateDate = use(TimeCategory) { new Date() - 30.seconds}
		def gmsMessageTable = "GMS_MESSAGE_LOG_${terminateDate.format('yyyyMM')}"
		def gmsMessageRecipientTable = "GMS_MESSAGE_RECIPIENT_LOG_${terminateDate.format('yyyyMM')}"
		def sql = new Sql(sessionFactory.currentSession.connection())
		
		createLogTableIfNotExist(sql, gmsMessageTable, gmsMessageRecipientTable)
		
		def (terminated, moved, deleted) = [0, 0, 0]
		def criteria = GmsMessage.where{ sentTime != null && sentTime <= terminateDate && isTerminated == false }
		terminated = criteria.updateAll(isTerminated: true)
		if(terminated > 0){
			moved = sql.executeUpdate("INSERT INTO ${Sql.expand(gmsMessageTable)} SELECT * FROM gms_message WHERE is_terminated = 1")
			deleted =  criteria.deleteAll()
		}
		log.info "Terminated (Intance: #${instanceId}, terminatePreserveDays: ${terminatePreserveDays}): ${terminated} messages terminated, ${moved} moved to Log, ${deleted} deleted"
		
		def criteria2 = GmsMessageRecipient.where{ sentTime != null && sentTime <= terminateDate && isTerminated == false }
		terminated = criteria2.updateAll(isTerminated: true)
		if(terminated > 0){
			moved = sql.executeUpdate("INSERT INTO ${Sql.expand(gmsMessageRecipientTable)} SELECT * FROM gms_message_recipient WHERE is_terminated = 1")
			deleted =  criteria2.deleteAll()
		}
		log.info "Terminated (Intance: #${instanceId}, terminatePreserveDays: ${terminatePreserveDays}): ${terminated} recipients terminated, ${moved} moved to Log, ${deleted} deleted"
	}
	
	/**
	 * 
	 * @param sql
	 * @param gmsMessageTable
	 * @param gmsMessageRecipientTable
	 */
	def createLogTableIfNotExist(Sql sql, String gmsMessageTable, String gmsMessageRecipientTable){
		try{
			sql.firstRow("SELECT COUNT(1) FROM ${Sql.expand(gmsMessageTable)} WHERE 1=0")
		}catch(Exception ex){
			Environment.executeForCurrentEnvironment {
				production {
					sql.execute("CREATE TABLE ${Sql.expand(gmsMessageTable)} AS (SELECT * FROM GMS_MESSAGE WHERE 1=0)")
				}
				development {
					sql.execute("CREATE TABLE ${Sql.expand(gmsMessageTable)} AS (SELECT * FROM GMS_MESSAGE WHERE 1=0) WITH NO DATA")
				}
			}
			log.info "Table ${gmsMessageTable} created."
		}
		try{
			sql.firstRow("SELECT COUNT(1) FROM ${Sql.expand(gmsMessageRecipientTable)} WHERE 1=0")
		}catch(Exception ex){
			Environment.executeForCurrentEnvironment {
				production {
					sql.execute("CREATE TABLE ${Sql.expand(gmsMessageRecipientTable)} AS (SELECT * FROM GMS_MESSAGE_RECIPIENT WHERE 1=0)")
				}
				development {
					sql.execute("CREATE TABLE ${Sql.expand(gmsMessageRecipientTable)} AS (SELECT * FROM GMS_MESSAGE_RECIPIENT WHERE 1=0) WITH NO DATA")
				}
			}
			log.info "Table ${gmsMessageRecipientTable} created."
		}
	}

	/**
	 * GCM메시지를 수신자 1명에게 전송
	 * @param message
	 */
	def sendGCM(GmsMessageRecipient message) {
		if(gcmSender == null){
			gcmSender = new Sender(grailsApplication.config.gms.gcmApiKey)
		}
		// registrationId가 null이면 사용자정보에서 userId를 찾지 못한 경우임
		if(message.registrationId == null){
			message.isFailed = true
			message.error = 'Invalid userId'
		}else{
			Message gcmMessage = new Message.Builder()
					.collapseKey('GMS')
					.addData('GMS.id', message.id.toString())
					.addData('GMS.ownType', message.ownType)
					.addData('GMS.msgType', message.msgType)
					.addData('GMS.content', URLEncoder.encode(message.content, 'UTF-8') )
					.addData('GMS.senderId', message.senderId)
					.build()
			//log.debug 'GCM message: ' + gcmMessage.toString()
	
			Result result = gcmSender.send(gcmMessage, message.registrationId, 3)
			//log.debug 'Result :' + message.userId + ',' + result.messageId
			if(result.messageId){
				message.isSent = true
			}else{
				message.isFailed = true
				message.error = result.errorCode
			}
		}
		message.save()
	}

	/**
	 * 메시지 전송 테스트	
	 * @param message
	 */
	def sendTest(GmsMessageRecipient message) {
		// registrationId가 null이면 사용자정보에서 userId를 찾지 못한 경우임
		if(message.registrationId == null){
			message.isFailed = true
			message.error = 'Invalid userId'
		}else{
			message.isSent = true
		}
		message.save()
	}

	/**
	 * 메시지 읽음 처리
	 * @param message
	 */
	def GmsMessage read(GmsMessage message){
		if(!message.isRead){
			message.isRead = true
		}
		return message
	}
	


}
