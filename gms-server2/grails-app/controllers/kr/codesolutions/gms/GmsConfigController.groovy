package kr.codesolutions.gms



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional
class GmsConfigController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def gmsConfigService
	
	def beforeInterceptor = {
		log.info params
	}
	
    def initData() {
		gmsConfigService.initData()
		
		render 'Datas initialized...'
    }

   def testData() {
		gmsConfigService.testData()
		
		render 'Test datas generated...'
    }
 }
