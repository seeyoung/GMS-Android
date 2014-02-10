
<%@ page import="kr.codesolutions.gms.GmsMessage" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsMessageOutBox.message.label', default: 'Sents')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-gmsMessageOutBox" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-gmsMessageOutBox" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="message.draftTime" title="${message(code: 'gmsMessage.draftTime.label', default: 'Draft Time')}" style="width:130px;"/>
					
						<g:sortableColumn property="message.subject" title="${message(code: 'gmsMessage.subject.label', default: 'Subject')}" style="text-align:left;"/>
					
						<g:sortableColumn property="message.recipients" title="${message(code: 'gmsMessage.recipients.label', default: 'Recipients')}" style="width:100px;"/>

						<g:sortableColumn property="message.status" title="${message(code: 'gmsMessage.status.label', default: 'Status')}" style="width:100px;"/>

						<g:sortableColumn property="message.sendTime" title="${message(code: 'gmsMessage.sendTime.label', default: 'Request Time')}" style="width:130px;"/>

						<g:sortableColumn property="message.sentTime" title="${message(code: 'gmsMessage.sentTime.label', default: 'Sent Time')}" style="width:130px;"/>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsMessageInstanceList}" status="i" var="gmsMessageInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:formatDate date="${gmsMessageInstance.draftTime}" format="MM/dd HH:mm"/></td>
						
						<td><g:link action="show" id="${gmsMessageInstance.id}">${gmsMessageInstance.subject}</g:link></td>
						
						<td style="text-align:center">${gmsMessageInstance.recipients.size()}</td>
						
						<td style="text-align:center">${gmsMessageInstance.status}</td>
						
						<td><g:formatDate date="${gmsMessageInstance.sendTime}" format="MM/dd HH:mm"/></td>
						
						<td><g:formatDate date="${gmsMessageInstance.sentTime}" format="MM/dd HH:mm"/></td>
					
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
