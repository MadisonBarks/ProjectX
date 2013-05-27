package com.focused.projectf.multiplayer;

import java.io.Serializable;

/**
 * A nice helper class that allows us to send stuff to and fro the server in one class, that can be turned into a byte buffer
 * @author Austin Bolstridge
 *
 */
public class Action implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3639415148786429694L;
	private String type;
	private Object[] data;
	private String user = "broadcast";
	
	public Action(String type, Object[] data) {
		this.type = type;
		this.data = data;
	}
	public Action(String type, Object[] data, String user) {
		this.type = type;
		this.data = data;
		this.user = user;
	}
	public String getActionType() {
		return type;
	}
	public Object[] getActionData() {
		return data;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getActionUser() {
		return user;
	}
}
