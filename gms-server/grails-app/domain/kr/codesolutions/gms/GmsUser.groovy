package kr.codesolutions.gms

import java.util.Date;

class GmsUser {
	static mapping = {
		id generator:'sequence', params:[sequence:'SQ_GMSUSER_ID']
		name index: 'IDX_GMSUSER_0'
		phoneNumber index: 'IDX_GMSUSER_0'
		registrationId index: 'IDX_GMSUSER_1'
	}
	
	String userId
	String name
	String phoneNumber
	String registrationId
	boolean isSendable = false
	boolean enabled = true
	int errorCount = 0

	Date createdTime
	String createdBy
	Date modifiedTime
	String modifiedBy

	static constraints = {
		userId unique: true, blank: false, maxSize: 50
		name blank: false, maxSize: 50
		phoneNumber blank: false, maxSize: 20
		registrationId blank: false, maxSize: 255
		errorCount min: 0, max: 3
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
		if (isDirty('errorCount') && errorCount >= 3) {
			enabled = false
		}
	}
}
