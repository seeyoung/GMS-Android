<%@ page import="kr.codesolutions.gms.GmsUser" %>

<div class="fieldcontain ${hasErrors(bean: gmsUserInstance, field: 'userId', 'error')} required">
	<label for="userId">
		<g:message code="gmsUser.userId.label" default="User Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="userId" value="${gmsUserInstance?.userId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsUserInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="gmsUser.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" value="${gmsUserInstance?.name}" />
</div>

<div class="fieldcontain ${hasErrors(bean: gmsUserInstance, field: 'phoneNumber', 'error')} required">
	<label for="phoneNumber">
		<g:message code="gmsUser.phoneNumber.label" default="Phone Number" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="phoneNumber" value="${gmsUserInstance?.phoneNumber}" />
</div>

<div class="fieldcontain ${hasErrors(bean: gmsUserInstance, field: 'registrationId', 'error')} required">
	<label for="registrationId">
		<g:message code="gmsUser.registrationId.label" default="Registration ID" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="registrationId" value="${gmsUserInstance?.registrationId}" rows="5" cols="40"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsUserInstance, field: 'email', 'error')} required">
	<label for="email">
		<g:message code="gmsUser.email.label" default="Email" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="email" value="${gmsUserInstance?.email}"/>
</div>
 
<div class="fieldcontain ${hasErrors(bean: gmsUserInstance, field: 'isSendable', 'error')} required">
	<label for="isSendable">
		<g:message code="gmsUser.isSendable.label" default="Sendable" />
		<span class="required-indicator">*</span>
	</label>
	<g:checkBox name="isSendable" value="${gmsUserInstance?.isSendable}" />
</div>

<div class="fieldcontain ${hasErrors(bean: gmsUserInstance, field: 'enabled', 'error')} required">
	<label for="enabled">
		<g:message code="gmsUser.enabled.label" default="Enabled" />
		<span class="required-indicator">*</span>
	</label>
	<g:checkBox name="enabled" value="${gmsUserInstance?.enabled}" />
</div>
