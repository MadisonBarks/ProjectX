package com.focused.projectf.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Rect;
import com.focused.projectf.TileConstants;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.shaders.ShaderProgram;

public class TileRendering implements TileConstants {

	public static ShaderProgram Shader;
	public static Map map;
	public static Image tileMask;
	public static Image[] tileImages;

	public static void loadResources() {
		tileImages = new Image[7];
		tileImages[TileConstants.GRASS] 	= Content.getImage("tiles/grass1.png");
		tileImages[TileConstants.SAND] 		= Content.getImage("tiles/sand1.png");
		tileImages[TileConstants.WATER] 	= Content.getImage("tiles/water1.png");

		tileMask 							= Content.getImage("tiles/tile-mask.png");		
//		if(Shader != null)
//			Shader.dispose();
		Shader = null;
	}

	public static void renderTiles(Rect clip) {
		map = Map.get();
		if(Shader != null && Shader.isLoaded()){// && !UserProfile.ActiveProfile.ShadeTiles) {
			renderMapShaderNew(clip);
		} else {
			Shader = new ShaderProgram("shaders/mapTiling-new.glsl");
			renderMap(clip);
		}
		ErrorManager.GLErrorCheck();
	}

	public static void renderMapShaderNew(Rect clip) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		boolean onlyOnce = true;
		Shader.bind();
		Shader.bindTexture("mask", 1, tileMask);
		Shader.setUniformFloats("mapWidthInTiles", Map.get().getWidthInTiles());
		Shader.setUniformFloats("mapHeightInTiles", Map.get().getHeightInTiles());
		GL11.glGetError();

		final float overlap = 11f / 8f;

		float x0, x1, x2, x3;
		float y0, y1, y2, y3;
		
		int startX 	= (int) Map.toTile(clip.getTopLeft()).X;
		int startY 	= (int) Map.toTile(clip.getBottomLeft()).Y;
		int endX 	= (int) Map.toTile(clip.getBottomRight()).X;
		int endY 	= (int) Map.toTile(clip.getTopRight()).Y;

		for(int tileX = startX; tileX < endX; tileX++) {
			for(int tileY = startY; tileY < endY; tileY++) {
				float ty = -(tileY - tileX) * Map.tileHalfHeight;
				float tx = (tileX + tileY - map.getWidthInTiles()) * Map.tileHalfWidth + Map.tileHalfWidth;

				if(!clip.contains(tx, ty)) continue;
				int img = map.getTileType(tileX, tileY);
				if(img == 0) continue;

				boolean discovered = map.isTileDiscovered(tileX, tileY);
				if(!discovered)							 					GL11.glColor3f(0.15f, 0.15f, 0.15f);
				else if(!map.isTileVissible(tileX, tileY)) 					GL11.glColor3f(0.5f, 0.5f, 0.5f);
				else if(map.getTileFlag(tileX, tileY, TILE_OCCUPIED_FLAG))	Color.RED.bind();
				else 														GL11.glColor3f(1, 1, 1);


				// offset the drawing so that it lines up with tile boundries (post mixing)
				tx += 4 * overlap;
				ty -= overlap; 

				x0 = tx + Map.tileHalfWidth * overlap;
				y0 = ty;
				x1 = tx;
				y1 = ty - Map.tileHalfHeight * overlap;
				x2 = tx - Map.tileHalfWidth * overlap;
				y2 = ty;
				x3 = tx;
				y3 = ty + Map.tileHalfHeight * overlap;

				boolean coast = map.getTileFlag(tileX, tileY, COAST_LINE_TILE_FLAG);

				while(true) { 
					Shader.bindTexture("img", 0, tileImages[(onlyOnce && coast)? SAND : img]);
					GL11.glGetError();
					ErrorManager.GLErrorCheck();

					GL11.glBegin(GL11.GL_QUADS); {
						GL11.glVertex3f(x0, y0, 0);
						GL11.glVertex3f(x1, y1, 1);
						GL11.glVertex3f(x2, y2, 2);
						GL11.glVertex3f(x3, y3, 3);

					} GL11.glEnd();
					ErrorManager.GLErrorCheck();

					if(!(coast && onlyOnce) || !discovered) 
						break;	// jump to next tile

					// changes required for drawing the center of coast line tiles
					final float coastOverlap = Map.tileHalfWidth * (overlap - 1f) * 0.55f;
					final float coastOverlap2 = coastOverlap / 2;

					x0 -= overlap * 4f;
					x1 -= overlap * 4f;
					x2 -= overlap * 4f;
					x3 -= overlap * 4f;
					
					if(map.getTileType(tileX, tileY - 1) == Map.WATER) { // tile to the left
						x2 += coastOverlap;
						y2 -= coastOverlap2;
						x3 += coastOverlap;
						y3 -= coastOverlap2;							
					} if(map.getTileType(tileX, tileY + 1) == Map.WATER) { // tile to the right
						x0 -= coastOverlap;
						y0 += coastOverlap2;
						x1 -= coastOverlap;
						y1 += coastOverlap2;							
					} if(map.getTileType(tileX - 1, tileY) == Map.WATER) {	// tile above
						x1 += coastOverlap;
						y1 += coastOverlap2;
						x2 += coastOverlap;
						y2 += coastOverlap2;						
					} if(map.getTileType(tileX + 1, tileY) == Map.WATER) {	// tile below
						x0 -= coastOverlap;
						y0 -= coastOverlap2;
						x3 -= coastOverlap;
						y3 -= coastOverlap2;							
					}
					onlyOnce = false;
				}
				onlyOnce = true;
			}
		}
		Image.unbind(1);
		Image.unbind(0);		
		Shader.unbind();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void renderMapShader(Rect clip) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		boolean onlyOnce = true;
		Shader.bind();
		Shader.bindTexture("mask", 1, tileMask);
		Shader.setUniformFloats("mapWidthInTiles", Map.get().getWidthInTiles());
		Shader.setUniformFloats("mapHeightInTiles", Map.get().getHeightInTiles());
		GL11.glGetError();

		final float overlap = 11f / 8f;
		float[] x = new float[4];
		float[] y = new float[4];

		int startX 	= (int) Map.toTile(clip.getTopLeft()).X;
		int startY 	= (int) Map.toTile(clip.getBottomLeft()).Y;
		int endX 	= (int) Map.toTile(clip.getBottomRight()).X;
		int endY 	= (int) Map.toTile(clip.getTopRight()).Y;

		for(int tileX = startX; tileX < endX; tileX++) {
			for(int tileY = startY; tileY < endY; tileY++) {
				float ty = -(tileY - tileX) * Map.tileHalfHeight;
				float tx = (tileX + tileY - map.getWidthInTiles()) * Map.tileHalfWidth + Map.tileHalfWidth;

				if(!clip.contains(tx, ty)) continue;
				int img = map.getTileType(tileX, tileY);
				if(img == 0) continue;

				if(!map.isTileDiscovered(tileX, tileY)) 					GL11.glColor3b((byte)0, (byte)0, (byte)0);
				else if(!map.isTileVissible(tileX, tileY)) 					GL11.glColor3f(0.5f, 0.5f, 0.5f);
				else if(map.getTileFlag(tileX, tileY, TILE_OCCUPIED_FLAG))	Color.RED.bind();
				else 														GL11.glColor3f(1, 1, 1);


				// offset the drawing so that it lines up with tile boundries (post mixing)
				tx += 4 * overlap;
				ty -= overlap; 

				x[0] = tx + Map.tileHalfWidth * overlap;
				y[0] = ty;
				x[1] = tx;
				y[1] = ty - Map.tileHalfHeight * overlap;
				x[2] = tx - Map.tileHalfWidth * overlap;
				y[2] = ty;
				x[3] = tx;
				y[3] = ty + Map.tileHalfHeight * overlap;

				boolean coast = map.getTileFlag(tileX, tileY, COAST_LINE_TILE_FLAG);

				while(true) { 
					Shader.bindTexture("img", 0, tileImages[(onlyOnce && coast) ? SAND : img]);
					GL11.glGetError();
					ErrorManager.GLErrorCheck();

					GL11.glBegin(GL11.GL_QUADS); {

						GL11.glVertex3f(x[0], y[0], 0);
						GL11.glVertex3f(x[1], y[1], 1);
						GL11.glVertex3f(x[2], y[2], 2);
						GL11.glVertex3f(x[3], y[3], 3);

					} GL11.glEnd();
					ErrorManager.GLErrorCheck();

					if(!(coast && onlyOnce)) 
						break;	// jump to next tile

					// changes required for drawing the center of coast line tiles
					final float coastOverlap = Map.tileHalfWidth * (overlap - 1f) * 0.65f;
					final float coastOverlap2 = coastOverlap / 2;

					x[0] -= overlap * 5f;
					x[1] -= overlap * 5f;
					x[2] -= overlap * 5f;
					x[3] -= overlap * 5f;

					if(map.getTileType(tileX, tileY - 1) == Map.WATER) { // tile to the left
						x[2] += coastOverlap;
						y[2] -= coastOverlap2;
						x[3] += coastOverlap;
						y[3] -= coastOverlap2;							
					} if(map.getTileType(tileX, tileY + 1) == Map.WATER) { // tile to the right
						x[0] -= coastOverlap;
						y[0] += coastOverlap2;
						x[1] -= coastOverlap;
						y[1] += coastOverlap2;							
					} if(map.getTileType(tileX - 1, tileY) == Map.WATER) {	// tile above
						x[1] += coastOverlap;
						y[1] += coastOverlap2;
						x[2] += coastOverlap;
						y[2] += coastOverlap2;						
					} if(map.getTileType(tileX + 1, tileY) == Map.WATER) {	// tile below
						x[0] -= coastOverlap;
						y[0] -= coastOverlap2;
						x[3] -= coastOverlap;
						y[3] -= coastOverlap2;							
					}
					onlyOnce = false;
				}
				onlyOnce = true;
			}
		}
		Image.unbind(1);
		Image.unbind(0);		
		Shader.unbind();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void renderMap(Rect clip) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
		Color.bindWhite();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL20.glUseProgram(0);
		boolean onlyOnce = true;

		for(int tileX = 0; tileX < map.getWidthInTiles(); tileX++) {
			for(int tileY = 0; tileY < map.getHeightInTiles(); tileY++) {
				float ty = -(tileY - tileX) / 2f * Map.tileHeight;
				float tx = (tileX + tileY - map.getWidthInTiles()) / 2f * Map.tileWidth + Map.tileHalfWidth;

				if(!clip.contains(tx, ty)) continue;
				int img = map.getTileType(tileX, tileY);				
				if(img == 0) continue;
				if(!map.isTileDiscovered(tileX, tileY)) GL11.glColor3b((byte)0, (byte)0, (byte)0);
				if(!map.isTileVissible(tileX, tileY)) 	GL11.glColor4f(0.5f, 0.5f, 0.5f, 1);
				else 									GL11.glColor4f(1, 1, 1, 1);

				float overlap = 11f / 8f;
				float[] x = new float[4];
				float[] y = new float[4];

				x[0] = tx + Map.tileHalfWidth * overlap;
				y[0] = ty;
				x[1] = tx;
				y[1] = ty - Map.tileHalfHeight * overlap;
				x[2] = tx - Map.tileHalfWidth * overlap;
				y[2] = ty;
				x[3] = tx;
				y[3] = ty + Map.tileHalfHeight * overlap;

				while(true) { 
					//  This code will only happen once in most cases. If the tile being drawn is Map.COAST,
					//  it will be repeated with the other tile once and then skipped. This could have been 
					//  done without a loop, but using the loop saved about 30 lines of code so whatever

					// calculates the texture coordinates to the 
					float[] v = new float[4], h = new float[4];

					for(int i = 0; i < 4; i++) {
						v[i] = ((x[i] / Map.tileWidth - (y[i] - Map.tileHalfHeight) / Map.tileHeight) - 1) * Map.tileStepX;
						h[i] = ((Map.get().getWidthInTiles() - v[i] + x[i] / Map.tileHalfWidth) - 1) * Map.tileStepX;
					}

					GL14.glBlendFuncSeparate(GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_SRC_COLOR, GL11.GL_ZERO);	// lay down the mask alpha
					tileMask.bind();
					GL11.glBegin(GL11.GL_QUADS);
					; GL11.glTexCoord2f(0.96f, 0.96f);
					; GL11.glVertex2f(x[0], y[0]);
					; GL11.glTexCoord2f(0.96f, 0.04f);
					; GL11.glVertex2f(x[1], y[1]);
					; GL11.glTexCoord2f(0.04f, 0.04f);
					; GL11.glVertex2f(x[2], y[2]);
					; GL11.glTexCoord2f(0.04f, 0.96f);
					; GL11.glVertex2f(x[3], y[3]);
					GL11.glEnd();

					GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA); // draw the tile using the mask outline


					if(map.getTileFlag(tileX, tileY, COAST_LINE_TILE_FLAG) && onlyOnce)
						tileImages[SAND].bind();
					else
						tileImages[img].bind();

					GL11.glBegin(GL11.GL_QUADS);
					for(int i = 0; i < 4; i++) {
						GL11.glTexCoord2f(h[i], v[i]);
						GL11.glVertex2f(x[i], y[i]);
					}
					GL11.glEnd();

					if(!(map.getTileFlag(tileX, tileY, COAST_LINE_TILE_FLAG) && onlyOnce)) break;	// jump to next tile

					// changes required for drawing the center of coast line tiles
					final float coastOverlap = Map.tileHalfWidth * (overlap - 1f) * 0.65f;
					final float coastOverlap2 = coastOverlap / 2;

					x[0] -= overlap * 5f;
					x[1] -= overlap * 5f;
					x[2] -= overlap * 5f;
					x[3] -= overlap * 5f;

					if(map.getTileType(tileX, tileY - 1) == Map.WATER) { // tile to the left
						x[2] += coastOverlap;
						y[2] -= coastOverlap2;
						x[3] += coastOverlap;
						y[3] -= coastOverlap2;							
					} if(map.getTileType(tileX, tileY + 1) == Map.WATER) { // tile to the right
						x[0] -= coastOverlap;
						y[0] += coastOverlap2;
						x[1] -= coastOverlap;
						y[1] += coastOverlap2;							
					} if(map.getTileType(tileX - 1, tileY) == Map.WATER) {	// tile above
						x[1] += coastOverlap;
						y[1] += coastOverlap2;
						x[2] += coastOverlap;
						y[2] += coastOverlap2;						
					} if(map.getTileType(tileX + 1, tileY) == Map.WATER) {	// tile below
						x[0] -= coastOverlap;
						y[0] -= coastOverlap2;
						x[3] -= coastOverlap;
						y[3] -= coastOverlap2;							
					}
					onlyOnce = false;
				}
				onlyOnce = true;
			}
		}

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glColorMask(false, false, false, true);
		// clear the alpha channel
		Canvas.fillRectangle(Canvas.getRect(), Color.WHITE);
		GL11.glColorMask(true, true, true, true);
	}
}
