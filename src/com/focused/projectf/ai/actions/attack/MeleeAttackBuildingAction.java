package com.focused.projectf.ai.actions.attack;

import com.focused.projectf.ai.actions.MoveToAction;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.resources.images.FlareUnitAnimation;

public class MeleeAttackBuildingAction extends MeleeAttackAction<Building> {

	public MeleeAttackBuildingAction(Unit unit, Building bld) {
		super(unit, bld);
	}

	@Override
	public void startAction() {
		if(Target.getBounds().getBorderingPoint(Unit.getPosition(), Unit.getType().Size * 1.25f).distSq(Unit.getPosition()) 
				> Unit.getType().Size * Unit.getType().Size * 1.25f * 1.25f) {
			Unit.getActionStack().add(new MoveToAction(Unit, Target));
		}
		
	}

	@Override
	public void setState(FlareUnitAnimation img) {
		
		
	}

	@Override
	public void updateUnit(float elapsed) {
		
	}
}
