package com.focused.projectf.entities.units;

import com.focused.projectf.Point;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.players.Player;

public class GroundMilitaryUnit extends Unit {
	
	public GroundMilitaryUnit(Player owner, Point position, UnitType type) {
		super(owner, position, type);
	}

	@Override
	public void onSelected() { }
	@Override
	public void onDeselected() { }
	@Override
	public void draw() { super.draw(); }
	@Override
	public void remove() { super.remove(); }

	@Override
	public void drawSelected() {
		super.drawSelected();
	}
}
