package kr.codesolutions.gms

import kr.codesolutions.gms.constants.MessageStatus

class GmsBoxDraft {
	static mapping = {
		version false
		id generator:'sequence', params:[sequence:'SQ_GMSDRAFTBOX_ID']
	}

	static belongsTo = [owner: GmsUser, message: GmsMessage]
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()
	
	static constraints = {
	}
	
	def beforeInsert() {
	   message.status = MessageStatus.DRAFT
	 }
	
	def beforeUpdate() {
	   modifiedTime = new Date()
	 }
}
