package kr.codesolutions.gms

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.time.TimeCategory

import org.springframework.transaction.UnexpectedRollbackException

import com.google.android.gcm.server.Message
import com.google.android.gcm.server.MulticastResult
import com.google.android.gcm.server.Result
import com.google.android.gcm.server.Sender

@Transactional
class GmsMessageService {
	def grailsApplication
	Sender gcmSender
	
	def Map listUser(UserSearchCommand cmd, Map params) {
		def criteria = new DetachedCriteria(GmsUser).build{
			and {
				like ('userId', cmd.userId)
				like ('name', cmd.name)
				like ('phoneNumber', cmd.phoneNumber)
				eq ('enabled', true)
			}
		}
		def totalCount = criteria.count()
		def list = criteria.list(params)
		
		return [list: list, totalCount: totalCount]
	}
	
	def GmsMessage create(GmsMessage gmsMessageInstance, Map params) {
		params.senderId = params.senderId?:'gmsmaster'
		gmsMessageInstance.sender = GmsUser.findByUserId(params.senderId)
		gmsMessageInstance.owner = gmsMessageInstance.sender
		gmsMessageInstance.sendPolicy = 'ADVENCED'
		gmsMessageInstance.validate()
		if (gmsMessageInstance.hasErrors()) {
			return gmsMessageInstance
		}
		gmsMessageInstance.save()
		
		// 메시지 수신자 저장
		if(params.recipientGroup != null && !params.recipientGroup.empty){
			def gmsUserGroupInstance = GmsUserGroup.get(params.recipientGroup)
			if(gmsUserGroupInstance){
				gmsUserGroupInstance?.members.each { gmsUserInstance -> createRecipient(gmsMessageInstance, gmsUserInstance)}
			}
		}
		if(params.recipientId != null && !params.recipientId.empty){
			params.recipientId.split(';').each { userId ->
				def gmsUserInstance = GmsUser.findByUserId(userId)
				if(gmsUserInstance){
					createRecipient(gmsMessageInstance, gmsUserInstance)
				}
			}
		}
		if(gmsMessageInstance.recipients == null) {
			throw new UnexpectedRollbackException("recipients is empty")
		}

		// 예약시간이 있으면 예약메시지함으로 저장(스케쥴러가 예약시간을 확인하여 발송한다)
		// 그외의 메시지는 발송대기함으로 저장(스케쥴러가 즉시 발송한다)
		if(gmsMessageInstance.reservationTime){
			new GmsMessageReserveBox(message:gmsMessageInstance).save flush:true
		}else{
			new GmsMessageWaitingBox(message:gmsMessageInstance).save flush:true
		}
		// 모든 메시지는 보낸메시지함에도 보관한다
		new GmsMessageOutBox(owner:gmsMessageInstance.owner, message:gmsMessageInstance).save flush:true
		
		return gmsMessageInstance
	}
	
	def createRecipient(GmsMessage gmsMessageInstance, GmsUser gmsUserInstance){
		def gmsMessageRecipientInstance =
			new GmsMessageRecipient(userId: gmsUserInstance.userId,
									name: gmsUserInstance.name,
									phoneNumber: gmsUserInstance.phoneNumber,
									registrationId: gmsUserInstance.registrationId,
									message: gmsMessageInstance).save()
		gmsMessageInstance.addToRecipients(gmsMessageRecipientInstance)
		new GmsMessageInBox(owner: gmsUserInstance,
							message: gmsMessageInstance).save()

	}
	
	def GmsMessage read(GmsMessage gmsMessageInstance, GmsMessageRecipient gmsMessageRecipientInstance){
		if(!gmsMessageRecipientInstance.isRead){
			gmsMessageRecipientInstance.isRead = true
			gmsMessageRecipientInstance.save flush:true
		}
		if(!gmsMessageInstance.isRead){
			gmsMessageInstance.isRead = true
			gmsMessageInstance.save flush:true
		}
		def box = GmsMessageResendBox.findByMessage(gmsMessageInstance)
		if(box != null){
			box.delete()
			new GmsMessageSentBox(message:gmsMessageInstance).save flush:true
		}
		return gmsMessageInstance
	}
	
	def GmsMessageInBox deleteInBox(GmsUser gmsUserInstance, GmsMessage gmsMessageInstance){
		def box = GmsMessageInBox.findByOwnerAndMessage(gmsUserInstance, gmsMessageInstance)
		if(box != null){
			box.delete flush:true
		}
	}
	
	def GmsMessageOutBox deleteOutBox(GmsUser gmsUserInstance, GmsMessage gmsMessageInstance){
		def box = GmsMessageOutBox.findByOwnerAndMessage(gmsUserInstance, gmsMessageInstance)
		if(box != null){
			box.delete flush:true
		}
		return box
	}

	def GmsMessage createAndSend(GmsMessage gmsMessageInstance, Map params) {
		def sentCount = 0
		gmsMessageInstance = create(gmsMessageInstance, params)
		if (gmsMessageInstance.hasErrors()){
			throw new UnexpectedRollbackException(gmsMessageInstance.errors)
		}
		
		GmsMessageWaitingBox.findByMessage(gmsMessageInstance).delete(flush:true)
		new GmsMessageSendingBox(message:gmsMessageInstance).save(flush:true).delete(flush:true)
		sentCount = sendGCM(gmsMessageInstance)
		new GmsMessageResendBox(message:gmsMessageInstance).save(flush:true)
		
		return gmsMessageInstance
	}
	

    def send(GmsMessage gmsMessageInstance) {
		switch(gmsMessageInstance.sendPolicy){
			case ['GCM','COMPLEX','ADVENCED']:
				sendGCM gmsMessageInstance
				break
			case 'SMS':
				sendSMS gmsMessageInstance
				break
		}
    }

	// GCM메시지를 모든 수신자에게 전송 발송정책에따라 GCM미수신자에게는 SMS메시지를 전송한다.
	def sendGCM(GmsMessage gmsMessageInstance) {
		if(gcmSender == null){
			gcmSender = new Sender(grailsApplication.config.gms.gcmApiKey)
		}
		Message gcmMessage = new Message.Builder()
				.collapseKey('GMS')
				.addData('GMS.id', gmsMessageInstance.id.toString())
				.addData('GMS.ownType', gmsMessageInstance.ownType)
				.addData('GMS.msgType', gmsMessageInstance.msgType)
				.addData('GMS.subject', URLEncoder.encode(gmsMessageInstance.subject, 'UTF-8') )
				.addData('GMS.content', URLEncoder.encode(gmsMessageInstance.content, 'UTF-8') )
				.addData('GMS.senderId', gmsMessageInstance.sender.userId)
				.build()
		log.debug 'GCM message: ' + gcmMessage.toString()
		def gcmRecipients = gmsMessageInstance.recipients.grep{it.registrationId != null}
		log.info 'Recipient Count : ' + gcmRecipients.size()
		if(gcmRecipients.size() > 0){
			try{
				MulticastResult result = gcmSender.send(gcmMessage, gcmRecipients*.registrationId, 3)
				gcmRecipients.eachWithIndex{gmsMessageRecipientInstance, index ->
					log.info 'Result[' + index + '] :' + gmsMessageRecipientInstance.name + ',' + result.results[index].messageId
					if(result.results[index].messageId){
						gmsMessageRecipientInstance.isSent = true
						gmsMessageRecipientInstance.sendType = 'GCM'
					}else{
						gmsMessageRecipientInstance.error = result.results[index].errorCode
					}
					gmsMessageRecipientInstance.save()
				}
			}catch(Exception ex){
				gmsMessageInstance.error = ex.message
				log.error ex.message
			}
		}
		// GCM메시지를 전송하지 못하거나 RegistrationID가 없는 사용자에게 SMS메시지를 전송한다.
		if(gmsMessageInstance.sendPolicy == 'COMPLEX'){
			def smsRecipients = gmsMessageInstance.recipients.grep{it.registrationId == null || it.errorCode != null}
			if(smsRecipients.size() > 0){
				try{
					smsRecipients.each { recipient ->
						sendSMS(gmsMessageInstance.content, gmsMessageInstance.sender, recipient)
					}
				}catch(Exception ex){
					gmsMessageInstance.error = ex.message
					log.error ex.message
				}
			}
		}
		return gmsMessageInstance.recipients.grep{it.isSent}.size()
    }
	
	// GCM메시지를 수신자 1명에게 전송
	def sendGCM(Message gcmMessage, Sender gcmSender, GmsMessageRecipient gmsMessageRecipientInstance){
		Result result = gcmSender.send(gcmMessage, gmsMessageRecipientInstance.registrationId, 3)
		if(result.messageId){
			gmsMessageRecipientInstance.isSent = true
			gmsMessageRecipientInstance.sendType = 'GCM'
		}else{
			gmsMessageRecipientInstance.error = result.errorCode
		}
		gmsMessageRecipientInstance.save()
	}

	// SMS메시지를 모든 수신자에게 전송
	def sendSMS(GmsMessage gmsMessageInstance) {
		gmsMessageInstance.recipients.each{ recipient ->
			sendSMS(gmsMessageInstance.content, gmsMessageInstance.sender, recipient)
		}
    }
	
	// SMS메시지를 수신자 1명에게 전송
	def sendSMS(String messageContent, GmsUser sender, GmsMessageRecipient gmsMessageRecipientInstance) {
		gmsMessageRecipientInstance.isSent = true
		gmsMessageRecipientInstance.sendType = 'SMS'
		gmsMessageRecipientInstance.save()
	}

	/**
	 * 발송대기메시지를 발송메시지함으로 이동한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @return
	 */
	def sendWaitingMessage(int instance) {
		def results = GmsMessageWaitingBox.list(sort:'createdTime', order:'asc')
		if(results != null){
			results.each { box ->
				if(!box.isDirty()){
					box.delete flush:true
					log.info "message ${box.message.id} is moved to sendingbox."
					new GmsMessageSendingBox(message:box.message).save flush:true
				}
			}
		}
	}
	
	/**
	 * 발송예약시간을 초과한 예약메시지를 발송대기메시지함으로 이동한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @return
	 */
	def sendReservedMessage(int instance) {
		def criteria = new DetachedCriteria(GmsMessageReserveBox).build{
							message{
								lt('reservationTime', new Date())
							}
		}
		def results = criteria.list(sort:'createdTime', order:'asc')
		if(results != null){
			results.each { box ->
				if(!box.isDirty()){
					box.delete flush:true
					log.info "message ${box.message.id} is moved to waitingbox."
					new GmsMessageWaitingBox(message:box.message).save flush:true
				}
			}
		}
	}
	
	/**
	 * 발송메시지함에서 가장먼저 들어온 메시지 하나만 발송한다. 
	 * 
	 * @param instance 서버 Instance번호
	 * @return
	 */
	def sendMessage(int instance) {
		def results = GmsMessageSendingBox.list(sort:'createdTime', order:'asc')
		if(results != null){
			results.each { box ->
				if(!box.isDirty()){
					box.delete flush:true
					send(box.message)
					log.info "message ${box.message.id} is sent."
					if(box.message.sendPolicy == 'ADVENCED'){
						log.info "message ${box.message.id} is moved to resendBox."
						new GmsMessageResendBox(message:box.message).save flush:true
					}else{
						log.info "message ${box.message.id} is moved to sentBox."
						new GmsMessageSentBox(message:box.message).save flush:true
					}
				}
			}
		}
	}
	
	/**
	 * 재발송이 필요한 메시지를 제외하고 완료메시지함으로 이동한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @return
	 */
	def completeMessage(int instance) {
		def results = GmsMessageSentBox.list(sort:'createdTime', order:'asc')
		if(results != null){
			results.each { box ->
				if(!box.isDirty()){
					box.delete flush:true
					// 발신오류가 발생한 수신자의 errorCount를 증가시킨다. errorCount가 3이상이면 사용불가
					def gcmRecipients = box.message.recipients.grep{it.error != null}
					gcmRecipients.each{ gmsMessageRecipientInstance ->
						def gmsUserInstance = GmsUser.findByUserId(gmsMessageRecipientInstance.userId)
						gmsUserInstance.errorCount++
						gmsUserInstance.save()
					}
					log.info "message ${box.message.id} is moved to completeBox."
					new GmsMessageCompleteBox(message:box.message).save flush:true
				}
			}
		}
	}

	/**
	 * 재발송이 필요한 메시지를 SMS발송하고 완료메시지함으로 이동한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @return
	 */
	def resendMessage(int instance, int retryPendingSeconds) {
		def criteria = new DetachedCriteria(GmsMessageResendBox).build{
							lt('createdTime', use(TimeCategory) { new Date() - retryPendingSeconds.seconds})
		}
		def results = criteria.list(sort:'createdTime', order:'asc')
		if(results != null){
			results.each { box ->
				if(!box.isDirty()){
					box.delete flush:true
					// 수신자가 1명이라도 확인하지 않는 메시지는 모든 수신자에게 SMS로 재발송한다.
					if(!box.message.isRead){
						log.info "message ${box.message.id} is resent."
						sendSMS(box.message)
					}
					log.info "message ${box.message.id} is moved to sentBox."
					new GmsMessageSentBox(message:box.message).save flush:true
				}
			}
		}
	}
	
	/**
	 * 관리기간이 종료된 메시지를 종결메시지함으로 이동한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @param terminatePendingDays 관리기간
	 * @return
	 */
	def terminateMessage(int instance, int terminatePendingDays) {
		def criteria = new DetachedCriteria(GmsMessageCompleteBox).build{
							lt('createdTime', use(TimeCategory) { new Date() - terminatePendingDays.days})
		}
		def results = criteria.list(sort:'createdTime', order:'asc')
		if(results != null){
			results.each { box ->
				if(!box.isDirty()){
					box.delete flush:true
					log.info "message ${box.message.id} is moved to terminateBox."
					new GmsMessageTerminateBox(message:box.message).save flush:true
				}
			}
		}
	}


}
