package kr.codesolutions.gms

import java.util.Date;

class GmsUserGroup {
	static mapping = {
		version false
		name column: 'NAME', index: 'IDX_GMSUSERGROUP_0'
	}

	String name;
	String filter;
	
	Date createdTime =  new Date()
	Date modifiedTime =  new Date()
	
	static constraints = {
		name blank: false, maxSize: 255
		filter nullable: true, maxSize: 255
	}
	
	def beforeInsert() {
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}