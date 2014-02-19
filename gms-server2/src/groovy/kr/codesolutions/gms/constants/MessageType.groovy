package kr.codesolutions.gms.constants

public enum MessageType {
	NORMAL('NORMAL'), MASS('MASS')
	MessageType(String value) { this.value = value }
	private final String value
	public String value() { return value }
}
