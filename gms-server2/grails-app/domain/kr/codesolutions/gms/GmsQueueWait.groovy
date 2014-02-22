package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsQueueWait {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSQUEUEWAIT_ID']
		instance index: 'IDX_GMSQUEUEWAIT_1'
	}
	
	byte instance = 0 // GMS Instance 번호
	
	GmsMessage message
	long offset = 0 // 발송할 처음 GmsMessageRecipient ID위치
	long end = 0 // 발송할 마지막 GmsMessageRecipient ID위치
	int recipientCount = 0
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
		instance range: 0..99
	}
	
	def beforeInsert() {
		if(message.status == MessageStatus.PUBLISHING){
			message.status = MessageStatus.WAITING
		}
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
