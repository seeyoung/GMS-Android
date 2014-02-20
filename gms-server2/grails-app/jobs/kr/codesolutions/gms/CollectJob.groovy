package kr.codesolutions.gms

import kr.codesolutions.gms.constants.InstanceLock

import org.quartz.JobExecutionContext

class CollectJob {
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

		log.info "<START> Collect message job (${d})"
		if(gmsInstanceLockService.lock(InstanceLock.COLLECT, instanceId)){
			try{
				gmsMessageService.collect(instanceId, channelRange, queueSize)
			}catch(Exception ex){
				log.error ex.message
			}
			gmsInstanceLockService.unlock(InstanceLock.COLLECT, instanceId)
		}
		log.info "<END> Collect message job (${d})"
	}
}
