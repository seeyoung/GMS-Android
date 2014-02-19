
<%@ page import="kr.codesolutions.gms.GmsMessage" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsMessage.label', default: 'GmsMessage')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-gmsMessage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
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
			
				<g:if test="${gmsMessageInstance?.recipients}">
				<li class="fieldcontain">
					<span id="recipients-label" class="property-label"><g:message code="gmsMessage.recipients.label" default="Recipients" /></span>
						<g:each in="${gmsMessageInstance.recipients}" var="r">
						<span class="property-value" aria-labelledby="recipients-label">${r.name}(${r.userId})</span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.sendPolicy}">
				<li class="fieldcontain">
					<span id="sendPolicy-label" class="property-label"><g:message code="gmsMessage.sendPolicy.label" default="Send Policy" /></span>
					
						<span class="property-value" aria-labelledby="sendPolicy-label">${gmsMessageInstance?.sendPolicy}</span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.reservationTime}">
				<li class="fieldcontain">
					<span id="reservationTime-label" class="property-label"><g:message code="gmsMessage.reservationTime.label" default="Reservation Time" /></span>
					
						<span class="property-value" aria-labelledby="reservationTime-label"><g:formatDate date="${gmsMessageInstance?.reservationTime}" format="yyyy-MM-dd HH:mm" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="gmsMessage.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label">${gmsMessageInstance?.status}</span>
					
				</li>
				</g:if>

				<g:if test="${gmsMessageInstance?.isSent}">
				<li class="fieldcontain">
					<span id="isSent-label" class="property-label"><g:message code="gmsMessage.isSent.label" default="Is Sent" /></span>
					
						<span class="property-value" aria-labelledby="isSent-label"><g:checkBox name="isSent" value="${gmsMessageInstance?.isSent}" disabled="disabled" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.error}">
				<li class="fieldcontain">
					<span id="error-label" class="property-label"><g:message code="gmsMessage.error.label" default="Error" /></span>
					
						<span class="property-value" aria-labelledby="error-label">${gmsMessageInstance?.error}</span>
					
				</li>
				</g:if>

				<g:if test="${gmsMessageInstance?.draftTime}">
				<li class="fieldcontain">
					<span id="draftTime-label" class="property-label"><g:message code="gmsMessage.draftTime.label" default="Draft Time" /></span>
					
						<span class="property-value" aria-labelledby="draftTime-label"><g:formatDate date="${gmsMessageInstance?.draftTime}" format="yyyy-MM-dd HH:mm" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.sendTime}">
				<li class="fieldcontain">
					<span id="sendTime-label" class="property-label"><g:message code="gmsMessage.sendTime.label" default="Send Time" /></span>
					
						<span class="property-value" aria-labelledby="sendTime-label"><g:formatDate date="${gmsMessageInstance?.sendTime}" format="yyyy-MM-dd HH:mm" /></span>
					
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
