package kr.codesolutions.gms



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class GmsMessageOutBoxController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def beforeInterceptor = {
		log.info params
	}
	
    def index(Integer max, String sort, String order) {
        params.max = Math.min(max ?: 10, 100)
		params.sort = sort?:'id'
		params.order = order?:'desc'
		
		def gmsMessageInstanceList = GmsMessageOutBox.list(params)*.message
        respond gmsMessageInstanceList, model:[gmsMessageInstanceCount: GmsMessageOutBox.count()]
    }

    def show(GmsMessage gmsMessageInstance) {
        respond gmsMessageInstance
    }
	
    @Transactional
    def delete(GmsMessageOutBox gmsMessageOutBoxInstance) {

        if (gmsMessageOutBoxInstance == null) {
            notFound()
            return
        }

        gmsMessageOutBoxInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gmsMessage.label', default: 'Message'), gmsMessageOutBoxInstance.id])
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
