package kr.codesolutions.gms

import kr.codesolutions.*


class GmsInstanceLock {
	static mapping = {
		version false
		id column: 'NAME', generator: 'assigned'
	}
	String id
	int instance = 0 // Lock Instance
	boolean isLocked(){ instance==0?false:true }
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static transients = ['isLocked']
	
	static constraints = {
		id maxSize: 10 
		instance range: 0..99
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
