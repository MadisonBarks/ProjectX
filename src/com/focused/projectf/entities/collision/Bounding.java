package com.focused.projectf.entities.collision;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;

public abstract class Bounding {

	protected boolean IsStatic;
	protected Point Center;
	public Bounding(Point center, boolean isStatic) {
		Center = center;
		IsStatic = isStatic;
	}

	public Bounding() {}

	public boolean isStatic() { return IsStatic; }
	public Point getBoundsCenter() { return Center; }
	
	public abstract Point calcNormal(Bounding other);
	public abstract boolean collides(Bounding other);
	public abstract boolean boundsContains(Point point);
	public abstract boolean boundsIntersects(Rect rect);
	/** 
	 * Determines where along the ray, if ever this bounding is intersected.
	 * Returns a Point with length 0 if never.
	 */
	public abstract Point rayTest(Point begin, Point direction);
	
	public void moveCenter(Point amount) { Center.plusEquals(amount); }
	public void setCenter(Point pos) { Center = pos.clone(); }
	public Point getCenter() { return Center.clone(); }
	
	
	public boolean pointInsideTriangle(Point s, Point a, Point b, Point c) {
		float as_x = s.X - a.X;
	    float as_y = s.Y - a.Y;

	    boolean s_ab = (b.X - a.X) * as_y - (b.Y - a.Y) * as_x > 0;

	    if((c.X - a.X) * as_y - (c.Y - a.Y) * as_x > 0 == s_ab) 
	    	return false;

	    if((c.X - b.X) * (s.Y - b.Y) - (c.Y - b.Y) * (s.X - b.X) > 0 != s_ab) 
	    	return false;

	    return true;
	}
	public boolean pointInsideTriangle(float sX, float sY, float aX, float aY, float bX, float bY, float cX, float cY) {
		float as_x = sX - aX;
	    float as_y = sY - aY;

	    boolean s_ab = (bX - aX) * as_y - (bY - aY) * as_x > 0;

	    if((cX - aX) * as_y - (cY - aY) * as_x > 0 == s_ab) 
	    	return false;

	    if((cX - bX) * (sY - bY) - (cY - bY) * (sX - bX) > 0 != s_ab) 
	    	return false;

	    return true;
	}
	public abstract void glDraw(int mode, float depth);

	public abstract Point getBorderingPoint(Point position, float away);

	public abstract float getMinRadius();
}
