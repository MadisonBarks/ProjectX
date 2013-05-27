package com.focused.projectf.ai.pathfinding.custom;

import java.util.ArrayList;
import java.util.Collections;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Path;
import com.focused.projectf.ai.pathfinding.custom.heuristics.ClosestSquaredHeuristic;


/**
 * A path finder implementation that uses the AStar heuristic based algorithm
 * to determine a path.
 */
public class AStarPathFinder {
	private ArrayList<Node> closed = new ArrayList<Node>();
	private SortedList<Node> open = new SortedList<Node>();

	private TileBasedMap map;
	private Node[][] nodes;
	@SuppressWarnings("unused")
	private AStarHeuristic heuristic;

	public AStarPathFinder(TileBasedMap map) {
		this(map, new ClosestSquaredHeuristic());
	}

	public AStarPathFinder(TileBasedMap map, AStarHeuristic heuristic) {
		this.heuristic = heuristic;
		this.map = map;

		nodes = new Node[map.getWidthInTiles()][map.getHeightInTiles()];
		for (int x = 0; x < map.getWidthInTiles(); x++)
			for (int y = 0; y < map.getHeightInTiles(); y++)
				nodes[x][y] = new Node(x, y);
	}

	private final int distSq(int x0, int y0, int x1, int y1) {
		return (x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1);
	}

	public synchronized Path findPath(Mover mover, int sx, int sy, int tx, int ty, Point end) {

		int closestX = sx, closestY = sy;
		int closestDistSq = distSq(sx, sy, tx, ty);

		// initial state for A*. The closed group is empty. Only the starting
		// tile is in the open list and it's cost is zero, i.e. we're already there

		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);

		nodes[tx][ty].parent = null;

		long start = System.nanoTime();
		final int MAX_SEARCH_TIME = (int) 2.5e8;	// 2500 ms

		while (open.size() != 0 && (System.nanoTime() - start) < MAX_SEARCH_TIME) {
			// pull out the first node in our open list, this is determined to 
			// be the most likely to be the next step based on our heuristic
			Node current = getFirstInOpen();
			if (current == nodes[tx][ty]) 
				break;

			removeFromOpen(current);
			addToClosed(current);

			int maxDepth = 0;

			// search through all the neighbors of the current node evaluating
			// them as next steps
			for (int x=-1;x<2;x++) {
				int xp = x + current.x;
				for (int y=-1;y<2;y++) {
					// not a neighbor, its the current tile
					if ((x == 0) && (y == 0)) 
						continue;

					int yp = y + current.y;

					if (isValidLocation(mover, current.x, current.y, xp, yp)) {
						// the cost to get to this node is cost the current plus the movement
						// cost to reach this node. Note that the heuristic value is only used
						// in the sorted open list
						int nextStepCost = current.cost + getMovementCost(mover, current.x, current.y, xp, yp);
						Node neighbour = nodes[xp][yp];
						
						int pDistSq = distSq(tx, ty, xp, yp);
						if(pDistSq < closestDistSq) {
							closestX = xp;
							closestY = yp;
							closestDistSq = pDistSq;
						}

						// if the new cost we've determined for this node is lower than 
						// it has been previously makes sure the node hasn't been discarded. We've
						// determined that there might have been a better path to get to
						// this node so it needs to be re-evaluated
						if (nextStepCost < neighbour.cost) {
							if (inOpenList(neighbour)) 
								removeFromOpen(neighbour);

							if (inClosedList(neighbour)) 
								removeFromClosed(neighbour);							
						}

						// if the node hasn't already been processed and discarded then
						// reset it's cost to our current cost and add it as a next possible
						// step (i.e. to the open list)
						if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
							neighbour.cost = nextStepCost;
							neighbour.heuristic = getHeuristicCost(mover, xp, yp, tx, ty);
							maxDepth = Math.max(maxDepth, neighbour.setParent(current));
							addToOpen(neighbour);
						}
					}
				}
			}
		}


		// since we've got an empty open list or we've run out of search 
		// there was no path. use the closest point that was found.
		if (nodes[tx][ty].parent == null) {
			tx = closestX;
			ty = closestY;
		}

		// At this point we've definitely found a path so we can uses the parent
		// references of the nodes to find out way from the target location back
		// to the start recording the nodes on the way.

		Path path = new Path();

		Node target = nodes[tx][ty];
		Node stopAt = nodes[sx][sy];
		Node lastBlocked = target;

		while (target != null && target != stopAt) {
			if(map.blocked(mover, target.x, target.y))
				lastBlocked = target;
			target = target.parent;
		}
		target = lastBlocked;

		while (target != null && target != stopAt) {
			path.add(Map.fromTile(target.x, target.y));
			target = target.parent;
		}
		Point e = Map.toTile(end);
		if(e.X == lastBlocked.x && e.Y == lastBlocked.y)
			path.insert(end);

		ErrorManager.logInfo("A* Search time: " + (System.nanoTime() - start) / 1e6 + "ms");

		return path;
	}

	protected Node getFirstInOpen() {
		return (Node) open.first();
	}

	protected void addToOpen(Node node) {
		open.add(node);
	}

	protected boolean inOpenList(Node node) {
		return open.contains(node);
	}

	protected void removeFromOpen(Node node) {
		open.remove(node);
	}

	protected void addToClosed(Node node) {
		closed.add(node);
	}

	protected boolean inClosedList(Node node) {
		return closed.contains(node);
	}

	protected void removeFromClosed(Node node) {
		closed.remove(node);
	}

	protected boolean isValidLocation(Mover mover, int sx, int sy, int x, int y) {
		
		boolean validAndOpen =
				(x >= 0) && (y >= 0) &&
				(x < map.getWidthInTiles()) &&
				(y < map.getHeightInTiles()) && 
				!map.blocked(mover, x, y);
				
		if(x != sx && y != sy) {
			validAndOpen &= !(Map.get().getTileFlag(sx, y, Map.TILE_OCCUPIED_FLAG) || Map.get().getTileFlag(x, sy, Map.TILE_OCCUPIED_FLAG));
		}
		
		return validAndOpen;
	}

	public int getMovementCost(Mover mover, int sx, int sy, int tx, int ty) {
		if(map.blocked(mover, tx, ty))
			return 1000;

		return map.getCost(mover, sx, sy, tx, ty);
	}

	public int getHeuristicCost(Mover mover, int x, int y, int tx, int ty) {
		return 1;
	}


	private class SortedList<T extends Comparable<T>> {
		private ArrayList<T> list = new ArrayList<T>();		

		public T first() { return list.get(0); }

		public void clear() { list.clear(); }

		public void add(T o) {
			list.add(o);
			Collections.sort(list);
		}

		public void remove(Object o) {
			list.remove(o);
		}

		public int size() { return list.size(); }

		public boolean contains(Object o) { return list.contains(o); }
	}


	public static class Node implements Comparable<Node> {
		private int x;
		private int y;
		private int cost;
		private Node parent;
		private int heuristic;
		private int depth;

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int setParent(Node parent) {
			this.parent = parent;			
			return (depth = parent.depth + 1);
		}

		public int compareTo(Node o) {			
			float f = heuristic + cost;
			float of = o.heuristic + o.cost;

			if (f < of)	return -1;
			if (f > of)	return 1;

			return 0;
		}
	}
}
