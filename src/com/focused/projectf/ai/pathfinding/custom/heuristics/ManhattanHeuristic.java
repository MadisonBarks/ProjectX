package com.focused.projectf.ai.pathfinding.custom.heuristics;

import com.focused.projectf.ai.pathfinding.custom.AStarHeuristic;
import com.focused.projectf.ai.pathfinding.custom.Mover;
import com.focused.projectf.ai.pathfinding.custom.TileBasedMap;

/**
 * A heuristic that drives the search based on the Manhattan distance
 * between the current location and the target
 */
public class ManhattanHeuristic implements AStarHeuristic {
	
	private int minimumCost;
	
	public ManhattanHeuristic(int minimumCost) {
		this.minimumCost = minimumCost;
	}
	
	public int getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
		return minimumCost * (Math.abs(x-tx) + Math.abs(y-ty));
	}
}
