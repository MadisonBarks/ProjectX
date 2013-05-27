package com.focused.projectf.entities;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ResourceType;
import com.focused.projectf.entities.collision.EllipseBounds;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.interfaces.IResourceItem;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.images.SpriteMapImage;
import com.focused.projectf.utilities.random.Chance;

public class ResourceElement extends SelectableEntity implements IResourceItem {

	protected Point drawPos, tile;
	public ResourceType DepositType;
	protected int ResourceAmount;
	public byte tick;
	private Villager Collector = null;

	public ResourceElement(Point position, ResourceType type, int amount) {
		super(Map.roundToTileCoord(position), new EllipseBounds(position, Map.tileWidth / 3f, true));
		DepositType = type;
		ResourceAmount = amount;
		tick = -1;
		Map.get().setTileFlag((int)tile.X, (int)tile.Y, Map.TILE_OCCUPIED_FLAG, true);
	}

	public int getResourceAmount() { return ResourceAmount; }
	@Override
	public boolean takeResource() {
		return (--ResourceAmount > 0);
	}
	@Override
	public ResourceType getResourceType() {
		return DepositType;
	}
	@Override
	public void setCollectingFrom(Villager vill) {
		Collector = vill;
	}
	@Override
	public Villager getCollector() {
		return Collector;
	}
	@Override
	public boolean isUnderCollection() {
		return Collector != null;
	}

	protected Image getImg() {
		/*
		if(DepositType == ResourceType.Wood)

			return Trees[tick];
		else if(ResourceAmount > 400)
			return Talls[DepositType.ordinal()];

		return Shorts[DepositType.ordinal()];
		 */
		return null;
	}

	@Override
	public void draw() {
		float depth = Canvas.calcDepth(position.Y - Map.tileHalfHeight);
		SpriteMapImage img = (SpriteMapImage)getImg();

		drawPos = position.plus(img.DefaultLeftOffset - img.getWidth() / 2, img.DefaultBottomOffset - img.getHeight());
		Canvas.drawImage(
				img,
				drawPos, 
				((Map.get().isTileVissible(tile))? Color.WHITE : Color.HALF_COLOR), 
				depth);
	}

	@Override
	public void drawSelected() {
		float depth = Canvas.calcDepth(position.Y - Map.tileHalfHeight);
		Canvas.drawEllipse(getPosition(), Map.tileWidth / 3, Map.tileHeight / 3, Color.WHITE, 2, depth + 0.1f);
		Canvas.drawEllipse(getPosition(), Map.tileWidth / 3, Map.tileHeight / 3, Color.WHITE.withAlpha(0.31f), 2, -1);
	}

	public void setPosition(Point pt) {
		position = Map.roundToTileCoord(pt);
		tile = Map.toTile(position);		
	}

	@Override
	public void remove() {
		Point tile = Map.toTile(position);
		Selection.remove(this);
		Map.get().setTileFlag((int)tile.X, (int)tile.Y, Map.TILE_OCCUPIED_FLAG, false);
		Map.get().removeEntity(this);
	}

	public static ResourceElement insertResource(int x, int y, ResourceType type, int amount) {
		ResourceElement element = new ResourceElement(Map.fromTile(x, y), type, amount);
		Map.get().addEntity(element);
		return element;
	}
	public static ResourceElement insertTree(int x, int y, int amount, int rnd) {
		ResourceElement element = new ResourceElement(Map.fromTile(x, y), ResourceType.Wood, amount);
		element.tick = (byte)(rnd);
		Map.get().addEntity(element);
		return element;
	}
	public static ResourceElement insertTree(int x, int y, int amount) {
		ResourceElement element = new ResourceElement(Map.fromTile(x, y), ResourceType.Wood, amount);
		element.tick = (byte) Chance.nextInt(4);
		Map.get().addEntity(element);
		return element;
	}

	@Override
	public Image getIcon() {
		Image image = getImg();
		if(image != null) {
			if(image instanceof SpriteMapImage) {
				SpriteMapImage base = (SpriteMapImage)image;
				int wh = Math.min(Math.min(image.getWidth(), image.getHeight()), 48);
				return new SpriteMapImage(base.getTexture(), base.getX(), base.getY(), wh, wh);
			}
			return new SpriteMapImage(image.getTexture(), 0, -16, 32, 32);
		}
		return Content.Images.get(Content.Images.keySet().toArray()[0].toString());
	}

	@Override
	public String getDisplayName() {
		return DepositType.DepositName.replace('_', ' ');
	}

	@Override
	public String[] getInfo() {
		return new String[] {
				DepositType.name(), "%u"
		};
	}

	@Override
	public Player getOwner() {
		return null;
	}
}
