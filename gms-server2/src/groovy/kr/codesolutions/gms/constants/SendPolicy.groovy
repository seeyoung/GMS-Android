package kr.codesolutions.gms.constants

public enum SendPolicy {
	GCM('GCM'), SMS('SMS'), EMAIL('EMAIL'), COMPLEX('COMPLEX'), ADVENCED('ADVENCED')
	SendPolicy(String value) { this.value = value }
	private final String value
	public String value() { return value }
}
