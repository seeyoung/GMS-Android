package kr.codesolutions.gms


class GmsMessageSms {
	static mapping = {
		table 'SC_TRAN'
		version false
		id column: 'TR_NUM', generator: 'assigned'
		message column: 'TR_SERIALNUM'
		reservationTime column: 'TR_SENDDATE'
		subject column: 'TR_ETC1'
		content column: 'TR_MSG'
		userId column: 'TR_ETC2'
		name column: 'TR_ETC3'
		phoneNumber column: 'TR_PHONE'
		senderPhoneNumber column: 'TR_CALLBACK'
		sentTime column: 'TR_REALSENDDATE'
		modifiedTime column: 'TR_MODIFIED'
	}

	Date reservationTime //trSenddate //메세지를 전송할 시간, 미래 시간을 넣으면 예약 발송됨
	GmsMessage message //  trSerialnum //고객이 발급한 번호로 NULL 값이어도 됨
	String trId //고객이 발급한 SubId로 null값이어도 됨
	String trSendstat = '0' //발송상태 (0:발송대기, 1:전송완료, 2:결과수신완료)
	String trRsltstat = '00'//발송 결과수신 값으로 세부 사항은 결과 코드표 참조 (00:결과수신대기, 06:전송성공)
	String trMsgtype = '0' //문자전송 형태 (0:일반메세지, 1:콜백 URL 메세지)
	String phoneNumber //trPhone //수신할 핸드폰 번호
	String senderPhoneNumber //trCallback //송신자 전화번호
	Date trRsltdate //이동통신사로부터 결과를 통보받은 시간
	Date modifiedTime = new Date() //trModified //프로그램 내부적으로 사용
	String content //trMsg //전송할 메세지
	String trNet //전송완료후 최종 이동통신사 정보
	String subject //trEtc1 //기타필드 사용자가 자유롭게 값을 입력하여 사용가능
	String userId //trEtc2
	String name //trEtc3
	String trEtc4
	String trEtc5
	String trEtc6
	Date sentTime //trRealsenddate //실제 모듈이 발송(DELIVER)한시간
	String trRouteid //실제 발송한 세션 ID

	static constraints = {
		trId nullable: true, maxSize: 16
		trSendstat inList: ['0', '1', '2'], maxSize: 1
		trRsltstat nullable: true, maxSize: 2
		trMsgtype inList: ['0', '1'], maxSize: 1
		phoneNumber blank: false, maxSize: 20
		senderPhoneNumber blank: false, maxSize: 20
		trRsltdate nullable: true
		modifiedTime nullable: true
		content blank: false, maxSize: 160
		trNet nullable: true, inList: ['010', '011', '016', '019'], maxSize: 4
		subject blank: false, maxSize: 160
		userId blank: false, maxSize: 160
		name blank: false, maxSize: 160
		trEtc4 nullable: true, maxSize: 160
		trEtc5 nullable: true, maxSize: 160
		trEtc6 nullable: true, maxSize: 160
		sentTime nullable: true
		trRouteid nullable: true, maxSize:20
	}
}
