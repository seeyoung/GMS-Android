package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus

class GmsBoxComplete {
	static mapping = {
		version false
	}

	static belongsTo = [message: GmsMessage]
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()
	
	static constraints = {
	}
	
	def beforeInsert() {
	   message.status = MessageStatus.COMPLETED
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}
