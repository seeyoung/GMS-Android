
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
					
						<g:sortableColumn property="channels" title="${message(code: 'gmsInstance.channels.label', default: 'Channels')}" />
					
						<g:sortableColumn property="queueSize" title="${message(code: 'gmsInstance.queueSize.label', default: 'Queue Size')}" />
					
						<g:sortableColumn property="distributeIntervalSeconds" title="${message(code: 'gmsInstance.distributeIntervalSeconds.label', default: 'Distribute Interval Seconds')}" />
					
						<g:sortableColumn property="publishIntervalSeconds" title="${message(code: 'gmsInstance.publishIntervalSeconds.label', default: 'Publish Interval Seconds')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsInstanceInstanceList}" status="i" var="gmsInstanceInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${gmsInstanceInstance.id}">${fieldValue(bean: gmsInstanceInstance, field: "instanceId")}</g:link></td>
					
						<td>${fieldValue(bean: gmsInstanceInstance, field: "host")}</td>
					
						<td>${fieldValue(bean: gmsInstanceInstance, field: "channels")}</td>
					
						<td>${fieldValue(bean: gmsInstanceInstance, field: "queueSize")}</td>
					
						<td>${fieldValue(bean: gmsInstanceInstance, field: "distributeIntervalSeconds")}</td>
					
						<td>${fieldValue(bean: gmsInstanceInstance, field: "publishIntervalSeconds")}</td>
					
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
