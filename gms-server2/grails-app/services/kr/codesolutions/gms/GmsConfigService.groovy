package kr.codesolutions.gms

import grails.transaction.Transactional
import kr.codesolutions.gms.constants.InstanceLock

import org.hibernate.Session

@Transactional
class GmsConfigService {
	def grailsApplication
	def sessionFactory
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
	def gmsMessageService
	
	def cleanUpGorm() {
		Session session = sessionFactory.currentSession
		session.flush()
		session.clear()
		propertyInstanceMap.get().clear()
	}

	def initData(){
		// GMS 서버 Instance 번호(1이상 순차부여)
		def instance = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instance)
		if(gmsInstance == null){
			def i = new GmsInstance()
			i.id = instance
			i.save(flush:true)
	
			InstanceLock.values().each { lock ->
				def l = new GmsInstanceLock()
				l.id = lock
				l.save(flush:true)
			}
			
			def m = new GmsUser(
					userId: 'gmsmaster',
					name: '발송관리자',
					phoneNumber: '-',
					registrationId: '-',
					email: 'gmsmaster@gmail.com',
					isSendable: true,
					enabled: true
					)
			m.save()

			def u = new GmsUser(
					userId: 'max',
					name: '류영은',
					phoneNumber: '01037445845',
					registrationId: 'APA91bGGBz_Gs66M55APc6Oty3FOUT9uywIjEAcRTYHuiBcVaQLnBDeeexwbyHAtrkfEi0SLAeS4hynG8CjbCr7ZR7PXMYShZMjpBpQfKwuc0JeCCmiVZjUDlU9P6aHy_G5mlbWFiBWj1zd8AIV2yfPDUlwFE0CSSg',
					email: 'youngeun.ryu@gmail.com',
					enabled: true
					)
			u.save()

			def u2 = new GmsUser(
					userId: 'dong',
					name: '류동소',
					phoneNumber: '01028295845',
					registrationId: '-',
					email: 'youngeun_ryu@naver.com',
					enabled: true
					)
			u2.save()
			
			new GmsUserGroup(
					name: '모든 사용자',
					owner: m).save()
						
//			10013.times{
//				new GmsUser(
//					userId: "ddong${it}",
//					name: "개똥${it}호",
//					phoneNumber: "tel${it}",
//					registrationId: "reg${it}",
//					email: "youngeun_ryu${it}@naver.com",
//					enabled: true
//				).save()
//				if(it % 100 == 0) cleanUpGorm()
//			}

			startUpJob()
		}
	
//		100.times { id ->
//			new GmsMassMessageRequest(trSenddate: new Date(), 
//						recipientId: 'dong', trMsg: 'Test').save()
//			new GmsMassMessageRequest(trSenddate: new Date(), 
//						recipientId: 'max', trMsg: 'Test2').save()
//			new GmsMassMessageRequest(trSenddate: new Date(), 
//						recipientId: 'max3', trMsg: 'Test3').save()
//		}
		
		def m = new GmsMessage(reservationTime: new Date(),
			subject: '${name}님 Hello!', 
			content: '${name}님의 아이디는 ${userId}입니다.',
			recipientUserId: 'dong'
			//recipientFilter: "user_id LIKE 'ddong%'"
			).save()
		gmsMessageService.submit(m)
		
	}
	
	def startUpJob(){
		// GMS 서버 Instance 번호(1이상 순차부여)
		def instance = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instance)
		
		if(gmsInstance.autoStart){
			// 메시지 Instance분배 작업
			if(gmsInstance.distributeIntervalSeconds > 0){
				DistributeJob.schedule(gmsInstance.distributeIntervalSeconds*1000, -1, [instance: instance, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Distribute Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 발행 작업
			if(gmsInstance.publishIntervalSeconds > 0){
				PublishJob.schedule(gmsInstance.publishIntervalSeconds*1000, -1, [instance: instance, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Publish Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 수집 작업
			if(gmsInstance.collectIntervalSeconds > 0){
				CollectJob.schedule(gmsInstance.collectIntervalSeconds*1000, -1, [instance: instance, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Collect Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 채널분배 작업
			if(gmsInstance.postIntervalSeconds > 0){
				PostJob.schedule(gmsInstance.postIntervalSeconds*1000, -1, [instance: instance, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Post Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 발송 작업 시작
			if(gmsInstance.sendIntervalSeconds > 0){
				gmsInstance.channelRange.each{ channel ->
					try{
						def job = Class.forName("kr.codesolutions.gms.channel.Channel${channel}Job")
						job.schedule(gmsInstance.sendIntervalSeconds*1000, -1, [instance: instance, channel: channel, queueSize: gmsInstance.queueSize])
						log.info "<STARTED> #${channel} Channel job "
					}catch(Exception ex){
						log.error ex.message
					}
				}
			}
			
			// 메시지 완료처리 작업
			if(gmsInstance.completeIntervalSeconds > 0){
				CompleteJob.schedule(gmsInstance.completeIntervalSeconds*1000, -1, [instance: instance, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize])
				log.info "<STARTED> Terminate Message job"
				Thread.sleep(3000)
			}
			
			// 메시지 폐기처리 작업
			if(gmsInstance.terminateIntervalSeconds > 0){
				TerminateJob.schedule(gmsInstance.terminateIntervalSeconds*1000, -1, [instance: instance, channelRange: gmsInstance.channelRange, queueSize: gmsInstance.queueSize, preserveDays: gmsInstance.preserveDays])
				log.info "<STARTED> Terminate Message job"
				Thread.sleep(3000)
			}
			
		}

	}
}
