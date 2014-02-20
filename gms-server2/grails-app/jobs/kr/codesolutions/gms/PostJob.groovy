package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class PostJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def gmsInstanceLockService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date().format('yyyyMMddHHmmssSSS')
		def instanceId = context.mergedJobDataMap.get('instance')
		def channelRange = context.mergedJobDataMap.get('channelRange')
		def queueSize = context.mergedJobDataMap.get('queueSize')
		
		log.info "<START> Post message to channel job (${d})"
		try{
			gmsMessageService.post(instanceId, channelRange, queueSize)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> Post message to channel job (${d})"
	}
}
