
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
				<li><g:link class="create" action="create"><g:message code="default.compose.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-gmsMessage" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			<g:form url="[action:'index']" method="PUT" >
			<fieldset style="text-align:left; background-color:#ffffff;">
				<label for="status">
					<g:message code="gmsMessage.status.label" default="Status" />
				</label>
				<g:select name="status" from="${kr.codesolutions.gms.constants.MessageStatus}" optionKey="value" optionValue="value" value="${params?.status}" noSelection="['':'-Choose one-']"/>
			</fieldset>
			<fieldset class="buttons buttons-whitebar" style="text-align:right; background-color:#ffffff;">
				<g:actionSubmit class="search" action="index" value="${message(code:'default.button.search.label', default:'Search')}" />
			</fieldset>
			</g:form>
			
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="subject" title="${message(code: 'gmsMessage.subject.label', default: 'Subject')}" />
					
						<g:sortableColumn property="status" title="${message(code: 'gmsMessage.status.label', default: 'Status')}" />

						<g:sortableColumn property="submitTime" title="${message(code: 'gmsMessage.submitTime.label', default: 'Submit Time')}" />
					
						<g:sortableColumn property="reservationTime" title="${message(code: 'gmsMessage.reservationTime.label', default: 'Reservation Time')}" />
					
						<g:sortableColumn property="recipientCount" title="${message(code: 'gmsMessage.recipientCount.label', default: 'Recipients')}" />
						
						<g:sortableColumn property="sentCount" title="${message(code: 'gmsMessage.sentCount.label', default: 'Sents')}" />
						
						<g:sortableColumn property="sentTime" title="${message(code: 'gmsMessage.sentTime.label', default: 'Sent Time')}" />
					
						<g:sortableColumn property="readTime" title="${message(code: 'gmsMessage.readTime.label', default: 'Read Time')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsMessageInstanceList}" status="i" var="gmsMessageInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${gmsMessageInstance.id}">${gmsMessageInstance.subject}</g:link></td>
					
						<td style="text-align:center">${gmsMessageInstance.status}</td>
					
						<td style="text-align:center"><g:formatDate date="${gmsMessageInstance.submitTime}"  format="MM/dd HH:mm:ss" /></td>
					
						<td style="text-align:center"><g:formatDate date="${gmsMessageInstance.reservationTime}"  format="MM/dd HH:mm:ss" /></td>
					
						<td style="text-align:center"><g:formatNumber number="${gmsMessageInstance.recipientCount}" format="###,###,##0" /></td>
					
						<td style="text-align:center"><g:formatNumber number="${gmsMessageInstance.sentCount}" format="###,###,##0" /></td>
					
						<td style="text-align:center"><g:formatDate date="${gmsMessageInstance.sentTime}"  format="MM/dd HH:mm:ss" /></td>
					
						<td style="text-align:center"><g:formatDate date="${gmsMessageInstance.readTime}"  format="MM/dd HH:mm:ss" /></td>
					
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
