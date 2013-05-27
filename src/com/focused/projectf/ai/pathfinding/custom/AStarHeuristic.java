package com.focused.projectf.ai.pathfinding.custom;

/**
 * The description of a class providing a cost for a given tile based
 * on a target location and entity being moved. This heuristic controls
 * what priority is placed on different tiles during the search for a path
 */
public interface AStarHeuristic {

	public int getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty);
}
