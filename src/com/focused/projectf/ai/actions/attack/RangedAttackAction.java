package com.focused.projectf.ai.actions.attack;

import com.focused.projectf.entities.Unit;
import com.focused.projectf.interfaces.IEntity;

public abstract class RangedAttackAction<T extends IEntity> extends AttackAction<T> {

	public RangedAttackAction(Unit unit, T target) {
		super(unit, target);
		
	}

	@Override
	public void startAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateUnit(float elapsed) {
		// TODO Auto-generated method stub
		
	}

}
