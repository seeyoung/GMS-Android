package kr.codesolutions.gms.rest

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import kr.codesolutions.gms.GmsUser

@Transactional(readOnly = true)
class UserController {

	def gmsUserService
	
	def beforeInterceptor = {
		log.info params
	}
	
	def get(){
		def gmsUserInstance = GmsUser.findByUserId(params.userId)
		if (gmsUserInstance == null) {
			notFound()
			return
		}
		respond gmsUserInstance
	}
	
    @Transactional
    def register() {
		def gmsUserInstance = gmsUserService.create(params)
		if (gmsUserInstance == null) {
			notFound()
			return
		}

		if (gmsUserInstance.hasErrors()) {
			respond gmsUserInstance.errors
			return
		}
		
        request.withFormat {
            '*' { render status: OK }
        }
		
    }

    @Transactional
	def unregister(){
		if(params.userId){
			def gmsUserInstance = GmsUser.findByUserId(params.userId)
			
			if (gmsUserInstance == null) {
				notFound()
				return
			}
			gmsUserService.delete(gmsUserInstance)
		}else if(params.registrationId){
			def gmsUserInstance = GmsUser.findByRegistrationId(params.registrationId)
			if (gmsUserInstance == null) {
				notFound()
				return
			}
			gmsUserService.delete(gmsUserInstance)
		}
		

        request.withFormat {
            '*'{ render status: OK }
        }
	}
	
    protected void notFound() {
        request.withFormat {
            '*'{ render status: NOT_FOUND }
        }
    }
}
