
<%@ page import="kr.codesolutions.gms.GmsUser" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsUser.label', default: 'GmsUser')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		
		<r:script disposition="head">
			$( document ).ready(function() {
				$("input[name='userId']").focus();
			});
		</r:script>
		
	</head>
	<body >
		<a href="#list-gmsUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-gmsUser" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			<g:form url="[action:'index']" method="PUT" >
			<fieldset style="text-align:left; background-color:#ffffff;">
				<label for="userId">
					<g:message code="gmsUser.userId.label" default="User ID" />
				</label>
				<g:textField name="userId" value="${params.userId}" />
				<label for="name">
					<g:message code="gmsUser.name.label" default="Name" />
				</label>
				<g:textField name="name" value="${params.name}" />
				<label for="phoneNumber">
					<g:message code="gmsUser.phoneNumber.label" default="Phone Number" />
				</label>
				<g:textField name="phoneNumber" value="${params.phoneNumber}" />
			</fieldset>
			<fieldset class="buttons buttons-whitebar" style="text-align:right; background-color:#ffffff;">
				<g:actionSubmit class="search" action="index" value="${message(code:'default.button.search.label', default:'Search')}" />
			</fieldset>
			</g:form>
			
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="userId" title="${message(code: 'gmsUser.userId.label', default: 'User ID')}"/>

						<g:sortableColumn property="name" title="${message(code: 'gmsUser.name.label', default: 'Name')}"/>

						<g:sortableColumn property="phoneNumber" title="${message(code: 'gmsUser.phoneNumber.label', default: 'Phone Number')}"/>

						<g:sortableColumn property="enabled" title="${message(code: 'gmsUser.enabled.label', default: 'Enabled')}"/>
					
						<g:sortableColumn property="isSendable" title="${message(code: 'gmsUser.isSendable.label', default: 'isSendable')}"/>
					
						<g:sortableColumn property="errorCount" title="${message(code: 'gmsUser.errorCount.label', default: 'Error Count')}"/>
					
						<g:sortableColumn property="modifiedTime" title="${message(code: 'gmsUser.modifiedTime.label', default: 'Modified Time')}"/>

						<g:sortableColumn property="createdTime" title="${message(code: 'gmsUser.createdTime.label', default: 'Created Time')}"/>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsUserInstanceList}" status="i" var="gmsUserInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${gmsUserInstance.id}">${fieldValue(bean: gmsUserInstance, field: "userId")}</g:link></td>
					
						<td>${fieldValue(bean: gmsUserInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: gmsUserInstance, field: "phoneNumber")}</td>
					
						<td style="text-align:center"><g:checkBox name="enabled" value="${gmsUserInstance.enabled}" disabled="disabled" /></td>
					
						<td style="text-align:center"><g:checkBox name="isSendable" value="${gmsUserInstance.isSendable}" disabled="disabled" /></td>
					
						<td style="text-align:center">${fieldValue(bean: gmsUserInstance, field: "errorCount")}</td>
					
						<td><g:formatDate date="${gmsUserInstance.modifiedTime}" format="MM/dd HH:mm:ss"/></td>

						<td><g:formatDate date="${gmsUserInstance.createdTime}" format="MM/dd HH:mm:ss"/></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${gmsUserInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
