package com.focused.projectf.utilities;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


/**
 * A texture to be bound within LWJGL. This object is responsible for 
 * keeping track of a given OpenGL texture and for calculating the
 * texturing mapping coordinates of the full image.
 * 
 * Since textures need to be powers of 2 the actual texture may be
 * considerably bigger that the source image and hence the texture
 * mapping coordinates need to be adjusted to matchup drawing the
 * sprite against the texture.
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
	
	public static Texture create(int width, int height, int filter, int wrap, int colorMode) {
		int target = GL11.GL_TEXTURE_2D;
		int textureID = GL11.glGenTextures();
		ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glBindTexture(target, textureID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glTexImage2D(target, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		return new Texture(target, textureID, width, height);
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
	
	void setWidth(int realWidth, int textureWidth) {
		width = realWidth;
		texWidth = textureWidth;
		widthRatio = ((float) width) / texWidth;
	}
	
	void setHeight(int realHeight, int textureHeight) {
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
