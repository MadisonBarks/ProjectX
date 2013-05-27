package com.focused.projectf.ai;


/**
 * A basic describer of all units for a given player
 */
public class UnitStats {
	
	public int MaxHealth = 100;
	public float Speed;
	public float Attack;
	public float Defense;
	public float Range;
	public float AttackCooldown;
	public float RangeOfSight;
	
	public UnitStats() {
		Speed = 40;
		Attack = 5;
		Defense = 2;
		AttackCooldown = 0.6f;
		Range = 0;
		RangeOfSight = 6;
	}
}
