package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsQueueSend {
	static mapping = {
		version false
		id generator:'native', params:[sequence:'SQ_GMSQUEUESEND_ID']
		instance index: 'IDX_GMSQUEUESEND_1'
		channel index: 'IDX_GMSQUEUESEND_1'
	}
	
	byte instance = 1 // GMS Instance 번호
	byte channel = 1 // 메시지 발송 채널 번호

	GmsMessage message
	GmsMessageRecipient recipient
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
		instance range: 1..99
		channel range: 1..99
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
