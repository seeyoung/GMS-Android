package kr.codesolutions.gms.rest

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import kr.codesolutions.gms.GmsMessage
import kr.codesolutions.gms.GmsMessageRecipient
import kr.codesolutions.gms.GmsUser

@Transactional
class MessageController {

	def gmsMessageService
	
	def beforeInterceptor = {
		log.info params
	}
	
	def read(GmsMessageRecipient gmsMessageRecipientInstance) {
		if (gmsMessageRecipientInstance == null) {
			notFound()
			return
		}
		if (gmsMessageRecipientInstance.registrationId != params.registrationId) {
			notFound()
			return
		}
		gmsMessageService.read(gmsMessageRecipientInstance)
		
        request.withFormat {
            '*'{ render status: OK }
        }
	}
	
	def delete(GmsMessageRecipient gmsMessageRecipientInstance) {
		if (gmsMessageRecipientInstance == null) {
			notFound()
			return
		}
		def gmsUserInstance = GmsUser.findByUserId(params.userId)
		if(gmsUserInstance == null){
			notFound()
			return
		}
		gmsMessageService.deleteBoxIn(gmsUserInstance, gmsMessageRecipientInstance)
		
        request.withFormat {
            '*'{ render status: OK }
        }
	}

    def send(GmsMessage gmsMessageInstance) {
		def message = new Message()
        if (gmsMessageInstance == null) {
			respond message
            return
        }

		try{
			if(gmsMessageInstance.senderUserId == null){
				gmsMessageInstance.senderUserId = params.senderId
			}
			if(gmsMessageInstance.recipientUserId != null){
				gmsMessageInstance.recipientFilter = "user_id='${gmsMessageInstance.recipientUserId}'"
			}else if(params.recipientId != null){
				def recipientIds = params.recipientId.replaceAll(";","','")
				gmsMessageInstance.recipientFilter =  "user_id IN ('${recipientIds}')"
			}
			gmsMessageInstance.isCallback = true
			gmsMessageInstance = gmsMessageService.createAndSend(gmsMessageInstance)
			
			message.messageId = gmsMessageInstance.id
			if(gmsMessageInstance.error != null) message.error = gmsMessageInstance.error
			def recipients = GmsMessageRecipient.where{message == gmsMessageInstance}.list()
			recipients.collect(message.results){ recipient ->
				new Result(userId: recipient.userId, isSent: recipient.isSent)
			}
			
		}catch(Exception ex){
			message.error = ex.message
			log.error ex
		}
		respond message, [formats:['json']]
    }
	
    protected void notFound() {
        request.withFormat {
            '*'{ render status: NOT_FOUND }
        }
    }

}

class Message{
	Serializable messageId
	def results = []
	String error = ''
}

class Result{
	String userId
	boolean isSent = false
}
