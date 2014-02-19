package kr.codesolutions.gms.constants

public enum SendType {
	GCM('GCM'), SMS('SMS'), EMAIL('EMAIL')
	SendType(String value) { this.value = value }
	private final String value
	public String value() { return value }
}
