package com.focused.projectf.interfaces;

import com.focused.projectf.ResourceType;
import com.focused.projectf.entities.units.Villager;

/**
 * Any entity that holds resources that can be collected by units.
 */
public interface IResourceItem extends IEntity {
	public int getResourceAmount();
	public boolean takeResource();
	public ResourceType getResourceType();
	public void setCollectingFrom(Villager collector);
	public Villager getCollector();
	public boolean isUnderCollection();
}

