package com.focused.projectf.players;

import java.util.Hashtable;
import java.util.Vector;

import com.focused.projectf.graphics.Color;
import com.focused.projectf.players.Player.DiplomacyState;

public class Team {

	public static Vector<Team> Teams = new Vector<Team>();
	public static final Team Team1 = addTeam("Team 1", Color.BLUE);
	public static final Team Team2 = addTeam("Team 2", Color.RED);	

	public String Name;
	public Color MainColor, DarkColor;

	protected Hashtable<Team, DiplomacyState> Diplomacy;
	public Vector<Player> Players;
	public final int Index;
	protected static int pindex = 0;
	/**
	 * Creates a new team and adds it to <code>Team.Teams</code>. Don't add it manually
	 */
	protected Team(String name, Color color) {
		Index = pindex++;

		Players = new Vector<Player>();		
		Name = name;
		MainColor = color;
		DarkColor = color.darken();

		Diplomacy = new Hashtable<Team, DiplomacyState>();

		for(Team t : Teams) {
			Diplomacy.put(t, DiplomacyState.Enemy);
			t.Diplomacy.put(this, DiplomacyState.Enemy);
		}
		Teams.add(this);
	}

	public static Team addTeam(String name, Color color) {
		return new Team(name, color);
	}

	public DiplomacyState getDiplomacy(Team otherTeam) {
		if(otherTeam == this)
			return DiplomacyState.SameOwner;
		
		return Diplomacy.get(otherTeam);
	}
}
