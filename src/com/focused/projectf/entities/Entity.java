package com.focused.projectf.entities;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.interfaces.IDrawable;
import com.focused.projectf.interfaces.IEntity;

public abstract class Entity implements IDrawable, IEntity {
	
	protected Point position;
	protected Bounding Bounds;
	
	public Entity(Point pos, Bounding bounds) {
		setPosition(pos);
		Bounds = bounds;
	}
	
	public Entity() { }

	public abstract void remove();
	
	public Point getPosition() { return position; }
	public void setPosition(Point pos) { position = pos; }
	public void move(Point amount) { 		
		if(amount.isInvalid()) {
			ErrorManager.logWarning("MoveToAction passed a invalid point: " + amount.toString(), null);
			position.plusEquals(Point.ZERO);
		}		
		position.plusEquals(amount); 
		Bounds.moveCenter(amount);
	}
	
	public void move(float x, float y) {
		position.plusEquals(x, y); 
		Bounds.setCenter(position);
	}
	
	@Override
	public Bounding getBounds() { return Bounds; }
	
	public static float calculateDepth(float y) {
		float max = Map.get().getHeightInTiles() * Map.tileHeight;
		float min = 0;
		GL11.glDepthRange(min, max);
		return y - (max / 2f);
	}
}
