
class BootStrap {

	def gmsConfigService
	def gmsInstanceService
		
	def init = { servletContext ->
		environments {
			production {
			}
			development {
			}
		}
		gmsConfigService.initData()
		gmsInstanceService.autostart()
	}

	def destroy = {
		gmsInstanceService.shutdown()
	}
}
