package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class CollectJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date()
		def instance = context.mergedJobDataMap.get('instance')
		def channelRange = context.mergedJobDataMap.get('channelRange')
		def queueSize = context.mergedJobDataMap.get('queueSize')
		def transactionSize = context.mergedJobDataMap.get('transactionSize')

		log.info "<START> Collect message job (${d})"
		try{
			gmsMessageService.collect(instance, channelRange, queueSize, transactionSize)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> Collect message job (${d})"
	}
}
