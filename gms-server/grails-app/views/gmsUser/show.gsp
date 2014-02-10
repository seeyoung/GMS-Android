
<%@ page import="kr.codesolutions.gms.GmsUser" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsUser.label', default: 'User')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-gmsUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-gmsUser" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
					
			<ol class="property-list gmsUser">
			
				<g:if test="${gmsUserInstance}">
				
				<li class="fieldcontain">
					<span id="userId-label" class="property-label"><g:message code="gmsUser.userId.label" default="User Id" /></span>
					
						<span class="property-value" aria-labelledby="userId-label"><g:fieldValue bean="${gmsUserInstance}" field="userId"/></span>
					
				</li>

				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="gmsUser.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${gmsUserInstance}" field="name"/></span>
					
				</li>
			
				<li class="fieldcontain">
					<span id="phoneNumber-label" class="property-label"><g:message code="gmsUser.phoneNumber.label" default="Phone Number" /></span>
					
						<span class="property-value" aria-labelledby="phoneNumber-label"><g:fieldValue bean="${gmsUserInstance}" field="phoneNumber"/></span>
					
				</li>
			
				<li class="fieldcontain">
					<span id="registrationId-label" class="property-label"><g:message code="gmsUser.registrationId.label" default="Registration ID" /></span>

						<span class="property-value" aria-labelledby="registrationId-label">
						<g:textArea name="registrationId" value="${gmsUserInstance.registrationId}" rows="5" cols="40" readonly="true"/>
						</span>

				</li>

				<li class="fieldcontain">
					<span id="isSendable-label" class="property-label"><g:message code="gmsUser.isSendable.label" default="Sendable" /></span>
					
						<span class="property-value" aria-labelledby="isSendable-label"><g:checkBox name="isSendable" value="${gmsUserInstance?.isSendable}" disabled="disabled"/></span>
					
				</li>
			
				<li class="fieldcontain">
					<span id="enabled-label" class="property-label"><g:message code="gmsUser.enabled.label" default="Enabled" /></span>
					
						<span class="property-value" aria-labelledby="enabled-label"><g:checkBox name="enabled" value="${gmsUserInstance?.enabled}" disabled="disabled"/></span>
					
				</li>
			
				<li class="fieldcontain">
					<span id="errorCount-label" class="property-label"><g:message code="gmsUser.errorCount.label" default="Sendable" /></span>
					
						<span class="property-value" aria-labelledby="errorCount-label"><g:fieldValue bean="${gmsUserInstance}" field="errorCount"/></span>
					
				</li>
			
				<li class="fieldcontain">
					<span id="modifiedTime-label" class="property-label"><g:message code="gmsUser.modifiedTime.label" default="Modified Time" /></span>
					
						<span class="property-value" aria-labelledby="modifiedTime-label"><g:formatDate date="${gmsUserInstance?.modifiedTime}" /></span>
					
				</li>

				<li class="fieldcontain">
					<span id="createdTime-label" class="property-label"><g:message code="gmsUser.createdTime.label" default="Created Time" /></span>
					
						<span class="property-value" aria-labelledby="createdTime-label"><g:formatDate date="${gmsUserInstance?.createdTime}" /></span>
					
				</li>

				</g:if>
			
			</ol>
			<g:form url="[resource:gmsUserInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${gmsUserInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
