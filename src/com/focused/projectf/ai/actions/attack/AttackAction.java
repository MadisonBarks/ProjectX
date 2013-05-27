package com.focused.projectf.ai.actions.attack;

import com.focused.projectf.ai.Action;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.interfaces.IDamageable;
import com.focused.projectf.interfaces.IEntity;

public abstract class AttackAction<T extends IEntity> extends Action<Unit> {

	protected T Target;
	public float AttackCooldown;
	public AttackAction(Unit unit, T target) {
		super(unit);
		setTarget(target);
	}
	@Override
	public boolean holdPosition() { return false; }

	@Override
	public void draw(Unit unit) { }

	public T getTarget() {
		return Target;
	}
	
	public void setTarget(T newTarget) {
		Target = newTarget;
	}
	
	public static AttackAction<?> create(Unit attacker, IDamageable target) {
		if(attacker.getType().isRanged) {
			if(target instanceof Building) 
				return new RangedAttackBuildingAction(attacker, (Building)target);
			
			if(target instanceof Unit)
				return new RangedAttackUnitAction(attacker, (Unit)target);
			
			throw new Error("Unimplemented attack target");
			
		} else {
			if(target instanceof Building) 
				return new MeleeAttackBuildingAction(attacker, (Building)target);
			
			if(target instanceof Unit)
				return new MeleeAttackUnitAction(attacker, (Unit)target);
			
			throw new Error("Unimplemented attack target");
		}
	}
}
