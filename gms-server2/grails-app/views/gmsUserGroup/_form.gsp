<%@ page import="kr.codesolutions.gms.GmsUserGroup" %>


<div class="fieldcontain ${hasErrors(bean: gmsUserGroupInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="gmsUserGroup.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" value="${gmsUserGroupInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsUserGroupInstance, field: 'filter', 'error')} required">
	<label for="filter">
		<g:message code="gmsUserGroup.filter.label" default="Filter" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="filter" value="${gmsUserGroupInstance?.filter}"/>
</div>
