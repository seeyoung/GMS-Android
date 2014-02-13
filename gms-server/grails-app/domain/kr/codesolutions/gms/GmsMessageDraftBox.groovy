package kr.codesolutions.gms

import java.util.Date;

class GmsMessageDraftBox {
	static mapping = {
		id generator:'sequence', params:[sequence:'SQ_GMSDRAFTBOX_ID']
	}

	static belongsTo = [owner: GmsUser, message: GmsMessage]
	
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
	   message.status = 'DRAFT'
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}
