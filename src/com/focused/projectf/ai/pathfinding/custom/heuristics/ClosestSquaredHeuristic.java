package com.focused.projectf.ai.pathfinding.custom.heuristics;

import com.focused.projectf.ai.pathfinding.custom.AStarHeuristic;
import com.focused.projectf.ai.pathfinding.custom.Mover;
import com.focused.projectf.ai.pathfinding.custom.TileBasedMap;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile. In this case the sqrt is removed
 * and the distance squared is used instead
 */
public class ClosestSquaredHeuristic implements AStarHeuristic {

	public int getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {		
		int dx = tx - x;
		int dy = ty - y;
		
		return ((dx*dx)+(dy*dy));
	}
}
