<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>U119 Push 서비스</title>
		<style type="text/css" media="screen">
			#status {
				background-color: #eee;
				border: .2em solid #fff;
				margin: 2em 2em 1em;
				padding: 1em;
				width: 12em;
				float: left;
				-moz-box-shadow: 0px 0px 1.25em #ccc;
				-webkit-box-shadow: 0px 0px 1.25em #ccc;
				box-shadow: 0px 0px 1.25em #ccc;
				-moz-border-radius: 0.6em;
				-webkit-border-radius: 0.6em;
				border-radius: 0.6em;
			}

			.ie6 #status {
				display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
			}

			#status ul {
				font-size: 0.9em;
				list-style-type: none;
				margin-bottom: 0.6em;
				padding: 0;
			}

			#status li {
				line-height: 1.3;
			}

			#status h1 {
				text-transform: uppercase;
				font-size: 1.1em;
				margin: 0 0 0.3em;
			}

			#page-body {
				margin: 2em 1em 1.25em 18em;
			}

			h2 {
				margin-top: 1em;
				margin-bottom: 0.3em;
				font-size: 1em;
			}

			p {
				line-height: 1.5;
				margin: 0.25em 0;
			}

			#controller-list ul {
				list-style-position: inside;
			}

			#controller-list li {
				line-height: 1.3;
				list-style-position: inside;
				margin: 0.25em 0;
			}

			@media screen and (max-width: 480px) {
				#status {
					display: none;
				}

				#page-body {
					margin: 0 1em 1em;
				}

				#page-body h1 {
					margin-top: 0;
				}
			}
		</style>
		<r:script disposition="head">
			function doTest(){
				$("#success").text("Testing....");
				var contentHtml = $("#content").val();
				$.ajax({
						type: "POST",
						url: "/gms/message/send",
						data: {recipientId:"max3",subject:"응급상황 발생!",content:contentHtml,senderId:"gmsmaster"},
						//data: {id:"34", registrationId:"APA91bEkIO2dj4nvpp-2EuhPA7BO6iOED84K0NuG1oIHsoBgZhuMOSLSMUD3PzqT_cwqu0WC_Mka2OG1kRRawSN9QFYy1uMnJZKFrZB6Ncy_T5TYUFcx3NuQzbKj7K9uFX__qLrVSBQIRuU1eMRx1b-NypoDMYBo4g"},
						dataType: "text",
						success: function(result){
									$("#success").text("Sent : " + result);
   								}  								
						,fail: function(result){
									$("#success").text("error:" + result);
   								}  								
   						});
			}
			
		</r:script>
		
	</head>
	<body>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="status" role="complementary">
			<h1>Push 서비스 관리</h1>
			<ul>
				<li>> <g:link controller="GmsMessage" action="create"><g:message code="default.menu.message.label" default="Compose"/></g:link></li>
				<li>> <g:link controller="GmsMessageOutBox"><g:message code="default.menu.messageOutBox.label" default="Sents"/></g:link></li>
				<!-- li>> <g:link controller="GmsMessageInBox" params="[userId:'admin']"><g:message code="default.menu.messageInBox.label" default="In Box"/></g:link></li> -->
				<li>> <g:link controller="GmsUser"><g:message code="default.menu.user.label" default="Users"/></g:link></li>
				<li>> <g:link controller="GmsUserGroup"><g:message code="default.menu.group.lable" default="Groups"/></g:link></li>
			</ul>
			<!-- 
			<h1>Application Status</h1>
			<ul>
				<li>App version: <g:meta name="app.version"/></li>
				<li>Grails version: <g:meta name="app.grails.version"/></li>
				<li>Groovy version: ${GroovySystem.getVersion()}</li>
				<li>JVM version: ${System.getProperty('java.version')}</li>
				<li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
				<li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
				<li>Domains: ${grailsApplication.domainClasses.size()}</li>
				<li>Services: ${grailsApplication.serviceClasses.size()}</li>
				<li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
			</ul>
			<h1>Installed Plugins</h1>
			<ul>
				<g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
					<li>${plugin.name} - ${plugin.version}</li>
				</g:each>
			</ul>
			 -->
		</div>
		<div id="page-body" role="main">
			<h1>Push 서비스 관리자</h1>
			<p> Push 서비스 정보를 관리하고 사용자들에게 Push메시지를 발송하는 메뉴입니다.<br>
				먼저 "사용자 관리"에서 수신 사용자를 등록하고 "그룹 관리"에서 사용자들을 업무별로 그룹핑하면
				"메시지 발송" 메뉴를 이용해 Push메시지를 발송합니다.
			</p>
			<ul>
				<a href="javascript:doTest();">Test</a>
			</ul>
			<div id="success">
			result...
			</div>
			<ul>
				<a href="/gms/gms-android.apk">Download Test App</a>
			</ul>
			<ul>
				<a href="/gms/gmsConfig/initData">Data initialize</a>
			</ul>
			<ul>
				<textArea id="content" rows="5" cols="40"><html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head><body>김아무개님 응급상황발생!\n보호자께서는 즉시 <a href='tel:010-2829-5845'>010-2829-5845</a> 으로 연락 바랍니다.</body></html></textArea>					
			</ul>
			<!-- 
			<div id="controller-list" role="navigation">
				<h2>Available Controllers:</h2>
				<ul>
					<g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
						<li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
					</g:each>
				</ul>
			</div>
			-->
		</div>
	</body>
</html>
