package kr.codesolutions.gms

import java.util.Date;

class GmsMessageWaitingBox {
	static mapping = {
		id generator:'sequence', params:[sequence:'SEQ_GMSMESSAGEWAITINGBOX_ID']
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
	   message.status = 'WAITING'
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}
