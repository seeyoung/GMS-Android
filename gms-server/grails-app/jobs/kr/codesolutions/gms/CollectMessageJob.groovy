package kr.codesolutions.gms

import org.quartz.JobExecutionContext

class CollectMessageJob {
	static triggers = {}
	def concurrent = false
	def gmsMassMessageService
	def group = 'GMS_MASS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date()
		def instance = context.mergedJobDataMap.get('instance')
		def channelRange = context.mergedJobDataMap.get('channelRange')
		def queueSize = context.mergedJobDataMap.get('queueSize')

		log.info "<START> Collect message job (${d})"
		try{
			gmsMassMessageService.collect(instance, channelRange, queueSize)
		}catch(Exception ex){
			log.error ex
		}
		log.info "<END> Collect message job (${d})"
	}
}
