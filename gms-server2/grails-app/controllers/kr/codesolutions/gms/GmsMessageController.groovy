package kr.codesolutions.gms

import static org.springframework.http.HttpStatus.*
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

@Transactional(readOnly = true)
class GmsMessageController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def gmsMessageService

	def beforeInterceptor = {
		log.info params
	}
	
	def index(Integer max, String sort, String order) {
        params.max = Math.min(max ?: 10, 100)
		params.sort = sort?:'id'
		params.order = order?:'desc'
		def results
		if(params?.status){
			results = GmsMessage.where{status == params.status}.list(params)
		}else{
			results = GmsMessage.list(params)
		}
        respond results, model:[gmsMessageInstanceCount: results.size()]
    }

    def show(GmsMessage gmsMessageInstance) {
        respond gmsMessageInstance
    }

    def create() {
        respond new GmsMessage(params)
    }

//    def searchUser(UserSearchCommand cmd, Integer max) {
//        params.max = Math.min(max ?: 10, 100)
//		def result = gmsMessageService.listUser(cmd, params)
//        respond result.list, model:[gmsUserInstanceCount: result.totalCount]
//    }
//
    @Transactional
    def save(GmsMessage gmsMessageInstance) {
        if (gmsMessageInstance == null) {
            notFound()
            return
        }
		if (gmsMessageInstance.hasErrors()) {
            respond gmsMessageInstance.errors, view:'create'
            return
		}
		gmsMessageInstance = gmsMessageService.submit(gmsMessageInstance)
		if (gmsMessageInstance.isFailed) {
			flash.message = gmsMessageInstance.error
            respond gmsMessageInstance.errors, view:'create'
            return
		}

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gmsMessage.label', default: 'GmsMessage'), gmsMessageInstance.id])
                redirect gmsMessageInstance
            }
            '*' { respond gmsMessageInstance, [status: CREATED] }
        }
    }

    def edit(GmsMessage gmsMessageInstance) {
        respond gmsMessageInstance
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gmsMessage.label', default: 'GmsMessage'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	
}
