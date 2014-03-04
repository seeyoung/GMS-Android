package kr.codesolutions.gms

import java.util.Date;

class GmsUser {
	static mapping = {
		version false
		id generator:'native', params:[sequence:'SQ_GMSUSER_ID']
		name index: 'IDX_GMSUSER_1'
		phoneNumber index: 'IDX_GMSUSER_1'
		registrationId index: 'IDX_GMSUSER_2'
		lastEventTime index: 'IDX_GMSRECIPIENT_3'
	}
	
	String userId
	String name
	String phoneNumber
	String registrationId
	String email
	boolean isSendable = false
	boolean enabled = true
	int errorCount = 0

	Date createdTime = new Date()
	Date modifiedTime = new Date()
	String lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
	
	static constraints = {
		userId unique: true, blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber nullable: true, maxSize: 20
		registrationId nullable: true, maxSize: 255
		email nullable: true, email: true, maxSize: 50
		errorCount min: 0, max: 3
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
		lastEventTime = modifiedTime.format('yyyyMMddHHmmss')
		if (isDirty('errorCount') && errorCount >= 3) {
			enabled = false
		}
	}
}
