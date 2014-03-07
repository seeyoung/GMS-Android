
<%@ page import="kr.codesolutions.gms.GmsInstance" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsInstance.label', default: 'GmsInstance')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-gmsInstance" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-gmsInstance" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="instanceId" title="${message(code: 'gmsInstance.instanceId.label', default: 'Instance Id')}" />
					
						<g:sortableColumn property="host" title="${message(code: 'gmsInstance.host.label', default: 'Host')}" />
					
						<g:sortableColumn property="isRunning" title="${message(code: 'gmsInstance.isRunning.label', default: 'Is Running')}" />
					
						<g:sortableColumn property="autoStart" title="${message(code: 'gmsInstance.autoStart.label', default: 'Auto Start')}" />
					
						<g:sortableColumn property="channels" title="${message(code: 'gmsInstance.channels.label', default: 'Channels')}" />
					
						<g:sortableColumn property="queueSize" title="${message(code: 'gmsInstance.queueSize.label', default: 'Queue Size')}" />
					
						<g:sortableColumn property="sendIntervalSeconds" title="${message(code: 'gmsInstance.sendIntervalSeconds.label', default: 'Send Interval Seconds')}" />
						
						<g:sortableColumn property="preserveDays" title="${message(code: 'gmsInstance.preserveDays.label', default: 'Preserve Days')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsInstanceInstanceList}" status="i" var="gmsInstanceInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td style="text-align:center"><g:link action="show" id="${gmsInstanceInstance.id}">${gmsInstanceInstance.id}</g:link></td>
					
						<td style="text-align:center">${gmsInstanceInstance.host}:${gmsInstanceInstance.port}</td>
					
						<td style="text-align:center">${gmsInstanceInstance.isRunning?'Running':'Stopped'}</td>
						
						<td style="text-align:center"><g:checkBox name="autoStart" value="${gmsInstanceInstance.autoStart}" disabled="disabled" /></td>
						
						<td style="text-align:center">${gmsInstanceInstance.channels}</td>
					
						<td style="text-align:center">${gmsInstanceInstance.queueSize}</td>
					
						<td style="text-align:center">${gmsInstanceInstance.sendIntervalSeconds}</td>
					
						<td style="text-align:center">${gmsInstanceInstance.preserveDays}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${gmsInstanceInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
