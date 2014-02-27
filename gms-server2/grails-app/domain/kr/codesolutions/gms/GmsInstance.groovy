package kr.codesolutions.gms

import kr.codesolutions.*


class GmsInstance {
	static mapping = {
		version false
		id column: 'instanceId', generator: 'assigned'
	}
	
	byte instanceId = 1 // 서버 Instance 번호
	boolean autoStart = true // 메시지 작업 자동시작
	boolean isRunning = true // Instance가 가동중인지 여부 
	String channels = '1..1' // 전송채널 범위 1..20 범위내 (예:1..5 -> 1,2,3,4,5  5개 채널로 전송작업 동시진행)
	Range getChannelRange(){ Eval.me(channels) }
	int queueSize = 100 // 전송단위(예:100  1개 채널이 1회에 100개씩 전송)
	int distributeIntervalSeconds = 10 // 메시지 Instance분배 주기(초)(예:10 -> 10초)
	int publishIntervalSeconds = 10 // 메시지 발행 주기(초)(예:10 -> 10초)
	int collectIntervalSeconds = 10 // Instance 할당 주기(초)(예:10 -> 10초)
	int postIntervalSeconds = 10 // 메시지 채널할당 주기(초)(예:10 -> 10초)
	int sendIntervalSeconds = 10 // 메시지 전송주기(초)(예:12 -> 12초, 1개 channel이 GCM메시지 100건 발송시 약 12초 소요됨)
	int completeIntervalSeconds = 60 // 메시지 완료처리 주기(초)(예:10 -> 10초)
	int terminateIntervalSeconds = 60 // 메시지 폐기처리 주기(초)(예:60 -> 60초)
	int preserveDays = 7 // 보존기간(일)(예:7 -> 발송후 7일 경과된 메시지는 삭제대상)
	int resendPendingSeconds = 30 // 메시지 재발송 대기시간(초)(예:10 -> 10초)
	
	
	Date createdTime = new Date()
	Date modifiedTime = new Date()

	static transients = ['channelRange']

	static constraints = {
		instanceId range: 1..99
		channels maxSize: 6
		queueSize range: 100..1000
		distributeIntervalSeconds range: 1..120
		publishIntervalSeconds range: 1..120
		collectIntervalSeconds range: 1..120
		postIntervalSeconds range: 1..120
		sendIntervalSeconds range: 1..120
		terminateIntervalSeconds range: 1..120
		preserveDays range: 1..120
		resendPendingSeconds range: 1..120
	}
	
	def beforeInsert() {
	}
	
	def beforeUpdate() {
		modifiedTime = new Date()
	}
}
