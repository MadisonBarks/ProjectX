package com.focused.projectf.entities;

import com.focused.projectf.Point;
import com.focused.projectf.ai.ActionStack;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.interfaces.IDamageable;
import com.focused.projectf.players.Player;

public abstract class ControllableEntity extends SelectableEntity implements IDamageable {
	
	protected ActionStack actions;
	protected Player Owner;
	
	public ControllableEntity(Player owner, Bounding bounds, Point pos) {
		super(pos, bounds);
		this.Owner = owner;
		actions = new ActionStack(null);
	}
	
	public ControllableEntity(Player owner) {
		super();
		this.Owner = owner;
		// TODO Auto-generated constructor stub
	}

	public Player getOwner() { return Owner; }
	public void setOwner(Player player) { Owner = player; }
	public abstract void update(float elapsed);
}
