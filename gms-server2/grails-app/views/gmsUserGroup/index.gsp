
<%@ page import="kr.codesolutions.gms.GmsUserGroup" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsUserGroup.label', default: 'GmsUserGroup')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		
		<r:script disposition="head">
			$( document ).ready(function() {
				$("input[name='name']").focus();
			});
		</r:script>
		
	</head>
	<body>
		<a href="#list-gmsUserGroup" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-gmsUserGroup" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			<g:form url="[action:'index']" method="PUT" >
			<fieldset style="text-align:left; background-color:#ffffff;">
				<label for="name">
					<g:message code="gmsUserGroup.name.label" default="Group Name" />
				</label>
				<g:textField name="name" value="${params.name}" />
			</fieldset>
			<fieldset class="buttons buttons-whitebar">
				<g:actionSubmit class="search" action="index" value="${message(code:'default.button.search.label', default:'Search')}" />
			</fieldset>
			</g:form>
			
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'gmsUserGroup.name.label', default: 'Group Name')}" />
					
						<g:sortableColumn property="filter" title="${message(code: 'gmsUserGroup.filter.label', default: 'Filter')}" />
					
						<g:sortableColumn property="modifiedTime" title="${message(code: 'gmsUserGroup.modifiedTime.label', default: 'Modified Time')}" />
					
						<g:sortableColumn property="createdTime" title="${message(code: 'gmsUserGroup.createdTime.label', default: 'Created Time')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsUserGroupInstanceList}" status="i" var="gmsUserGroupInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${gmsUserGroupInstance.id}">${fieldValue(bean: gmsUserGroupInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: gmsUserGroupInstance, field: "filter")}</td>

						<td><g:formatDate date="${gmsUserGroupInstance.createdTime}" format="MM/dd HH:mm:ss" /></td>
					
						<td><g:formatDate date="${gmsUserGroupInstance.modifiedTime}" format="MM/dd HH:mm:ss" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${gmsUserGroupInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
