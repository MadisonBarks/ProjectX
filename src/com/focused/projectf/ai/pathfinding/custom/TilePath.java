package com.focused.projectf.ai.pathfinding.custom;

import java.util.ArrayList;

/**
 * A path determined by some path finding algorithm. A series of steps from
 * the starting location to the target location. This includes a step for the
 * initial location.
 */
public class TilePath {

	private ArrayList<Step> steps = new ArrayList<Step>();


	public TilePath() {

	}

	public int getLength() {
		return steps.size();
	}

	public Step getStep(int index) {
		return steps.get(index);
	}

	public int getX(int index) {
		return getStep(index).x;
	}

	public int getY(int index) {
		return getStep(index).y;
	}

	public void appendStep(int x, int y) {
		steps.add(new Step(x,y));
	}

	public void prependStep(int x, int y) {
		steps.add(0, new Step(x, y));
	}

	public boolean contains(int x, int y) {
		return steps.contains(new Step(x,y));
	}

	public class Step {

		public final int x, y;

		public Step(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int hashCode() {
			return x * y;
		}

		public boolean equals(Object other) {
			if (other instanceof Step) {
				Step o = (Step) other;				
				return (o.x == x) && (o.y == y);
			}			
			return false;
		}

		public int distSq(Step s1) {
			return (x - s1.x) * (x - s1.x) + (y - s1.y) * (y - s1.y);
		}
	}
}
