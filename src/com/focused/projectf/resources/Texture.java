package com.focused.projectf.resources;

import org.lwjgl.opengl.GL11;


/**
 * A texture to be bound within LWJGL. This object is responsible for 
 * keeping track of a given OpenGL texture and for calculating the
 * texturing mapping coordinates of the full image.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class Texture {
	
	/** The GL target type */
	private int target; 
	private int textureID;
	private int height;
	private int width;
	private int texWidth;
	private int texHeight;
	private float widthRatio = 1;
	private float heightRatio = 1;
	
	/**
	 * Create a new texture
	 * @param target The GL target 
	 * @param textureID The GL texture ID
	 */
	public Texture(int target, int textureID) {
		this.target = target;
		this.textureID = textureID;
	}	
	public Texture(int target, int textureID, int width, int height) {
		this.target = target;
		this.textureID = textureID;
		this.width = width;
		this.height = height;
	}	
	
	public void setId(int newId) {
		textureID = newId;
	}
	
	/** @return the width of the image in pixels */
	public int getWidth() { return width; }
	/** @return the width of the image in pixels */
	public int getHeight() { return height; }
	
	/** @return height / width */
	public float getHeightRatio() { return heightRatio; }
	/** @return width / height */
	public float getWidthRatio() { return widthRatio; }
	
	public Texture getTexture() { return this; }
	
	public void setWidth(int realWidth, int textureWidth) {
		width = realWidth;
		texWidth = textureWidth;
		widthRatio = ((float) width) / texWidth;
	}
	
	public void setHeight(int realHeight, int textureHeight) {
		height = realHeight;
		texHeight = textureHeight;
		heightRatio = ((float) height) / texHeight;
	}

	public int getId() {
		return textureID;
	}
	public int getTarget() {
		return target;
	}

	public void bind() {
		GL11.glBindTexture(target, textureID);
	}
}
