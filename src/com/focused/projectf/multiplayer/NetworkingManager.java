package com.focused.projectf.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.utilities.Serializer;

/**
 * A class that manages many network-related functions. Please use whenever possible.
 * @author Austin Bolstridge
 *
 */
public class NetworkingManager {
	private InetAddress host;
	private DatagramSocket socket;
	private boolean connected;
	
	public NetworkingManager(String host) {
		try {
			this.host = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			ErrorManager.logFatal("Host can not be found", e);
		}
		try {
			socket = new DatagramSocket();
		} catch (IOException e) {
			ErrorManager.logFatal("Error creating a new socket", e);
		}
		if(socket.isConnected()) {
			connected = true;
		}
	}
	public void sendAction(Action action) {
		try {
			byte[] buf = Serializer.serialize(action);
			socket.send(new DatagramPacket(buf, buf.length, host, 15635));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public DatagramSocket getSocket() {
		return socket;
	}
	public void setAddress(String address) {
		try {
			host = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendActionToUser(Action action, String user) {
		action.setUser(user);
		
		try {
			byte[] buf = Serializer.serialize(action);
			socket.send(new DatagramPacket(buf, buf.length, host, 15635));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public InetAddress getConnectionIP() {
		return host;
	}
	
	public void shutDown() {
		socket.close();
	}
}