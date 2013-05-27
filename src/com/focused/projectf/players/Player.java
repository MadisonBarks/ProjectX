package com.focused.projectf.players;

import java.util.ArrayList;

import com.focused.projectf.entities.ControllableEntity;
import com.focused.projectf.utilities.random.Chance;

/**
 * Describes a single user.
 * @author josh
 *
 */
public class Player extends Controller {

	public static ArrayList<Player> ConnectedPlayers = new ArrayList<Player>();
	public static Player thisMachinesPlayer = new Player("Local " + Chance.nextInt(100), Team.Team1);
	public static Player debuggingEnemy = new Player("Debugging Enemy CPU", Team.Team2);
	
	public String Name;
	public byte PlayerId;

	public int Food		= 1000;
	public int Wood		= 1000;
	public int Gold		= 1000;
	public int Stone	= 1000;
	public int Radium	= 200;

	public int getPing() {
		return 0;
	}
	
	public final boolean OnThisMachine() { 
		return thisMachinesPlayer == this; 
	}

	public Player(String name, Team team) {
		Name = name;
		MyTeam = team;
		PlayerId = 0;
		ConnectedPlayers.add(this);
		MyTeam.Players.add(this);
	}
	
	public Player(String name, Team team, byte playerID) {
		Name = name;
		MyTeam = team;
		PlayerId = playerID;
		ConnectedPlayers.add(this);
		MyTeam.Players.add(this);
	}

	public void disconnect() {
		
	}

	public static Player getThisPlayer() { return thisMachinesPlayer; }

	public static Player findPlayerById(byte id) {
		for(Player p : ConnectedPlayers)
			if(p.PlayerId == id)
				return p;

		return null;
	}

	public static Player getDebuggingEnemyPlayer() {
		return debuggingEnemy;
	}

	public static boolean canIControl(ControllableEntity entity) {
		return entity.getOwner() == Player.getThisPlayer();
	}

	public DiplomacyState getDiplomacyWith(Player me, Player them) {
		if(me.equals(them))
			return DiplomacyState.SameOwner;
		if(me.MyTeam == them.MyTeam)
			return DiplomacyState.Ally;
		
		return me.MyTeam.getDiplomacy(MyTeam);
	}

	public enum DiplomacyState {
		Enemy			(true),
		Neutral			(true),
		Ally			(false),
		SameOwner		(false),
		Unknown			(false), 
		;
		public final boolean CanAttack;
		DiplomacyState(boolean canAttack) {
			CanAttack = canAttack;
		}
	}

	
	public DiplomacyState getDiplomacy() {
		return getDiplomacyWith(Player.getThisPlayer());
	}
}