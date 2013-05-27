package com.focused.projectf.resources.images;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Texture;

public class SpriteMapImage extends StaticImage {

	public float[] xCoords, yCoords;
	protected int Width, Height;
	
	public SpriteMapImage(Texture texture, Rect area) {
		this(texture, area.getXi(), area.getYi(), area.getWidthi(), area.getHeighti());
	}	
	public SpriteMapImage(Texture texture, int x, int y, int width, int height) {
		super(texture);
		
		float StartX 	= (float)x / texture.getWidth();
		float StartY 	= (float)y / texture.getHeight();
		float EndX 		= (float)(x + width) / texture.getWidth();
		float EndY 		= (float)(y + height) / texture.getHeight();
		
		xCoords = new float[] { StartX, StartX, EndX, EndX };
		yCoords = new float[] { StartY, EndY, EndY, StartY };
		
		Width = width;
		Height = height;
	}
	
	public SpriteMapImage() {
		super();
		xCoords = Image.xCoords;
		yCoords = Image.yCoords;
		
		// TODO Auto-generated constructor stub
	}
	public int getWidth() { return Width; }
	public int getHeight() { return Height; }
	
	public void bindCoords(int corner, float xFactor, float yFactor) {
		if(getTexture() == null)
			GL11.glTexCoord2f(xCoords[corner] * xFactor, (yCoords[corner]) * yFactor);
		else
			GL11.glTexCoord2f(xCoords[corner] * getWidthRatio() * xFactor, ((yCoords[corner] * getHeightRatio())) * yFactor);
	}

	public boolean isSolid = false;	
	public int DefaultBottomOffset = 0;
	public int DefaultLeftOffset = 0;

	public int getX() {
		return (int) (xCoords[1] * texture.getWidth());
	}
	public int getY() {
		return (int) (yCoords[0] * texture.getHeight());
	}
	
	public boolean isLoaded() {
		return Width != 0 && Height != 0 && super.isLoaded();
	}
}
