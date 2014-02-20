package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus

class GmsBoxTerminate {
	static mapping = {
		version false
	}

	static belongsTo = [message: GmsMessage]

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	
	static constraints = {
	}
	
	def beforeInsert() {
	   message.status = MessageStatus.TERMINATED
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}
