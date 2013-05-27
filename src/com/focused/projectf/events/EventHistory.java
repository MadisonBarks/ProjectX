package com.focused.projectf.events;

import java.util.Vector;

import com.focused.projectf.graphics.Canvas;

public class EventHistory {

	private static int EventStackSize = 1000;
	
	private Vector<Event> eventStack;
	
	public void addEvent(Event event) {
		eventStack.add(event);
		if(eventStack.size() > EventStackSize)
			eventStack.remove(eventStack.size() - 1);
	}
	
	public void drawEventStack(Canvas canvas) {
		
	}
}