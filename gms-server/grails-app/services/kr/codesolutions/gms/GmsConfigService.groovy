package kr.codesolutions.gms

import grails.transaction.Transactional
import kr.codesolutions.gms.GmsConstantMessageStatus
import kr.codesolutions.gms.GmsConstantSendPolicy
import kr.codesolutions.gms.GmsConstantSendType
import kr.codesolutions.gms.GmsMassMessageRequest;
import kr.codesolutions.gms.GmsUser
import kr.codesolutions.gms.GmsUserGroup

import org.hibernate.StatelessSession
import org.hibernate.Transaction

@Transactional
class GmsConfigService {
	def grailsApplication
	def sessionFactory
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
	
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
					isSendable: true,
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
//			new GmsMassMessageRequest(trSenddate: new Date(), instance: 0, channel: 0,
//						recipientId: 'dong', trMsg: 'Test').save()
//			new GmsMassMessageRequest(trSenddate: new Date(), instance: 0, channel: 0,
//						recipientId: 'max', trMsg: 'Test2').save()
//			new GmsMassMessageRequest(trSenddate: new Date(), instance: 0, channel: 0,
//						recipientId: 'max3', trMsg: 'Test3').save()
//		}
//		new GmsMassMessageReqBox(trSenddate: new Date(), instance: 0, channel: 0,
//			userId: 'max', trMsgtype: '1', trMsg: 'Test for callback').save()

		cleanUpGorm()
	}
}
