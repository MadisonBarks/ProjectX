package com.focused.projectf.eventbus;

import java.util.ArrayList;

public class EventBus {
	private static ArrayList<EventListener> listeners = new ArrayList<EventListener>();
	
	public static void sendEvent(Event e) {
		for(EventListener l : listeners) {
			if(l.getSubscribedEvent() == e.getEventType()) {
				l.newEvent(e);
			}
		}
	}
	public static void subscribe(EventListener l) {
		listeners.add(l);
	}
	public static void unSubscribe(EventListener l) {
		listeners.remove(l);
	}
}
