package com.focused.projectf.interfaces;

import com.focused.projectf.Point;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.players.Player;

public interface IEntity {
	public Point getPosition();
	public Bounding getBounds();
	public void remove();
	public Player getOwner();
}
