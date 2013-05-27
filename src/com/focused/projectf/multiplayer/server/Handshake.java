package com.focused.projectf.multiplayer.server;

import com.focused.projectf.players.Team;

public class Handshake {
	public String userName;
	public Team userTeam;
	public Handshake(String username, Team team) {
		userName = username;
		userTeam = team;
	}
}
