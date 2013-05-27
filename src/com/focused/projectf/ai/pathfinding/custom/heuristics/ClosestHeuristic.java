package com.focused.projectf.ai.pathfinding.custom.heuristics;

import com.focused.projectf.ai.pathfinding.custom.AStarHeuristic;
import com.focused.projectf.ai.pathfinding.custom.Mover;
import com.focused.projectf.ai.pathfinding.custom.TileBasedMap;


/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 * 
 * @author Kevin Glass
 */
public class ClosestHeuristic implements AStarHeuristic {

	public int getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {		
		float dx = tx - x;
		float dy = ty - y;
		
		return (int) Math.sqrt((dx*dx)+(dy*dy));
	}
}
