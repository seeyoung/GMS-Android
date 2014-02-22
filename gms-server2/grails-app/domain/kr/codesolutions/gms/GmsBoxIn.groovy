package kr.codesolutions.gms

import java.util.Date;

class GmsBoxIn {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSBOXIN_ID']
	}

	static belongsTo = [owner:GmsUser]
	GmsMessageRecipient recipient
	
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
