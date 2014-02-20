package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsQueueDraft {
	static mapping = {
		version false
	}
	
	static belongsTo = [message: GmsMessage]
	
	long offset = 0 // Publish할 처음 GmsUser ID위치
	long end = 0 // Publish할 마지막 GmsUser ID위치
	int recipientCount = 0
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
