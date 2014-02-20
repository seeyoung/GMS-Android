package kr.codesolutions.gms

import grails.transaction.Transactional
import kr.codesolutions.gms.constants.InstanceLock

import org.springframework.transaction.annotation.Propagation

@Transactional 
class GmsInstanceLockService {
	
	@Transactional(propagation=Propagation.REQUIRES_NEW) 
	def boolean lock(InstanceLock lock, int instanceId){
		try{
			def gmsInstanceLock = GmsInstanceLock.lock(lock.value)
			if(!gmsInstanceLock.locked){
				gmsInstanceLock.instance = instanceId
				gmsInstanceLock.save(flush:true)
				return true
			}
			log.error lock.value + " is locked by Instance #${gmsInstanceLock.instance}(Lock)."
		}catch(ex){
			log.error ex.message
		}
		return false
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW) 
	def boolean unlock(InstanceLock lock, int instanceId){
		try{
			def gmsInstanceLock = GmsInstanceLock.lock(lock.value)
			if(gmsInstanceLock.locked){
				if(gmsInstanceLock.instance == instanceId){
					gmsInstanceLock.instance = 0
					gmsInstanceLock.save(flush:true)
					return true
				}
				log.error lock.value + " is locked by Instance #${gmsInstanceLock.instance}(Unlock)."
			}
		}catch(ex){
			log.error ex.message
		}
		return false
	}
}
