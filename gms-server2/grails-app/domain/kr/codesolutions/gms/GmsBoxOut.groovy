package kr.codesolutions.gms

import java.util.Date;

class GmsBoxOut {
	static mapping = {
		version false
		id generator:'native', params:[sequence:'SQ_GMSBOXOUT_ID']
	}

	static belongsTo = [owner:GmsUser]
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
