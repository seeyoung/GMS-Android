package kr.codesolutions.gms

import kr.codesolutions.gms.constants.InstanceLock

import org.quartz.JobExecutionContext

class DistributeJob {
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
		
		log.info "<START> Distribute message job (${d})"
		if(gmsInstanceLockService.lock(InstanceLock.DISTRIBUTE, instanceId)){
			try{
				gmsMessageService.distribute(instanceId, channelRange, queueSize)
			}catch(Exception ex){
				log.error ex.message
			}
			gmsInstanceLockService.unlock(InstanceLock.DISTRIBUTE, instanceId)
		}
		log.info "<END> Distribute message job (${d})"
	}
}
