package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.SendPolicy

import org.grails.databinding.BindingFormat

class GmsMessage {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSMESSAGE_ID']
		status index: 'IDX_GMSMESSAGE_1'
		lastEventTime index: 'IDX_GMSMESSAGE_1'
	}

	String subject
	String content
	@BindingFormat('yyyy-MM-dd HH:mm')
	Date reservationTime
	boolean isPersonal = false
	boolean isBulk = false
	boolean isCallback = false
	SendPolicy sendPolicy = SendPolicy.GCM
	MessageStatus status = MessageStatus.DRAFT
	
	String senderUserId = 'gmsmaster'
	String senderName
	String senderPhoneNumber
	String senderRegistrationId
	String senderEmail
	String recipientUserId
	String recipientFilter
	int recipientCount = 0
	int failedCount = 0
	int sentCount = 0
	
	Date draftTime = new Date()
	Date publishTime
	Date waitTime
	Date sendTime
	Date failedTime
	Date sentTime
	Date readTime
	Date completedTime
	Date terminatedTime
	boolean isFailed = false
	boolean isSent = false
	boolean isRead = false
	
	String error

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	String lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
	
	static constraints = {
		subject blank: false, maxSize: 255
		content blank: false, maxSize: 2000
		sendPolicy blank: false, maxSize: 10
		status blank: false, maxSize: 10
		senderUserId blank: false, maxSize: 50
		senderName nullable: true, maxSize: 50
		senderPhoneNumber nullable: true, maxSize: 20
		senderRegistrationId nullable: true, maxSize: 255
		senderEmail nullable: true, maxSize: 50
		recipientUserId nullable: true, maxSize: 50
		recipientFilter nullable: true, maxSize: 255
		draftTime nullable: true
		publishTime nullable: true
		waitTime nullable: true
		sendTime nullable: true
		failedTime nullable: true
		sentTime nullable: true
		completedTime nullable: true
		readTime nullable: true
		terminatedTime nullable: true
		error nullable: true, maxSize: 255
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
		lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
		
		if (isDirty('failedCount') && isDirty('sentCount')) {
			if(failedCount >= recipientCount){
				isFailed = true
			}else if(failedCount + sentCount >= recipientCount){
				isSent = true
			}
		}
		if (isDirty('isFailed') && isFailed) {
			failedTime = new Date()
			status = MessageStatus.COMPLETED
		}
		if (isDirty('isSent') && isSent) {
			sentTime = new Date()
			status = MessageStatus.COMPLETED
		}
		if (isDirty('isRead') && isRead) {
			readTime = new Date()
		}
		if (isDirty('status')) {
			switch(status){
				case MessageStatus.DRAFT: draftTime = new Date(); break
				case MessageStatus.PUBLISHING: publishTime = new Date(); break
				case MessageStatus.WAITING: waitTime = new Date(); break
				case MessageStatus.SENDING: sendTime = new Date(); break
				case MessageStatus.SENT: sentTime = new Date(); isSent = true; break
				case MessageStatus.COMPLETED: completedTime = new Date(); break
				case MessageStatus.TERMINATED: terminatedTime = new Date(); break
			}
		}
	 }
}
