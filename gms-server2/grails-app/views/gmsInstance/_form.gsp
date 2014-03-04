<%@ page import="kr.codesolutions.gms.GmsInstance" %>



<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'instanceId', 'error')} required">
	<label for="instanceId">
		<g:message code="gmsInstance.instanceId.label" default="Instance Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="instanceId" from="${1..99}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'instanceId')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'host', 'error')} ">
	<label for="host">
		<g:message code="gmsInstance.host.label" default="Host" />
		
	</label>
	<g:textField name="host" maxlength="20" value="${gmsInstanceInstance?.host}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'port', 'error')} required">
	<label for="port">
		<g:message code="gmsInstance.port.label" default="Port" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="port" type="number" value="${gmsInstanceInstance.port}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'autoStart', 'error')} ">
	<label for="autoStart">
		<g:message code="gmsInstance.autoStart.label" default="Auto Start" />
		
	</label>
	<g:checkBox name="autoStart" value="${gmsInstanceInstance?.autoStart}" />
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'channels', 'error')} ">
	<label for="channels">
		<g:message code="gmsInstance.channels.label" default="Channels" />
		
	</label>
	<g:textField name="channels" maxlength="6" value="${gmsInstanceInstance?.channels}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'queueSize', 'error')} required">
	<label for="queueSize">
		<g:message code="gmsInstance.queueSize.label" default="Queue Size" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="queueSize" from="${100..1000}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'queueSize')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'distributeIntervalSeconds', 'error')} required">
	<label for="distributeIntervalSeconds">
		<g:message code="gmsInstance.distributeIntervalSeconds.label" default="Distribute Interval Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="distributeIntervalSeconds" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'distributeIntervalSeconds')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'publishIntervalSeconds', 'error')} required">
	<label for="publishIntervalSeconds">
		<g:message code="gmsInstance.publishIntervalSeconds.label" default="Publish Interval Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="publishIntervalSeconds" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'publishIntervalSeconds')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'collectIntervalSeconds', 'error')} required">
	<label for="collectIntervalSeconds">
		<g:message code="gmsInstance.collectIntervalSeconds.label" default="Collect Interval Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="collectIntervalSeconds" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'collectIntervalSeconds')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'postIntervalSeconds', 'error')} required">
	<label for="postIntervalSeconds">
		<g:message code="gmsInstance.postIntervalSeconds.label" default="Post Interval Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="postIntervalSeconds" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'postIntervalSeconds')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'sendIntervalSeconds', 'error')} required">
	<label for="sendIntervalSeconds">
		<g:message code="gmsInstance.sendIntervalSeconds.label" default="Send Interval Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="sendIntervalSeconds" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'sendIntervalSeconds')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'terminateIntervalSeconds', 'error')} required">
	<label for="terminateIntervalSeconds">
		<g:message code="gmsInstance.terminateIntervalSeconds.label" default="Terminate Interval Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="terminateIntervalSeconds" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'terminateIntervalSeconds')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'preserveDays', 'error')} required">
	<label for="preserveDays">
		<g:message code="gmsInstance.preserveDays.label" default="Preserve Days" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="preserveDays" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'preserveDays')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'resendPendingSeconds', 'error')} required">
	<label for="resendPendingSeconds">
		<g:message code="gmsInstance.resendPendingSeconds.label" default="Resend Pending Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="resendPendingSeconds" from="${0..120}" class="range" required="" value="${fieldValue(bean: gmsInstanceInstance, field: 'resendPendingSeconds')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsInstanceInstance, field: 'completeIntervalSeconds', 'error')} required">
	<label for="completeIntervalSeconds">
		<g:message code="gmsInstance.completeIntervalSeconds.label" default="Complete Interval Seconds" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="completeIntervalSeconds" type="number" value="${gmsInstanceInstance.completeIntervalSeconds}" required=""/>
</div>
