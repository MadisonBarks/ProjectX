package com.focused.projectf.global;

import java.io.Serializable;

public class UserProfile implements Serializable{

	private static final long serialVersionUID = 4031639731008353643L;

	public transient static UserProfile ActiveProfile = new UserProfile();
	
	public String UserName;
	
	public boolean ShadeTiles = true;

	public int UnitBehindSomethingShadeMode = 1;
	
	public float FXVolume				= 1.0f;
	public float UnitChatterVolume		= 0.7f;
	public float BGMusicVolume			= 0.8f;
	public float MainVolume			= 1.0f;
}
