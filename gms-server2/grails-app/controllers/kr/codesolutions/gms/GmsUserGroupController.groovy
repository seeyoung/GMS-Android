package kr.codesolutions.gms



import static org.springframework.http.HttpStatus.*
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

import org.grails.databinding.BindUsing

@Transactional(readOnly = true)
class GmsUserGroupController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def gmsUserGroupService

	def beforeInterceptor = {
		log.info params
	}
	
    def index(UserGroupSearchCommand cmd, Integer max, String sort, String order) {
        params.max = Math.min(max ?: 10, 100)
		params.sort = sort?:'id'
		params.order = order?:'desc'

		def result = gmsUserGroupService.list(cmd, params)
        respond result.list, model:[gmsUserGroupInstanceCount: result.totalCount]
    }

	def show(GmsUserGroup gmsUserGroupInstance) {
		flash.message = params.postMessage?:null
        respond gmsUserGroupInstance
    }

    def create() {
        respond new GmsUserGroup(params)
    }

    @Transactional
    def save(GmsUserGroup gmsUserGroupInstance) {
		if (gmsUserGroupInstance == null) {
            notFound()
            return
        }
		gmsUserGroupInstance = gmsUserGroupService.create(gmsUserGroupInstance)
        if (gmsUserGroupInstance.hasErrors()) {
            respond gmsUserGroupInstance.errors, view:'create'
            return
        }

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'gmsUserGroup.label', default: 'User Group'), gmsUserGroupInstance.name])
                redirect gmsUserGroupInstance
            }
            '*' { respond gmsUserGroupInstance, [status: CREATED] }
        }
    }

    def edit(GmsUserGroup gmsUserGroupInstance) {
        respond gmsUserGroupInstance
    }

    @Transactional
    def update(GmsUserGroup gmsUserGroupInstance) {
        if (gmsUserGroupInstance == null) {
            notFound()
            return
        }

        if (gmsUserGroupInstance.hasErrors()) {
            respond gmsUserGroupInstance.errors, view:'edit'
            return
        }

		gmsUserGroupService.update(gmsUserGroupInstance)

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'gmsUserGroup.label', default: 'User Group'), gmsUserGroupInstance.name])
                redirect gmsUserGroupInstance
            }
            '*'{ respond gmsUserGroupInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(GmsUserGroup gmsUserGroupInstance) {

        if (gmsUserGroupInstance == null) {
            notFound()
            return
        }

 		gmsUserGroupService.delete(gmsUserGroupInstance)

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'gmsUserGroup.label', default: 'User Group'), gmsUserGroupInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

	def searchUser(GmsUserGroup gmsUserGroupInstance, UserSearchCommand cmd, Integer max) {
		params.max = Math.min(max ?: 10, 100)
		flash.message = params.postMessage?:null

		if (gmsUserGroupInstance == null) {
			notFound()
			return
		}
		def result = gmsUserGroupService.listUser(gmsUserGroupInstance, cmd, params)
		
		respond result.list, model:[gmsUserInstanceCount: result.totalCount, gmsUserGroupInstance: gmsUserGroupInstance]
	}

	def addUser(GmsUserGroup gmsUserGroupInstance) {
		log.error params
		if(gmsUserGroupInstance.members.find{ it.userId == params.userId}){
			render message(code: 'default.duplicated.message', args: [message(code: 'gmsUser.label', default: 'User'), gmsUserInstance.name])
			return
		}
		
		def gmsUserInstance = GmsUser.get(params.userId)
		if (gmsUserInstance == null) {
			notFound()
			return
		}
		gmsUserGroupService.addUser(gmsUserGroupInstance, gmsUserInstance)
		
		render message(code: 'default.added.message', args: [message(code: 'gmsUser.label', default: 'User'), gmsUserInstance.name])
	}
	
	def deleteUser(GmsUserGroup gmsUserGroupInstance) {
		if (gmsUserGroupInstance == null) {
			notFound()
			return
		}
		def gmsUserInstance = GmsUser.get(params.userId)
		if (gmsUserInstance == null) {
			notFound()
			return
		}
		gmsUserGroupService.removeUser(gmsUserGroupInstance, gmsUserInstance)
		
		render message(code: 'default.deleted.message', args: [message(code: 'gmsUser.label', default: 'User'), gmsUserInstance.name])
	}


    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'gmsUserGroup.label', default: 'User Group'), params.name])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}


@BindUsing(SearchCommandBindHelper)
@grails.validation.Validateable
class UserGroupSearchCommand{
	String name = '%';
	
	def String toString(){
		return 'name=' + name 
	}
}

