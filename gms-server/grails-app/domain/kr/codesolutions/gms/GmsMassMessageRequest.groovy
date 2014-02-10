package kr.codesolutions.gms


class GmsMassMessageRequest {
	static mapping = {
		version false
		id column: 'TR_NUM', generator: 'sequence', params: [sequence: 'SEQ_GMSMESSAGE_ID']
//		senderId column: 'SENDER_ID', defaultValue: 'gmsmaster'
//		trSendstat column: 'TR_SENDSTAT', defaultValue: '0'
//		trRsltstat column: 'TR_RSLTSTAT', defaultValue: '00'
//		trMsgtype column: 'TR_MSGTYPE', defaultValue: '0'
	}

	int instance = 0 // 서버 Instance 번호
	int channel = 0 // 메시지 발송 채널 번호
	String recipientId //(수신자 userId)
	String senderId = 'gmsmaster' //송신자 userId, default:'gmsmaster'
	String registrationId // GCM 등록ID
	
	Date trSenddate //메세지를 전송할 시간, 미래 시간을 넣으면 예약 발송됨
	int  trSerialnum //고객이 발급한 번호로 NULL 값이어도 됨
	String trId //고객이 발급한 SubId로 null값이어도 됨
	String trSendstat = '0' //발송상태 (0:발송대기, 1:전송완료, 2:결과수신완료)
	String trRsltstat = '00'//발송 결과수신 값으로 세부 사항은 결과 코드표 참조 (00:결과수신대기, 06:전송성공)
	String trMsgtype = '0' //문자전송 형태 (0:일반메세지, 1:콜백 URL 메세지)
	String trPhone //수신할 핸드폰 번호
	String trCallback //송신자 전화번호
	Date trRsltdate //이동통신사로부터 결과를 통보받은 시간
	Date trModified //프로그램 내부적으로 사용
	String trMsg //전송할 메세지
	String trNet //전송완료후 최종 이동통신사 정보
	String trEtc1 //기타필드 사용자가 자유롭게 값을 입력하여 사용가능
	String trEtc2
	String trEtc3
	String trEtc4
	String trEtc5
	String trEtc6
	Date trRealsenddate //실제 모듈이 발송(DELIVER)한시간
	String trRouteid //실제 발송한 세션 ID

	static constraints = {
		instance range: 0..99
		channel range: 0..99
		recipientId blank: false, maxSize: 50
		senderId blank: false, maxSize: 50
		registrationId nullable: true, maxSize: 255
		trSerialnum nullable: true
		trId nullable: true, maxSize: 16
		trModified nullable: true
		trSendstat inList: ['0', '1', '2'], maxSize: 1
		trRsltstat nullable: true, maxSize: 2
		trMsgtype inList: ['0', '1'], maxSize: 1
		trPhone nullable: true, maxSize: 20
		trCallback nullable: true, maxSize: 20
		trRsltdate nullable: true
		trModified nullable: true
		trMsg blank: false, maxSize: 160
		trNet nullable: true, inList: ['010', '011', '016', '019'], maxSize: 4
		trEtc1 nullable: true, maxSize: 160
		trEtc2 nullable: true, maxSize: 160
		trEtc3 nullable: true, maxSize: 160
		trEtc4 nullable: true, maxSize: 160
		trEtc5 nullable: true, maxSize: 160
		trEtc6 nullable: true, maxSize: 160
		trRealsenddate nullable: true
		trRouteid nullable: true, maxSize:20
	}
}
