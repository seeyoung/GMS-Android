
class BootStrap {

	def gmsConfigService
		
	def init = { servletContext ->
		environments {
			production {
			}
			development {
				gmsConfigService.initData()
			}
		}
		//gmsConfigService.startUpJob()
	}

	def destroy = {
	}
}
