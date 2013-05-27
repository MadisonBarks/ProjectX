package com.focused.projectf.entities.units;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ResourceType;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.players.Player;
import com.focused.projectf.resources.images.AnimatedImage;

public class Villager extends Unit {

	public Villager(Player owner, Point position) {
		super(owner, position, UnitType.Villager);
	}

	@Override
	public void onSelected() {
		// Play sound
	}

	@Override
	public void onDeselected() { }

	public int getHealth() { return 100; }

	public void damage(int points, DamageType type) {
		// TODO Auto-generated method stub
	}

	AnimatedImage img;

	@Override
	public void draw() {
		super.draw();

		if(actions.size() > 0)
			actions.running().draw(this);
	}

	public ResourceType CollectedResourceType = ResourceType.None;
	public int CollectedResourceAmount = 0;

	public void setResourceType(ResourceType resourceType) {
		if(CollectedResourceType != resourceType) {
			CollectedResourceAmount = 0;
		}
		CollectedResourceType = resourceType;	
	}

	public void dropOffResources() {
		if(CollectedResourceAmount > 0) {
			switch(CollectedResourceType) {
				case Food:	getOwner().Food		+= CollectedResourceAmount; break;
				case Gold:	getOwner().Gold		+= CollectedResourceAmount; break;
				case Stone:	getOwner().Stone	+= CollectedResourceAmount; break;
				case Wood:	getOwner().Wood		+= CollectedResourceAmount; break;
				case Radium: getOwner().Radium	+= CollectedResourceAmount; break;
				default:
					throw new Error("Resource type not yet implemented");								
			}
		}
		CollectedResourceAmount = 0;
	}

	@Override
	public void remove() {
		Map.get().removeEntity(this);
	}
}