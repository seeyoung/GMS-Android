package kr.codesolutions.gms



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class GmsMessageInBoxController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def beforeInterceptor = {
		log.info params
	}
	
    def index(Integer max, String sort, String order) {
        params.max = Math.min(max ?: 10, 100)
		params.sort = sort?:'id'
		params.order = order?:'desc'

		def gmsUserInstance = GmsUser.findByUserId(params.userId)
		def gmsMessageInstanceList = GmsMessageInBox.findAllByOwner(gmsUserInstance, params)*.message
        respond gmsMessageInstanceList, model:[gmsMessageInstanceCount: GmsMessageInBox.count()]
    }

    def show(GmsMessage gmsMessageInstance) {
        respond gmsMessageInstance
    }

    @Transactional
    def delete(GmsMessageInBox gmsMessageInBoxInstance) {

        if (gmsMessageInBoxInstance == null) {
            notFound()
            return
        }

        gmsMessageInBoxInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gmsMessage.label', default: 'Message'), gmsMessageInBoxInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gmsMessage.label', default: 'Message'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
