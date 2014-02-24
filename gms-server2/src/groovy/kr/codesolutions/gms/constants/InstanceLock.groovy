package kr.codesolutions.gms.constants

public enum InstanceLock {
	DISTRIBUTE('DISTRIBUTE'), PUBLISH('PUBLISH'), COLLECT('COLLECT'), POST('POST'), SEND('SEND'), COMPLETE('COMPLETE'), TERMINATE('TERMINATE')
	InstanceLock(String value) { this.value = value }
	private final String value
	public String value() { return value }
}
