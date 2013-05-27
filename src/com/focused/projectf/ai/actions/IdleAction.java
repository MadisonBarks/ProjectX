package com.focused.projectf.ai.actions;

import com.focused.projectf.ai.Action;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.TimeKeeper;

public class IdleAction extends Action<Unit> {

	public IdleAction(Unit unit) {
		super(unit);
	}

	@Override
	public void startAction() {
	}

	@Override
	public void updateUnit(float elapsed) {
	}

	@Override
	public void draw(Unit unit) {
	}

	@Override
	public boolean holdPosition() { return false; }

	@Override
	public void setState(FlareUnitAnimation img) {
		int frame = (TimeKeeper.getAnimMS() / 250) % 4;
		img.setState(0, Unit.Direction, frame);
	}
}