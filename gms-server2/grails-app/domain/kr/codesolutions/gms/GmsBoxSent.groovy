package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus

class GmsBoxSent {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSSENTBOX_ID']
	}

	static belongsTo = [message: GmsMessage]
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()
	
	static constraints = {
	}
	
	def beforeInsert() {
	   message.status = MessageStatus.SENT
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}
