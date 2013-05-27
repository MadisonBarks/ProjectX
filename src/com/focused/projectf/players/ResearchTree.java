package com.focused.projectf.players;

import java.util.Vector;

public class ResearchTree {
	
	protected Vector<String> completedResearch = new Vector<String>();
	public boolean isCompleted(String research) { 
		return completedResearch.contains(research); 
	}
	public void setCompleted(String research) { 
		completedResearch.add(research);
	}
	public void setNotCompleted(String research) { 
		completedResearch.remove(research);
	}
}
