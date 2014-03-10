
<%@ page import="kr.codesolutions.gms.GmsStatusQueue" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsStatusQueue.label', default: 'GmsStatusQueue')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-gmsStatusQueue" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-gmsStatusQueue" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="queueName" title="${message(code: 'gmsStatusQueue.queueName.label', default: 'Queue Name')}" />
					
						<g:sortableColumn property="queueSize" title="${message(code: 'gmsStatusQueue.queueSize.label', default: 'Queue Size')}" />
					
						<g:sortableColumn property="messageSize" title="${message(code: 'gmsStatusQueue.messageSize.label', default: 'Message Size')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsStatusQueueInstanceList}" status="i" var="gmsStatusQueueInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td>${fieldValue(bean: gmsStatusQueueInstance, field: "queueName")}</td>
					
						<td>${fieldValue(bean: gmsStatusQueueInstance, field: "queueSize")}</td>
					
						<td>${fieldValue(bean: gmsStatusQueueInstance, field: "messageSize")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${gmsStatusQueueInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
