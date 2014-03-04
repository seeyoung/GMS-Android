package kr.codesolutions.gms



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class GmsInstanceController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	
	def gmsInstanceService
	
	def beforeInterceptor = {
		log.info params
	}

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond GmsInstance.list(params), model:[gmsInstanceInstanceCount: GmsInstance.count()]
    }

    def start(GmsInstance gmsInstanceInstance) {
		if(gmsInstanceInstance != null){
			gmsInstanceService.start(gmsInstanceInstance)
		}
    }
	
    def stop(GmsInstance gmsInstanceInstance) {
		if(gmsInstanceInstance != null){
			gmsInstanceService.stop(gmsInstanceInstance)
		}
    }
	
    def show(GmsInstance gmsInstanceInstance) {
        respond gmsInstanceInstance
    }

    def create() {
        respond new GmsInstance(params)
    }

    @Transactional
    def save(GmsInstance gmsInstanceInstance) {
        if (gmsInstanceInstance == null) {
            notFound()
            return
        }

        if (gmsInstanceInstance.hasErrors()) {
            respond gmsInstanceInstance.errors, view:'create'
            return
        }

        gmsInstanceInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gmsInstanceInstance.label', default: 'GmsInstance'), gmsInstanceInstance.id])
                redirect gmsInstanceInstance
            }
            '*' { respond gmsInstanceInstance, [status: CREATED] }
        }
    }

    def edit(GmsInstance gmsInstanceInstance) {
        respond gmsInstanceInstance
    }

    @Transactional
    def update(GmsInstance gmsInstanceInstance) {
        if (gmsInstanceInstance == null) {
            notFound()
            return
        }

        if (gmsInstanceInstance.hasErrors()) {
            respond gmsInstanceInstance.errors, view:'edit'
            return
        }

        gmsInstanceInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'GmsInstance.label', default: 'GmsInstance'), gmsInstanceInstance.id])
                redirect gmsInstanceInstance
            }
            '*'{ respond gmsInstanceInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(GmsInstance gmsInstanceInstance) {

        if (gmsInstanceInstance == null) {
            notFound()
            return
        }

        gmsInstanceInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'GmsInstance.label', default: 'GmsInstance'), gmsInstanceInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gmsInstanceInstance.label', default: 'GmsInstance'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
