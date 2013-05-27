package com.focused.projectf.entities.units;

import com.focused.projectf.Point;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.players.Player;

/**
 * Describes a unit that can attack other units. this covers rougly 90% of them.
 * @author josh
 *
 */
public class SoldierUnit extends Unit {

	protected int Health;
	
	public SoldierUnit(Player owner, Point position, UnitType type) {
		super(owner, position, type);
		
	}

	@Override
	public int getHealth() { return Health; }

	@Override
	public void damage(float points, DamageType type) {
		Health -= (int)(points * type.InfantryMultiplier);
	}
}
