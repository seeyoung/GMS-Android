package kr.codesolutions.gms.channel

import org.quartz.JobExecutionContext

class ChannelJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date()
		def instance = context.mergedJobDataMap.get('instance')
		def channel = context.mergedJobDataMap.get('channel')
		def queueSize = context.mergedJobDataMap.get('queueSize')
		
		log.info "<START> #${channel} Channel message send job (${d})"
		try{
			gmsMessageService.send(instance, channel, queueSize)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> #${channel} Channel message send job (${d})"
	}
}
