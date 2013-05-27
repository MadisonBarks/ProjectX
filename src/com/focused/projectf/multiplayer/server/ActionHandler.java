package com.focused.projectf.multiplayer.server;

import java.util.HashMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.focused.projectf.Consts;
import com.focused.projectf.Consts.Units;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.global.ChatLog;
import com.focused.projectf.global.ChatLog.ChatMessage;
import com.focused.projectf.players.Player;

public class ActionHandler extends SimpleChannelHandler {
	private Map gameMap;
	private HashMap<String, Player> players;
	
	public ActionHandler(Map map) {
		gameMap = map;
		players = new HashMap<String, Player>();
	}
	
	static final ChannelGroup channels = new DefaultChannelGroup();
	
	public void channelConnected(ChannelHandlerContext ctx, ChannelEvent e) {
		//TODO Implement a system to send the user to clients that are connected.
		channels.add(e.getChannel());
	}
	
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelEvent e) {
		channels.remove(e.getChannel());
		//TODO Implement a system to send the user to clients that are connected.
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		 if(e.getMessage() instanceof ChatMessage) {
			 parseChatMessage((ChatMessage) e.getMessage());
			 return;
		 }
		 else if(e.getMessage() instanceof Handshake) {
			 handleHandshake((Handshake) e.getMessage());
		 }
		 int[] action = (int[]) e.getMessage();
		 if(action[0] == Consts.CLIENT_MAP_DATA) {
			 sendMapData(ctx, e.getChannel());
		 }
		 else if(action[0] == Consts.CLIENT_NEW_ENTITY || action[0] == Consts.CLIENT_REMOVE_ENTITY) {
			 parseEntity(action, (action[0] == Consts.CLIENT_REMOVE_ENTITY));
		 }
		 else {
			 for(int actionPart : action) {
				 channels.write(actionPart);
			 }
		 }
	}
	
	private void handleHandshake(Handshake message) {
		Player player = new Player(message.userName, message.userTeam, (byte) (Player.ConnectedPlayers.size() + 1));
		if(!players.containsKey(message.userName)) {
			players.put(message.userName, player);
			Player.ConnectedPlayers.add(player);
		}
		channels.write(player);
	}

	private void parseChatMessage(ChatMessage message) {
		ChatLog.Messages.add(message);
		channels.write(ChatLog.Messages);
	}

	private void parseEntity(int[] action, boolean removeEntity) {
		if(removeEntity) {
			gameMap.removeEntity(action[1], action[2]);
			channels.write(new int[] {Consts.SERVER_ENTITY_REMOVED, action[1], 0, 0, 0, 0, 0});
			return;
		}
		if(action[1] == Consts.ENTITY_TYPE_BUILDING) {
			for(BuildingType type : BuildingType.values()) {
				if(type.typeId == action[2]) {
					Building building = new Building(Player.findPlayerById((byte) action[3]), new Point(action[4], action[5]), type);
					gameMap.addEntity(building);
					channels.write(building);
				}
			}
		}
		else if(action[1] == Consts.ENTITY_TYPE_OTHER) {
			
		}
		else {
			for(Units unit : Consts.Units.values()) {
				if(unit.unitType == action[2]) {
					Unit unitNew = unit.createNewInstance(Player.findPlayerById((byte) action[3]), new Point(action[4], action[5]));
					gameMap.addEntity(unitNew);
					channels.write(unitNew);
					break;
				}
			}
		}
	}

	private void sendMapData(ChannelHandlerContext ctx, Channel channel) {
		channel.write(gameMap);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		e.getCause().printStackTrace();
		Channel ch = e.getChannel();
		ch.close();
	}
}
