package com.focused.projectf.entities;

import java.util.Vector;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.TileConstants;
import com.focused.projectf.entities.collision.TileBounds;
import com.focused.projectf.global.BuildingStats;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.players.Player;
import com.focused.projectf.resources.Content;
import com.focused.projectf.utilities.FMath;

public class Building extends ControllableEntity implements TileConstants {

	private Rect drawRect;
	public int tw, th;
	public Image texture;
	public BuildingType Type;
	private int UID;
	private int Health;
	private ActionQueue ActionQueue;
	public Object UnitTarget = null;
	
	public Vector<Unit> Garrison;
	
	public Building(Player owner, Point pos, BuildingType type) {
		super(owner);
		Health = ResearchManager.getStats(owner, type).MaxHealth;
		ActionQueue = new ActionQueue(this);
		Type = type;
		tw = type.widthInTiles;
		th = type.heightInTiles;
		texture = Content.getImage(type.texturePath);
		pos = Map.roundToTileCoord(pos);
		setPosition(pos);
		if(type.MaxGarrison > 0)
			Garrison = new Vector<Unit>();
	}

	public void setPosition(Point pos) {
		position = pos;
		if(texture != null) {
			float width = ((float)tw) * Map.tileWidth - 4f;
			float height = this.texture.getHeight() * ((float)width + 4f) / this.texture.getWidth();
			float y = pos.Y - height - 1;
			float x = pos.X - width / 2f;
			drawRect = new Rect(x, y, width, height);
		} else 
			drawRect = new Rect(pos.X - 20, pos.Y - 20, 40, 40);

		updateMap(true);
		Bounds = new TileBounds(position, Type.widthInTiles, Type.heightInTiles, true);		
	}

	public void updateMap(boolean isThere) {
		Point tileXY = Map.toTile(position);
		int tx = (int) tileXY.X;
		int ty = (int) tileXY.Y + Type.heightInTiles;

		for(int x = tx - Type.widthInTiles; x < tx; x++)
			for(int y = ty - Type.heightInTiles; y < ty; y++) 
				Map.get().setTileFlag(x + 1, y , TileConstants.TILE_OCCUPIED_FLAG, isThere);
	}

	public BuildingType getType() { return Type; }

	public void remove() {
		updateMap(false);
		Map.get().removeEntity(this);
	}

	@Override
	public void update(float elapsed) {
		ActionQueue.update();
	}

	public void draw() {
		float depth = Canvas.calcDepth(Bounds.getCenter().Y);
		if(texture != null) 
			Canvas.drawImage(texture, drawRect, Color.WHITE, depth);
	}

	public Rect getDrawRect() { 
		float width = ((float)tw) * Map.tileWidth - 4f;
		float height = this.texture.getHeight() * ((float)width) / this.texture.getWidth();
		float y = position.Y - height + Map.tileHalfHeight;
		float x = position.X - width / 2f;
		drawRect = new Rect(x, y, width, height);
		return drawRect;
	}

	public int getUID() { return UID; }
	public void setUID(int uID) { UID = uID; }

	public ActionQueue getActionQueue() { return ActionQueue; }

	@Override
	public void drawSelected() {
		float depth = Canvas.calcDepth(position.Y - Map.tileHalfHeight * th) + 0.0001f;
		TileBounds bounds = (TileBounds) getBounds();
		getOwner().MyTeam.MainColor.bind(0.85f);
		GL11.glLineWidth(3);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		bounds.corners[0].bind3(depth);
		bounds.corners[1].bind3(depth);
		bounds.corners[2].bind3(depth);
		bounds.corners[3].bind3(depth);
		bounds.corners[0].bind3(depth);
		GL11.glEnd();
	}

	public Image getIcon() {
		return Content.getImage(getType().iconPath);
	}

	@Override
	public float getHealthFraction() {
		return Health / (float)ResearchManager.getStats(Owner, getType()).MaxHealth;
	}
	@Override
	public int getHealth() { 
		return Health;
	}
	@Override
	public void damage(float points, DamageType type) { 
		Health -= (int)((float)points * type.BuildingMultiplier);
		if(Health <= 0) {
			// TODO: building die
		} 

		Health = FMath.min(Health, ResearchManager.getStats(Owner, getType()).MaxHealth);
	}

	@Override
	public String getDisplayName() {
		return Type.name().replace('_', ' ');
	}

	@Override
	public String[] getInfo() {
		BuildingStats stats = ResearchManager.getStats(Owner, Type);
		return new String[] {
				"Garison", "%u / " + stats.MaxGarrison,
				"Attack",		"" + stats.Attack,
				"Defense", 		"" + stats.Defense,
				"Range",		"" + stats.Range,
		};
	}

	/**
	 * Attempts to have a unit enter the building. If it is full or doesn't allow units to 
	 * enter, false is returned. Otherwise, the unit is removed from the map and true is returned.
	 */
	public boolean garison(Unit unit) {
		if(Garrison == null || Garrison.size() >= Type.MaxGarrison)
			return false;
		
		Garrison.add(unit);
		Map.get().removeEntity(unit);
		return true;
	}
}
