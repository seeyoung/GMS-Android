<%@ page import="kr.codesolutions.gms.GmsMessage" %>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'recipientGroup', 'error')} required">
	<label for="recipientGroup">
		<g:message code="gmsMessage.recipientGroup.label" default="Recipient Group" />
		<span class="required-indicator">*</span>
	</label>
	
	<g:select name="recipientGroup" from="${kr.codesolutions.gms.GmsUserGroup.list(sort:'name')}" optionKey="id" optionValue="name" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'sender', 'error')} required">
	<label for="sender">
		<g:message code="gmsMessage.sender.label" default="Sender" />
		<span class="required-indicator">*</span>
	</label>
	
	<g:textField name="senderUserId" value="${gmsMessageInstance?.senderUserId}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'subject', 'error')} required">
	<label for="subject">
		<g:message code="gmsMessage.subject.label" default="Subject" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="subject" value="${gmsMessageInstance?.subject}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'content', 'error')} required">
	<label for="content">
		<g:message code="gmsMessage.content.label" default="Content" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="content" value="${gmsMessageInstance?.content}" rows="5" cols="40"/>
</div>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'sendPolicy', 'error')} required">
	<label for="sendPolicy">
		<g:message code="gmsMessage.sendPolicy.label" default="Send Policy" />
		<span class="required-indicator">*</span>
	</label>
	
	<g:select name="sendPolicy" from="${kr.codesolutions.gms.constants.SendPolicy}" optionKey="value" optionValue="value" value="${gmsMessageInstance?.sendPolicy}" class="many-to-one" />

</div>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'reservationTime', 'error')} ">
	<label for="reservationTime">
		<g:message code="gmsMessage.reservationTime.label" default="Reservation Time" />
		
	</label>
	<jqueryPicker:time name="reservationTime" value="${gmsMessageInstance?.reservationTime}" />
</div>


