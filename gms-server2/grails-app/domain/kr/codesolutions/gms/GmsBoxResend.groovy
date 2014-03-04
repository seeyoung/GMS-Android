package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus


class GmsBoxResend {
	static mapping = {
		version false
		id generator:'native', params:[sequence:'SQ_GMSBOXSEND_ID']
	}
	
	GmsMessage message
	
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
