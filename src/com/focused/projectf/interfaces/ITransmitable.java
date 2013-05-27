package com.focused.projectf.interfaces;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ITransmitable<T> {
	
	public static final byte TYPE_CHAT_MESSAGE				= 0;
	
	public static final byte TYPE_UNIT_MOVEMENT				= 1;
	public static final byte TYPE_UNIT_GROUP_MOVEMENT			= 2;
	public static final byte TYPE_UNIT_CREATED				= 3;
	
	public static final byte TYPE_RESEARCH_COMPLETE			= 17;
	
	/** a unique id for the type of message object being sent. Make sure each one is unique  */
	public byte getTypeId();
	public T readObjectFromStream(ObjectInputStream stream);
	public void writeObjectToStream(ObjectOutputStream stream);
}
