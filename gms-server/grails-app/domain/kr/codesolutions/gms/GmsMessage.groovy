package kr.codesolutions.gms

import org.grails.databinding.BindingFormat

class GmsMessage {
	static mapping = {
		id generator:'sequence', params:[sequence:'SEQ_GMSMESSAGE_ID']
	}

	static belongsTo = [owner: GmsUser]
	String ownType = '0' // 소유구분 (0:개인메시지, 1:공지메시지)
	String msgType = '1' // 메시지구분 (0:일반메시지, 1:콜백메시지(읽음확인))
	String subject
	String content
	GmsUser sender
	@BindingFormat('yyyy-MM-dd HH:mm')
	Date reservationTime
	String sendPolicy
	String status
	
	static hasMany = [recipients: GmsMessageRecipient]
	
	Date draftTime
	Date waitTime
	Date sendTime
	Date sentTime
	Date completeTime
	Date terminateTime
	Date cancelTime
	boolean isSent = false
	boolean isRead = false
	Date readTime

	String error

	Date createdTime
	String createdBy
	Date modifiedTime
	String modifiedBy
	
	static constraints = {
		ownType blank: false, maxSize: 1
		msgType blank: false, maxSize: 1
		subject blank: false, maxSize: 255
		content blank: false, maxSize: 2000
		sendPolicy nullable: true, maxSize: 10
		status nullable: true, maxSize: 10
		recipients minSize: 1, maxSize: 1000
		draftTime nullable: true
		waitTime nullable: true
		sendTime nullable: true
		sentTime nullable: true
		readTime nullable: true
		reservationTime nullable: true
		completeTime nullable: true
		terminateTime nullable: true
		cancelTime nullable: true
		error nullable: true, maxSize: 255
		createdTime nullable: true
		createdBy nullable: true
		modifiedTime nullable: true
		modifiedBy nullable: true
	}
	
	def beforeInsert() {
		createdTime = new Date()
		modifiedTime = new Date()
		status = 'DRAFT'
		draftTime = new Date()
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
		if (isDirty('status')) {
			switch(status){
				case 'DRAFT': draftTime = new Date(); break
				case 'WAITING': waitTime = new Date(); break
				case 'SENDING': sendTime = new Date(); break
				case 'SENT': sentTime = new Date(); isSent = true; break
				case 'COMPLETED': completeTime = new Date(); break
				case 'TERMINATED': terminateTime = new Date(); break
				case 'CANCELED': cancelTime = new Date(); break
			}
		}
		if (isDirty('isRead') && isRead) {
			readTime = new Date()
		}
	 }
}
