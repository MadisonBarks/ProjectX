package com.focused.projectf.ai.actions;

import java.util.List;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ResourceType;
import com.focused.projectf.ai.Action;
import com.focused.projectf.ai.pathfinding.PathFinder;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Farm;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.SelectableEntity;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.interfaces.IResourceItem;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.TimeKeeper;

public class CollectResourceAction extends Action<Villager> {

	public float collectCoolDown = 2;
	public IResourceItem CollectingFrom;
	private byte moveAttempts;

	public CollectResourceAction(Villager unit, IResourceItem resource) {
		super(unit);
		CollectingFrom = resource;		
		((Villager)unit).setResourceType(CollectingFrom.getResourceType());
	}


	@Override
	public void startAction() {
		if(CollectingFrom == null || CollectingFrom.getResourceAmount() == 0 || PathFinder.isBlockedOff(CollectingFrom.getPosition()) || CollectingFrom.getCollector() != Unit) {
			CollectingFrom = findNearestResourceElement(
					(CollectingFrom == null) ? Unit.getPosition() : CollectingFrom.getPosition(),
							Unit.CollectedResourceType);
			if(CollectingFrom == null){
				Unit.getActionStack().compleated(this);
				return;
			}
		} 
		if(moveAttempts == 4) {
			CollectingFrom.setCollectingFrom(null);
			CollectingFrom = findNearestResourceElement(Unit.getPosition(), CollectingFrom.getResourceType());
			moveAttempts -= 3;
		} else if (moveAttempts > 4) {
			CollectingFrom.setCollectingFrom(null);
			Unit.getActionStack().compleated(this);
			return;
		} 

		if(Unit.getPosition().distSq(CollectingFrom.getPosition()) >= Map.tileHalfWidth * Map.tileWidth) {
			if(CollectingFrom instanceof Farm)
				Unit.getActionStack().insert(new MoveToAction(Unit, CollectingFrom.getPosition()));
			else 
				Unit.getActionStack().insert(new MoveToAction(Unit, CollectingFrom));
			moveAttempts += 2;
		} else if(Unit.CollectedResourceAmount >= 10) {
			Building bld = getNearestDeposit(Unit);
			Unit.getActionStack().insert(new MoveToAction(Unit, bld));
		}
		Unit.Direction = Unit.faceTowards(CollectingFrom.getPosition());
		CollectingFrom.setCollectingFrom(Unit);
	}

	public void stopAction() {
		if(CollectingFrom != null)
			CollectingFrom.setCollectingFrom(null);
	}

	public static ResourceElement findNearestResourceElement(Point from, ResourceType type) {
		ResourceElement newTarget = null;
		float distSq = Map.tileWidth * Map.tileHeight * 100;
		Map map = Map.get();
		for(SelectableEntity e : map.getResources()) {
			if(e instanceof ResourceElement) {
				ResourceElement res = (ResourceElement)e;
				if(res.DepositType.matches(type) && !res.isUnderCollection() && !PathFinder.isBlockedOff(res.getPosition())) {
					float dSq = res.getPosition().distSq(from);
					if(dSq < distSq) {
						distSq = dSq;
						newTarget = res;
					}
				}
			}
		}
		return newTarget;
	}

	@Override
	public void updateUnit(float elapsed) {
		collectCoolDown -= elapsed;

		//Unit.Direction = Unit.faceTowards(CollectingFrom.getPosition());

		if(collectCoolDown < 0) {
			collectCoolDown = 0.8f;	// TODO: different values based upon resource type and researched techs

			if(CollectingFrom.takeResource()) 
				Unit.CollectedResourceAmount++;
			
			if(CollectingFrom.getResourceAmount() == 0) {
				CollectingFrom.remove();
				CollectingFrom = null;
				Building bld = getNearestDeposit(Unit);
				Unit.getActionStack().insert(new MoveToAction(Unit, bld));
			}
			collectCoolDown = 0.8f;	// TODO: different values based upon resource type and researched techs
			moveAttempts = 0;
		}
		
		if(Unit.CollectedResourceAmount >= 10) {
			Building bld = getNearestDeposit(Unit);
			Unit.getActionStack().insert(new MoveToAction(Unit, bld));
		}
	}

	private static Building getNearestDeposit(Unit unit) {
		List<Building> buildings = Map.get().getBuildings();
		Building target = null;
		float distSq = Float.POSITIVE_INFINITY;
		for(Building bld : buildings) {
			if(bld.getOwner() == unit.getOwner()) {
				if(bld.getType().AcceptsResources()) {
					float ds = bld.getPosition().distSq(unit.getPosition());
					if(ds < distSq) {
						target = bld;
						distSq = ds;
					}
				}
			}
		}
		return target;
	}

	@Override
	public void draw(Unit unit) {

	}

	@Override
	public boolean holdPosition() { return true; }

	@Override
	public void setState(FlareUnitAnimation img) {
		int frame = (TimeKeeper.getAnimMS() / 150) % 5;
		Unit.Direction = Unit.faceTowards(CollectingFrom.getPosition());
		img.setState(2, Unit.Direction, frame);
	}
}
