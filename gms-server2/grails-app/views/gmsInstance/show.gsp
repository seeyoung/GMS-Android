
<%@ page import="kr.codesolutions.gms.GmsInstance" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsInstance.label', default: 'GmsInstance')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-gmsInstance" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-gmsInstance" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list gmsInstance">
			
				<li class="fieldcontain">
					<span id="instanceId-label" class="property-label"><g:message code="gmsInstance.instanceId.label" default="Instance Id" /></span>
					
						<span class="property-value" aria-labelledby="instanceId-label">${gmsInstanceInstance.id}</span>
					
				</li>
			
				<li class="fieldcontain">
					<span id="isRunning-label" class="property-label"><g:message code="gmsInstance.isRunning.label" default="Is Running" /></span>
					
						<span class="property-value" aria-labelledby="isRunning-label">${gmsInstanceInstance.isRunning?'Running':'Stopped'}
						<g:if test="${gmsInstanceInstance.isRunning}">
							<g:link action="stop" resource="${gmsInstanceInstance}"><g:message code="default.button.stop.label" default="Stop" /></g:link>
						</g:if>
						<g:else>
							<g:link action="start" resource="${gmsInstanceInstance}"><g:message code="default.button.start.label" default="Start" /></g:link>
						</g:else>
						</span>
					
				</li>
			
				<g:if test="${gmsInstanceInstance?.host}">
				<li class="fieldcontain">
					<span id="host-label" class="property-label"><g:message code="gmsInstance.host.label" default="Host" /></span>
					
						<span class="property-value" aria-labelledby="host-label"><g:fieldValue bean="${gmsInstanceInstance}" field="host"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.port}">
				<li class="fieldcontain">
					<span id="port-label" class="property-label"><g:message code="gmsInstance.port.label" default="Port" /></span>
					
						<span class="property-value" aria-labelledby="port-label"><g:fieldValue bean="${gmsInstanceInstance}" field="port"/></span>
					
				</li>
				</g:if>
			
				<li class="fieldcontain">
					<span id="autoStart-label" class="property-label"><g:message code="gmsInstance.autoStart.label" default="Auto Start" /></span>
					
						<span class="property-value" aria-labelledby="autoStart-label"><g:checkBox name="autoStart" value="${gmsInstanceInstance.autoStart}" disabled="disabled" /></span>
					
				</li>
			
				<g:if test="${gmsInstanceInstance?.channels}">
				<li class="fieldcontain">
					<span id="channels-label" class="property-label"><g:message code="gmsInstance.channels.label" default="Channels" /></span>
					
						<span class="property-value" aria-labelledby="channels-label"><g:fieldValue bean="${gmsInstanceInstance}" field="channels"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.queueSize}">
				<li class="fieldcontain">
					<span id="queueSize-label" class="property-label"><g:message code="gmsInstance.queueSize.label" default="Queue Size" /></span>
					
						<span class="property-value" aria-labelledby="queueSize-label"><g:fieldValue bean="${gmsInstanceInstance}" field="queueSize"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.distributeIntervalSeconds}">
				<li class="fieldcontain">
					<span id="distributeIntervalSeconds-label" class="property-label"><g:message code="gmsInstance.distributeIntervalSeconds.label" default="Distribute Interval Seconds" /></span>
					
						<span class="property-value" aria-labelledby="distributeIntervalSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="distributeIntervalSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.publishIntervalSeconds}">
				<li class="fieldcontain">
					<span id="publishIntervalSeconds-label" class="property-label"><g:message code="gmsInstance.publishIntervalSeconds.label" default="Publish Interval Seconds" /></span>
					
						<span class="property-value" aria-labelledby="publishIntervalSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="publishIntervalSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.collectIntervalSeconds}">
				<li class="fieldcontain">
					<span id="collectIntervalSeconds-label" class="property-label"><g:message code="gmsInstance.collectIntervalSeconds.label" default="Collect Interval Seconds" /></span>
					
						<span class="property-value" aria-labelledby="collectIntervalSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="collectIntervalSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.postIntervalSeconds}">
				<li class="fieldcontain">
					<span id="postIntervalSeconds-label" class="property-label"><g:message code="gmsInstance.postIntervalSeconds.label" default="Post Interval Seconds" /></span>
					
						<span class="property-value" aria-labelledby="postIntervalSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="postIntervalSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.sendIntervalSeconds}">
				<li class="fieldcontain">
					<span id="sendIntervalSeconds-label" class="property-label"><g:message code="gmsInstance.sendIntervalSeconds.label" default="Send Interval Seconds" /></span>
					
						<span class="property-value" aria-labelledby="sendIntervalSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="sendIntervalSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.completeIntervalSeconds}">
				<li class="fieldcontain">
					<span id="completeIntervalSeconds-label" class="property-label"><g:message code="gmsInstance.completeIntervalSeconds.label" default="Complete Interval Seconds" /></span>
					
						<span class="property-value" aria-labelledby="completeIntervalSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="completeIntervalSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.terminateIntervalSeconds}">
				<li class="fieldcontain">
					<span id="terminateIntervalSeconds-label" class="property-label"><g:message code="gmsInstance.terminateIntervalSeconds.label" default="Terminate Interval Seconds" /></span>
					
						<span class="property-value" aria-labelledby="terminateIntervalSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="terminateIntervalSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.preserveDays}">
				<li class="fieldcontain">
					<span id="preserveDays-label" class="property-label"><g:message code="gmsInstance.preserveDays.label" default="Preserve Days" /></span>
					
						<span class="property-value" aria-labelledby="preserveDays-label"><g:fieldValue bean="${gmsInstanceInstance}" field="preserveDays"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.resendPendingSeconds}">
				<li class="fieldcontain">
					<span id="resendPendingSeconds-label" class="property-label"><g:message code="gmsInstance.resendPendingSeconds.label" default="Resend Pending Seconds" /></span>
					
						<span class="property-value" aria-labelledby="resendPendingSeconds-label"><g:fieldValue bean="${gmsInstanceInstance}" field="resendPendingSeconds"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.createdTime}">
				<li class="fieldcontain">
					<span id="createdTime-label" class="property-label"><g:message code="gmsInstance.createdTime.label" default="Created Time" /></span>
					
						<span class="property-value" aria-labelledby="createdTime-label"><g:formatDate date="${gmsInstanceInstance?.createdTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsInstanceInstance?.modifiedTime}">
				<li class="fieldcontain">
					<span id="modifiedTime-label" class="property-label"><g:message code="gmsInstance.modifiedTime.label" default="Modified Time" /></span>
					
						<span class="property-value" aria-labelledby="modifiedTime-label"><g:formatDate date="${gmsInstanceInstance?.modifiedTime}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:gmsInstanceInstance, action:'edit']" method="PUT">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${gmsInstanceInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
