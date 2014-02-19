package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class DistributeJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date().format('yyyyMMddHHmmssSSS')
		def instance = context.mergedJobDataMap.get('instance')
		def channelRange = context.mergedJobDataMap.get('channelRange')
		def queueSize = context.mergedJobDataMap.get('queueSize')
		def transactionSize = context.mergedJobDataMap.get('transactionSize')
		
		log.info "<START> Distribute message job (${d})"
		try{
			gmsMessageService.distribute(instance, queueSize, transactionSize)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> Distribute message job (${d})"
	}
}
