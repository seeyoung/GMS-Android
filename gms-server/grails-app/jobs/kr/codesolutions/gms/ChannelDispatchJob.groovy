package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class ChannelDispatchJob {
	static triggers = {}
	def concurrent = false
	def gmsMassMessageService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date()
		def instance = context.mergedJobDataMap.get('instance')
		def channelRange = context.mergedJobDataMap.get('channelRange')
		def queueSize = context.mergedJobDataMap.get('queueSize')
		
		log.info "<START> Dispatch message to channel job (${d})"
		try{
			gmsMassMessageService.dispatch(instance, channelRange, queueSize)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> Dispatch message to channel job (${d})"
	}
}
