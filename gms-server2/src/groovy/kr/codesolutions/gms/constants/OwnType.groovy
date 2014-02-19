package kr.codesolutions.gms.constants

public enum OwnType {
	PERSONAL('PERSONAL'), PUBLIC('PUBLIC')
	OwnType(String value) { this.value = value }
	private final String value
	public String value() { return value }
}
