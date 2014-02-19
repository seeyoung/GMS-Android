
<%@ page import="kr.codesolutions.gms.GmsMessage" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsMessageOutBox.message.label', default: 'Sents')}" />
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
			
				<g:if test="${gmsMessageInstance?.recipients}">
				<li class="fieldcontain">
					<span id="recipients-label" class="property-label"><g:message code="gmsMessage.recipients.label" default="Recipients" /></span>
						<span class="property-value" aria-labelledby="recipients-label">
						<table>
						<thead>
								<tr>
								
									<g:sortableColumn property="userId" title="${message(code: 'gmsMessageRecipient.userId.label', default: 'User ID')}"/>
			
									<g:sortableColumn property="name" title="${message(code: 'gmsMessageRecipient.name.label', default: 'Name')}"/>
			
									<g:sortableColumn property="isSent" title="${message(code: 'gmsMessageRecipient.isSent.label', default: 'Is Sent')}"/>
								
									<g:sortableColumn property="sentTime" title="${message(code: 'gmsMessageRecipient.sentTime.label', default: 'Sent Time')}"/>
			
									<g:sortableColumn property="isRead" title="${message(code: 'gmsMessageRecipient.isRead.label', default: 'Is Read')}"/>
								
									<g:sortableColumn property="readTime" title="${message(code: 'gmsMessageRecipient.readTime.label', default: 'Read Time')}"/>
								
								</tr>
							</thead>
							<tbody>
							<g:each in="${gmsMessageInstance.recipients}" status="i" var="gmsMessageRecipientInstance">
								<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
								
									<td>${fieldValue(bean: gmsMessageRecipientInstance, field: "userId")}</td>
								
									<td>${fieldValue(bean: gmsMessageRecipientInstance, field: "name")}</td>
								
									<td style="text-align:center"><g:checkBox name="isSent" value="${gmsMessageRecipientInstance.isSent}" disabled="disabled" /></td>
								
									<td><g:formatDate date="${gmsMessageRecipientInstance.sentTime}" format="MM/dd HH:mm"/></td>
								
									<td style="text-align:center"><g:checkBox name="isRead" value="${gmsMessageRecipientInstance.isRead}" disabled="disabled" /></td>
								
									<td><g:formatDate date="${gmsMessageRecipientInstance.readTime}" format="MM/dd HH:mm"/></td>
								
								</tr>
							</g:each>
							</tbody>
						</table>
						</span>
					
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

				<li class="fieldcontain">
					<span id="isSent-label" class="property-label"><g:message code="gmsMessage.isSent.label" default="Is Sent" /></span>
					
						<span class="property-value" aria-labelledby="isSent-label"><g:checkBox name="isSent" value="${gmsMessageInstance?.isSent}" disabled="disabled" /></span>
					
				</li>
			
				<g:if test="${gmsMessageInstance?.error}">
				<li class="fieldcontain">
					<span id="error-label" class="property-label"><g:message code="gmsMessage.error.label" default="Error" /></span>
					
						<span class="property-value" aria-labelledby="error-label">${gmsMessageInstance?.error}</span>
					
				</li>
				</g:if>

				<li class="fieldcontain">
					<span id="isRead-label" class="property-label"><g:message code="gmsMessage.isRead.label" default="Is Read" /></span>
					
						<span class="property-value" aria-labelledby="isRead-label"><g:checkBox name="isRead" value="${gmsMessageInstance?.isRead}" disabled="disabled" /></span>
					
				</li>
			
				<g:if test="${gmsMessageInstance?.readTime}">
				<li class="fieldcontain">
					<span id="readTime-label" class="property-label"><g:message code="gmsMessage.readTime.label" default="Read Time" /></span>
					
						<span class="property-value" aria-labelledby="readTime-label"><g:formatDate date="${gmsMessageInstance?.readTime}" format="yyyy-MM-dd HH:mm" /></span>
					
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
			
				<g:if test="${gmsMessageInstance?.completeTime}">
				<li class="fieldcontain">
					<span id="completeTime-label" class="property-label"><g:message code="gmsMessage.completeTime.label" default="Complete Time" /></span>
					
						<span class="property-value" aria-labelledby="completeTime-label"><g:formatDate date="${gmsMessageInstance?.completeTime}" format="yyyy-MM-dd HH:mm" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.terminateTime}">
				<li class="fieldcontain">
					<span id="terminateTime-label" class="property-label"><g:message code="gmsMessage.terminateTime.label" default="Terminate Time" /></span>
					
						<span class="property-value" aria-labelledby="terminateTime-label"><g:formatDate date="${gmsMessageInstance?.terminateTime}" format="yyyy-MM-dd HH:mm" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.cancelTime}">
				<li class="fieldcontain">
					<span id="cancelTime-label" class="property-label"><g:message code="gmsMessage.cancelTime.label" default="Cancel Time" /></span>
					
						<span class="property-value" aria-labelledby="cancelTime-label"><g:formatDate date="${gmsMessageInstance?.cancelTime}" format="yyyy-MM-dd HH:mm" /></span>
					
				</li>
				</g:if>
			
			</ol>
		</div>
	</body>
</html>
