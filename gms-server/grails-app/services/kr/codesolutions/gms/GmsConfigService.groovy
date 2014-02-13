package kr.codesolutions.gms

import grails.transaction.Transactional

@Transactional
class GmsConfigService {
	def grailsApplication
	def sessionFactory
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
	def gmsMessageService
	def gmsMassMessageService
	
	def cleanUpGorm() {
		def session = sessionFactory.currentSession
		session.flush()
		session.clear()
		propertyInstanceMap.get().clear()
	}

	def initData(){
		
		if(GmsConstantSendType.findByName('GCM') == null){
			for(sendType in ['GCM':'GCM', 'SMS':'SMS']){
				new GmsConstantSendType(name:sendType.key, description:sendType.value).save()
			}
		}
		if(GmsConstantSendPolicy.findByName('GCM') == null){
			for(sendPolicy in ['GCM':'GCM', 'SMS':'SMS', 'COMPLEX':'COMPLEX', 'ADVENCED':'ADVENCED']){
				new GmsConstantSendPolicy(name:sendPolicy.key, description:sendPolicy.value).save()
			}
		}
		if(GmsConstantMessageStatus.findByName('DRAFT') == null){
			for(messageStatus in ['DRAFT':'작성중','WAITING':'발송대기','RESERVED':'예약발송','SENDING':'발송중','SENT':'발송완료','COMPLETED':'완료','TERMINATED':'종결','CANCELED':'취소']){
				new GmsConstantMessageStatus(name:messageStatus.key, description:messageStatus.value).save()
			}
		}

		if(GmsUser.findByUserId('gmsmaster') == null){
			def a = new GmsUser(
					userId: 'admin',
					name: 'Admin',
					phoneNumber: '-',
					registrationId: '-',
					isSendable: false,
					enabled: true,
					createdBy: 'Admin',
					modifiedBy: 'Admin'
					)
			a.save()
					
			def m = new GmsUser(
					userId: 'gmsmaster',
					name: '발송관리자',
					phoneNumber: '-',
					registrationId: '-',
					isSendable: true,
					enabled: true,
					createdBy: 'Admin',
					modifiedBy: 'Admin'
					)
			m.save()

			def u = new GmsUser(
					userId: 'max',
					name: '류영은',
					phoneNumber: '01037445845',
					registrationId: 'APA91bGGBz_Gs66M55APc6Oty3FOUT9uywIjEAcRTYHuiBcVaQLnBDeeexwbyHAtrkfEi0SLAeS4hynG8CjbCr7ZR7PXMYShZMjpBpQfKwuc0JeCCmiVZjUDlU9P6aHy_G5mlbWFiBWj1zd8AIV2yfPDUlwFE0CSSg',
					enabled: true,
					createdBy: 'Admin',
					modifiedBy: 'Admin'
					)
			u.save()

			def u2 = new GmsUser(
					userId: 'dong',
					name: '류동소',
					phoneNumber: '01023435845',
					registrationId: '-',
					enabled: true,
					createdBy: 'Admin',
					modifiedBy: 'Admin'
					)
			u2.save()

			new GmsUserGroup(
					name: '서울지역',
					owner: m,
					members: [u, u2]).save()
		}
	
//		100.times { id ->
//			new GmsMassMessageRequest(trSenddate: new Date(), 
//						recipientId: 'dong', trMsg: 'Test').save()
//			new GmsMassMessageRequest(trSenddate: new Date(), 
//						recipientId: 'max', trMsg: 'Test2').save()
//			new GmsMassMessageRequest(trSenddate: new Date(), 
//						recipientId: 'max3', trMsg: 'Test3').save()
//		}
		new GmsMassMessageRequest(trSenddate: new Date(), 
			recipientId: 'max', trMsgtype: '1', trMsg: 'Test for callback').save()

		cleanUpGorm()
	}
	
	def startUpJob(){
		
		// GMS 서버 Instance 번호(1이상 순차부여)
		def instance = Eval.me(grailsApplication.config.gms.instance)

		// 메시지 발송 작업 스케쥴러 시작
		def sendAutoStart = Eval.me(grailsApplication.config.gms.send.autoStart)
		if(sendAutoStart){
			def sendIntervalSeconds = Eval.me(grailsApplication.config.gms.send.intervalSeconds)
			def sendRetryPendingSeconds = Eval.me(grailsApplication.config.gms.send.retryPendingSeconds)
			SendMessageJob.schedule(sendIntervalSeconds*1000, -1, [instance: instance, retryPendingSeconds: sendRetryPendingSeconds])
			log.info "<STARTED> Send Message job"
		}
		
		// 메시지 완료처리 작업 스케쥴러 시작
		def terminateAutoStart = Eval.me(grailsApplication.config.gms.terminate.autoStart)
		if(terminateAutoStart){
			def terminateIntervalSeconds = Eval.me(grailsApplication.config.gms.terminate.intervalSeconds)
			def terminatePreserveDays = Eval.me(grailsApplication.config.gms.terminate.preserveDays)
			TerminateMessageJob.schedule(terminateIntervalSeconds*1000, -1, [instance: instance, terminatePreserveDays: terminatePreserveDays])
			log.info "<STARTED> Terminate Message job"
		}
		
		// 메시지 대량발송 작업 스케쥴러 시작
		def autoStart =  Eval.me(grailsApplication.config.gms.mass.autoStart)
		if(autoStart){
			def channelRange = Eval.me(grailsApplication.config.gms.mass.channelRange)
			def intervalSeconds = Eval.me(grailsApplication.config.gms.mass.intervalSeconds)
			def queueSize = Eval.me(grailsApplication.config.gms.mass.queueSize)
			def collectIntervalSeconds = Eval.me(grailsApplication.config.gms.mass.collectIntervalSeconds)
			def dispatchIntervalSeconds = Eval.me(grailsApplication.config.gms.mass.dispatchIntervalSeconds)
			
			CollectMessageJob.schedule(collectIntervalSeconds*1000, -1, [instance: instance, channelRange: channelRange, queueSize: queueSize])
			log.info "<STARTED> Channel dispatch job"
			Thread.sleep(3000)
			
			ChannelDispatchJob.schedule(dispatchIntervalSeconds*1000, -1, [instance: instance, channelRange: channelRange, queueSize: queueSize])
			log.info "<STARTED> Channel dispatch job"
			Thread.sleep(3000)
			
			channelRange.each{ channel ->
				try{
					def job = Class.forName("kr.codesolutions.gms.channel.Channel${channel}Job")
					job.schedule(intervalSeconds*1000, -1, [instance: instance, channel: channel, queueSize: queueSize])
					log.info "<STARTED> #${channel} Channel job "
				}catch(Exception ex){
					log.error ex.message
				}
			}
			
		}

	}
}
