package kr.codesolutions.gms

import java.util.Date;

class GmsUserGroup {
	static mapping = {
		id generator:'sequence', params:[sequence:'SEQ_GMSUSERGROUP_ID']
		name column: 'NAME', index: 'IDX_GMSUSERGROUP_0'
	}

	static belongsTo = [owner: GmsUser]
	String name;
	static hasMany = [members: GmsUser]
	boolean enabled = true
	
	Date createdTime
	String createdBy
	Date modifiedTime
	String modifiedBy
	
	static constraints = {
		name blank: false, maxSize: 255
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
