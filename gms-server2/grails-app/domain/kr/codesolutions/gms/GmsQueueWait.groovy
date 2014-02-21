package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsQueueWait {
	static mapping = {
		version false
	}
	
	byte instance = 0 // GMS Instance 번호
	
	static belongsTo = [message: GmsMessage]
	long offset = 0 // 발송할 처음 GmsMessageRecipient ID위치
	long end = 0 // 발송할 마지막 GmsMessageRecipient ID위치
	int recipientCount = 0
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
		instance range: 0..99
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
