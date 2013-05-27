package com.focused.projectf.interfaces;

import java.io.ObjectInputStream;
/**
 * The common receiver of Internet messages. You must register all receivers in 
 * <code>NetworkManager.registerMessageReciever()</code>
 */
public interface IMessageReciever {
	public void recieveMessage(short messageType, ObjectInputStream stream);
}
