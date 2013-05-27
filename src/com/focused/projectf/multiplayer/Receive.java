package com.focused.projectf.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.utilities.Serializer;

/**
 * A nice helper class that allows us to spawn a new thread to receive some stuff. 
 * Since, really, it blocks until it receives something.
 * @author Austin Bolstridge
 * @category Multiplayer Server Communication
 *
 */
public class Receive implements Runnable{
	private NetworkingManager netManager;
	/**
	 * Only set if something was received.
	 */
	private Action actionReceived;
	public Receive(NetworkingManager manager) {
		netManager = manager;
	}
	@Override
	public void run() {
		DatagramSocket socket = netManager.getSocket();
		byte[] buffer = new byte[4096];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		Object object = null;
		try {
			socket.receive(packet);
			object = Serializer.deserialize(packet.getData());
		} catch (IOException e) {
			ErrorManager.logWarning("Problem while receiving the packet", e);
		} catch (ClassNotFoundException e) {
			ErrorManager.logWarning("Error in deserializing the data", e);
		}
		if(object instanceof Action) {
			Action action = (Action) object;
			actionReceived = action;
		}
	}
	public Action getReceivedAction() {
		return actionReceived;
	}
}
