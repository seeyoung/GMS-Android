package kr.codesolutions.gms

import grails.transaction.Transactional
import kr.codesolutions.gms.constants.InstanceLock

@Transactional
class GmsInstanceLockService {
	def lock(InstanceLock lock){
		GmsInstanceLock gmsInstanceLock
		try{
			gmsInstanceLock = GmsInstanceLock.lock(lock.value)
			if(gmsInstanceLock.isLocked) return null
			gmsInstanceLock.isLocked = true
			gmsInstanceLock.save(flush:true)
		}catch(ex){
			log.error ex.message
			return null
		}
		return gmsInstanceLock
	}
	
	def unlock(GmsInstanceLock gmsInstanceLock){
		gmsInstanceLock.isLocked = false
		gmsInstanceLock.save(flush:true)
	}
}
