package kr.codesolutions.gms

import java.util.Date;

class GmsMessageRecipient {
	static mapping = {
		id generator:'sequence', params:[sequence:'SEQ_GMSMESSAGERECIPIENT_ID']
	}

	static belongsTo = [message:GmsMessage]
	String userId
	String name
	String phoneNumber
	String registrationId
	String sendType
	
	boolean isSent = false
	Date sentTime
	boolean isRead = false
	Date readTime
	
	String error

	Date createdTime
	String createdBy
	Date modifiedTime
	String modifiedBy
	
	static constraints = {
		userId blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber blank: false, maxSize: 20
		registrationId blank: false, maxSize: 255
		sendType nullable: true, maxSize: 10
		error nullable: true, maxSize: 255
		sentTime nullable: true
		readTime nullable: true
		createdTime nullable: true
		createdBy nullable: true
		modifiedTime nullable: true
		modifiedBy nullable: true
	}
	
	def beforeInsert() {
		createdTime = new Date()
	   	modifiedTime = new Date()
	 }
	
	def beforeUpdate() {
		modifiedTime = new Date()
		if (isDirty('isSent') && isSent) {
			sentTime = new Date()
		}
		if (isDirty('isRead') && isRead) {
			readTime = new Date()
		}
	 }
}
