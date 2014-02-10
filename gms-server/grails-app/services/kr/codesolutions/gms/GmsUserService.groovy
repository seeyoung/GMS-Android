package kr.codesolutions.gms

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

@Transactional
class GmsUserService {

	def Map list(UserSearchCommand cmd, Map params) {
		def criteria = new DetachedCriteria(GmsUser).build{
			and {
				like ('userId', cmd.userId)
				like ('name', cmd.name)
				like ('phoneNumber', cmd.phoneNumber)
			}
		}
		def totalCount = criteria.count()
		def list = criteria.list(params)
		
		return [list: list, totalCount: totalCount]
	}
	
	def GmsUser create(Map params) {
		def gmsUserInstance = GmsUser.findByUserId(params.userId)
		if(gmsUserInstance == null){
			gmsUserInstance = new GmsUser(params)
			if (gmsUserInstance == null || gmsUserInstance.hasErrors()) {
				return gmsUserInstance
			}
		}else{
			gmsUserInstance.phoneNumber = params.phoneNumber
			gmsUserInstance.registrationId = params.registrationId
		}
		gmsUserInstance.save flush:true
		
		return gmsUserInstance
	}

	def GmsUser update(GmsUser gmsUserInstance) {
		gmsUserInstance.save flush:true
		
		return gmsUserInstance
	}

	def GmsUser delete(GmsUser gmsUserInstance) {
		// User가 포함된 그룹에서 User를 제거한다.
		def criteria = new DetachedCriteria(GmsUserGroup).build{
			members {
				eq ('id',gmsUserInstance.id)
			}
		}
		def gmsUserGroupInstanceList = criteria.list()
		gmsUserGroupInstanceList.each{ gmsUserGroupInstance ->
			gmsUserGroupInstance.removeFromMembers(gmsUserInstance)
			gmsUserGroupInstance.save()
		}

		def gmsMessageInBoxCriteria = new DetachedCriteria(GmsMessageInBox).build{
			eq ('owner', gmsUserInstance)
		}
		gmsMessageInBoxCriteria.deleteAll()

		def gmsMessageOutBoxCriteria = new DetachedCriteria(GmsMessageOutBox).build{
			eq ('owner', gmsUserInstance)
		}
		gmsMessageOutBoxCriteria.deleteAll()
		
		gmsUserInstance.delete flush:true
		
		return gmsUserInstance
	}

}
