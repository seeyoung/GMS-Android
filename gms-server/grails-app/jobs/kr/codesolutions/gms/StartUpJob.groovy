package kr.codesolutions.gms

class StartUpJob {
	static triggers = {
		simple startDelay:60000l, repeatInterval: 90000000l, repeatCount: 0 // execute job one time
	}
	def concurrent = false
	def grailsApplication
	def group = 'GMS_MESSAGE'
	
	def execute() {
		
		// GMS 서버 Instance 번호(1이상 순차부여) 
		def instance = Eval.me(grailsApplication.config.gms.instance)

		// 메시지 발송 작업 스케쥴러 시작
		def sendAutoStart = Eval.me(grailsApplication.config.gms.send.autoStart)
		if(sendAutoStart){
			def sendIntervalSeconds = Eval.me(grailsApplication.config.gms.send.intervalSeconds)
			def sendRetryPendingSeconds = Eval.me(grailsApplication.config.gms.send.retryPendingSeconds)
			SendMessageJob.schedule(sendIntervalSeconds*1000, -1, [instance: instance, retryPendingSeconds: sendRetryPendingSeconds])
			log.info "<STARTED> Send Message job"
		}
		
		// 메시지 완료처리 작업 스케쥴러 시작
		def terminateAutoStart = Eval.me(grailsApplication.config.gms.terminate.autoStart)
		if(terminateAutoStart){
			def terminateIntervalSeconds = Eval.me(grailsApplication.config.gms.terminate.intervalSeconds)
			def terminatePreserveDays = Eval.me(grailsApplication.config.gms.terminate.preserveDays)
			TerminateMessageJob.schedule(terminateIntervalSeconds*1000, -1, [instance: instance, terminatePreserveDays: terminatePreserveDays])
			log.info "<STARTED> Terminate Message job"
		}
		
		// 메시지 대량발송 작업 스케쥴러 시작
		def autoStart =  Eval.me(grailsApplication.config.gms.mass.autoStart)
		if(autoStart){
			def channelRange = Eval.me(grailsApplication.config.gms.mass.channelRange)
			def intervalSeconds = Eval.me(grailsApplication.config.gms.mass.intervalSeconds)
			def queueSize = Eval.me(grailsApplication.config.gms.mass.queueSize)
			def collectIntervalSeconds = Eval.me(grailsApplication.config.gms.mass.collectIntervalSeconds)
			def dispatchIntervalSeconds = Eval.me(grailsApplication.config.gms.mass.dispatchIntervalSeconds)
			
			CollectMessageJob.schedule(collectIntervalSeconds*1000, -1, [instance: instance, channelRange: channelRange, queueSize: queueSize])
			log.info "<STARTED> Channel dispatch job"
			Thread.sleep(3000)
			
			ChannelDispatchJob.schedule(dispatchIntervalSeconds*1000, -1, [instance: instance, channelRange: channelRange, queueSize: queueSize])
			log.info "<STARTED> Channel dispatch job"
			Thread.sleep(3000)
			
			channelRange.each{ channel ->
				try{
					def job = Class.forName("kr.codesolutions.gms.channel.Channel${channel}Job")
					job.schedule(intervalSeconds*1000, -1, [instance: instance, channel: channel, queueSize: queueSize])
					log.info "<STARTED> #${channel} Channel job "
				}catch(Exception ex){
					log.error ex.message
				}
			}
			
		}
	}
}
