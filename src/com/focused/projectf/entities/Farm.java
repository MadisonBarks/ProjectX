package com.focused.projectf.entities;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ResourceType;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.entities.collision.TileBounds;
import com.focused.projectf.graphics.Color;

public class Farm extends ResourceElement {

	protected final float xChange	= Map.tileWidth * 1.45f;
	protected final float yChange	= Map.tileHeight * 1.45f;
	
	protected final Color FillColor = Color.fromHex("FFA67000");
	
	public Farm(Point position) {
		super(position, ResourceType.Food, 500);
		Bounds = new TileBounds(Map.roundToTileCoord(position).plus(0, Map.tileHalfHeight), 3, 3, true);
		Map.get().setTileFlag((int)tile.X, (int)tile.Y, Map.TILE_OCCUPIED_FLAG, false);
	}

	public Bounding getBounds() {
		return Bounds;
	}

	public void draw() {

		TileBounds bounds = (TileBounds) Bounds;
		Point center = bounds.getCenter().minus(0, Map.tileHalfHeight);
		GL11.glBegin(GL11.GL_QUADS); {
			FillColor.bind();
			GL11.glVertex2f(center.X + xChange, center.Y);
			GL11.glVertex2f(center.X, center.Y - yChange);
			GL11.glVertex2f(center.X - xChange, center.Y);
			GL11.glVertex2f(center.X, center.Y + yChange);
		} GL11.glEnd();
	}

	@Override
	public void drawSelected() {
		float depth = -0.11f;	
		GL11.glLineWidth(2f);
		TileBounds bounds = (TileBounds) Bounds;
		Color.WHITE.bind();
		GL11.glBegin(GL11.GL_LINE_STRIP);
		bounds.corners[0].bind3(depth);
		bounds.corners[1].bind3(depth);
		bounds.corners[2].bind3(depth);
		bounds.corners[3].bind3(depth);
		bounds.corners[0].bind3(depth);
		GL11.glEnd();
	}
}
