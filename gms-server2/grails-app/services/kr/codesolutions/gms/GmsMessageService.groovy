package kr.codesolutions.gms

import grails.transaction.Transactional
import grails.util.Environment
import groovy.sql.Sql
import groovy.time.TimeCategory
import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.SendPolicy
import kr.codesolutions.gms.constants.SendType

import org.hibernate.Session
import org.hibernate.SessionFactory
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
	 * 
	 * @param message 메시지
	 */
	def draft(GmsMessage message){
		def instance = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instance)
		
		def sender = GmsUser.findByUserId(message.senderUserId)
		if(sender != null){
			message.senderName = sender.name
			message.senderPhoneNumber = sender.phoneNumber
			message.senderRegistrationId = sender.registrationId
			message.senderEmail = sender.email
		}
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
		if(recipientCount >= gmsInstance.queueSize) message.isBulk = true 
		new GmsQueueDraft(message: message, offset: offset?:0, end: end?:0, recipientCount: recipientCount).save()
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
		def queues = GmsQueueDraft.where{message.reservationTime <= new Date()}.list(max:queueSize, sort:'id', order:'asc')
		queues.each{ queue ->
			def message = queue.message
			def prevQueue = new GmsQueuePublish(instance: instanceId, message: message, offset: queue.offset, end: queue.end, recipientCount: queue.recipientCount).save()
			// 대량메시지는 Publish에 많은 시간이 소요되므로 모든 Instance에 본배한다.
			if(queue.recipientCount > queueSize){
				def instances = GmsInstance.where{isRunning == true}.list(sort:'id', order:'asc')
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
			def users = GmsUser.createCriteria().list{
				sqlRestriction(message.recipientFilter + " AND id >= ${queue.offset}")
				maxResults(queueSize)
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
														content: message.content
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
							
			recipients.eachWithIndex{ recipient, idx ->
				new GmsQueueSend(instance: instanceId, channel: channelId, message: queue.message, recipient: recipient).save()
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
				def returnCode = send(message, recipient)
				
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
						GmsQueuePublish.where{ message == gmsMessageInstance }.count() +
						GmsQueuePublish.where{ message == gmsMessageInstance }.count()
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
					new Date() - 300.seconds
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
			if(criteria.find() != null){
				def (terminated, moved, deleted) = [0, 0, 0]
				terminated = criteria.updateAll(status: MessageStatus.TERMINATED, terminatedTime: new Date())
				if(terminated > 0){
					moved = sql.executeUpdate("INSERT INTO ${Sql.expand(targetTable)} SELECT * FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
					deleted =  sql.executeUpdate("DELETE FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
					log.info "Terminated (Intance: #${instanceId}, terminateDate: ${terminateDate}): ${terminated} messages terminated, ${moved} moved to Log, ${deleted} deleted"
				}
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
			if(criteria.find() != null){
				def (terminated, moved, deleted) = [0, 0, 0]
				terminated = criteria.updateAll(status: MessageStatus.TERMINATED, terminatedTime: new Date())
				if(terminated > 0){
					moved = sql.executeUpdate("INSERT INTO ${Sql.expand(targetTable)} SELECT * FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
					deleted =  sql.executeUpdate("DELETE FROM ${Sql.expand(sourceTable)} WHERE status = 'TERMINATED'")
					log.info "Terminated (Intance: #${instanceId}, terminateDate: ${terminateDate}): ${terminated} messages terminated, ${moved} moved to Log, ${deleted} deleted"
				}
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
	def send(GmsMessage message, GmsMessageRecipient recipient){
		Environment.executeForCurrentEnvironment {
			production {
				switch(recipient.sendType){
					case SendType.GCM: return sendGCM(message, recipient)
					case SendType.SMS: return sendSMS(message, recipient)
					case SendType.EMAIL: return sendEMAIL(message, recipient)
				}
			}
			development {
			}
		}
	}

	/**
	 * 메시지를 수신자 1명에게 GCM 전송
	 * 
	 * @param message
	 * @param recipient
	 * @return 에러코드
	 */
	def sendGCM(GmsMessage message, GmsMessageRecipient recipient) {
		if(recipient.registrationId == null){
			return 'Invalid registrationId'
		}
		if(gcmSender == null){
			gcmSender = new Sender(grailsApplication.config.gms.gcmApiKey)
		}
		Message gcmMessage = new Message.Builder()
				.collapseKey('GMS')
				.addData('GMS.id', recipient.id.toString())
				.addData('GMS.ownType', message.isPersonal)
				.addData('GMS.msgType', message.isCallback)
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
	def sendSMS(GmsMessage message, GmsMessageRecipient recipient) {
	}
	
	/**
	 * 메시지를 수신자 1명에게 EMAIL 전송
	 * 
	 * @param message
	 * @param recipient
	 * @return 에러코드
	 */
	def sendEMAIL(GmsMessage message, GmsMessageRecipient recipient) {
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
	


}
