package com.focused.projectf.eventbus;

public abstract class EventListener {
	public abstract EventType getSubscribedEvent();
	public abstract void newEvent(Event e);
}
