package kr.codesolutions.gms

import kr.codesolutions.gms.constants.InstanceLock

import org.quartz.JobExecutionContext

class PublishJob {
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

		log.info "<START> Publish message job(${d})"
		try{
			gmsMessageService.publish(instanceId, channelRange, queueSize)
		}catch(Exception ex){
			log.error ex
		}
		gmsInstanceLockService.unlock(InstanceLock.PUBLISH, instanceId)
		log.info "<END> Publish message job(${d})"
			
	}
}
