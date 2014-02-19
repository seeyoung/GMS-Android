package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class PublishJob {
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

		if(instance == 1){
			log.info "<START> Publish message job(${d})"
			try{
				gmsMessageService.publish(instance, queueSize, transactionSize)
			}catch(Exception ex){
				log.error ex
			}
			log.info "<END> Publish message job(${d})"
		}
			
	}
}
