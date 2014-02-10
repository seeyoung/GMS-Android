
<%@ page import="kr.codesolutions.gms.GmsUser" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="popup">
		<g:set var="entityName" value="${message(code: 'gmsUser.label', default: 'GmsUser')}" />
		<title><g:message code="default.add.label" args="[entityName]" /></title>
		
		<r:script disposition="head">
			$( document ).ready(function() {
				$("input[name='userId']").focus();
			});
			function addUser(userId){
				opener.addUser(userId)
			}			
		</r:script>
		
	</head>
	<body >
		<div id="list-gmsUser" class="content scaffold-list" role="main">
			<div class="message" role="status">
			<g:if test="${flash.message}">
				${flash.message}
			</g:if>
			<g:else>
				<g:message code="gmsUserGroup.user.add.dblClick.message" default="Double Click an user to add." /><br/>
			</g:else>
			</div>
			
			<g:form name="searchForm" action="searchUser" method="PUT" >
			<fieldset style="text-align:left; background-color:#ffffff;">
				<label for="userId">
					<g:message code="gmsUser.userId.label" default="User ID" />
				</label>
				<g:textField name="userId" value="${params.userId}" style="width:100px" />
				<label for="name">
					<g:message code="gmsUser.name.label" default="Name" />
				</label>
				<g:textField name="name" value="${params.name}" style="width:100px" />
				<label for="phoneNumber">
					<g:message code="gmsUser.phoneNumber.label" default="Phone Number" />
				</label>
				<g:textField name="phoneNumber" value="${params.phoneNumber}" style="width:100px" />
			</fieldset>
			<fieldset class="buttons buttons-whitebar" style="text-align:right; background-color:#ffffff;">
				<g:actionSubmit class="search" action="searchUser" value="${message(code:'default.button.search.label', default:'Search')}" />
			</fieldset>
			</g:form>
			
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="userId" title="${message(code: 'gmsUser.userId.label', default: 'User ID')}"/>

						<g:sortableColumn property="name" title="${message(code: 'gmsUser.name.label', default: 'Name')}"/>

						<g:sortableColumn property="phoneNumber" title="${message(code: 'gmsUser.phoneNumber.label', default: 'Phone Number')}"/>

						<g:sortableColumn property="enabled" title="${message(code: 'gmsUser.enabled.label', default: 'Enabled')}"/>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${gmsUserInstanceList}" status="i" var="gmsUserInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}" onDblClick="addUser('${fieldValue(bean: gmsUserInstance, field: "userId")}')">
					
						<td>${fieldValue(bean: gmsUserInstance, field: "userId")}</td>
					
						<td>${fieldValue(bean: gmsUserInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: gmsUserInstance, field: "phoneNumber")}</td>
					
						<td style="text-align:center"><g:checkBox name="enabled" value="${gmsUserInstance.enabled}" disabled="disabled" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination" style="height:30px">
				<g:paginate total="${gmsUserInstanceCount ?: 0}" />
			</div>

				<fieldset class="buttons" style="text-align:center;">
					<a class="close" href="javascript:window.close();"><g:message code="default.button.close.label" default="Close" /></a>
				</fieldset>
		</div>
	</body>
</html>
