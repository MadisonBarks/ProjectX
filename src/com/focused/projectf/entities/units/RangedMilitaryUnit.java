package com.focused.projectf.entities.units;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.players.Player;

public class RangedMilitaryUnit extends Unit {

	int health = 100;

	public RangedMilitaryUnit(Player owner, Point position, UnitType type) {
		super(owner, position, type);
	}

	@Override
	public int getHealth() { return health; }
	@Override
	public void damage(float points, DamageType type) {
		health -= (int)(points * type.InfantryMultiplier);
	}

	@Override
	public void draw() {
		super.draw();
				
		Canvas.drawLine(position.plus(-10, -5), position.plus( 10,  5), 2, Color.BLACK);
		Canvas.drawLine(position.plus( 10, -5), position.plus(-10,  5), 2, Color.BLACK);
	}

	@Override
	public void remove() {
		Map.get().removeEntity(this);
	}
}
