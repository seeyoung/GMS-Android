import kr.codesolutions.gms.GmsConstantMessageStatus
import kr.codesolutions.gms.GmsConstantSendPolicy
import kr.codesolutions.gms.GmsConstantSendType
import kr.codesolutions.gms.GmsMassMessageRequest;
import kr.codesolutions.gms.GmsUser
import kr.codesolutions.gms.GmsUserGroup

class BootStrap {

	def gmsConfigService
		
	def init = { servletContext ->
		gmsConfigService.initData()
	}

	def destroy = {
	}
}
