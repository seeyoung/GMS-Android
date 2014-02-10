import grails.rest.render.json.JsonRenderer
import grails.rest.render.xml.XmlRenderer
import kr.codesolutions.gms.GmsUser


// Place your Spring DSL code here
beans = {
	/*
	h2Server(org.h2.tools.Server, "-tcp,-tcpPort,8043") { bean ->
		bean.factoryMethod = "createTcpServer"
		bean.initMethod = "start"
		bean.destroyMethod = "stop"
	}
	*/
    userXmlRenderer(XmlRenderer, GmsUser) {
        includes = ['userId','name','phoneNumber','registrationId']
    }
    userJsonRenderer(JsonRenderer, GmsUser) {
        includes = ['userId','name','phoneNumber','registrationId']
    }

}


