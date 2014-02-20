package kr.codesolutions.gms

import java.util.Date;

class GmsMessageSender {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSSENDER_ID']
	}

	static belongsTo = [message:GmsMessage]
	
	String userId
	String name
	String phoneNumber
	String registrationId
	String email
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()
	
	static constraints = {
		userId blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber blank: false, maxSize: 20
		registrationId nullable: true, maxSize: 255
		email nullable: true, maxSize: 50
	}
	
	def beforeInsert() {
	 }
	
	def beforeUpdate() {
		modifiedTime = new Date()
	 }
}
