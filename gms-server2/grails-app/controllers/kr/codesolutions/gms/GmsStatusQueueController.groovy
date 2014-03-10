package kr.codesolutions.gms

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import groovy.sql.Sql

@Transactional(readOnly = true)
class GmsStatusQueueController {

	def gmsMessageService
	
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		
		def results = gmsMessageService.queueStatus()
		log.info "size => " + results.size()
        respond results, model:[gmsStatusQueueInstanceCount: results.size()]
    }

    def show(GmsStatusQueue gmsStatusQueueInstance) {
        respond gmsStatusQueueInstance
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gmsStatusQueueInstance.label', default: 'GmsStatusQueue'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
