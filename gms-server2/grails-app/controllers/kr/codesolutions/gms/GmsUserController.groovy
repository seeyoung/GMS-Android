package kr.codesolutions.gms

import static org.springframework.http.HttpStatus.*
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

import org.grails.databinding.BindUsing
import org.grails.databinding.BindingHelper
import org.grails.databinding.DataBindingSource

@Transactional(readOnly = true)
class GmsUserController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def gmsUserService
	
	def beforeInterceptor = {
		log.info params
	}
	
    def index(UserSearchCommand cmd, Integer max, String sort, String order) {
        params.max = Math.min(max ?: 10, 100)
		params.sort = sort?:'id'
		params.order = order?:'desc'

		def result = gmsUserService.list(cmd, params)
        respond result.list, model:[gmsUserInstanceCount: result.totalCount]
    }

    @Transactional
    def save() {
		def gmsUserInstance = gmsUserService.create(params)
        if (gmsUserInstance == null) {
            notFound()
            return
        }

        if (gmsUserInstance.hasErrors()) {
            respond gmsUserInstance.errors, view:'create'
            return
        }

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gmsUser.label', default: 'User'), gmsUserInstance.name])
                redirect gmsUserInstance
            }
            '*' { respond gmsUserInstance, [status: CREATED] }
        }
		
    }

    def edit(GmsUser gmsUserInstance) {
        respond gmsUserInstance
    }

    @Transactional
    def update(GmsUser gmsUserInstance) {
        if (gmsUserInstance == null) {
            notFound()
            return
        }

        if (gmsUserInstance.hasErrors()) {
            respond gmsUserInstance.errors, view:'edit'
            return
        }
		gmsUserService.update(gmsUserInstance)
		
        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'gmsUser.label', default: 'User'), gmsUserInstance.name])
                redirect gmsUserInstance
            }
            '*'{ respond gmsUserInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(GmsUser gmsUserInstance) {
        if (gmsUserInstance == null) {
            notFound()
            return
        }
		gmsUserService.delete(gmsUserInstance)
		
        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gmsUser.label', default: 'User'), gmsUserInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

	def show(GmsUser gmsUserInstance) {
		flash.message = params.postMessage?:null
		respond gmsUserInstance
    }

    def create() {
        respond new GmsUser(params)
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gmsUser.label', default: 'User'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}



@BindUsing(SearchCommandBindHelper)
@grails.validation.Validateable
class UserSearchCommand{
	String userId = '%';
	String name = '%';
	String phoneNumber = '%';
	
	def String toString(){
		return 'userId=' + userId + ', name=' + name + ', phoneNumber=' + phoneNumber
	}
}

class SearchCommandBindHelper implements BindingHelper{
	public Object getPropertyValue(Object obj, String propertyName, DataBindingSource source){
		def n = source[propertyName]
		return n?"%$n%":'%'
	}
}
