package kr.codesolutions.gms

import grails.transaction.Transactional

import org.quartz.Scheduler
import org.quartz.impl.matchers.GroupMatcher

@Transactional
class GmsInstanceService{
	def grailsApplication
	Scheduler quartzScheduler
	
	def autostart(){
		def instanceId = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instanceId)
		if(gmsInstance != null && gmsInstance.autoStart){
			start(gmsInstance)
		}
	}
	
	def shutdown(){
		def instanceId = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instanceId)
		if(gmsInstance != null && gmsInstance.autoStart){
			stop(gmsInstance)
		}
		quartzScheduler.shutdown()
	}

	def boolean start(GmsInstance gmsInstance){
		if(gmsInstance == null) return false
		// 자신의 Instance와 일치하는 경우만 Start처리
		def instance = Eval.me(grailsApplication.config.gms.instance)
		if(gmsInstance.id != instance){
			def started = false
			def uri = "http://${gmsInstance.host}:${gmsInstance.port}/gms/gmsInstance/start/${gmsInstance.id}"
			log.info "Start Instance Uri = ${uri}"
			withHttp(uri: uri) {
				try{
					def result = get(query : [requestInstance:"${instance}"])
					started = Eval.me(result[0].text())
					gmsInstance.refresh()
				}catch(Exception ex){
					log.error ex.message
				}
			}
			return started
		}
		if(!gmsInstance.isRunning){
			quartzScheduler.start()
			// 메시지 Instance분배 작업
			if(gmsInstance.distributeIntervalSeconds > 0){
				DistributeJob.schedule(gmsInstance.distributeIntervalSeconds*1000, -1, [instance: gmsInstance.id, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Distribute Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 발행 작업
			if(gmsInstance.publishIntervalSeconds > 0){
				PublishJob.schedule(gmsInstance.publishIntervalSeconds*1000, -1, [instance: gmsInstance.id, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Publish Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 수집 작업
			if(gmsInstance.collectIntervalSeconds > 0){
				CollectJob.schedule(gmsInstance.collectIntervalSeconds*1000, -1, [instance: gmsInstance.id, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Collect Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 채널분배 작업
			if(gmsInstance.postIntervalSeconds > 0){
				PostJob.schedule(gmsInstance.postIntervalSeconds*1000, -1, [instance: gmsInstance.id, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Post Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 발송 작업 시작
			if(gmsInstance.sendIntervalSeconds > 0){
				gmsInstance.channelRange.each{ channel ->
					try{
						def job = Class.forName("kr.codesolutions.gms.channel.Channel${channel}Job")
						job.schedule(gmsInstance.sendIntervalSeconds*1000, -1, [instance: gmsInstance.id, channel: channel, queueSize: gmsInstance.queueSize])
						log.info "<STARTED> #${channel} Channel job "
					}catch(Exception ex){
						log.error ex.message
					}
				}
			}
			
			// 메시지 완료처리 작업
			if(gmsInstance.completeIntervalSeconds > 0){
				CompleteJob.schedule(gmsInstance.completeIntervalSeconds*1000, -1, [instance: gmsInstance.id, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Terminate Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 폐기처리 작업
			if(gmsInstance.terminateIntervalSeconds > 0){
				TerminateJob.schedule(gmsInstance.terminateIntervalSeconds*1000, -1, [instance: gmsInstance.id, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize, preserveDays: gmsInstance.preserveDays])
				log.info "<STARTED> Terminate Message job"
				Thread.sleep(3000)
			}
			
			gmsInstance.isRunning = true
			gmsInstance.save(flush: true)
			
			log.info "GMS Instance #${gmsInstance.id} started..."
			return true
		}
		return false
	}
	
	def boolean stop(GmsInstance gmsInstance){
		if(gmsInstance == null) return false
		// 자신의 Instance와 일치하는 경우만 Stop처리
		def instance = Eval.me(grailsApplication.config.gms.instance)
		if(gmsInstance.id != instance){
			def stopped = false
			def uri = "http://${gmsInstance.host}:${gmsInstance.port}/gms/gmsInstance/stop/${gmsInstance.id}"
			log.info "Stop Instance Uri = ${uri}"
			withHttp(uri: uri) {
				try{
					def result = get(query : [requestInstance:"${instance}"])
					stopped = Eval.me(result[0].text())
					gmsInstance.refresh()
				}catch(Exception ex){
					log.error ex.message
				}
			}
			return stopped
		}
		if(gmsInstance.isRunning){
			quartzScheduler.getTriggerGroupNames().each { triggerGroupName ->
				def triggerKeys = quartzScheduler.getTriggerKeys(GroupMatcher.jobGroupEquals(triggerGroupName))
				triggerKeys.each{ key -> 
					def trigger = quartzScheduler.getTrigger(key)
					if (trigger) {
						quartzScheduler.unscheduleJob(key)
					}
					
				}
			}
			quartzScheduler.standby()
			gmsInstance.isRunning = false
			gmsInstance.save(flush: true)
			
			log.info "GMS Instance #${gmsInstance.id} stopped..."
			return true
		}
		return false
	}
}
