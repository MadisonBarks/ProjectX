package com.focused.projectf.entities.collision;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.TileConstants;
import com.focused.projectf.utilities.FMath;

public class TileBounds extends Bounding implements TileConstants {

	public Rect tileCoords;
	public int TilesWide, TilesTall;
	public Point[] corners = new Point[4];
	
	public TileBounds(Point bottomCorner, int tilesWide, int tilesTall, boolean isStatic) {
		TilesWide = tilesWide;
		TilesTall = tilesTall;
		Point corner = Map.roundToTileCoord(bottomCorner);
		corner.minusEquals(0,  Map.tileHalfHeight * (tilesTall - 1));
		setCenter(corner);
	}

	public void setCenter(Point pt) {
		this.Center = pt;
		corners[0] = pt.plus(0, Map.tileHalfHeight * TilesTall + 3);
		corners[1] = pt.plus(Map.tileHalfWidth * TilesWide + 6, 0);
		corners[2] = pt.plus(0, -Map.tileHalfHeight * TilesTall - 3);
		corners[3] = pt.plus(-Map.tileHalfWidth * TilesWide - 6, 0);
		Point tl = Map.toTileSmooth(corners[0]);
		Point br = Map.toTileSmooth(corners[2]);
		tileCoords = new Rect(tl, br);		
	}
	
	@Override
	public Point calcNormal(Bounding other) { return null; }

	@Override
	public boolean collides(Bounding other) { return false; }

	@Override
	public boolean boundsContains(Point point) {
		return tileCoords.contains(Map.toTileSmooth(point));
	}

	@Override
	public boolean boundsIntersects(Rect rect) { return false; }

	@Override
	public Point rayTest(Point begin, Point direction) { return null; }

	public void glDraw(int mode, float depth) {
		GL11.glBegin(mode);
		corners[0].bind3(depth);
		corners[1].bind3(depth);
		corners[2].bind3(depth);
		corners[3].bind3(depth);
		corners[0].bind3(depth);
		GL11.glEnd();
	}
	
	@Override
	public Point getBorderingPoint(final Point pos, final float away) {
		
		Point targ = corners[0].clone();
		float distSq = corners[0].distSq(pos);
		for(int i = 0; i < 4; i++) {
			Point pt = FMath.closestPointToLineSegment(corners[i], corners[(i + 1) % 4], pos);
			float dSq = pt.distSq(pos);
			if(dSq <= distSq) {
				targ = pt;
				distSq = dSq;
			}
		}

		// OPT: targ = targ.minus(Center.minus(targ).normalizeEquals(away));
		float awayX = targ.X - Center.X;
		float awayY = targ.Y - Center.Y;
		float invLength = 1.0f / FMath.sqrt(awayX * awayX + awayY + awayY);
		targ.X += awayX * invLength;
		targ.Y += awayY * invLength;
		return targ;
	}

	@Override
	public float getMinRadius() {
		return Math.min(TilesTall * Map.tileHalfHeight, TilesWide * Map.tileHalfWidth);
	}
}
