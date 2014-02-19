<%@ page import="kr.codesolutions.gms.GmsMessage" %>

<g:hiddenField name="userId" value="admin"/>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'recipients', 'error')} required">
	<label for="recipients">
		<g:message code="gmsMessage.recipients.label" default="Recipients" />
		<span class="required-indicator">*</span>
	</label>
	
	<g:select name="recipientGroup" from="${kr.codesolutions.gms.GmsUserGroup.findAll{members.size() > 0 && enabled == true}}" optionKey="id" optionValue="name" noSelection="['':'']" class="many-to-one"/>
	<g:textField name="recipientId" value="${gmsMessageInstance?.recipients*.userId}" onKeyDown="return false;"/>
	<a id="buttonSearch" class="buttons add" href="javascript:openSearchWindow('${createLink(action:'searchUser')}','SearchWindow');"><g:message code="default.button.add.label" default="Add" /></a>

</div>

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'sender', 'error')} required">
	<label for="sender">
		<g:message code="gmsMessage.sender.label" default="Sender" />
		<span class="required-indicator">*</span>
	</label>
	
	<g:select name="senderId" from="${kr.codesolutions.gms.GmsUser.findAllByIsSendable(true)}" optionKey="userId" optionValue="name" required="" value="${gmsMessageInstance?.sender?.userId}" class="many-to-one"/>

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

<div class="fieldcontain ${hasErrors(bean: gmsMessageInstance, field: 'reservationTime', 'error')} ">
	<label for="reservationTime">
		<g:message code="gmsMessage.reservationTime.label" default="Reservation Time" />
		
	</label>
	<jqueryPicker:time name="reservationTime" value="${gmsMessageInstance?.reservationTime}" />
</div>


