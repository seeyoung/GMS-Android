package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.SendType

class GmsMessageRecipient {
	static mapping = {
		version false
		id generator:'native', params:[sequence:'SQ_GMSRECIPIENT_ID']
		status index: 'IDX_GMSRECIPIENT_1'
		lastEventTime index: 'IDX_GMSRECIPIENT_1'
	}

	static belongsTo = [message:GmsMessage]
	
	String userId
	String name
	String phoneNumber
	String registrationId
	String email

	String subject
	String content
	SendType sendType = SendType.GCM
	MessageStatus status = MessageStatus.WAITING
	
	boolean isSent = false
	Date sentTime
	boolean isRead = false
	Date readTime
	boolean isFailed = false
	Date failedTime

	Date completedTime
	Date terminatedTime
	
	String error

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	String lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
	
	static constraints = {
		userId blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber nullable: true, maxSize: 20
		registrationId nullable: true, maxSize: 255
		email nullable: true, email: true, maxSize: 50
		sendType nullable: true, maxSize: 10
		subject blank: false, maxSize: 255
		content blank: false, maxSize: 2000
		status blank: false, maxSize: 10
		error nullable: true, maxSize: 255
		sentTime nullable: true
		readTime nullable: true
		failedTime nullable: true
		completedTime nullable: true
		terminatedTime nullable: true
	}
	
	def beforeInsert() {
	 }
	
	def beforeUpdate() {
		modifiedTime = new Date()
		lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
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
				case MessageStatus.COMPLETED: completedTime = new Date(); break
				case MessageStatus.TERMINATED: terminatedTime = new Date(); break
			}
		}
	 }
}
