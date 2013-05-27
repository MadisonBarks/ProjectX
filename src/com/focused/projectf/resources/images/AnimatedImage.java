package com.focused.projectf.resources.images;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Texture;
/**
 * An animated image. Useful for walk animations and such.
 * CAUTION: you must make a new instance of this class for each animated element, 
 * or it will cause other things playing the same animation to glitch and synchronize with
 * this instance. Don't worry about over usage, this class is only 16 bytes per instance.
 */

public class AnimatedImage extends Image {

	protected Texture Texture;
	protected long animationStartTime;
	protected float timePerFrame;
	protected int currentFrame;

	protected int FramesWide;
	protected int FramesTall;
	protected int FrameCount;

	public AnimatedImage(Texture texture, int framesWide, int framesTall, float timePerFrame) {
		this(texture, framesWide, framesTall, framesWide * framesTall, timePerFrame);
	}

	public AnimatedImage(Texture texture, int framesWide, int framesTall, int frameCount, float timePerFrame) {
		Texture = texture;
		FramesWide = framesWide;
		FramesTall = framesTall;
		FrameCount = frameCount;
		this.timePerFrame = timePerFrame;
		start();
	}	

	public AnimatedImage() {
		FrameCount = 1;
		FramesWide = 1;
		FramesTall = 1;
		try {
			Texture = new Texture(GL11.GL_TEXTURE_2D, GL11.glGenTextures());
		} catch(Exception ex) {
			Texture = new Texture(GL11.GL_TEXTURE_2D, Content.popTextureId());
		}
	}

	public int getWidth() { return Texture.getWidth() / FramesWide; }
	public int getHeight() { return Texture.getHeight() / FramesTall; }

	public int getFrameCount() { return FrameCount; }
	public float getFrameTime() { return timePerFrame; }

	/** Starts the animation over from the beginning. */
	public void start() { animationStartTime = System.nanoTime(); }

	public int getCurrentFrame() {
		long elapsed =	System.nanoTime() - animationStartTime;
		currentFrame = (int)(elapsed / timePerFrame / 1000f / 1000f) % FrameCount;
		return currentFrame;
	}

	@Override
	public Texture getTexture() {		
		getCurrentFrame();
		return Texture;
	}

	public Object clone() { return new AnimatedImage(Texture, FramesWide, FramesTall, FrameCount, timePerFrame); }

	@Override
	public float getWidthRatio() { return getWidth() / getHeight(); }
	@Override
	public float getHeightRatio() { return getHeight() / getWidth(); }

	@Override
	public void bindCoords(int corner) {
		bindCoords(corner, 1, 1);
	}
	@Override
	public void bindCoords(int corner, float x, float y) {
		float fx = currentFrame % FramesWide;
		float fy = currentFrame / FramesWide;
		fx = (fx + xCoords[corner]) / (float)(FramesWide);
		fy = (fy + yCoords[corner]) / (float)(FramesTall);
		fx = fx * x * Texture.getWidthRatio();
		fy = fy * y * Texture.getHeightRatio();
		GL11.glTexCoord2f(fx, fy);
	}

	public float getPlayTime() { return (float)(FrameCount) * (float)timePerFrame / 500f; }
	@Override
	public void setTexture(Texture tex) { Texture = tex; }
	@Override
	public int getID() { return Texture.getId(); }

	@Override
	public boolean dispose() {
		// TODO: 
		return false;
	}

	public void rebuild(float timePerFrame, int framesWide, int framesTall, int frameCount) {
		this.timePerFrame = timePerFrame;
		FramesWide = framesWide;
		FramesTall = framesTall;
		FrameCount = frameCount;
	}

	public int getFramesWide() { return FramesWide; }
	public int getFramesTall() { return FramesTall; }

	@Override
	public boolean isLoaded() {
		return FramesWide > 1 || FramesTall > 1;
	}
}