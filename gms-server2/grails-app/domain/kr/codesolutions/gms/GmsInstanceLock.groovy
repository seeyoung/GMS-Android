package kr.codesolutions.gms

import kr.codesolutions.*


class GmsInstanceLock {
	static mapping = {
		id column: 'NAME', generator: 'assigned'
	}
	String id
	boolean isLocked = false // Locked 여부
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static constraints = {
		id maxSize: 10 
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
