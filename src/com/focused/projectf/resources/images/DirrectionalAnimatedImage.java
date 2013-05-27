package com.focused.projectf.resources.images;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.resources.Texture;
import com.focused.projectf.utilities.FMath;

public class DirrectionalAnimatedImage extends AnimatedImage {
	
	public static final int DOWN 			= 0; 
	public static final int DOWN_RIGHT	= 1; 
	public static final int RIGHT			= 2; 
	public static final int UP_RIGHT		= 3; 
	public static final int UP 			= 4; 
	public static final int UP_LEFT		= 5; 
	public static final int LEFT 			= 6; 
	public static final int DOWN_LEFT		= 7; 
	
	private int dirrection;
	
	public DirrectionalAnimatedImage(Texture texture, int framesPerAnimation, float timePerFrame) {
		super(texture, framesPerAnimation, 8, framesPerAnimation, timePerFrame);
	}
	
	public void setDirrection(int d) {
		dirrection = FMath.wrap(d, 0, FramesTall - 1);
	}

	public int getFrameCount() { return FramesTall; }
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
	
	public Object clone() {
		return new AnimatedImage(Texture, FramesWide, FramesTall, FrameCount, timePerFrame);
	}

	@Override
	public void bindCoords(int corner) {
		bindCoords(corner, 1, 1);
	}
	@Override
	public void bindCoords(int corner, float x, float y) {
		float fx = currentFrame % FramesWide;
		float fy = dirrection;
		fx = (fx + xCoords[corner]) / (float)(FramesWide);
		fy = (fy + yCoords[corner]) / (float)(FramesTall);
		fx = fx * x * Texture.getWidthRatio();
		fy = (1f - (fy * y) * Texture.getHeightRatio());
		GL11.glTexCoord2f(fx, fy);
	}

	public float getPlayTime() {
		return (float)(FrameCount) * (float)timePerFrame / 500f;
	}
}
