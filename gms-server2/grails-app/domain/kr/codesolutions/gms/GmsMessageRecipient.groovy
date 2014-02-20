package kr.codesolutions.gms

import java.util.Date;

import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.MessageType;
import kr.codesolutions.gms.constants.OwnType;
import kr.codesolutions.gms.constants.SendPolicy
import kr.codesolutions.gms.constants.SendType

class GmsMessageRecipient {
	static mapping = {
		version false
//		id generator:'sequence', params:[sequence:'SQ_GMSRECIPIENT_ID']
	}

	static belongsTo = [message:GmsMessage]
	
	String userId
	String name
	String phoneNumber
	String registrationId
	String email
	
	String subject
	String content
	Date reservationTime
	OwnType ownType = OwnType.PERSONAL 
	MessageType messageType = MessageType.NORMAL
	boolean isCallback = true
	SendType sendType = SendType.GCM
	MessageStatus status = MessageStatus.WAITING
	
	boolean isSent = false
	Date sentTime
	boolean isFailed = false
	Date failedTime
	boolean isCompleted = false
	Date completeTime
	boolean isRead = false
	Date readTime
	boolean isTerminated = false
	Date terminateTime
	
	String error

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	
	static constraints = {
		userId blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber blank: false, maxSize: 20
		registrationId nullable: true, maxSize: 255
		email nullable: true, maxSize: 50
		sendType nullable: true, maxSize: 10
		ownType blank: false, maxSize: 10
		messageType blank: false, maxSize: 10
		subject blank: false, maxSize: 255
		content blank: false, maxSize: 2000
		status blank: false, maxSize: 10
		error nullable: true, maxSize: 255
		sentTime nullable: true
		failedTime nullable: true
		completeTime nullable: true
		readTime nullable: true
		terminateTime nullable: true
	}
	
	def beforeInsert() {
	 }
	
	def beforeUpdate() {
		modifiedTime = new Date()
		if (isDirty('isSent') && isSent) {
			sentTime = new Date()
			isCompleted = true
			completeTime = new Date()
			status = MessageStatus.COMPLETED
		}
		if (isDirty('isFailed') && isFailed) {
			failedTime = new Date()
			isCompleted = true
			completeTime = new Date()
			status = MessageStatus.COMPLETED
		}
		if (isDirty('isRead') && isRead) {
			readTime = new Date()
		}
		if (isDirty('isTerminated') && isTerminated) {
			terminateTime = new Date()
			status = MessageStatus.TERMINATED
		}
	 }
}
