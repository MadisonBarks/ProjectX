package com.focused.projectf.ai.actions;

import java.util.List;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Action;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.interfaces.IDamageable.DamageType;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.TimeKeeper;
import com.focused.projectf.utilities.random.Chance;

public class RepairAction extends Action<Villager> {

	public Building Site;
	private float coolDown;
	public RepairAction(Villager unit, Building site) {
		super(unit);
		Site = site;
	}

	@Override
	public boolean holdPosition() { return false; }

	@Override
	public void startAction() { 
		coolDown = 0.0f;
		if(Site == null) {
			Site = findNearestDamagedBuilding(Unit.getPosition());
			if(Site == null){
				Unit.getActionStack().compleated(this);
				return;
			}
		} 

		Point nearest = Site.getBounds().getBorderingPoint(Unit.getPosition(), Unit.getType().Size - 10);

		if(Unit.getPosition().distSq(nearest) >= Unit.getType().Size * Unit.getType().Size) {
			Unit.getActionStack().insert(new MoveToAction(Unit, Site));
		} else {
			Unit.Direction = Unit.faceTowards(Site.getPosition());
		}
	}

	private Building findNearestDamagedBuilding(Point from) {
		List<Building> blds = Map.get().getBuildings();
		Building newTarget = null;
		float distSq = Float.POSITIVE_INFINITY;

		for(int i = 0; i < blds.size(); i++) {
			Building e = blds.get(i);
			if(e instanceof Building) {
				float dSq = e.getPosition().distSq(from) + Chance.randomInRange(32, 1600);
				if(dSq < distSq) {
					distSq = dSq;
					newTarget = (Building) e;
				}
			}
		}
		return newTarget;
	}

	@Override
	public void updateUnit(float elapsed) {
		coolDown -= elapsed;
		if(coolDown < 0.0f) {
			coolDown += 1;
			int dmg = -Math.min(10, ResearchManager.getStats(Site).MaxHealth - Site.getHealth());
			Site.damage(dmg, DamageType.Repair);

			if(Site.getHealthFraction() == 1.0f) {
				Site = findNearestDamagedBuilding(Unit.getPosition());
				startAction();
				return;
			}
		}
	}

	@Override
	public void setState(FlareUnitAnimation img) {
		int frame = (TimeKeeper.getAnimMS() / 150) % 5;
		img.setState(2, Unit.Direction, frame);
	}
}
