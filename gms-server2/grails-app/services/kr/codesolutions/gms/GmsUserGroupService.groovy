package kr.codesolutions.gms

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

@Transactional
class GmsUserGroupService {

	def Map list(UserGroupSearchCommand cmd, Map params) {
		def criteria = new DetachedCriteria(GmsUserGroup).build{
			and {
				like ('name', cmd.name)
			}
		}
		def totalCount = criteria.count()
		def list = criteria.list(params)
		
		return [list: list, totalCount: totalCount]
	}
	
	def GmsUserGroup create(GmsUserGroup gmsUserGroupInstance) {
		if (gmsUserGroupInstance.hasErrors()) {
			return gmsUserGroupInstance
		}
		gmsUserGroupInstance.save flush:true

		return gmsUserGroupInstance
	}

	def GmsUserGroup update(GmsUserGroup gmsUserGroupInstance) {
		gmsUserGroupInstance.save flush:true
		
		return gmsUserGroupInstance
	}

	def GmsUserGroup delete(GmsUserGroup gmsUserGroupInstance) {
		gmsUserGroupInstance.delete flush:true
		
		return gmsUserGroupInstance
	}
	
	
	def Map listUser(GmsUserGroup gmsUserGroupInstance, UserSearchCommand cmd, Map params) {
		def criteria = new DetachedCriteria(GmsUser).build{
			and {
				like ('userId', cmd.userId)
				like ('name', cmd.name)
				like ('phoneNumber', cmd.phoneNumber)
				not{ 'in'('userId', gmsUserGroupInstance.members*.userId)}
			}
		}
		def totalCount = criteria.count()
		def list = criteria.list(params)
		
		return [list: list, totalCount: totalCount]
	}

	def GmsUser addUser(GmsUserGroup gmsUserGroupInstance, GmsUser gmsUserInstance) {
		gmsUserGroupInstance.addToMembers(gmsUserInstance);
		gmsUserGroupInstance.save flush:true
		
		return gmsUserInstance
	}

	def GmsUser removeUser(GmsUserGroup gmsUserGroupInstance, GmsUser gmsUserInstance) {
		gmsUserGroupInstance.removeFromMembers(gmsUserInstance);
		gmsUserGroupInstance.save flush:true
		
		return gmsUserInstance
	}
}
