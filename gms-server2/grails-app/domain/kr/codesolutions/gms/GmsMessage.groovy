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
	GmsUserGroup recipientGroup
	String recipientFilter
	int recipientCount = 0
	int failedCount = 0
	int sentCount = 0
	
	boolean isSent = false
	Date sentTime
	boolean isRead = false
	Date readTime
	boolean isFailed = false
	Date failedTime
	
	Date draftTime = new Date()
	Date submitTime
	Date publishTime
	Date waitTime
	Date sendTime
	Date completedTime
	Date terminatedTime
	
	String error

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	String lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
	
	static constraints = {
		subject blank: false, maxSize: 255
		content blank: false, maxSize: 2000
		reservationTime nullable: true
		sendPolicy blank: false, maxSize: 10
		status blank: false, maxSize: 10
		senderUserId blank: false, maxSize: 50
		senderName nullable: true, maxSize: 50
		senderPhoneNumber nullable: true, maxSize: 20
		senderRegistrationId nullable: true, maxSize: 255
		senderEmail nullable: true, maxSize: 50
		recipientUserId nullable: true, maxSize: 50
		recipientGroup nullable: true
		recipientFilter nullable: true, maxSize: 255
		sentTime nullable: true
		readTime nullable: true
		failedTime nullable: true
		draftTime nullable: true
		submitTime nullable: true
		publishTime nullable: true
		waitTime nullable: true
		sendTime nullable: true
		completedTime nullable: true
		terminatedTime nullable: true
		error nullable: true, maxSize: 255
	}
	
	def beforeInsert() {
		if(reservationTime == null) reservationTime = new Date()
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
		lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
		
		if (isDirty('failedCount') || isDirty('sentCount')) {
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
				case MessageStatus.SUBMIT: submitTime = new Date(); break
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
