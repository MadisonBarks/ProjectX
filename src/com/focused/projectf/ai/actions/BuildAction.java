package com.focused.projectf.ai.actions;

import java.util.List;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Action;
import com.focused.projectf.entities.BuildingSite;
import com.focused.projectf.entities.Entity;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.TimeKeeper;
import com.focused.projectf.utilities.random.Chance;

public class BuildAction extends Action<Villager> {

	public BuildingSite Site;

	public BuildAction(Villager unit, BuildingSite site) {
		super(unit);
		Site = site;
	}

	@Override
	public boolean holdPosition() { return false; }

	@Override
	public void startAction() { 
		if(Site == null) {
			Site = findNearestBuildingSite(Unit.getPosition());
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

	private BuildingSite findNearestBuildingSite(Point from) {
		List<Entity> ents = Map.get().getOtherEntities();
		BuildingSite newTarget = null;
		float distSq = Float.POSITIVE_INFINITY;

		for(Entity e : ents) {
			if(e instanceof BuildingSite) {
				float dSq = e.getPosition().distSq(from) + Chance.randomInRange(32, 1600);
				if(dSq < distSq) {
					distSq = dSq;
					newTarget = (BuildingSite) e;
				}
			}
		}
		return newTarget;
	}

	@Override
	public void updateUnit(float elapsed) {
		if(!Site.readyToBuild())
			return;
		if(Site.Compleation > Site.TotalBuildTime) {
			Site = findNearestBuildingSite(Unit.getPosition());
			startAction();
		} else
			Site.Compleation += elapsed;
	}

	@Override
	public void setState(FlareUnitAnimation img) {
		int frame = (TimeKeeper.getAnimMS() / 150) % 5;
		img.setState(2, Unit.Direction, frame);
	}
}
