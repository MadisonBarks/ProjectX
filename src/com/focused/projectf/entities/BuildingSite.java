package com.focused.projectf.entities;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.TileConstants;
import com.focused.projectf.ai.ActionStack;
import com.focused.projectf.ai.actions.BuildAction;
import com.focused.projectf.ai.actions.CollectResourceAction;
import com.focused.projectf.ai.actions.MoveToAction;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.entities.collision.TileBounds;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;
import com.focused.projectf.resources.Content;

public class BuildingSite extends ControllableEntity {

	public final BuildingType Type;
	public float Compleation = 0;
	public float TotalBuildTime;

	public BuildingSite(Player owner, Point position, BuildingType type) {
		super(owner, 
				new TileBounds(Map.roundToTileCoord(position), type.widthInTiles, type.heightInTiles, true),
				Map.roundToTileCoord(position));
		Type = type;
		TotalBuildTime = (Type.FoodCost + Type.GoldCost + Type.RadiumCost + Type.WoodCost + Type.StoneCost) / 3;
	}
	
	public void draw() {
		{
			float depth = -0.1f;
			TileBounds bounds = (TileBounds) Bounds;
			Point center = bounds.getCenter();//.plus(0, Map.tileHalfHeight);
			GL11.glPointSize(5);
			Color.RED.bind();
			GL11.glBegin(GL11.GL_POINTS);
			center.bind3(-1);
			GL11.glEnd();
			
			GL11.glBegin(GL11.GL_QUADS);
			Color.fromHex("FF999999").bind();

			center.plus(Map.tileHalfWidth * Type.widthInTiles * 0.95f, 0).bind3(depth);
			center.plus(0, Map.tileHalfHeight * Type.heightInTiles * -0.95f).bind3(depth);
			center.plus(Map.tileHalfWidth * Type.widthInTiles * -0.95f, 0).bind3(depth);
			center.plus(0, Map.tileHalfHeight * Type.heightInTiles * 0.95f).bind3(depth);

			GL11.glEnd();
		}

		GL11.glPointSize(5);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glColor3f(1, 0, 0);
		if(!areaEmpty) {
			List<Unit> units = Map.get().getUnits();
			for(int i = 0; i < units.size(); i++) {
				Unit unit = units.get(i);
				Point pos = unit.getPosition();
				Bounding bounds = getBounds();

				if(bounds.boundsContains(pos))
					pos.bind3(-1);
			}
		}
		/*
		Map map = Map.get();
		Point tileXY = Map.toTile(position);
		int tx = (int) tileXY.X + 1;
		int ty = (int) tileXY.Y + Type.heightInTiles;
		Color.BLUE.bind();
		int tx2 = tx - Type.widthInTiles - 1;
		for(int y = ty - Type.heightInTiles - 1; y <= ty; y++) {
			if(!map.getTileFlag(tx, y, Map.TILE_OCCUPIED_FLAG))
				Map.fromTile(tx, y).bind3(-1);
			if(!map.getTileFlag(tx2, y, Map.TILE_OCCUPIED_FLAG))
				Map.fromTile(tx2, y).bind3(-1);
		}
		for(int x = tx - Type.widthInTiles; x < tx; x++) {
			if(!map.getTileFlag(x, ty, Map.TILE_OCCUPIED_FLAG))
				Map.fromTile(x, ty).bind3(-1);
			if(!map.getTileFlag(x, (int)tileXY.Y - 1, Map.TILE_OCCUPIED_FLAG))
				Map.fromTile(x, (int)tileXY.Y - 1).bind3(-1);
		}
		 */
		GL11.glEnd();
	}

	@Override
	public void drawSelected() {
		float depth = -0.11f;

		GL11.glLineWidth(2);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		Color.WHITE.bind();
		TileBounds bounds = (TileBounds) Bounds;
		for(int i = 0; i < 5; i++)
			bounds.corners[i % 4].bind3(depth);
		Color.WHITE.bind(0.3f);
		for(int i = 0; i < 5; i++)
			bounds.corners[i % 4].bind3(-1);
		GL11.glEnd();
		GL11.glGetError();	// XXX: 
	}

	private boolean areaEmpty = false;
	public boolean underConstruction = false;
	public boolean readyToBuild() {
		underConstruction = true;
		return areaEmpty;
	}

	@Override
	public void remove() {
		Map.get().removeEntity(this);
	}

	public int getTilesHeight()  { return Type.heightInTiles; }
	public int getTilesWide() { return Type.widthInTiles; }

	@Override
	public int getHealth() {
		return (int)(Compleation * ResearchManager.getStats(Owner, Type).MaxHealth / TotalBuildTime);
	}

	@Override
	public void damage(float points, DamageType type) {
		Compleation -= (int)(points * ResearchManager.getStats(Owner, Type).MaxHealth / TotalBuildTime);
		if(Compleation <= 0) {
			// TODO: create pile of bricks or whatever
			remove();
		}
	}

	@Override
	public float getHealthFraction() {
		return Compleation / TotalBuildTime;
	}

	@Override
	public Image getIcon() {
		return Content.getImage(Type.iconPath);
	}

	@Override
	public void update(float elapsed) {

		if(!areaEmpty && underConstruction) {
			if(Type == BuildingType.Farm) {
				areaEmpty = true;
				return;
		 	}
			boolean occupied = false;
			List<Unit> units = Map.get().getUnits();
			for(int i = 0; i < units.size(); i++) {
				Unit unit = units.get(i);
				Point pos = unit.getPosition();
				Bounding bounds = getBounds();

				if(bounds.boundsContains(pos)) {
					occupied = true;
					ActionStack actions = unit.getActionStack();

					if(actions.running() instanceof MoveToAction && ((MoveToAction)actions.running()).TrueTarget == this)
						continue;
					Map map = Map.get();
					Point tileXY = Map.toTile(position);
					int tx = (int) tileXY.X + 1;
					int ty = (int) tileXY.Y + Type.heightInTiles;
					Point target = null;// = bounds.getBorderingPoint(pos);

					for(int x = tx - Type.widthInTiles; x < tx; x++) {
						if(!map.getTileFlag(x, ty, Map.TILE_OCCUPIED_FLAG)) {
							target = Map.fromTile(x, ty);
							break;
						} else if(!map.getTileFlag(x, (int)tileXY.Y - 1, Map.TILE_OCCUPIED_FLAG)) {
							target = Map.fromTile(x, (int)tileXY.Y - 1);
							break;
						}
					}
					if(target == null) {
						int tx2 = tx - Type.widthInTiles - 1;
						for(int y = ty - Type.heightInTiles; y < ty; y++) {
							if(!map.getTileFlag(tx2, y, Map.TILE_OCCUPIED_FLAG)) {
								target = Map.fromTile(tx2, y);
								break;
							} else if(!map.getTileFlag(tx, y, Map.TILE_OCCUPIED_FLAG)) {
								target = Map.fromTile(tx, y);
								break;
							}
						}
						continue;
					}

					MoveToAction act = new MoveToAction(unit, target);
					act.pauseTime = 0.5f;
					act.TrueTarget = this;
					actions.insert(act);
				}
			}
			areaEmpty = !occupied;
			if(areaEmpty) {
				Point tileXY = Map.toTile(position);
				int tx = (int) tileXY.X + 1;
				int ty = (int) tileXY.Y + Type.heightInTiles;

				for(int x = tx - Type.widthInTiles; x < tx; x++)
					for(int y = ty - Type.heightInTiles; y < ty; y++) 
						Map.get().setTileFlag(x, y, TileConstants.TILE_OCCUPIED_FLAG, Type != BuildingType.Farm);
			}
		}

		if(Compleation >= TotalBuildTime) {
			SelectableEntity newEnt;
			if(Type == BuildingType.Farm) {
				Farm f;
				Map.get().addEntity(newEnt = f = new Farm(getPosition()));
				List<Unit> units = Selection.getUnits();

				for(int i = 0; i < units.size(); i++) {
					Unit u = units.get(i);
					if(u instanceof Villager) {
						ActionStack acts = ((Villager)u).getActionStack();
						if(acts.running() instanceof BuildAction && ((BuildAction)acts.running()).Site == this) {
							((Villager)u).getActionStack().set(new CollectResourceAction((Villager)u, f));
							break;
						}
					}
				}

			} else {
				Map.get().addEntity(newEnt = new Building(Player.thisMachinesPlayer, getPosition(), Type));
			}
			remove();
			if(Selection.contains(newEnt))
				Selection.set(newEnt);
		}
	}
	public void clearOutBuildingSite() {

	}

	@Override
	public String getDisplayName() {
		return Type.name().replace('_', ' ') + " Site"; 
	}

	@Override
	public String[] getInfo() {
		return new String[] { };
	}
}
