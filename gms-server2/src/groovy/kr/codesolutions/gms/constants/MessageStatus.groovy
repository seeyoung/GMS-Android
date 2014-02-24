package kr.codesolutions.gms.constants

public enum MessageStatus {
	DRAFT('DRAFT'), SUBMIT('SUBMIT'), PUBLISHING('PUBLISHING'), WAITING('WAITING'), SENDING('SENDING'), SENT('SENT'), COMPLETED('COMPLETED'), TERMINATED('TERMINATED'), FAILED('FAILED')
	MessageStatus(String value) { this.value = value }
	private final String value
	public String value() { return value }
}
