package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus
import kr.codesolutions.gms.constants.MessageType
import kr.codesolutions.gms.constants.OwnType
import kr.codesolutions.gms.constants.SendPolicy

import org.grails.databinding.BindingFormat

class GmsMessage {
	static mapping = {
		id generator:'sequence', params:[sequence:'SQ_GMSMESSAGE_ID']
	}

	String subject
	String content
	@BindingFormat('yyyy-MM-dd HH:mm')
	Date reservationTime
	OwnType ownType = OwnType.PERSONAL
	MessageType messageType = MessageType.NORMAL
	boolean isCallback = true
	SendPolicy sendPolicy = SendPolicy.GCM
	MessageStatus status = MessageStatus.DRAFT
	
	GmsMessageSender sender
	String recipientFilter
	int recipientCount = 0
	
	Date draftTime = new Date()
	Date publishTime
	Date waitTime
	Date sendTime
	Date sentTime
	Date readTime
	Date completeTime
	Date terminateTime
	boolean isSent = false
	boolean isRead = false
	boolean isTerminated = false
	
	String error

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	
	static constraints = {
		ownType blank: false, maxSize: 10
		messageType blank: false, maxSize: 10
		subject blank: false, maxSize: 255
		content blank: false, maxSize: 2000
		sendPolicy blank: false, maxSize: 10
		status blank: false, maxSize: 10
		recipientFilter nullable: true, maxSize: 255
		draftTime nullable: true
		publishTime nullable: true
		waitTime nullable: true
		sendTime nullable: true
		sentTime nullable: true
		readTime nullable: true
		completeTime nullable: true
		terminateTime nullable: true
		error nullable: true, maxSize: 255
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
		if (isDirty('status')) {
			switch(status){
				case MessageStatus.DRAFT: draftTime = new Date(); break
				case MessageStatus.PUBLISHING: publishTime = new Date(); break
				case MessageStatus.WAITING: waitTime = new Date(); break
				case MessageStatus.SENDING: sendTime = new Date(); break
				case MessageStatus.SENT: sentTime = new Date(); isSent = true; break
				case MessageStatus.COMPLETED: completeTime = new Date(); break
				case MessageStatus.TERMINATED: terminateTime = new Date(); break
			}
		}
		if (isDirty('isRead') && isRead) {
			readTime = new Date()
		}
	 }
}
