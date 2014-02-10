
<%@ page import="kr.codesolutions.gms.GmsMessage" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsMessage.label', default: 'GmsMessage')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-gmsMessage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-gmsMessage" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="sentTime" title="${message(code: 'gmsMessage.sentTime.label', default: 'Sent Time')}" />
					
						<g:sortableColumn property="readTime" title="${message(code: 'gmsMessage.readTime.label', default: 'Read Time')}" />
					
						<g:sortableColumn property="reservationTime" title="${message(code: 'gmsMessage.reservationTime.label', default: 'Reservation Time')}" />
					
						<g:sortableColumn property="createdTime" title="${message(code: 'gmsMessage.createdTime.label', default: 'Created Time')}" />
					
						<g:sortableColumn property="createdBy" title="${message(code: 'gmsMessage.createdBy.label', default: 'Created By')}" />
					
						<g:sortableColumn property="modifiedTime" title="${message(code: 'gmsMessage.modifiedTime.label', default: 'Modified Time')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsMessageInstanceList}" status="i" var="gmsMessageInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${gmsMessageInstance.id}">${fieldValue(bean: gmsMessageInstance, field: "sentTime")}</g:link></td>
					
						<td><g:formatDate date="${gmsMessageInstance.readTime}"  format="MM/dd HH:mm:ss" /></td>
					
						<td><g:formatDate date="${gmsMessageInstance.reservationTime}"  format="MM/dd HH:mm:ss" /></td>
					
						<td><g:formatDate date="${gmsMessageInstance.createdTime}"  format="MM/dd HH:mm:ss" /></td>
					
						<td>${fieldValue(bean: gmsMessageInstance, field: "createdBy")}</td>
					
						<td><g:formatDate date="${gmsMessageInstance.modifiedTime}"  format="MM/dd HH:mm:ss" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${gmsMessageInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
