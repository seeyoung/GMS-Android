
<%@ page import="kr.codesolutions.gms.GmsMessage" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsMessageInBox.message.label', default: 'In Box')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-gmsMessageInBox" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-gmsMessageInBox" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="message.isRead" title="${message(code: 'gmsMessage.isRead.label', default: 'Read')}" style="width:50px;"/>
					
						<g:sortableColumn property="message.sender" title="${message(code: 'gmsMessage.sender.label', default: 'Sender')}" style="width:100px;"/>
					
						<g:sortableColumn property="message.subject" title="${message(code: 'gmsMessage.subject.label', default: 'Subject')}" style="text-align:left;"/>
					
						<g:sortableColumn property="message.sentTime" title="${message(code: 'gmsMessageInBox.receiveTime.label', default: 'Receive Time')}" style="width:130px;"/>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsMessageInstanceList}" status="i" var="gmsMessageInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td style="text-align:center"><g:checkBox name="isRead" value="${gmsMessageInstance.isRead}" disabled="disabled" /></td>
					
						<td style="text-align:center">${gmsMessageInstance.sender.name}</td>
						
						<td><g:link action="show" id="${gmsMessageInstance.id}">${gmsMessageInstance.subject}</g:link></td>
						
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
