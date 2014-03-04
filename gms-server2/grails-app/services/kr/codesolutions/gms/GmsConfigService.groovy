package kr.codesolutions.gms

import javassist.util.Trigger;
import grails.transaction.Transactional
import kr.codesolutions.gms.constants.InstanceLock

import org.hibernate.Session
import org.quartz.Scheduler
import org.quartz.impl.matchers.GroupMatcher

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
		// GMS 서버 Instance 번호
		def instance = Eval.me(grailsApplication.config.gms.instance)
		def gmsInstance = GmsInstance.get(instance)
		if(gmsInstance == null){
			def i = new GmsInstance()
			i.id = instance
			i.host = grailsApplication.config.gms.host
			i.port = Eval.me(grailsApplication.config.gms.port)
			i.save(flush:true)
		}
		
		if(GmsInstanceLock.count() == 0){
			InstanceLock.values().each { lock ->
				def l = new GmsInstanceLock()
				l.id = lock
				l.save(flush:true)
			}
		}
			
		if(GmsUser.findByUserId('gmsmaster') == null){
			def m = new GmsUser(
					userId: 'gmsmaster',
					name: '발송관리자',
					phoneNumber: '-',
					registrationId: '-',
					email: 'gmssendmaster@gmail.com',
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
						
		}

	}
	
	def testData(){
		10013.times{
			new GmsUser(
				userId: "ddong${it}",
				name: "개똥${it}호",
				phoneNumber: "tel${it}",
				registrationId: "reg${it}",
				email: "youngeun_ryu${it}@naver.com",
				enabled: true
			).save()
			if(it % 100 == 0) cleanUpGorm()
		}
	}
	
}
