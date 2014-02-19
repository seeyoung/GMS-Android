package kr.codesolutions.gms.constants

public enum MessageStatus {
	DRAFT('DRAFT'), PUBLISHING('PUBLISHING'), WAITING('WAITING'), SENDING('SENDING'), SENT('SENT'), COMPLETED('COMPLETED'), TERMINATED('TERMINATED')
	MessageStatus(String value) { this.value = value }
	private final String value
	public String value() { return value }
}
