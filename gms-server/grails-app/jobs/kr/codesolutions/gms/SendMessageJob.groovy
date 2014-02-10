package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class SendMessageJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def group = 'GMS_MESSAGE'
	
	def execute(JobExecutionContext context) {
		def d = new Date()
		def instance = context.mergedJobDataMap.get('instance')
		def retryPendingSeconds = context.mergedJobDataMap.get('retryPendingSeconds')
		
		if(instance == 1){
			log.info "<START> Send message job(${d})"
			try{
				gmsMessageService.sendReservedMessage(instance)
			}catch(Exception ex){
				log.error ex
			}
	
			try{
				gmsMessageService.sendWaitingMessage(instance)
			}catch(Exception ex){
				log.error ex
			}
	
			try{
				gmsMessageService.sendMessage(instance)
			}catch(Exception ex){
				log.error ex
			}
			
	//		try{
	//			gmsMessageService.resendMessage(instance, retryPendingSeconds)
	//		}catch(Exception ex){
	//			log.error ex
	//		}
			
			try{
				gmsMessageService.completeMessage(instance)
			}catch(Exception ex){
				log.error ex
			}
			
			log.info "<END> Send message job(${d})"
		}
			
	}
}
