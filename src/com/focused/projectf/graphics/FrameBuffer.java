package com.focused.projectf.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.focused.projectf.Map;
import com.focused.projectf.utilities.FMath;

/**
 * Allows you to render to a texture which may then be used to render to the main buffer.
 * 
 */
public class FrameBuffer {

	public static final float[] xCoords = { 0.0f, 0.0f, 1.0f, 1.0f };
	public static final float[] yCoords = { 0.0f, 1.0f, 1.0f, 0.0f };

	public final int framebufferID, colorTextureID;
	public int ImageWidth, ImageHeight;

	protected float widthRatio, heightRatio;

	public FrameBuffer(int width, int height, boolean alpha) {
		this(width, height, (alpha)? GL11.GL_RGBA : GL11.GL_RGB);
	}

	public FrameBuffer(int width, int height) {
		this(width, height, GL11.GL_RGB);
	}

	public FrameBuffer(int width, int height, int colorFormat) {

		ImageWidth = width;
		ImageHeight = height;

		framebufferID 			= EXTFramebufferObject.glGenFramebuffersEXT();				// create a new framebuffer
		colorTextureID 			= GL11.glGenTextures();										// and a new texture used as a color buffer

		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID); 		// switch to the new framebuffer

		// initialize color texture
		GL11.glBindTexture(GL_TEXTURE_2D, colorTextureID);									// Bind the colorbuffer texture
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); 

		// if non-power-of-two texture sizes are supported, use them to reduce memory usage.
		// just about every graphics card <i>should</i> support this feature now.
		if(GLContext.getCapabilities().GL_ARB_texture_non_power_of_two) { 	
			widthRatio = 1.0f;
			heightRatio = 1.0f;		
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, colorFormat, width, height,
					0, GL11.GL_RED, GL11.GL_INT, (ByteBuffer) null);
		
		} else {
			int width2 = FMath.nextPowerOf2(width);
			int height2 = FMath.nextPowerOf2(height);
			
			widthRatio = width / (float)width2;
			heightRatio = height / (float)height2;		

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, colorFormat, width2, height2,
					0, GL11.GL_RED, GL11.GL_INT, (ByteBuffer) null);	
		}

		// attach it to the framebuffer
		EXTFramebufferObject.glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, colorTextureID, 0); 
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);	
	}


	public void bindCoords(int corner, float xFactor, float yFactor) {
		GL11.glTexCoord2f(xCoords[corner] * (Map.get().getWidthInTiles() / (float)ImageWidth) * xFactor,
				(1 - (yCoords[corner] * (Map.get().getHeightInTiles() / (float)ImageHeight))) * yFactor);
	}

	public void makeTarget() {
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);
	}

	public void revertTarget() {
		EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	public void bindAsTexture() {
		GL11.glBindTexture(GL_TEXTURE_2D, colorTextureID);
	}

	public void unBindAsTexture() {
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void bindCoorner(int corner) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); 
		GL11.glTexCoord2f(xCoords[corner], 1 - yCoords[corner]);// * widthRatio, (1 - (yCoords[corner] * heightRatio)));
	}
}