package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsQueueSubmit {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSQUEUEDRAFT_ID']
	}
	
	GmsMessage message
	long offset = 0 // Publish할 처음 GmsUser ID위치
	long end = 0 // Publish할 마지막 GmsUser ID위치
	int recipientCount = 0
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
	}
	
	def beforeInsert() {
		if(message.status == MessageStatus.DRAFT){
			message.status = MessageStatus.SUBMIT
		}
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
