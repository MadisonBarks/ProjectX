package com.focused.projectf;

import java.util.ArrayList;
import java.util.Random;

import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.players.Player;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.random.Chance;
import com.focused.projectf.utilities.random.PerlinNoiseGenerator;

public class MapGenerator implements TileConstants {


	public static Random rnd = new Random();

	public static Map generateMap(MapType type, MapSize size, long seed) {
		rnd.setSeed(seed);
		return generateMap(type, size);
	}

	public static Map generateMap(MapType type, MapSize size) {

		long startTime = System.currentTimeMillis();
		short[][] tiles;

		ArrayList<short[][]> landMasses = null;
		ArrayList<int[]> landMassLocations = new ArrayList<int[]>();
		switch(type) {
			case Islands:
				landMasses = createLandMasses(size);
				tiles = arrangeMasses(landMasses, landMassLocations, size);
				break;
			case Land:
				tiles = createLand(size);
				break;
			default:
				throw new Error();
		}		


		fillSpaces(tiles, type);
		removeSmallLakes(tiles);
		makeCoasts(tiles);

		Map map = new Map(tiles, size, type);

		addTreesUsingPerlin(map);

		addResources(map);


		for(Player p : Player.ConnectedPlayers) {
			int x, y;
			if(landMasses != null && landMasses.size() > 0) {
				landMasses.remove(0);
				int[] loc = landMassLocations.remove(0);			
				x = loc[0] + 38;
				y = loc[1] + 38;

			} else {
				x = rnd.nextInt(map.getWidthInTiles() - 75) + 38;
				y = rnd.nextInt(map.getWidthInTiles() - 75) + 38;
			}

			if(p == Player.getThisPlayer()) 
				Canvas.setCenter(Map.fromTile(x, y));

			insertTown(x, y, p, map);
		}

		long endTime = System.currentTimeMillis();
		ErrorManager.logDebug("Map generated in " + (endTime - startTime) + " milliseconds (" + type.name() + ", " + size.name() + ")");

		return map;
	}

	public static void insertTown(int tileX, int tileY, Player unitOwners, Map map) {
		tileX = FMath.clamp(tileX, map.getWidthInTiles() - 10, 10);
		tileY = FMath.clamp(tileY, map.getHeightInTiles() - 10, 10);

		final int RADIUS = 6;

		for(int x = tileX - RADIUS + 1; x < tileX + RADIUS; x++) {
			for(int y = tileY - RADIUS + 1; y < tileY + RADIUS; y++)  {
				map.setTileX(x, y, Map.GRASS);
				map.setTileFlag(x, y, Map.TILE_OCCUPIED_FLAG, false);
				ResourceElement res = map.resources[x][y];
				if(res != null)
					map.getResources().remove(res);
				map.resources[x][y] = null;
			}
		}
		/*
		List<MapElement> elements = map.getMapElements();
		for(int i = 0; i < elements.size(); i++)  {
			MapElement e = elements.get(i);
			if(e.isSolid) {
				Point tile = Map.toTile(e.getPosition());
				if(tile.X > tileX - RADIUS && tile.X < tileX + RADIUS && tile.Y > tileY - RADIUS && tile.Y < tileY + RADIUS) {
					elements.remove(i--);
					//map.resources
				}
			}
		}

		List<Entity> elements2 = map.getOtherEntities();
		for(int i = 0; i < elements2.size(); i++)  {
			Entity e = elements2.get(i);
			Point tile = Map.toTile(e.getPosition());
			if(tile.X > tileX - RADIUS && tile.X < tileX + RADIUS && tile.Y > tileY - RADIUS && tile.Y < tileY + RADIUS)
				elements2.remove(i--);

		}
		 */
		map.addEntity(new Villager(unitOwners, Map.fromTile(tileX + 3, tileY + 2)));
		map.addEntity(new Villager(unitOwners, Map.fromTile(tileX + 3, tileY)));
		map.addEntity(new Villager(unitOwners, Map.fromTile(tileX + 3, tileY - 2)));

		map.addEntity(new Building(unitOwners, Map.fromTile(tileX, tileY), BuildingType.Town_Square));
	}

	/**
	 * Adds trees to the map using a perlin noise map. 
	 */
	private static void addTreesUsingPerlin(Map map) {
		PerlinNoiseGenerator.init();
		float mult = map.Type.TreePatchSizeFactor * map.Size.Size / 200f;
		float lowerBound = map.Type.TreeLowerBound;
		int w = map.getWidthInTiles();
		int h = map.getHeightInTiles();
		for(int x = 0; x < w; x++) {
			for(int y = 0; y < h; y++) {
				if(map.getTileType(x, y) != Map.WATER) {
					if(!map.isCoastLine(x, y)) {

						int sides = 0; 
						try {
							sides += (map.isCoastLine(x + 1, y)) ? 1 : 0;
							sides += (map.isCoastLine(x - 1, y)) ? 1 : 0;
							sides += (map.isCoastLine(x, y + 1)) ? 1 : 0;
							sides += (map.isCoastLine(x, y - 1)) ? 1 : 0;
							sides += (map.isCoastLine(x + 1, y + 1)) ? 1 : 0;
							sides += (map.isCoastLine(x - 1, y + 1)) ? 1 : 0;
							sides += (map.isCoastLine(x + 1, y - 1)) ? 1 : 0;
							sides += (map.isCoastLine(x - 1, y - 1)) ? 1 : 0;
						} catch(Exception ex) {
							sides = 1;
						}
						if(sides == 0 && PerlinNoiseGenerator.tileableNoise2(x * mult, y * mult, w, h) > lowerBound)  {
							map.resources[x][y] = 
									ResourceElement.insertTree(x, y, 100, rnd.nextInt(4));
						}
					}
				}
			}
		}
	}

	public static void addResources(Map map) {
		for(int i = 0; i < 500; i++) {
			int x = rnd.nextInt(map.getWidthInTiles() - 5) + 2;
			int y = rnd.nextInt(map.getHeightInTiles() - 5) + 2;

			if(map.getTileType(x, y) != WATER && 
					!map.getTileFlag(x, y, COAST_LINE_TILE_FLAG) && 
					!map.getTileFlag(x, y, TILE_OCCUPIED_FLAG)) {

				ResourceType type = (new ResourceType[] { ResourceType.Stone, ResourceType.Gold, ResourceType.Radium })[rnd.nextInt(12) / 5];

				map.resources[x][y] =
						ResourceElement.insertResource(x, y, type,								
								Chance.random(new int[] { 400,  800}, new int[] { 5, 4 }));
			}
		}
	}

	private static ArrayList<short[][]> createLandMasses(MapSize mapSize) {
		ErrorManager.logDebug("Map is being generated");
		ArrayList<short[][]> landMasses = new ArrayList<short[][]>();
		short[][] tempArray;
		long startTime = 0;
		int numOfIslands = mapSize.NumberOfIslands;

		startTime = System.currentTimeMillis();
		for(int i = 0; i < numOfIslands - 1; i++) {
			int xsize = Chance.randomInRange(65, 95);
			int ysize = Chance.randomInRange(65, 95);
			tempArray = new short[xsize][ysize];
			short currTile[] = new short[2];
			tempArray[(int) Math.floor(xsize / 2)][(int) Math.floor(ysize / 2)] = GRASS;
			for(int y=1; y <= 10; y++) {
				currTile[0] = (short) Math.floor(xsize / 2);
				currTile[1] = (short) Math.floor(ysize / 2);
				for(int i1=0; i1 < (xsize*ysize); i1++) {
					int direction = Chance.nextInt(8);
					switch(direction) {
						case 0:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[0] = (short) (currTile[0] - 1);
							break;
						case 1:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[0] = (short) (currTile[0] - 1);
							currTile[1] = (short) (currTile[1] - 1);
							break;
						case 2:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[1] = (short) (currTile[1] - 1);
							break;
						case 3:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[0] = (short) (currTile[0] + 1);
							currTile[1] = (short) (currTile[1] - 1);
							break;
						case 4:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[0] = (short) (currTile[0] + 1);
							break;
						case 5:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[0] = (short) (currTile[0] + 1);
							currTile[1] = (short) (currTile[1] + 1);
							break;
						case 6:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[1] = (short) (currTile[1] + 1);
							break;
						case 7:
							if(currTile[0] >= xsize || currTile[1] >= ysize || currTile[0] < 0 || currTile[1] < 0) {
								break;
							}
							tempArray[currTile[0]][currTile[1]] = GRASS;
							currTile[0] = (short) (currTile[0] - 1);
							currTile[1] = (short) (currTile[1] + 1);
							break;
					}
				}
			}
			landMasses.add(tempArray);
		}
		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;
		ErrorManager.logDebug("Map has been generated in " + timeTaken + " milliseconds");
		return landMasses;
	}

	private static short[][] createLand(MapSize mapSize) {
		short[][] tempArray = new short[mapSize.Size][mapSize.Size];
		short currTile[] = new short[2];
		tempArray[(int) Math.floor(mapSize.Size / 2)][(int) Math.floor(mapSize.Size / 2)] = GRASS;
		for(int y=1; y <= 10; y++) {
			currTile[0] = (short) Math.floor(mapSize.Size / 2);
			currTile[1] = (short) Math.floor(mapSize.Size / 2);
			for(int i1=0; i1 < (mapSize.Area); i1++) {
				int direction = rnd.nextInt(8);
				switch(direction) {
					case 0:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[0] = (short) (currTile[0] - 1);
						break;
					case 1:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[0] = (short) (currTile[0] - 1);
						currTile[1] = (short) (currTile[1] - 1);
						break;
					case 2:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[1] = (short) (currTile[1] - 1);
						break;
					case 3:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[0] = (short) (currTile[0] + 1);
						currTile[1] = (short) (currTile[1] - 1);
						break;
					case 4:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[0] = (short) (currTile[0] + 1);
						break;
					case 5:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[0] = (short) (currTile[0] + 1);
						currTile[1] = (short) (currTile[1] + 1);
						break;
					case 6:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[1] = (short) (currTile[1] + 1);
						break;
					case 7:
						if(currTile[0] >= mapSize.Size || currTile[1] >= mapSize.Size || currTile[0] < 0 || currTile[1] < 0) {
							break;
						}
						tempArray[currTile[0]][currTile[1]] = GRASS;
						currTile[0] = (short) (currTile[0] - 1);
						currTile[1] = (short) (currTile[1] + 1);
						break;
				}
			}
		}
		return tempArray;
	}

	private static short[][] arrangeMasses(ArrayList<short[][]> landMasses, ArrayList<int[]> landMassLocations, MapSize mapSize) {
		int size = mapSize.Size;
		short[][] map = new short[size][size];
		short[][] tempArray;
		for(int i=0; i < landMasses.size(); i++) {
			int xValue = rnd.nextInt(size - 75);
			int yValue = rnd.nextInt(size - 75);
			landMassLocations.add(new int[] { xValue, yValue });
			tempArray = landMasses.get(i);
			for(int x=0; x < tempArray.length; x++) {
				for(int y=0; y < tempArray[x].length - 1; y++) {
					if(x + xValue <= size - 1 && y + yValue <= size - 1 && map[x + xValue][y+yValue] != GRASS)
						map[x + xValue][y + yValue] = tempArray[x][y];
				}
			}
		}
		return map;
	}

	private static void fillSpaces(short[][] map, MapType mapType) {
		for(int x = 0; x < map.length; x++)
			for(int y = 0; y < map[x].length; y++)
				if(map[x][y] == UNSET_TILE) 
					map[x][y] = mapType.FillInTile;
	}

	private static void removeSmallLakes(short[][] map) {
		for(int x=1; x < map.length - 1; x++) 
			for(int y=1; y < map[x].length - 1; y++) 
				if(map[x][y] == WATER) 
					if(map[x-1][y] == GRASS && map[x-1][y-1] == GRASS && map[x-1][y+1] == GRASS && map[x][y-1] == GRASS && map[x+1][y] == GRASS && map[x+1][y+1] == GRASS && map[x][y+1] == GRASS && map[x+1][y-1] == GRASS)
						map[x][y] = GRASS;
	}

	private static void makeCoasts(short[][] map) {
		for(int x = 0; x < map.length; x++) 
			for(int y = 0; y < map[x].length; y++) 
				if(map[x][y] == GRASS) 
					if(x - 1 > 0 && y - 1 > 0 && y + 1 < map[0].length && x + 1 < map.length) 
						if(map[x-1][y] == WATER || map[x][y-1] == WATER || map[x+1][y] == WATER || map[x][y+1] == WATER)
							map[x][y] |= COAST_LINE_TILE_FLAG;
	}
}
