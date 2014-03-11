package kr.codesolutions.gms

import grails.transaction.Transactional
import groovy.sql.Sql

import org.hibernate.SessionFactory


@Transactional
class GmsStatusService {
	
	def statusQueue(){
		GmsQueueSubmit.withSession { session ->
			List<GmsStatusQueue> results = []
			def sql = new Sql(session.connection())
			sql.eachRow("""
					SELECT 'SUBMIT' AS queueName, SUM(recipient_count) AS messageSize, COUNT(1) AS queueSize FROM gms_queue_submit
					UNION ALL
					SELECT 'PUBLISH' AS queueName, SUM(recipient_count) AS messageSize, COUNT(1) AS queueSize FROM gms_queue_publish
					UNION ALL
					SELECT 'WAIT' AS queueName, SUM(recipient_count) AS messageSize, COUNT(1) AS queueSize FROM gms_queue_wait
					UNION ALL
					SELECT 'SEND' AS queueName, COUNT(1) AS messageSize, COUNT(1) AS queueSize FROM gms_queue_send
					"""){ row -> results.add(new GmsStatusQueue(queueName: row.queueName, messageSize: row.messageSize, queueSize: row.queueSize))}
			return results
		}
	}

	 
}
