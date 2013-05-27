package com.focused.projectf;

import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.entities.units.RangedMilitaryUnit;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.players.Player;

/**
 * Lots and lots and lots of constants, for use throughout the program
 * @author Austin Bolstridge
 * @author Josh Malezewski
 *
 */
public class Consts {
	public static final int TILE_WIDTH			= 100;
	public static final int TILE_HEIGHT 			= 50;
	public static final int TILE_HALF_WIDTH 		= TILE_WIDTH / 2;
	public static final int TILE_HALF_HEIGHT 		= TILE_HEIGHT / 2;
	
	/**--------------Multiplayer Communication Constants-------------*/
	/**
	 * Sent when a user leaves a server. 
	 * Always in a broadcast message.
	 * @Path Server to Client
	 */
	public static final int SERVER_USER_LEFT = 1;
	/**
	 * Sent when a user enters a server. 
	 * Always in a broadcast message.
	 * @Path Server to Client
	 */
	public static final int SERVER_USER_ENTERED = 2;
	/**
	 * Sent when a computer asks for the list of users on a server.
	 * Always a message GOING TO the server, but it's actually from the server.
	 * @Path Server to Client
	 */
	public static final int SERVER_USER_LIST = 3;
	/**
	 * Sent when a new user joins, and it requests the map for the server.
	 * Always a message GOING TO the server, but it's actually from the server.
	 * @Path Server to Client
	 */
	public static final int SERVER_MAP_DATA = 4;
	/**
	 * Sent when a user creates an entity, and it reaches the server.
	 * Always in a broadcast message.
	 * @Path Server to Client
	 */
	public static final int SERVER_ENTITY_CREATED = 5;
	/**
	 * Sent when a user removes an entity, and it reaches the server.
	 * Always in a broadcast message.
	 * @Path Server to Client
	 */
	public static final int SERVER_ENTITY_REMOVED = 6;
	/**
	 * Sent when the client wants the map data.
	 * NOTICE: Must be sent as a message to the server
	 * @Path Client to Server
	 */
	public static final int CLIENT_MAP_DATA = 7;
	/**
	 * Sent when the user removes an Entity
	 * NOTICE: Must be sent as a broadcast message
	 * @Path Client to Server
	 */
	public static final int CLIENT_CONNECTING = 8;
	/**
	 * Sent when the user removes an Entity
	 * NOTICE: Must be sent as a broadcast message
	 * @Path Client to Server
	 */
	public static final int CLIENT_DISCONNECTING = 9;
	/**
	 * Sent when the user creates a new Entity
	 * NOTICE: Must be sent as a broadcast message
	 * @Path Client to Server
	 */
	public static final int CLIENT_NEW_ENTITY = 10;
	/**
	 * Sent when the user removes an Entity
	 * NOTICE: Must be sent as a broadcast message
	 * @Path Client to Server
	 */
	public static final int CLIENT_REMOVE_ENTITY = 11;
	/**
	 * Sent when the user sends a chat message
	 * NOTICE: Must be sent as a broadcast message
	 * @Path Client to Server
	 */
	public static final int CLIENT_CHAT_MESSAGE = 12;
	/**
	 * Sent when the user enters the lobby for the server
	 * NOTICE: Must be sent as a broadcast message
	 * @Path Client to Server
	 */
	public static final int CLIENT_USER_ENTERING = 13;
	/**
	 * Sent when the user leaves the game, whether through a timeout, quitting, or other problems.
	 * NOTICE: Must be sent as a broadcast message
	 * @Path Client to Server
	 */
	public static final int CLIENT_USER_LEAVING = 14;
	/**
	 * ----------------------------------------------------------------------------------------------------
	 * -------------------------------------UNIT TYPES-----------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------
	 */
	public enum Units {
		VILLAGER(101, UnitType.Villager),
		ARCHER(200, UnitType.Archer),
		MUSKETER(300, UnitType.Musketer);
		
		public int unitType;
		public UnitType type;
		
		Units(int intType, UnitType type) {
			unitType = intType;
			this.type = type;
		}
		
		public Unit createNewInstance(Player player, Point pos) {
			if(unitType == 101) {
				return new Villager(player, pos);
			}
			else if(unitType < 300 && unitType >= 102) {
				return null;
			}
			else if(unitType < 300 && unitType >= 200) {
				return new RangedMilitaryUnit(player, pos, type);
			}
			else if(unitType < 400 && unitType >= 300) {
				return null;
			}
			else {
				return null;
			}
		}
	}
	/*
	 * -----------------------------------------------------------------------------------------------
	 * ---------------------------------------ENTITY TYPES--------------------------------------------
	 * -----------------------------------------------------------------------------------------------
	 */
	public static final int ENTITY_TYPE_UNIT = 100;
	public static final int ENTITY_TYPE_BUILDING = 1000;
	public static final int ENTITY_TYPE_MAPELEMENT = 1100;
	public static final int ENTITY_TYPE_OTHER = 1200;
}
