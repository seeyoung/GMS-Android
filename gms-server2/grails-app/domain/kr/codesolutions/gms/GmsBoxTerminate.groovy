package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus

class GmsBoxTerminate {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSTERMINATEBOX_ID']
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
