package kr.codesolutions.gms.rest

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import kr.codesolutions.gms.GmsMessage
import kr.codesolutions.gms.GmsMessageRecipient
import kr.codesolutions.gms.GmsUser

@Transactional(readOnly = true)
class MessageController {

	def gmsMessageService
	
	def beforeInterceptor = {
		log.info params
	}
	
	@Transactional
	def read() {
		def gmsMessageInstance = GmsMessage.get(params.id) 
		if (gmsMessageInstance == null) {
			notFound()
			return
		}
		def gmsMessageRecipientInstance = GmsMessageRecipient.findByMessageAndRegistrationId(gmsMessageInstance, params.registrationId)
		if(gmsMessageRecipientInstance == null) {
			notFound()
			return
		}
		gmsMessageService.read(gmsMessageInstance, gmsMessageRecipientInstance)
		
        request.withFormat {
            '*'{ render status: OK }
        }
	}
	
	@Transactional
	def delete(GmsMessage gmsMessageInstance) {
		if (gmsMessageInstance == null) {
			notFound()
			return
		}
		def gmsUserInstance = GmsUser.findByUserId(params.userId)
		if(gmsUserInstance == null){
			notFound()
			return
		}
		gmsMessageService.deleteInBox(gmsUserInstance, gmsMessageInstance)
		
        request.withFormat {
            '*'{ render status: OK }
        }
	}

    def send(GmsMessage gmsMessageInstance) {
		def sentCount = 0;
		def message = new Message()
        if (gmsMessageInstance == null) {
			respond message
            return
        }

		try{		
			gmsMessageInstance = gmsMessageService.createAndSend(gmsMessageInstance, params)
			
			message.messageId = gmsMessageInstance.id
			if(gmsMessageInstance.error != null) message.error = gmsMessageInstance.error
			gmsMessageInstance.recipients.collect(message.results){gmsMessageRecipientInstance ->
											new Result(userId:gmsMessageRecipientInstance.userId, isSent:gmsMessageRecipientInstance.isSent)
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
