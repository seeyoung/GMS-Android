package kr.codesolutions.gms

import java.util.Date;

class GmsBoxOut {
	static mapping = {
		id generator:'sequence', params:[sequence:'SQ_GMSOUTBOX_ID']
	}

	static belongsTo = [message: GmsMessage]
	
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
