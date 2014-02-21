package kr.codesolutions.gms

import java.util.Date;

import kr.codesolutions.gms.constants.MessageStatus;

class GmsMessageSender {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSSENDER_ID']
	}

	static belongsTo = [message:GmsMessage]
	
	String userId
	String name
	String phoneNumber
	String registrationId
	String email
	MessageStatus status = MessageStatus.WAITING
	
	Date completedTime
	Date terminatedTime
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()
	String lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
	
	static constraints = {
		userId blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber blank: false, maxSize: 20
		registrationId nullable: true, maxSize: 255
		email nullable: true, maxSize: 50
		completedTime nullable: true
		terminatedTime nullable: true
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
		lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
		
		if (isDirty('status')) {
			switch(status){
				case MessageStatus.COMPLETED: completedTime = new Date(); break
				case MessageStatus.TERMINATED: terminatedTime = new Date(); break
			}
		}
	}
}
