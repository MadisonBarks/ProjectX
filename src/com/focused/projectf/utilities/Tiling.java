package com.focused.projectf.utilities;

import com.focused.projectf.Map;
import com.focused.projectf.Point;

public class Tiling {

	public static Point tileSpiral(int x, int y, int i) {
		int xmax = Map.get().getWidthInTiles();
		int ymax = Map.get().getHeightInTiles();
		
		if(x < xmax & y < ymax)
			return new Point(x, y); 
		return null;
	}
}	
