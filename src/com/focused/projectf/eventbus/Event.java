package com.focused.projectf.eventbus;

import java.util.ArrayList;

public class Event {
	private EventType type;
	private ArrayList<Object> data;
	public Event(EventType type) {
		this.type = type;
		data = new ArrayList<Object>();
	}
	
	public void addData(Object dataPiece) {
		data.add(dataPiece);
	}
	public void addData(Object[] data) {
		for(Object dataPiece : data) {
			this.data.add(dataPiece);
		}
	}
	public EventType getEventType() {
		return type;
	}
}
