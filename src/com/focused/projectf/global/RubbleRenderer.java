package com.focused.projectf.global;


import java.util.Vector;

import com.focused.projectf.Point;
import com.focused.projectf.entities.Building;
import com.focused.projectf.utilities.random.Chance;

public class RubbleRenderer {

	public static final int MAX_RUBBLE = 200;
	private static Vector<Rubble> Rubble = new Vector<Rubble>();

	public static void addRubble(Building unit) {
		Rubble.add(new Rubble(unit));
	}

	public static void RenderCorpses() {
		for(int i = 0; i < Rubble.size(); i++) {
			//Rubble r = Rubble.get(i);
			
			
			
			// TODO: draw rubble.
		}
	}
	public static class Rubble {

		public final byte tilesWide, tilesTall;
		public int rnd;
		public Point position;
		public float timeDead = 0;

		public Rubble(Building bld) {
			position = bld.getPosition();
			tilesWide = (byte) bld.getType().widthInTiles;
			tilesTall = (byte) bld.getType().heightInTiles;
			rnd = Chance.nextInt(64);
		}
	}
}
