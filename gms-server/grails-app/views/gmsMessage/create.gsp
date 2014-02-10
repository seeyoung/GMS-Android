<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsMessage.label', default: 'GmsMessage')}" />
		<title><g:message code="default.compose.label" args="[entityName]" /></title>
		<r:script disposition="head">
			$( document ).ready(function() {
				$("#recipientGroup").change(function(){
					if(this.selectedIndex == 0){
						$("#recipientId,#buttonSearch").show();
					}else{
						$("#recipientId").val('').hide();
						$("#buttonSearch").hide();
					}
				})
			});
			
			function openSearchWindow(url, name){
				if($("#recipientGroup").prop("selectedIndex") > 0) return;
				var searchWindow = window.open(url, name, "width=650,height=600");
			}
			
			function addUser(userId){
				if($("#recipientGroup").prop("selectedIndex") > 0) return;
				var recipients = $("#recipientId");
				if(recipients.val().length > 0){
					if(recipients.val().search(new RegExp("(^" + userId + "$|;?" + userId + ";|;" + userId + ";?)")) < 0){
						recipients.val(recipients.val() + ";" + userId);
					}
				}else{
					recipients.val(userId);
				}
			}
		</r:script>
	</head>
	<body>
		<a href="#create-gmsMessage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="create-gmsMessage" class="content scaffold-create" role="main">
			<h1><g:message code="default.compose.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${gmsMessageInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${gmsMessageInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form url="[resource:gmsMessageInstance, action:'save']" >
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="send" class="save" value="${message(code: 'default.button.send.label', default: 'Send')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
