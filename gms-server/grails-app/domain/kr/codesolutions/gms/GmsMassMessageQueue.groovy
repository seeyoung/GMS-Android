package kr.codesolutions.gms

import java.util.Date;


class GmsMassMessageQueue {
	static mapping = {
		version false
		id column: 'MESSAGE_ID'
		channel index: 'IDX_GMSMASSMESSAGEQUEUE_1'
	}
	
	int instance // 서버 Instance 번호
	int channel // 메시지 발송 채널 번호
	static belongsTo = [message: GmsMassMessage]
	
	Date createdTime
	Date modifiedTime

	static constraints = {
		instance range: 1..99
		channel range: 1..99
		createdTime nullable: true
		modifiedTime nullable: true
	}
	
	def beforeInsert() {
		createdTime = new Date()
		modifiedTime = new Date()
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	 }
}
