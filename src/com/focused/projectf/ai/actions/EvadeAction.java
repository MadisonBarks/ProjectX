package com.focused.projectf.ai.actions;

import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.ai.Action;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.interfaces.IEntity;
public class EvadeAction extends Action<Unit> {

	public IEntity Evade;
	
	public EvadeAction(Unit unit, IEntity evade) {
		super(unit);
		Evade = evade;
		
	}

	@Override
	public boolean holdPosition() { return false; }

	@Override
	public void startAction() {
		
	}

	@Override
	public void updateUnit(float elapsed) {
		
	}

	@Override
	public void setState(FlareUnitAnimation img) {
		
	}
}
