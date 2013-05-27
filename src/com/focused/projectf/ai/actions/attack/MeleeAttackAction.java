package com.focused.projectf.ai.actions.attack;

import com.focused.projectf.entities.Unit;
import com.focused.projectf.interfaces.IEntity;

public abstract class MeleeAttackAction<T extends IEntity> extends AttackAction<T> {

	public static final int MODE_CHASE		= 0;
	public static final int MODE_STRIKE		= 1;
	
	protected float BeginChasingDistance, StopChasingDistance;
	
	protected int ActMode;
	
	public MeleeAttackAction(Unit unit, T target) {
		super(unit, target);
		setTarget(target);
		ActMode = MODE_CHASE;
	}
}
