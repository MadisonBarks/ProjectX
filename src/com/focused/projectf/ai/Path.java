package com.focused.projectf.ai;

import java.util.ArrayList;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.pathfinding.custom.TilePath;

public class Path {

	protected ArrayList<Point> Points = new ArrayList<Point>();
	protected int CurrentPoint;
	public boolean ReachDestination = true;

	public Path(TilePath path, Point end) {
		for(int i = path.getLength() - 2; i >= 0; i--) 
			add(Map.fromTile(path.getStep(i).x, path.getStep(i).y));

		add(end);
		CurrentPoint = 0;
	}

	public Path(Point begin, Point end) {
		add(end);
		add(begin);
		CurrentPoint = 0;
	}

	public Path() { CurrentPoint= 0; }

	public void restart() {
		CurrentPoint = 0;
	}

	public Point getTarget() { 
		if(Points.size() != 0)
			return Points.get(Points.size() - 1); 

		return new Point();
	}

	public Point step(float elapsed, Point currently, float speed) {
		Point dirrection = Points.get(CurrentPoint).minus(currently);
		if(dirrection.lengthSq() > speed * speed)
			return dirrection.normalizeEquals().timesEquals(speed);

		return dirrection;
	}

	public boolean next() {
		CurrentPoint++;
		return CurrentPoint < Points.size();
	}

	public Point getPoint() {
		if(CurrentPoint < Points.size())
			return Points.get(CurrentPoint);
		return Points.get(Points.size() - 1);
	}

	public void add(Point pt) {
		if(pt == null)
			throw new Error();
		Points.add(0, pt);
	}

	public void insert(Point pt) {
		if(pt == null)
			throw new Error();
		Points.add(pt);
	}

	public int getCurrentPoint() {
		return CurrentPoint;
	}

	public int length() {
		return Points.size();
	}

	public Point get(int i) {
		return Points.get(i);
	}




	/**
	 * removes unneeded points and creates a much nicer, smoother path from point A to point B.
	 * This is time consuming operation, so don't run this on the main thread, especially with complex paths.
	 */
	public void simplify() {
		// Increase complexity so the solver can solve it.

		try {
			expand();
			for(int step = Points.size() - 2; step > 1; step--) {
				for(int i = 0; i < Points.size() - step; i++) {

					Point s0 = Map.toTile(Points.get(i));
					Point s2 = Map.toTile(Points.get(i + step));	

					if(s0.equals(s2)) {
						Points.remove(i + 1);
						i--;
					}
					if(!drawLine((int)s0.X, (int)s0.Y, (int)s2.X, (int)s2.Y)) {
						for(int j = 1; j < step; j++)
							Points.remove(i + 1);
						i--;
					}
				}
			}
		} catch(Exception ex) { 
			ex.printStackTrace();
		}
	}


	public void expand() {
		for(int i = 0; i < Points.size() - 1; i++) {
			Point s0 = Points.get(i);
			Point s1 = Points.get(i + 1);			

			if(s0.distSq(s1) > Map.tileWidth * Map.tileWidth * 1.5f) {
				Points.add(i + 1, new Point((s0.X + s1.X) / 2, (s0.Y + s1.Y) / 2));
				i--;
			}
		}
	}


	void plot(int x, int y, float c) {
		if(c > 0.01f)
			blocked |= Map.get().blocked(null, x, y);
		else 
			blocked |= Map.get().getTileFlag(x, y, Map.TILE_OCCUPIED_FLAG) 
			&&		   Map.get().getTileFlag(x - 1, y - 1, Map.TILE_OCCUPIED_FLAG);
	}

	//int ipart(float x) { return (int) Math.floor(x); }
	//int round(float x) { return (int)x; }
	float fpart(float x) { return x - (int) Math.floor(x); }
	//float rfpart(float x) { return 1 - fpart(x); }

	boolean blocked = false;

	private boolean drawLine(int x0, int y0, int x1, int y1) {

		blocked = false;
		boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);

		int temp = 0;

		if(steep) {
			temp = x0; x0 = y0; y0 = temp;
			temp = x1; x1 = y1; y1 = temp;
		}
		if (x0 > x1) {
			temp = x0; x0 = x1; x1 = temp;
			temp = y0; y0 = y1; y1 = temp;
		}
		float dx = x1 - x0;
		float dy = y1 - y0;
		float gradient = dy / dx;

		// handle first endpoint
		int xend = (int)x0;
		int yend = (int)(y0 + gradient * (xend - x0));
		float xgap = 1 - fpart(x0 + 0.5f);
		int xpxl1 = xend;   //this will be used in the main loop
		int ypxl1 = (int) Math.floor(yend);// ipart(yend);
		if (steep) {
			plot(ypxl1,   xpxl1, (1 - fpart(yend)) * xgap);
			plot(ypxl1+1, xpxl1,  fpart(yend) * xgap);
		} else {
			plot(xpxl1, ypxl1  , (1 - fpart(yend)) * xgap);
			plot(xpxl1, ypxl1+1,  fpart(yend) * xgap);
		}	
		float intery = yend + gradient; // first y-intersection for the main loop

		// handle second endpoint

		xend = (int)x1;
		yend = (int) (y1 + gradient * (xend - x1));
		xgap = fpart(x1 + 0.5f);
		int xpxl2 = xend; //this will be used in the main loop
		int ypxl2 = (int) Math.floor(yend);// ipart(yend);
		if (steep) {
			plot(ypxl2  , xpxl2, (1 - fpart(yend)) * xgap);
			plot(ypxl2 + 1, xpxl2,  fpart(yend) * xgap);
		} else {
			plot(xpxl2, ypxl2, (1 - fpart(yend)) * xgap);
			plot(xpxl2, ypxl2 + 1, fpart(yend) * xgap);
		}
		// main loop

		float c;
		if(steep) {
			for (int x = xpxl1 + 1; x < xpxl2; x++) {
				c = fpart(intery);
				plot((int)Math.floor(intery), x, 1 - c);
				plot((int)Math.floor(intery) + 1, x, c);
				intery += gradient;
			}
		} else {

			for (int x = xpxl1 + 1; x < xpxl2; x++) {
				c = fpart(intery);
				plot(x, (int)Math.floor(intery),  1 - c);
				plot(x, (int)Math.floor(intery) + 1, c);
				intery += gradient;
			}
		}

		return blocked;
	}
}
