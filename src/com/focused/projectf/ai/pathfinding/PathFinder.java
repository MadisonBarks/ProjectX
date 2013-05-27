package com.focused.projectf.ai.pathfinding;


import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Path;
import com.focused.projectf.ai.pathfinding.custom.AStarPathFinder;
import com.focused.projectf.ai.pathfinding.custom.Mover;
import com.focused.projectf.ai.pathfinding.custom.heuristics.ManhattanHeuristic;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.Unit;

public class PathFinder {

	private static AStarPathFinder Finder;
	public static Mover Unit;

	public static void initialize(Map map) {
		ErrorManager.logDebug("Now initializing the A* Pathfinder");
		Finder = new AStarPathFinder(map, new ManhattanHeuristic(2));
	}

	public static Path computePath(Point startPoint, Point endPoint, Unit unit) {

		Point s = Map.toTile(startPoint);
		Point t = Map.toTile(endPoint.clone());
		int sx = (int) s.X;
		int sy = (int) s.Y;
		int tx = (int) t.X;
		int ty = (int) t.Y;

		if(sx == tx && sy == ty)		// if it's on the same tile, don't bother with A*.
			return new Path(startPoint, endPoint); 

		//if(testLine(sx, sy, tx, ty, unit))
		//	return new Path(startPoint, endPoint);

		Path path = Finder.findPath(unit, sx, sy, tx, ty, endPoint);
		if(path == null) {
			ErrorManager.logWarning("A* pathfinding failed.", null);
			return null;
		}

		ErrorManager.logDebug("A* Path Found");
		return path;
	}

	public static Point getNearestPointTo(ResourceElement target, Unit mover) {
		Point ptTarget = Map.roundToTileCoord(target.getPosition());
		ptTarget.plusEquals(mover.getPosition().minus(ptTarget).normalize().times(Map.tileWidth, Map.tileHeight));
		return Map.roundToTileCoord(ptTarget);
	}

	public static Point getNearestPointTo(Building target, Unit mover) {

		float distSq = Float.POSITIVE_INFINITY;
		Point endPoint = null;
		Point tC = Map.toTile(target.getBounds().getCenter());
		for(int x = -target.tw; x < 2; x++)
			for(int y = -2; y < target.th; y++) {
				Point np = Map.fromTile(tC.plus(x, y));
				if(np.distSq(mover.getPosition()) < distSq) {
					endPoint = np;
					distSq = np.distSq(mover.getPosition());
				}
			}

		return endPoint;
	}

	public static final int MAX_SEARCH_RANGE = 60;

	public static Point findNearestNotBlockedTile(Unit unit, int x, int y) {
		Map map = Map.get();

		Point ret = new Point(-1, -1);
		Point unitPos = Map.toTile(unit.getPosition());
		Point targetPos = new Point(x, y);

		float distSq = Float.POSITIVE_INFINITY;

		int yCap = y + 	MAX_SEARCH_RANGE;
		int xCap = x + MAX_SEARCH_RANGE;
		for(int xo = x - MAX_SEARCH_RANGE; xo < xCap; xo++) {
			for(int yo = y - MAX_SEARCH_RANGE; yo < yCap; yo++) {
				if(!map.safeblocked(unit, xo, yo)) {
					float dSq = targetPos.distanceSq(xo, yo) + unitPos.distanceSq(xo, yo);
					if(distSq > dSq) {
						ret.X = xo;
						ret.Y = yo;
						distSq = dSq;
					}
				}
			}
		}
		if(ret.X != -1 && ret.Y != -1) 
			return ret;

		throw new Error("Nearest non-blocked tile could not be found");
	}

	public static boolean isBlockedOff(int x, int y) {
		Map map = Map.get();
		return  map.safeblocked(null, x - 1, y - 1) &&
				map.safeblocked(null, x - 1, y) &&
				map.safeblocked(null, x - 1, y + 1) &&
				map.safeblocked(null, x, y - 1) &&
				map.safeblocked(null, x, y + 1) &&
				map.safeblocked(null, x + 1, y - 1) &&
				map.safeblocked(null, x + 1, y) &&
				map.safeblocked(null, x + 1, y + 1);
	}

	public static boolean isBlockedOff(Point position) {
		Point tile = Map.toTile(position);
		return isBlockedOff((int)tile.X, (int)tile.Y);
	}

	
	public static boolean testLine(Point start, Point end, Mover mover) {
		Point s = Map.toTile(start);
		Point t = Map.toTile(end);
		int sx = (int) s.X;
		int sy = (int) s.Y;
		int tx = (int) t.X;
		int ty = (int) t.Y;
		
		return testLine(sx, sy, tx, ty, mover);
	}
	
	public static boolean testLine(int x0, int y0, int x1, int y1, Mover mover) {

		boolean blocked = false;
		boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
		boolean checkEnds = (x0 - x1) + (y0 - y1) < 3;
		int temp = 0;

		if(steep) {
			temp = x0; x0 = y0; y0 = temp;
			temp = x1; x1 = y1; y1 = temp;
		}
		if (x0 > x1) {
			temp = x0; x0 = x1; x1 = temp;
			temp = y0; y0 = y1; y1 = temp;
		}
		float dx = x1 - x0;
		float dy = y1 - y0;
		float gradient = dy / dx;

		// handle first endpoint
		int xend = round(x0);
		int yend = (int) (y0 + gradient * (xend - x0));
		int xpxl1 = xend;   //this will be used in the main loop
		float xgap;
		xgap = rfpart(x0 + 0.5f);
		// skip checking where we are standing.
		//*
		if(checkEnds) {
			int ypxl1 = ipart(yend);
			if (steep) {
				blocked |= plot(ypxl1,   xpxl1, rfpart(yend) * xgap, mover);
				blocked |= plot(ypxl1+1, xpxl1,  fpart(yend) * xgap, mover);
			} else {
				blocked |= plot(xpxl1, ypxl1  , rfpart(yend) * xgap, mover);
				blocked |= plot(xpxl1, ypxl1+1,  fpart(yend) * xgap, mover);
			}	
		}
		float intery = yend + gradient; // first y-intersection for the main loop

		// handle second endpoint

		xend = round(x1);
		yend = (int) (y1 + gradient * (xend - x1));
		xgap = fpart(x1 + 0.5f);
		int xpxl2 = xend; //this will be used in the main loop
		if(checkEnds) {
			int ypxl2 = ipart(yend);
			if (steep) {
				blocked |= plot(ypxl2  , xpxl2, rfpart(yend) * xgap, mover);
				blocked |= plot(ypxl2 + 1, xpxl2,  fpart(yend) * xgap, mover);
			} else {
				blocked |= plot(xpxl2, ypxl2, rfpart(yend) * xgap, mover);
				blocked |= plot(xpxl2, ypxl2 + 1, fpart(yend) * xgap, mover);
			}
		}
		// main loop


		if(blocked)
			return false;


		if(steep) {
			for (int x = xpxl1 + 1; x < xpxl2; x++) {
				blocked |= plot(ipart(intery)  , x, rfpart(intery), mover);
				blocked |= plot(ipart(intery) + 1, x,  fpart(intery), mover);
				intery += gradient;
			}
		} else {
			for (int x = xpxl1 + 1; x < xpxl2; x++) {
				blocked |= plot(x, ipart(intery),  rfpart(intery), mover);
				blocked |= plot(x, ipart(intery) + 1, fpart(intery), mover);
				intery = intery + gradient;
			}
		}

		return !blocked;
	}

	private static boolean plot(int x, int y, float c, Mover mover) {
		if(c > 0.01f)
			return Map.get().blocked(mover, x, y);
		return false;
	}

	static int ipart(float x) { return (int) Math.floor(x); }
	static int round(float x) { return (int)x; }
	static float fpart(float x) { return x - ipart(x); }
	static float rfpart(float x) { return 1 - fpart(x); }
}

