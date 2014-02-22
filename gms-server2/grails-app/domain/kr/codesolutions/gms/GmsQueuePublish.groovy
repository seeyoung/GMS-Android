package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsQueuePublish {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSQUEUEPUBLISH_ID']
		instance index: 'IDX_GMSQUEUEPUBLISH_1'
	}
	
	byte instance = 0 // GMS Instance 번호

	GmsMessage message
	long offset = 0 // Publish할 처음 GmsUser ID위치
	long end = 0 // Publish할 마지막 GmsUser ID위치
	int recipientCount = 0
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
		instance range: 0..99
	}
	
	def beforeInsert() {
		if(message.status == MessageStatus.DRAFT){
			message.status = MessageStatus.PUBLISHING
		}
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
