
<%@ page import="kr.codesolutions.gms.GmsUserGroup" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'gmsUserGroup.label', default: 'GmsUserGroup')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<r:script disposition="head">
			function openSearchWindow(url, name){
				var searchWindow = window.open(url, name, "width=650,height=600");
			}
			
			function deleteUser(userId){
				$.ajax({
						url: "${createLink(action:'deleteUser',id:gmsUserGroupInstance?.id)}",
						data: {userId: userId},
						success: function(result){
									$("#postMessage").val(result);
     								$("#showForm").submit();
   								}
   						});
			}
			
			function refresh(){
				$("#showForm").submit();
			}
		</r:script>
	</head>
	<body>
		<a href="#show-gmsUserGroup" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-gmsUserGroup" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<div class="message" role="status">
			<g:if id="message" test="${flash.message}">
				${flash.message}
			</g:if>
			<g:else>
				<g:set var="userEntityName" value="${message(code: 'gmsUser.label', default: 'User')}" />
				<g:message code="default.delete.dblClick.message" args="[userEntityName]" default="Double Click an user to delete." /><br/>
			</g:else>
			</div>
			
			<g:form name="showForm" action="show" id="${gmsUserGroupInstance?.id}" method="GET" >
				<g:hiddenField name="postMessage" />
			</g:form>	
					
			<ol class="property-list gmsUserGroup">
			
				<g:if test="${gmsUserGroupInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="gmsUserGroup.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${gmsUserGroupInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsUserGroupInstance?.filter}">
				<li class="fieldcontain">
					<span id="filter-label" class="property-label"><g:message code="gmsUserGroup.filter.label" default="Filter" /></span>
					
						<span class="property-value" aria-labelledby="filter-label"><g:fieldValue bean="${gmsUserGroupInstance}" field="filter"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsUserGroupInstance?.modifiedTime}">
				<li class="fieldcontain">
					<span id="modifiedTime-label" class="property-label"><g:message code="gmsUserGroup.modifiedTime.label" default="Modified Time" /></span>
					
						<span class="property-value" aria-labelledby="modifiedTime-label"><g:formatDate date="${gmsUserGroupInstance?.modifiedTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${gmsUserGroupInstance?.createdTime}">
				<li class="fieldcontain">
					<span id="createdTime-label" class="property-label"><g:message code="gmsUserGroup.createdTime.label" default="Created Time" /></span>
					
						<span class="property-value" aria-labelledby="createdTime-label"><g:formatDate date="${gmsUserGroupInstance?.createdTime}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:gmsUserGroupInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${gmsUserGroupInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
