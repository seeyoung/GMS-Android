package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class TerminateJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date()
		def instance = context.mergedJobDataMap.get('instance')
		def transactionSize = context.mergedJobDataMap.get('transactionSize')
		def preserveDays = context.mergedJobDataMap.get('preserveDays')
		
		log.info "<START> Terminate message job(${d})"

		try{
			gmsMessageService.terminate(instance, transactionSize, preserveDays)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> Terminate message job(${d})"
	}
}
