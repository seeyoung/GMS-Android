package kr.codesolutions.gms

import java.util.Date;

class GmsMessageCompleteBox {
	static mapping = {
		id generator:'sequence', params:[sequence:'SQ_GMSCOMPLETEBOX_ID']
	}

	static belongsTo = [message: GmsMessage]
	
	Date createdTime
	String createdBy
	Date modifiedTime
	String modifiedBy
	
	static constraints = {
		createdTime nullable: true
		createdBy nullable: true
		modifiedTime nullable: true
		modifiedBy nullable: true
	}
	
	def beforeInsert() {
	   createdTime = new Date()
	   modifiedTime = new Date()
	   message.status = 'COMPLETED'
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}
