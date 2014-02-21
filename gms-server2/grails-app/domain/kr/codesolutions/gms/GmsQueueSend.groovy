package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsQueueSend {
	static mapping = {
		version false
		channel index: 'IDX_GMSQUEUESENDING_1'
	}
	
	byte instance = 1 // GMS Instance 번호
	byte channel = 1 // 메시지 발송 채널 번호
	static belongsTo = [message: GmsMessage, recipient: GmsMessageRecipient]
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
		instance range: 1..99
		channel range: 1..99
	}
	
	def beforeInsert() {
		recipient.status = MessageStatus.SENDING
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}