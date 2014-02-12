package kr.codesolutions.gms

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.sql.Sql
import groovy.time.TimeCategory

import com.google.android.gcm.server.Message
import com.google.android.gcm.server.Result
import com.google.android.gcm.server.Sender

@Transactional
class GmsMassMessageService {
	def grailsApplication
	def sessionFactory
	Sender gcmSender
	
	def GmsMassMessage read(GmsMassMessage gmsMassMessageInstance){
		if(!gmsMassMessageInstance.isRead){
			gmsMassMessageInstance.isRead = true
			gmsMassMessageInstance.status = '2'
			gmsMassMessageInstance.save flush:true
		}
		return gmsMassMessageInstance
	}
	

	// GCM메시지를 수신자 1명에게 전송
	def sendGCM(GmsMassMessage gmsMassMessageInstance) {
		if(gcmSender == null){
			gcmSender = new Sender(grailsApplication.config.gms.gcmApiKey)
		}
		// registrationId가 null이면 사용자정보에서 userId를 찾지 못한 경우임
		if(gmsMassMessageInstance.registrationId == null){
			gmsMassMessageInstance.error = 'Invalid userId'
			gmsMassMessageInstance.status = '9'
		}else{
			Message gcmMessage = new Message.Builder()
					.collapseKey('GMS')
					.addData('GMS.id', gmsMassMessageInstance.id.toString())
					.addData('GMS.ownType', gmsMassMessageInstance.ownType)
					.addData('GMS.msgType', gmsMassMessageInstance.msgType)
					.addData('GMS.content', URLEncoder.encode(gmsMassMessageInstance.content, 'UTF-8') )
					.addData('GMS.senderId', gmsMassMessageInstance.senderId)
					.build()
			//log.debug 'GCM message: ' + gcmMessage.toString()
	
			Result result = gcmSender.send(gcmMessage, gmsMassMessageInstance.registrationId, 3)
			//log.debug 'Result :' + gmsMassMessageInstance.userId + ',' + result.messageId
			if(result.messageId){
				gmsMassMessageInstance.isSent = true
				gmsMassMessageInstance.status = '1'
			}else{
				gmsMassMessageInstance.error = result.errorCode
				gmsMassMessageInstance.status = '9'
			}
		}
		gmsMassMessageInstance.sentTime = new Date()
		gmsMassMessageInstance.save()
	}


	/**
	 * queue에 저장된 메시지를 instance, channel로 queueSize개 검색하여 발송한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @param channel 메시지 발송 채널
	 * @param queueSize 채널의 QUEUE크기
	 * @return
	 */
	def transfer(int instance, int channel, int queueSize){
		def criteria = new DetachedCriteria(GmsMassMessageQueue).build {
							and{
								eq('instance', instance)
								eq('channel', channel)
							}
						}
		def results = criteria.list(max:queueSize, sort:'id', order:'asc')
		results.each { gmsTranQueueInstance -> 
			sendGCM(gmsTranQueueInstance.message)
			gmsTranQueueInstance.delete()
		}
		log.info("Transfered((Intance: #${instance}, Channel: #${channel}, queueSize: ${queueSize}) : ${results.size()} messages transfered to GCM")
	}
	
	/**
	 * 현재 가동중인 GMS서버 Instance에 메시지를 할당한다.
	 * 메시지 발송요청 저장소(GmsMassMessageReuest)에 instance번호와 channel=0을 할당한다.
	 * 처리단위는 channel 수 * queueSize
	 * 
	 * @param instance 서버 Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 채널의 QUEUE크기로 할당할 메시지 갯수
	 */
	
	def collect(int instance, Range channels, int queueSize){
		def sql = new Sql(sessionFactory.currentSession.connection())
		
		def (collectSize, collected) = [channels.size()*queueSize, 0]
		int count = sql.firstRow("""
								SELECT COUNT(1) as count 
								  FROM gms_mass_message_request 
								 WHERE tr_senddate <= CURRENT_TIMESTAMP 
								   AND instance = 0
								   AND ROWNUM = 1
								""").count
		if(count > 0){
			// instance가 할당되면 메시지 발송 대상(발송요청시간 경과 메시지중 instance, channel이 null인 것)
			collected = sql.executeUpdate("""
									UPDATE gms_mass_message_request t 
									   SET instance = ${instance}
									 WHERE tr_senddate <= CURRENT_TIMESTAMP 
									   AND instance = 0
									   AND (ROWNUM >= ${collectSize*(instance-1)+1} AND ROWNUM <= ${collectSize*instance})
									""")
		}
		log.info "Collected (Instance: #${instance}, Range: ${collectSize*(instance-1)+1}..${collectSize*instance}): ${collected} requests"
	}
	
	/**
	 * 1. Request의 recipientId로 (GmsUser)에서 registrationId를 찾아 저장한다.
	 * 2. chnnel=0으로 할당된 메시지중 (GmsUser)에 recipientId가 없으면 status='9' 발송실패로 처리하여 (GmsMassMessage)로 이동시키고 Request는 삭제한다.
	 * 3. 메시지 발송요청 저장소(GmsMassMessageRequest)에서 queueSize의 메시지를 읽어와 채널별 QUEUE에 할당한다.
	 * 
	 * @param instance 서버 Instance번호
	 * @param channels 할당할 채널 Range
	 * @param queueSize 채널의 QUEUE크기로 할당할 메시지 갯수
	 * @return
	 */
	def dispatch(int instance, Range channels, int queueSize){
		def sql = new Sql(sessionFactory.currentSession.connection())
		
		int count = sql.firstRow("""
								SELECT COUNT(1) as count 
								  FROM gms_mass_message_request 
								 WHERE instance = ${instance}
								   AND channel = 0
								   AND ROWNUM = 1
								""").count
		if(count > 0){
			channels.each { channel ->
				def (allocated, validated, created, queued, deleted) = [0, 0, 0, 0]
				// registrationId가 있으면 queueSize만큼씩 각 channel에 할당하여 queue로 이동한다.
				allocated = sql.executeUpdate("""
										UPDATE gms_mass_message_request 
										   SET channel = ${channel} 
										 WHERE instance = ${instance}
										   AND channel = 0
										   AND ROWNUM <= ${queueSize}
										""")
				if(allocated > 0){
					validated = sql.executeUpdate("""
									UPDATE gms_mass_message_request t 
									   SET registration_id = (SELECT u.registration_id FROM gms_user u WHERE u.user_id=t.recipient_id) 
									 WHERE instance = ${instance}
									   AND channel = ${channel} 
									""")

					created = sql.executeUpdate("""
										INSERT INTO gms_mass_message(message_id, instance, channel, event_id, own_type, msg_type, content, recipient_id, registration_id, sender_id, req_send_time, is_sent, is_read, is_terminated, status, created_time, modified_time)
											SELECT tr_num, instance, channel, tr_serialnum, '1', tr_msgtype, tr_msg, recipient_id, registration_id, sender_id, tr_senddate, false, false, false, '0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
											  FROM gms_mass_message_request
											 WHERE instance = ${instance}
											   AND channel = ${channel}
										""")
					queued = sql.executeUpdate("""
										INSERT INTO gms_mass_message_queue(instance, channel, message_id, created_time, modified_time)
												SELECT instance, channel, tr_num, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
												  FROM gms_mass_message_request
												 WHERE instance = ${instance}
												   AND channel = ${channel}
										""")
					deleted = sql.executeUpdate("""
										DELETE gms_mass_message_request 
										 WHERE instance = ${instance} 
										   AND channel = ${channel}
										""")
				}
				
				log.info "Dispatched (Intance: #${instance}, Channel: #${channel}, queueSize: ${queueSize}): ${allocated} channel allocated, ${validated} requests validated, ${created} messages created, ${queued} messages queued, ${deleted} requests deleted"
				if(allocated < queueSize){
					return
				}
			}
		}
	}
	
	/**
	 * 보존기간이 종료된 메시지를 월별 Log 테이블로 이동한다.
	 * @param instance 서버 Instance번호
	 * @param terminatePreserveDays 보존기간
	 * @return
	 */
	def terminateMessage(int instance, int terminatePreserveDays) {
		def queueSize = 10000
		def terminateDate = use(TimeCategory) { new Date() - 30.seconds}// terminatePreserveDays.days}
		def tableName = "GMS_MASS_MESSAGE_LOG_${terminateDate.format('yyyyMM')}"
		def sql = new Sql(sessionFactory.currentSession.connection())
		
		createLogTableIfNotExist(sql, tableName)
		def (terminated, moved, deleted) = [0, 0, 0]
		int count = sql.firstRow("""
								SELECT COUNT(1) as count 
								  FROM gms_mass_message 
								 WHERE instance = ${instance} 
								   AND sent_time <= ${terminateDate} 
								   AND sent_time IS NOT NULL
								   AND is_terminated = false
								   AND ROWNUM = 1
								""").count
		if(count > 0){
			terminated = sql.executeUpdate("""
											UPDATE gms_mass_message 
											   SET is_terminated = true
											 WHERE instance = ${instance} 
											   AND sent_time <= ${terminateDate}
											   AND sent_time IS NOT NULL
											   AND is_terminated = false
											   AND ROWNUM <= ${queueSize}
											""")
			moved = sql.executeUpdate("""
									INSERT INTO ${Sql.expand(tableName)}(message_id, instance, channel, event_id, own_type, msg_type, content, recipient_id, registration_id, sender_id, req_send_time, is_sent, sent_time, is_read, read_time, status, error, is_terminated, created_time, modified_time)
										SELECT message_id, instance, channel, event_id, own_type, msg_type, content, recipient_id, registration_id, sender_id, req_send_time, is_sent, sent_time, is_read, read_time, status, error, is_terminated, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP 
										  FROM gms_mass_message
										 WHERE instance = ${instance}
										   AND is_terminated = true
									""")
			deleted = sql.executeUpdate("""
									DELETE gms_mass_message 
									 WHERE instance = ${instance}
									   AND is_terminated = true
									""")
		}
		log.info "Terminated (Intance: #${instance}, terminatePreserveDays: ${terminatePreserveDays}, queueSize: ${queueSize}): ${terminated} terminated, ${moved} moved to Log, ${deleted} deleted from message"
	}
	
	def createLogTableIfNotExist(Sql sql, String tableName){
		try{		
			int count = sql.firstRow("SELECT COUNT(1) as count FROM ${Sql.expand(tableName)} WHERE ROWNUM = 1").count
		}catch(Exception ex){
			sql.execute("""
				CREATE TABLE ${Sql.expand(tableName)}(
					message_id 		BIGINT NOT NULL PRIMARY KEY,
					event_id 		BIGINT,
					instance 		INT,
					channel 		INT,
					own_type 		VARCHAR(1),
					msg_type 		VARCHAR(1),
					content 		VARCHAR(2000),
					recipient_id	VARCHAR(50),
					registration_id VARCHAR(255),
					sender_id 		VARCHAR(50),
					req_send_time	DATE,
					status 			VARCHAR(1),
					is_sent 		BOOLEAN,
					sent_time 		DATE,
					is_read 		BOOLEAN,
					read_time 		DATE,
					error 			VARCHAR(255),
					is_terminated	BOOLEAN,
					created_time 	DATE,
					modified_time 	DATE
				)
			""")
			log.info "Log table ${tableName} created."
		}
	}

}
