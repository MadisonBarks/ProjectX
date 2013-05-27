package com.focused.projectf.utilities;

import java.util.HashMap;

public class Locker {
	private HashMap<Object, Boolean> registeredVariables;
	public Locker() {
		registeredVariables = new HashMap<Object, Boolean>();
	}
	public void registerVariable(Object variable) {
		registeredVariables.put(variable, false);
	}
	public boolean lockVariable(Object variable) {
		if(!registeredVariables.containsKey(variable)) {
			return false;
		}
		else {
			//TODO Actually implement
			return true;
		}
	}
}
