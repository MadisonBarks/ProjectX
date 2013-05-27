package com.focused.projectf;

public interface TileConstants {
	public static final short TILE_TYPE_FILTER 			= 0x00FF; 
	public static final short TILE_DISCOVERED_FILTER 	= 0x1000; 
	public static final short TILE_VISSIBLE_FILTER 		= 0x4000;

	public static final short COAST_LINE_TILE_FLAG		= 0x0F00;
	/** A building, tree, rock, or other non-movable object is covering a tile */
	public static final short TILE_OCCUPIED_FLAG		= 0x2000;

	public static final short UNSET_TILE 	= 0;
	
	public static final short WATER 		= 1; 
	public static final short GRASS 		= 2; 
	public static final short SAND 			= 3; 
	
	

	public static final int tileWidth 	= 64,	tileHalfWidth 	= tileWidth / 2;
	public static final int tileHeight 	= 32,	tileHalfHeight 	= tileHeight / 2;
	public static final float tileSlope 	= (float)tileWidth / (float)tileHeight;
	public static final float tileStepX	= 1 / 8f;
	public static final float tileStepY	= 1 / 8f;
	
	
	public enum MapType {
		Land,
		Coastal,
		Islands,
		Lake; 
		
		public final short FillInTile;
		public final float TreeLowerBound;
		public final float TreePatchSizeFactor;
		MapType() {
			this(TileConstants.WATER);		
		}
		MapType(short fit) {
			FillInTile = fit;
			TreeLowerBound = 0.07f;
			TreePatchSizeFactor = 1 / 28f;
		}
		
	}
	public enum MapSize {
		Tiny		(100,	3),
		Small		(200,	6),
		Medium		(300,	12),
		Large		(400,	14),
		Huge		(500,	18),	
		Collosal	(750,	25);
		
		public final float UnitPointSize;
		public final int Size, NumberOfIslands;
		public final int Area;
		MapSize(int size, int numberOfIslands) {
			Size = size;
			Area = Size * Size;
			NumberOfIslands = numberOfIslands;
			UnitPointSize = size / 100f;
		}
	}
}
