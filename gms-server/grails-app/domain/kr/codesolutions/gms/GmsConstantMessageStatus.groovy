package kr.codesolutions.gms

import java.util.Date;

class GmsConstantMessageStatus {
	static mapping = {
		id generator:'sequence', params:[sequence:'SQ_GMSMESSAGESTATUS_ID']
	}

	String name
	String description
	
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
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}