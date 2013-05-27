package com.focused.projectf.entities.collision;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.utilities.FMath;

public class EllipseBounds extends Bounding {

	public float WidthRadius, HeightRadius;

	public EllipseBounds(Point center, float majorRadius, boolean isStatic) {
		this(center, majorRadius, majorRadius / 2f, isStatic);
	}
	public EllipseBounds(Point center, float widthRadius, float heightRadius, boolean isStatic) {
		super(center, isStatic);
		WidthRadius = widthRadius;
		HeightRadius = heightRadius;
	}

	public boolean collides(Bounding other) {
		if(other instanceof EllipseBounds) {
			EllipseBounds otherE = (EllipseBounds) other;
			Point dirr = Center.minus(otherE.Center).normalize();
			float dist = Center.distSq(otherE.Center);
			dirr.timesEquals(WidthRadius + otherE.WidthRadius, HeightRadius + otherE.HeightRadius);
			return dist < dirr.length();
		} else {
			return false;
		}
	}

	public boolean softCollides(EllipseBounds other) {
		Point dirr = Center.minus(other.Center).normalize();
		float dist = Center.distSq(other.Center);
		dirr.timesEquals(WidthRadius + other.WidthRadius, HeightRadius + other.HeightRadius);
		float len = dirr.lengthSq();
		return dist < len;
	}

	@Override
	public Point calcNormal(Bounding other) {
		if(other instanceof EllipseBounds) {
			Point norm = other.Center.times(1, 2).minus(Center.times(1, 2));
			float radiuses = (dirRadius(norm.angle()) + ((EllipseBounds)other).dirRadius(norm.angle() + FMath.PI));
			Point normal = norm.times(norm.length() / radiuses);
			return normal;
		}
		return null;
	}

	public static Point pointNearestToLine(Point lStart, Point lEnd, Point pt) {
		
		float xDif = lEnd.X - lStart.X;
		float yDif = lEnd.Y - lStart.Y;		
		float top = ((pt.X - lStart.X) * xDif) + ((pt.Y - lStart.Y) * yDif);
		float bottom = xDif * xDif + yDif * yDif;
		float u = top / bottom; 

		if (u < 0) return lStart;
		if (u > 1) return lEnd;
		
		return new Point(lStart.X + (u * xDif), lStart.X + (u * yDif));
	}
	
	public static Point pointNearestToLine(float x1, float y1, float x2, float y2, float x3, float y3) {
		float xDif = x2 - x1;
		float yDif = y2 - y1;		
		float top = ((x3 - x1) * (xDif)) + ((y3 - y1) * (yDif));
		float bottom = xDif * xDif + yDif * yDif;
		float u = top / bottom; 

		if (u < 0) return new Point(x1, y1);
		if (u > 1) return new Point(x2, y2);
		
		return new Point(x1 + (u * (x2 - x1)), y1 + (u * (y2 - y1)));
	}

	public boolean boundsContains(Point point) {
		Point diff = Center.minus(point);
		Point dir = diff.normalize();
		float r = dir.timesEquals(WidthRadius, HeightRadius).lengthSq();
		return r > diff.lengthSq();
	}

	public boolean boundsIntersects(Rect rect) {
		return rect.contains(Center);
	}

	protected float dirRadius(float angle) {
		float sin = FMath.sinW(angle) * WidthRadius;
		float cos = FMath.cosW(angle) * HeightRadius;
		return FMath.sqrt(cos * cos + sin * sin);
	}
	@Override
	public Point rayTest(Point ray_origin, Point ray_normal) {

		ray_origin.minusEquals(Center);
		ray_normal = ray_normal.normalize();
		float a = ((ray_normal.X * ray_normal.X) / (WidthRadius * WidthRadius))
				+ ((ray_normal.Y * ray_normal.Y) / (HeightRadius * HeightRadius));
		float b = ((2 * ray_origin.X * ray_normal.X) / (WidthRadius * WidthRadius))
				+ ((2 * ray_origin.Y * ray_normal.Y) / (HeightRadius * HeightRadius));
		float c = ((ray_origin.X * ray_origin.X) / (WidthRadius * WidthRadius))
				+ ((ray_origin.Y * ray_origin.Y) / (HeightRadius * HeightRadius))
				- 1;

		float d = ((b * b) - (4 * a * c));
		if (d < 0) 
			return Point.getIdentity();

		d = FMath.sqrt(d);
		float hit = (-b + d) / (2 * a);
		float hitsecond = (-b - d) / (2 * a);

		return ray_origin.plus(ray_normal, FMath.min(hit, hitsecond));
	}
	
	final float[] angles8 = { 0, FMath.PI / 4, FMath.HALF_PI, FMath.PI * 0.75f,
			FMath.PI, FMath.PI * 1.25f, FMath.PI * 1.5f, FMath.PI * 1.75f };
	
	@Override
	public Point getBorderingPoint(final Point pos, float away) {
		away = Math.abs(away) + 1;
		Point direction = pos.minus(Center).normalizeEquals();
		Point pt = Center.plus(direction.times(WidthRadius + away, HeightRadius + away));
		
		if(Map.get().blocked(null, pt)) {
			for(int i = 0; i < angles8.length; i++) {
				direction = Point.fromAngle(angles8[i], WidthRadius + away, HeightRadius + away);
				pt.X = Center.X + direction.X;
				pt.Y = Center.Y + direction.Y;
				if(!Map.get().blocked(null, pt))
					return pt;
			}
			throw new Error("Unable to reach resoruce at all. This method shouldn't ever be reached in this case. ");
		}
		
		return pt;
	}
	
	@Override
	public void glDraw(int mode, float depth) {
		Canvas.ellipse(mode, Center.X, Center.Y, WidthRadius, HeightRadius, depth);
	}
	@Override
	public float getMinRadius() {
		return Math.min(WidthRadius, HeightRadius);
	}
}