<%@ page import="kr.codesolutions.gms.GmsUserGroup" %>


<div class="fieldcontain ${hasErrors(bean: gmsUserGroupInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="gmsUserGroup.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" value="${gmsUserGroupInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsUserGroupInstance, field: 'enabled', 'error')} required">
	<label for="enabled">
		<g:message code="gmsUserGroup.enabled.label" default="Enabled" />
		<span class="required-indicator">*</span>
	</label>
	<g:checkBox name="enabled" value="${gmsUserGroupInstance?.enabled}" />
</div>
