
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
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.compose.label" args="[entityName]" /></g:link></li>
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
			
				<g:if test="${gmsMessageInstance?.senderUserId}">
				<li class="fieldcontain">
					<span id="sender-label" class="property-label"><g:message code="gmsMessage.sender.label" default="Sender" /></span>
					
						<span class="property-value" aria-labelledby="sender-label">${gmsMessageInstance?.senderName}(${gmsMessageInstance?.senderUserId})</span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.recipientGroup}">
				<li class="fieldcontain">
					<span id="recipientGroup-label" class="property-label"><g:message code="gmsMessage.recipientGroup.label" default="Recipient Group" /></span>
					
						<span class="property-value" aria-labelledby="recipientGroup-label">${gmsMessageInstance?.recipientGroup.name}</span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.recipientFilter}">
				<li class="fieldcontain">
					<span id="recipientFilter-label" class="property-label"><g:message code="gmsMessage.recipientFilter.label" default="Recipient Filter" /></span>
					
						<span class="property-value" aria-labelledby="recipientFilter-label">${gmsMessageInstance?.recipientFilter}</span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.recipientCount}">
				<li class="fieldcontain">
					<span id="recipientCount-label" class="property-label"><g:message code="gmsMessage.recipientCount.label" default="Recipient Count" /></span>
					
						<span class="property-value" aria-labelledby="recipientCount-label">${gmsMessageInstance?.recipientCount}</span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.sentCount}">
				<li class="fieldcontain">
					<span id="sentCount-label" class="property-label"><g:message code="gmsMessage.sentCount.label" default="Sent Count" /></span>
					
						<span class="property-value" aria-labelledby="sentCount-label">${gmsMessageInstance?.sentCount}</span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.failedCount}">
				<li class="fieldcontain">
					<span id="failedCount-label" class="property-label"><g:message code="gmsMessage.failedCount.label" default="Failed Count" /></span>
					
						<span class="property-value" aria-labelledby="failedCount-label">${gmsMessageInstance?.failedCount}</span>
					
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
					
						<span class="property-value" aria-labelledby="reservationTime-label"><g:formatDate date="${gmsMessageInstance?.reservationTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.isPersonal}">
				<li class="fieldcontain">
					<span id="isPersonal-label" class="property-label"><g:message code="gmsMessage.isPersonal.label" default="Is Personal" /></span>
					
						<span class="property-value" aria-labelledby="isPersonal-label"><g:checkBox name="isPersonal" value="${gmsMessageInstance?.isPersonal}" disabled="disabled" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.isBulk}">
				<li class="fieldcontain">
					<span id="isBulk-label" class="property-label"><g:message code="gmsMessage.isBulk.label" default="Is Bulk" /></span>
					
						<span class="property-value" aria-labelledby="isBulk-label"><g:checkBox name="isBulk" value="${gmsMessageInstance?.isBulk}" disabled="disabled" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.isCallback}">
				<li class="fieldcontain">
					<span id="isCallback-label" class="property-label"><g:message code="gmsMessage.isCallback.label" default="Is Callback" /></span>
					
						<span class="property-value" aria-labelledby="isCallback-label"><g:checkBox name="isCallback" value="${gmsMessageInstance?.isCallback}" disabled="disabled" /></span>
					
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
			
				<g:if test="${gmsMessageInstance?.sentTime}">
				<li class="fieldcontain">
					<span id="sentTime-label" class="property-label"><g:message code="gmsMessage.sentTime.label" default="Sent Time" /></span>
					
						<span class="property-value" aria-labelledby="sentTime-label"><g:formatDate date="${gmsMessageInstance?.sentTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.isRead}">
				<li class="fieldcontain">
					<span id="isRead-label" class="property-label"><g:message code="gmsMessage.isRead.label" default="Is Read" /></span>
					
						<span class="property-value" aria-labelledby="isRead-label"><g:checkBox name="isRead" value="${gmsMessageInstance?.isRead}" disabled="disabled" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.readTime}">
				<li class="fieldcontain">
					<span id="readTime-label" class="property-label"><g:message code="gmsMessage.readTime.label" default="Read Time" /></span>
					
						<span class="property-value" aria-labelledby="readTime-label"><g:formatDate date="${gmsMessageInstance?.readTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.isFailed}">
				<li class="fieldcontain">
					<span id="isFailed-label" class="property-label"><g:message code="gmsMessage.isFailed.label" default="Is Failed" /></span>
					
						<span class="property-value" aria-labelledby="isFailed-label"><g:checkBox name="isFailed" value="${gmsMessageInstance?.isFailed}" disabled="disabled" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.failedTime}">
				<li class="fieldcontain">
					<span id="failedTime-label" class="property-label"><g:message code="gmsMessage.failedTime.label" default="Failed Time" /></span>
					
						<span class="property-value" aria-labelledby="failedTime-label"><g:formatDate date="${gmsMessageInstance?.failedTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
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
					
						<span class="property-value" aria-labelledby="draftTime-label"><g:formatDate date="${gmsMessageInstance?.draftTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.publishTime}">
				<li class="fieldcontain">
					<span id="publishTime-label" class="property-label"><g:message code="gmsMessage.publishTime.label" default="Publish Time" /></span>
					
						<span class="property-value" aria-labelledby="publishTime-label"><g:formatDate date="${gmsMessageInstance?.publishTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.waitTime}">
				<li class="fieldcontain">
					<span id="waitTime-label" class="property-label"><g:message code="gmsMessage.waitTime.label" default="Wait Time" /></span>
					
						<span class="property-value" aria-labelledby="waitTime-label"><g:formatDate date="${gmsMessageInstance?.waitTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.sendTime}">
				<li class="fieldcontain">
					<span id="sendTime-label" class="property-label"><g:message code="gmsMessage.sendTime.label" default="Send Time" /></span>
					
						<span class="property-value" aria-labelledby="sendTime-label"><g:formatDate date="${gmsMessageInstance?.sendTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.sentTime}">
				<li class="fieldcontain">
					<span id="sentTime-label" class="property-label"><g:message code="gmsMessage.sentTime.label" default="Sent Time" /></span>
					
						<span class="property-value" aria-labelledby="sentTime-label"><g:formatDate date="${gmsMessageInstance?.sentTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.completedTime}">
				<li class="fieldcontain">
					<span id="completedTime-label" class="property-label"><g:message code="gmsMessage.completedTime.label" default="Completed Time" /></span>
					
						<span class="property-value" aria-labelledby="completedTime-label"><g:formatDate date="${gmsMessageInstance?.completedTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsMessageInstance?.terminatedTime}">
				<li class="fieldcontain">
					<span id="terminatedTime-label" class="property-label"><g:message code="gmsMessage.terminatedTime.label" default="Terminated Time" /></span>
					
						<span class="property-value" aria-labelledby="terminatedTime-label"><g:formatDate date="${gmsMessageInstance?.terminatedTime}" format="yyyy-MM-dd HH:mm:ss" /></span>
					
				</li>
				</g:if>
				
			</ol>
		</div>
	</body>
</html>
