package com.focused.projectf.players;

import com.focused.projectf.players.Player.DiplomacyState;

/**
 * A player, set of players, or CPU which has control over a group of units. 
 * All units who follow a controller will take actions from it alone.
 */
public abstract class Controller {
	
	public Team MyTeam;
	
	public Team getTeam() {
		return MyTeam;
	}
	public void setTeam(Team newTeam) { 
		MyTeam = newTeam;
	}

	public DiplomacyState getDiplomacyWith(Team otherTeam) {
		return MyTeam.getDiplomacy(otherTeam);
	}
	public DiplomacyState getDiplomacyWith(Controller controller) {
		return MyTeam.getDiplomacy(controller.getTeam());
	}
}
