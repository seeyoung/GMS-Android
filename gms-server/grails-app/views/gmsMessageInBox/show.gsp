
<%@ page import="kr.codesolutions.gms.GmsMessageInBox" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsMessageInBox.message.label', default: 'In Box')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-gmsMessage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-gmsMessage" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list gmsMessage">
			
			
				<g:if test="${gmsMessageInstance?.subject}">
				<li class="fieldcontain">
					<span id="subject-label" class="property-label"><g:message code="gmsMessage.subject.label" default="Subject" /></span>
					
						<span class="property-value" aria-labelledby="subject-label"><g:fieldValue bean="${gmsMessageInstance}" field="subject"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.content}">
				<li class="fieldcontain">
					<span id="content-label" class="property-label"><g:message code="gmsMessage.content.label" default="Content" /></span>
					
						<span class="property-value" aria-labelledby="content-label">
						<g:textArea name="content" value="${gmsMessageInstance.content}" rows="5" cols="40" readonly="true"/>
						</span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.sender}">
				<li class="fieldcontain">
					<span id="sender-label" class="property-label"><g:message code="gmsMessage.sender.label" default="Sender" /></span>
					
						<span class="property-value" aria-labelledby="sender-label"><g:link controller="gmsUser" action="show" id="${gmsMessageInstance?.sender?.id}">${gmsMessageInstance?.sender?.name}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.isRead}">
				<li class="fieldcontain">
					<span id="isRead-label" class="property-label"><g:message code="gmsMessage.isRead.label" default="Is Read" /></span>
					
						<span class="property-value" aria-labelledby="isRead-label"><g:formatBoolean boolean="${gmsMessageInstance?.isRead}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.readTime}">
				<li class="fieldcontain">
					<span id="readTime-label" class="property-label"><g:message code="gmsMessage.readTime.label" default="Read Time" /></span>
					
						<span class="property-value" aria-labelledby="readTime-label"><g:formatDate date="${gmsMessageInstance?.readTime}" format="yyyy-MM-dd HH:mm" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.sentTime}">
				<li class="fieldcontain">
					<span id="sentTime-label" class="property-label"><g:message code="gmsMessage.sentTime.label" default="Sent Time" /></span>
					
						<span class="property-value" aria-labelledby="sentTime-label"><g:formatDate date="${gmsMessageInstance?.sentTime}" format="yyyy-MM-dd HH:mm" /></span>
					
				</li>
				</g:if>

			</ol>
		</div>
	</body>
</html>
