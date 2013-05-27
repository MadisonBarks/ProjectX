package com.focused.projectf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.focused.projectf.ai.pathfinding.custom.Mover;
import com.focused.projectf.ai.pathfinding.custom.TileBasedMap;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Entity;
import com.focused.projectf.entities.Farm;
import com.focused.projectf.entities.MapElement;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.SelectableEntity;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.TimeKeeper;

public class Map implements Serializable, TileBasedMap, TileConstants {

	private static final long serialVersionUID = 8342288250259863457L;

	private short[][] tiles;
	public ResourceElement[][] resources;
	/**
	 * for mulitithreading reasons, fog of war data is kept in a seperate array. 
	 * only one bit is used at a time, but which bit decides 
	 */
	private byte[][] fogOfWar;

	private int FogOfWarBit = 0;
	private byte FOWFilter;
	public final MapSize Size;
	public final MapType Type;

	private ArrayList<Entity> entities;
	private ArrayList<ResourceElement> resourceList;
	private ArrayList<Building> buildings;
	private ArrayList<MapElement> mapElements;
	private ArrayList<Unit> units;

	public Map(short[][] mapTiles, MapSize size, MapType type) {
		tiles = mapTiles;
		fogOfWar = new byte[mapTiles.length][mapTiles[0].length];
		Size = size;
		Type = type;
		entities = new ArrayList<Entity>();
		buildings = new ArrayList<Building>();
		mapElements = new ArrayList<MapElement>();
		resourceList = new ArrayList<ResourceElement>();
		units = new ArrayList<Unit>();
		resources = new ResourceElement[getWidthInTiles()][getHeightInTiles()];

		instance = this;
	}

	public short getTileType(float x, float y) { return (short) (tiles[(int)x][(int)y] & TILE_TYPE_FILTER); }
	public short getTileType(int x, int y) { return (short) (tiles[x][y] & TILE_TYPE_FILTER); }
	public short getTileFlags(int x, int y) { return (short) (tiles[x][y] & ~TILE_TYPE_FILTER); }
	public boolean getTileFlag(int x, int y, short flag) { return (tiles[x][y] & flag) != 0; }

	public short getRawTile(int x, int y) { return tiles[x][y]; }

	public boolean isTileDiscovered(int x, int y) {
		return (tiles[x][y] & TILE_DISCOVERED_FILTER) != 0;
	}
	public boolean isTileDiscovered(Point pos) {
		return isTileDiscovered((int)pos.X, (int)pos.Y);
	}
	public boolean isTileVissible(int x, int y) {
		return (fogOfWar[x][y] & FOWFilter) != 0;
	}

	public boolean isTileVissible(Point tile) {
		return isTileVissible((int)tile.X, (int)tile.Y);
	}
	public boolean isCoastLine(int x, int y) {
		return (tiles[x][y] & COAST_LINE_TILE_FLAG) != 0;
	}
	public boolean isCoastLineSafe(int x, int y) {
		return (tiles[FMath.clamp(x, tiles.length - 1, 0)][FMath.clamp(x, tiles[0].length - 1, 0)] & COAST_LINE_TILE_FLAG) != 0;
	}

	public float sinceLastScanTick = 0.3f;

	public void update() {
		sinceLastScanTick += TimeKeeper.getTrueElapsed();
		if(sinceLastScanTick > 0.3f) {			
			sinceLastScanTick -= 0.3f;

			int FOWBit = (FogOfWarBit + 1) % 8;

			byte set	= (byte) (1 << FOWBit);
			byte clear 	= (byte) ~set;

			// set all tiles to not visible

			for(int x = tiles.length - 1; x >= 0; x--)
				for(int y = tiles.length - 1; y >= 0; y--)
					fogOfWar[x][y] &= clear;

			// set tiles to visible which units can see
			for(Unit unit : units) {
				if(!unit.getOwner().OnThisMachine()) 
					continue;

				Point center = Map.toTile(unit.getPosition());
				int radius = (int)(unit.getStats().RangeOfSight + 2) / 2;
				radius *= 2;
				int radiusSq = radius * radius - 1;
				int x = (int) center.X, y = (int) center.Y;

				for (int yo = -radius; yo <= radius; yo++) {
					float yoSq = yo * yo;
					for (int xo = -radius; xo <= radius; xo++)
						if ((xo * xo) + yoSq <= radiusSq) {
							int gx = FMath.clamp(x + xo, tiles.length - 1, 0);
							int gy = FMath.clamp(y + yo, tiles[0].length - 1, 0);
							fogOfWar[gx][gy] |= set;
							tiles[gx][gy] |= (short)(TILE_VISSIBLE_FILTER | TILE_DISCOVERED_FILTER);
						}
				}
			}

			for(Building building : buildings) {
				if(!building.getOwner().OnThisMachine()) continue;

				Point center = Map.toTile(building.getPosition().minus(0, tileHeight * (building.Type.widthInTiles)));
				int radius = 8; // TODO: (int) building.Type.getRangeOfSight() + 3;
				int radiusSq = radius * radius - 1;
				int x = (int) center.X, y = (int) center.Y;

				for (int yo = -radius; yo <= radius; yo++) {
					float yoSq = yo * yo;
					for (int xo = -radius; xo <= radius; xo++)
						if ((xo * xo) + yoSq <= radiusSq) {
							int gx = FMath.clamp(x + xo, tiles.length - 1, 0);
							int gy = FMath.clamp(y + yo, tiles[0].length - 1, 0);
							fogOfWar[gx][gy] |= set;
							tiles[gx][gy] |= (short)(TILE_VISSIBLE_FILTER | TILE_DISCOVERED_FILTER);
							//tiles[FMath.clamp(x + xo, tiles.length - 1, 0)]
							//		[FMath.clamp(y + yo, tiles[0].length - 1, 0)] |= (short)(TILE_VISSIBLE_FILTER | TILE_DISCOVERED_FILTER);
						}
				}
			}
			FogOfWarBit = FOWBit;
			FOWFilter = (byte) (1 << FogOfWarBit);
		}
	}

	/**
	 * Sets the type of tile at x,y on the map. Remember, (0,0) is the center of the map and 
	 * x and y are not gamespace coordinates, but tile indexes.
	 */
	public void setTile(int x, int y, short tileValue) {
		y += tiles.length / 2;
		x += tiles[0].length / 2;
		// info about the tile being discovered and visible is preserved
		tiles[x][y] = (short) (tileValue | (tiles[x][y] & ~TILE_TYPE_FILTER));
	}
	public void setTileX(int x, int y, short tileValue) {
		tiles[x][y] = (short) (tileValue | (tiles[x][y] & ~TILE_TYPE_FILTER));
	}
	public void setTile(Point pt, short tileValue) {
		Point tile = toTile(pt);
		int x = (int)tile.X + tiles[0].length / 2;
		int y = (int)tile.Y + tiles.length / 2;
		// info about the tile being discovered and visible is preserved
		tiles[x][y] = (short) (tileValue | (tiles[x][y] & ~TILE_TYPE_FILTER));
	}

	public void setTileFlag(int x, int y, short flag, boolean flagEnabled) {
		if(flagEnabled) 
			tiles[x][y] |= flag;
		else
			tiles[x][y] &= ~flag;
	}

	// ----------------------------------------------------------------------------------------------------- //
	// ------------------------------------  ArrayList Function  ------------------------------------------- //
	// ----------------------------------------------------------------------------------------------------- //

	public void setBuldingList(ArrayList<Building> newList) {
		buildings = newList;
	}
	public void setEntityList(ArrayList<Entity> newList) {
		entities = newList;
	}
	public void setUnitList(ArrayList<Unit> newList) {
		units = newList;
	}
	public void setMapElementList(ArrayList<MapElement> newList) {
		mapElements = newList;
	}

	// ----------------------------------------------------------------------------------------------------- //
	// ------------------------------------  Conversion Methods  ------------------------------------------- //
	// ----------------------------------------------------------------------------------------------------- //


	/**
	 * returns the X,y index of the tile which the provided Gamespace Point is on.
	 * returned values can be used with <code>setTile()</code> and <code>getTile()</code>
	 */
	public static Point toTile(Point p) {
		Point out = new Point();
		toTile(p.X, p.Y, out);
		return out;
	}

	public static Point toTile(float x, float y) {
		Point out = new Point();
		toTile(x, y, out);
		return out;
	}


	public static Point toTileSmooth(Point p) {
		Point out = new Point();
		float x = p.X + tileHalfWidth;
		out.Y = ((x / tileWidth - (p.Y - tileHalfHeight) / tileHeight) + Map.get().getHeightInTiles() / 2) - 1;
		out.X = (Map.get().getWidthInTiles() - out.Y + x / tileHalfWidth) - 1;
		return out;
	}

	public static void toTile(float x, float y, Point out) {		
		x += tileHalfWidth;
		float yf = ((x / tileWidth - (y - tileHalfHeight) / tileHeight) + Map.get().getHeightInTiles() / 2) - 1;
		float xf = (Map.get().getWidthInTiles() - yf + x / tileHalfWidth) - 1;
		Map map = Map.get();
		out.X = (int)FMath.clamp(xf, map.getWidthInTiles() - 1, 0);
		out.Y = (int)FMath.clamp(yf, map.getHeightInTiles() - 1, 0);
	}

	/**
	 * Same as toTile() but returned coordinates are not clamped to real tile indexes, meaning the 
	 * values returned would represent a tile outside of the map
	 */
	public static Point toTileUnclamped(Point p) {
		Point out = new Point();
		toTileUnclamped(p.X, p.Y, out);
		return out;
	}
	public static void toTileUnclamped(float x, float y, Point out) {		
		x += tileHalfWidth;
		float yf = ((x / tileWidth - (y - tileHalfHeight) / tileHeight) + Map.get().getHeightInTiles() / 2) - 1;
		out.X = (int)(Map.get().getWidthInTiles() - yf + x / tileHalfWidth) - 1;
		out.Y = (int)yf;
	}

	/**
	 * Returns the game space point at the center of tile x,y
	 * Caution: Screws up with negative tile indexes. Don't worry about it, it's probably never going to be an issue
	 */
	public static Point fromTile(int x, int y) {
		Point p = new Point();
		fromTile(x, y, p);
		return p;
	}
	public static Point fromTile(Point pt) {
		Point p = new Point();
		fromTile((int)pt.X, (int)pt.Y, p);
		return p;
	}
	public static void fromTile(int x, int y, Point out) {
		out.Y = -(y - x) * (float)Map.tileHalfHeight;
		out.X = (x + y - Map.get().getWidthInTiles() + 1) * Map.tileHalfWidth;
	}

	/**
	 * Attempts to round to the nearest tile and returns that tiles center.
	 */
	public static Point roundToTileCoord(Point position) {
		Point p = toTile(position);
		p = fromTile(p);
		return p;
	}


	// ----------------------------------------------------------------------------------------------------- //
	// -----------------------------------  Pathfinding methods  ------------------------------------------- //
	// ----------------------------------------------------------------------------------------------------- //


	@Override
	public int getWidthInTiles() { return tiles.length; }
	@Override
	public int getHeightInTiles() { return tiles[0].length; }

	@Override
	public void pathFinderVisited(int x, int y) { } // leave this blank

	@Override
	public boolean blocked(Mover mover, int x, int y) {
		int tile = getTileType(x, y);

		if(mover == null) 
			return getTileFlag(x, y, TILE_OCCUPIED_FLAG) || tile == WATER;

		if(mover.airborn())	return false;
		if(getTileFlag(x, y, TILE_OCCUPIED_FLAG)) return true;

		boolean mayPass = false;

		if(mover.canTransverseLand())
			mayPass |= (tile != WATER);		
		if(mover.canTransverseWater())	
			mayPass |= (tile == WATER);

		return !mayPass;
	}
	public boolean blocked(Unit u1, Point checker) {
		Point p = toTile(checker);
		return blocked(u1, (int)p.X, (int)p.Y);
	}

	@Override
	public int getCost(Mover mover, int sx, int sy, int tx, int ty) {
		if(sx == tx || sy == ty) {
			return 2;
		} else {
			return 3;
		}
	}


	// ----------------------------------------------------------------------------------------------------- //
	// ---------------------------------  Entity handling methods  ----------------------------------------- //
	// ----------------------------------------------------------------------------------------------------- //


	public int addEntity(Entity e) {
		if(e instanceof Unit) {
			units.add((Unit)e);
			return units.size();
		}
		else if(e instanceof Building) {
			Building building = (Building) e;
			building.setUID(buildings.size() + 1);
			buildings.add(building);
			return buildings.size();
		}
		else if(e instanceof MapElement) {
			mapElements.add((MapElement)e);
			return mapElements.size();
		}
		else if(e instanceof ResourceElement && !(e instanceof Farm)) {
			resourceList.add((ResourceElement) e);
			return -1; // I don't think you understand how ArrayLists work. 
		}
		else {
			entities.add(e);
			return entities.size();
		}
	}
	public void removeEntity(Entity e) {
		if(e instanceof Unit) {
			units.remove(e);

		} else if(e instanceof MapElement) {
			mapElements.remove(e);

		} else if(e instanceof Building) {
			((Building)e).updateMap(false);
			buildings.remove(e);
		} else if(e instanceof ResourceElement && !(e instanceof Farm)) {
			resourceList.remove((ResourceElement) e);
			Point tile = toTile(e.getPosition());
			setTileFlag((int)tile.X, (int)tile.Y, TILE_OCCUPIED_FLAG, false);
			resources[(int)tile.X][(int)tile.Y] = null; // remove from tile aligned
			
		} else {
			entities.remove(e);

		}
	}

	public void removeEntity(int entityUID, int entityType) {
		switch(entityType) {
			case Consts.ENTITY_TYPE_BUILDING:
				buildings.remove(entityUID);
				break;
			case Consts.ENTITY_TYPE_MAPELEMENT:
				mapElements.remove(entityUID);
				break;
			case Consts.ENTITY_TYPE_UNIT:
				units.remove(entityUID);
				break;
			case Consts.ENTITY_TYPE_OTHER:
				entities.remove(entityUID);
				break;
		}
		// XXX: I don't think you quite understand how ArrayLists work. 
	}

	public Entity getEntityAtPoint(Point gamePoint) {

		Point tile = Map.toTile(gamePoint);
		Map map = Map.get();
		if(map.resources[(int)tile.X][(int)tile.Y] != null)
			return map.resources[(int)tile.X][(int)tile.Y];

		for(Unit e : units)
			if(e.getBounds().boundsContains(gamePoint))
				return e;
		for(Building e : buildings)
			if(e.getBounds().boundsContains(gamePoint))
				return e;
		for(MapElement e : mapElements)
			if(e.getBounds().boundsContains(gamePoint))
				return e;
		for(ResourceElement e : resourceList)
			if(e.getBounds().boundsContains(gamePoint))
				return e;

		for(Entity e : entities)
			if(e.getBounds().boundsContains(gamePoint))
				return e;


		return null;
	}

	public List<SelectableEntity> getEntitiesInRegion(Rect region) {
		ArrayList<SelectableEntity> ents = new ArrayList<SelectableEntity>();
		for(Unit e : units)
			if(region.contains(e.getBounds().getCenter()))
				ents.add(e);
		if(ents.size() == 0)  {
			for(Building e : buildings)
				if(e.getBounds().boundsIntersects(region)) {
					ents.add(e);
					return ents;
				}
		}
		return ents;
	}	
	public List<SelectableEntity> getEntitiesInRegion(Point start, Point end) {
		Rect region = new Rect(start, end);
		return getEntitiesInRegion(region);
	}
	public List<SelectableEntity> getEntitiesNear(Point point, float distSq) {
		ArrayList<SelectableEntity> ents = new ArrayList<SelectableEntity>();

		for(Unit e : units)
			if(e.getBounds().getCenter().distSq(point) < distSq)
				ents.add(e);

		if(ents.size() == 0)  {
			for(Building e : buildings) {
				if(e.getBounds().getCenter().distSq(point) < distSq) {
					ents.add(e);
				}
			}
		}

		for(Entity e : entities)
			if(e instanceof SelectableEntity)
				if(e.getBounds().getCenter().distSq(point) < distSq) {
					ents.add((SelectableEntity) e);

				}

		return ents;
	}

	public List<Unit> getUnits() { return units; }
	public List<Building> getBuildings() { return buildings; }
	public List<MapElement> getMapElements() { return mapElements; }
	public List<ResourceElement> getResources() { return resourceList; }
	public List<Entity> getOtherEntities() { return entities; }


	// --------------- others methods ----------------- //

	private static Object instance;
	public static Map get() { return (Map)instance; }

	public boolean safeblocked(Mover unit, int x,int y) {
		if(x < 0 || y < 0 || x >= getWidthInTiles() || y >= getHeightInTiles())
			return true;

		return blocked(unit, x, y);
	}

	public boolean isAccessable(int x, int y) {
		return !(safeblocked(null, x + 1, y) &&
				safeblocked(null, x - 1, y) &&
				safeblocked(null, x, y + 1) &&
				safeblocked(null, x, y - 1));
	}
}