package com.focused.projectf.graphics;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.Texture;


public abstract class Image implements Resource {
	public static final float[] xCoords = { 0.0f, 0.0f, 1.0f, 1.0f };
	public static final float[] yCoords = { 0.0f, 1.0f, 1.0f, 0.0f };

	public static final int[] texIndexes = {
		GL13.GL_TEXTURE0,
		GL13.GL_TEXTURE1,
		GL13.GL_TEXTURE2,
		GL13.GL_TEXTURE3,
		GL13.GL_TEXTURE4,
		GL13.GL_TEXTURE5,
		GL13.GL_TEXTURE6,
		GL13.GL_TEXTURE7,
	};

	public static final FloatBuffer DEFAULT_TEXTURE_COORDS = FloatBuffer.wrap(
			new float[] { 0, 0, 0, 1, 1, 1, 1, 0 });

	public abstract Texture getTexture();
	public abstract int getWidth();
	public abstract int getHeight();
	public abstract float getWidthRatio();
	public abstract float getHeightRatio();
	public void bind() {
		GL13.glActiveTexture(texIndexes[0]);
		if(getTexture() != null)
			GL11.glBindTexture(getTexture().getTarget(), getTexture().getId());	
		else
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	public void bind(int index) {
		GL13.glActiveTexture(texIndexes[index]);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(getTexture().getTarget(), getTexture().getId());
	}

	public static void unbind(int index) {
		GL13.glActiveTexture(texIndexes[index]);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void unbindAll() {
		int maxTextures = GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS);
		for(int i = 0; i < maxTextures; i++) {		
			GL13.glActiveTexture(texIndexes[i]);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
	}
	public void bindCoords(int corner) {
		bindCoords(corner, 1.0f, 1.0f);
	}
	public void bindCoords(int corner, float xFactor, float yFactor) {
		if(getTexture() == null)
			GL11.glTexCoord2f(xCoords[corner] *  xFactor, (yCoords[corner]) * yFactor);
		else
			GL11.glTexCoord2f(xCoords[corner] * getWidthRatio() * xFactor, ((yCoords[corner] * getHeightRatio())) * yFactor);
	}
	
	public abstract void setTexture(Texture tex);
}
