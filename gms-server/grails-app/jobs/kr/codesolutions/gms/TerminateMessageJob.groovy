package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class TerminateMessageJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def gmsMassMessageService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date()
		def instance = context.mergedJobDataMap.get('instance')
		def terminatePreserveDays = context.mergedJobDataMap.get('terminatePreserveDays')
		
		log.info "<START> Terminate message job(${d})"

		if(instance == 1){
			try{
				gmsMessageService.terminateMessage(instance, terminatePreserveDays)
			}catch(Exception ex){
				log.error ex
			}
		}

		try{
			gmsMassMessageService.terminateMessage(instance, terminatePreserveDays)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> Terminate message job(${d})"
	}
}
