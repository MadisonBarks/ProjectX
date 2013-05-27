package com.focused.projectf.ai.actions.attack;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.players.Player.DiplomacyState;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.TimeKeeper;

public class MeleeAttackUnitAction extends MeleeAttackAction<Unit> {

	public MeleeAttackUnitAction(Unit unit, Unit target) {
		super(unit, target);
	}

	@Override
	public void startAction() { }

	public void setTarget(Unit target) {
		super.setTarget(target);
		StopChasingDistance = Unit.getType().Size * Unit.getType().Size + Target.getType().Size * Target.getType().Size;
		BeginChasingDistance = StopChasingDistance * 1.3f;
	}

	@Override
	public void updateUnit(float elapsed) {
		
		Target = findNewTarget(Unit, Unit.getStats().RangeOfSight); 
		if(Target == null) {
			Unit.getActionStack().compleated(this);
			return;
		}
		
		Point diff = Target.getPosition().minus(Unit.getPosition());
		
		switch(ActMode) {
			case MODE_STRIKE:
				AttackCooldown -= elapsed / Unit.getStats().AttackCooldown;
				if(AttackCooldown <= 0) {
					AttackCooldown = 1;

					if(diff.lengthSq() > BeginChasingDistance) {
						ActMode = MODE_CHASE;

					} else { 

						Target.damage(Unit.getStats().Attack, Unit.getType().getDamageType());
						if(Target.getHealth() <= 0)
							Target = null;
					}
				}
				break;

			case MODE_CHASE:
				if(diff.lengthSq() < StopChasingDistance) {
					ActMode = MODE_STRIKE;
					AttackCooldown = 0;
					Unit.TVelocity.X = 0;
					Unit.TVelocity.Y = 0;
				} else {
					Unit.TVelocity = diff.normalize(Unit.getStats().Speed);
				}
				break;
		}
	}

	public static Unit findNewTarget(Unit unit, float tilesAway) {
		Unit newTarg = null;
		float distSq = tilesAway * Map.tileHeight;
		distSq = distSq * distSq + 1;

		Map map = Map.get();
		for(int i = map.getUnits().size() - 1; i >= 0; i--) {
			Unit u = map.getUnits().get(i);
			if(u.getOwner().getDiplomacyWith(unit.getOwner()) == DiplomacyState.Enemy) {
				float udS = u.getPosition().distSq(unit.getPosition());
				if(udS < distSq) {
					newTarg = u;
					distSq = udS;
				}
			}
		}

		return newTarg;
	}

	@Override
	public void setState(FlareUnitAnimation img) {		
		int frame = (TimeKeeper.getAnimMS() / 100) % 8;
		img.setState(
				(ActMode == MODE_STRIKE) ? 2 : 1,
						Unit.Direction, frame);
	}
}
