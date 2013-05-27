package com.focused.projectf;

public enum ResourceType {
	Wood		(0,		"Tree"),
	Food		(1,		"Berry Bush"),
	Gold		(2,		"Gold vain"),
	Stone		(2,		"Rocks"),
	Radium		(2,		"Plutonium"),	 
	None		(-1,	"???????"), 
	;
	public String DepositName;
	private byte MatchNumber;
	ResourceType( int matchNumber, String name) {
		DepositName = name;
		MatchNumber = (byte)matchNumber;
	}
	public boolean matches(ResourceType type) {
		return MatchNumber == type.MatchNumber;
	}
}
