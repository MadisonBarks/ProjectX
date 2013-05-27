package com.focused.projectf.events;

import java.io.Serializable;

import com.focused.projectf.Point;

public class Event implements Serializable {
	
	private static final long serialVersionUID = 7913027204295763419L;
	
	public EventType Type;
	public transient float DisplayTime;
	public String Message;
	public Point Location;
	public Object eventData;
	public String SoundUrl;
	
	public enum EventType {
		ResearchCompleat,
		UnitProduced,
		Attacking,
		/** Team defeated, Player resigned, diplomacy changed */
		Major,
		/** Farm exhausted, construction completed, etc. */
		Minor,
		Other
	}
}
