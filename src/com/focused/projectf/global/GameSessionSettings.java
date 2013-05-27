package com.focused.projectf.global;

import java.io.Serializable;

public class GameSessionSettings implements Serializable {

	private static final long serialVersionUID = -5090779593998237869L;

	public VictoryCondition Victory = VictoryCondition.Conquest;
	
	public enum VictoryCondition {
		Conquest		(),
		Score			(),
	}
}