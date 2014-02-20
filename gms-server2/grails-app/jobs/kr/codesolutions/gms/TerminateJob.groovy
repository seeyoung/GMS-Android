package kr.codesolutions.gms

import kr.codesolutions.gms.constants.InstanceLock

import org.quartz.JobExecutionContext

class TerminateJob {
	static triggers = {}
	def concurrent = false
	def gmsMessageService
	def gmsInstanceLockService
	def group = 'GMS_MESSAGE'

	def execute(JobExecutionContext context) {
		def d = new Date().format('yyyyMMddHHmmssSSS')
		def instanceId = context.mergedJobDataMap.get('instance')
		def preserveDays = context.mergedJobDataMap.get('preserveDays')
		
		log.info "<START> Terminate message job(${d})"
		if(gmsInstanceLockService.lock(InstanceLock.TERMINATE, instanceId)){
			try{
				gmsMessageService.terminate(instanceId, preserveDays)
			}catch(Exception ex){
				log.error ex
			}
			gmsInstanceLockService.unlock(InstanceLock.TERMINATE, instanceId)
		}
		log.info "<END> Terminate message job(${d})"
	}
}
