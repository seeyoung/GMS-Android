package kr.codesolutions.gms

import java.util.Date;


class GmsMassMessage {
	static mapping = {
		version false
		id column: 'MESSAGE_ID'
		instance index: 'IDX_GMSMASSMESSAGE_1,IDX_GMSMASSMESSAGE_2'
		sentTime index: 'IDX_GMSMASSMESSAGE_1'
		isTerminated index: 'IDX_GMSMASSMESSAGE_2'
	}

	int instance // 서버 Instance 번호
	int channel // 메시지 발송 채널 번호
	
	long eventId // 메시지 발송 요청 번호(messageId의 상위 개념)
	String ownType // 소유구분 (0:개인메시지, 1:공지메시지)
	String msgType // 메시지구분 (0:일반메시지, 1:콜백메시지(읽음확인))
	String content
	String recipientId
	String registrationId
	String senderId
	Date reqSendTime // 발송요청시간, 미래시간이면 예약발송
	String status //발송상태 (0:발송대기, 1:전송완료, 2:결과수신완료, 9:발송실패)
	
	boolean isSent = false
	Date sentTime
	boolean isRead = false
	Date readTime
	boolean isTerminated = false
	
	String error

	Date createdTime
	Date modifiedTime

	static constraints = {
		instance range: 1..99
		channel range: 1..99
		eventId nullable: true
		ownType blank: false, maxSize: 1
		msgType blank: false, maxSize: 1
		content blank: false, maxSize: 2000
		recipientId blank: false, maxSize: 50
		registrationId nullable: true, maxSize: 255
		senderId blank: false, maxSize: 50
		status nullable: true, maxSize: 1
		sentTime nullable: true
		readTime nullable: true
		error nullable: true, maxSize: 255
		createdTime nullable: true
		modifiedTime nullable: true
	}
	
	def beforeInsert() {
		createdTime = new Date()
		modifiedTime = new Date()
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
		if (isDirty('isRead') && isRead) {
			readTime = new Date()
			status = '2'
		}
	 }
}
