package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.SendType

class GmsMessageRecipient {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSRECIPIENT_ID']
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
	
	Date failedTime
	Date sentTime
	Date completedTime
	Date readTime
	Date terminatedTime
	boolean isFailed = false
	boolean isSent = false
	boolean isRead = false
	
	String error

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	String lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
	
	static transients = ['failedCount','sentCount']
	
	static constraints = {
		userId blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber blank: false, maxSize: 20
		registrationId nullable: true, maxSize: 255
		email nullable: true, maxSize: 50
		sendType nullable: true, maxSize: 10
		subject blank: false, maxSize: 255
		content blank: false, maxSize: 2000
		status blank: false, maxSize: 10
		error nullable: true, maxSize: 255
		failedTime nullable: true
		sentTime nullable: true
		completedTime nullable: true
		readTime nullable: true
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
