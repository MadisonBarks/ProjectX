package com.focused.projectf.entities;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.players.Player;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.images.SpriteMapImage;
/**
 * A non-moving solid unchangeable object on the map, like a cliff or bolder. Units cannot pass through this
 */
public class MapElement extends Entity {

	private Image image;
	private Point drawPos, tile;
	public final boolean isSolid;
	
	public MapElement(Point position, Image img, boolean isSolid) {
		super(Map.roundToTileCoord(position), null);
		image = img;
		Point tile = Map.toTile(getPosition());
		this.isSolid = isSolid;
		if(isSolid)
			Map.get().setTileFlag((int)tile.X, (int)tile.Y, Map.TILE_OCCUPIED_FLAG, true);
	}
	
	@Override
	public void draw() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		tile = Map.toTile(getPosition());			
		float depth = Canvas.calcDepth(position.Y - Map.tileHalfHeight);
		Canvas.drawImage(image, 
				drawPos,
				(Map.get().isTileVissible(tile))? Color.WHITE : Color.HALF_COLOR,
				depth);
	}
	
	public void setPosition(Point pt) {
		position = pt.clone();
		tile = Map.toTile(getPosition());			
		if(image != null)
			drawPos = position.plus(-Map.tileWidth / 3f, Map.tileHeight / 4f - image.getHeight());
	}

	public static void insertMapElement(int tileX, int tileY, String sprite, boolean isSolid) {
		MapElement element = new MapElement(Map.fromTile(tileX, tileY),
				Content.getImage(sprite), isSolid);
		Map.get().addEntity(element);
	}
	public static MapElement insertMapElement(int tileX, int tileY, String sprite) {
		Image img = Content.getImage(sprite);
		MapElement element = new MapElement(Map.fromTile(tileX, tileY),
				img, ((SpriteMapImage)img).isSolid);
		Map.get().addEntity(element);
		return element;
	}
	

	@Override
	public void remove() {
		Point tile = Map.toTile(position);
		Map.get().setTileFlag((int)tile.X, (int)tile.Y, Map.TILE_OCCUPIED_FLAG, false);
		Map.get().removeEntity(this);
	}

	@Override
	public Player getOwner() {
		return null;
	}
}