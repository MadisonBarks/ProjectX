package com.focused.projectf.utilities;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

/**
 * Keeps track of the game time. All game elements which are subject to pausing should
 * check the time update here using getElapsed().
 */
public class TimeKeeper {
	
	private static boolean isPaused;
	
	private static float elapsed;
	private static float pElapsed;
	private static long prev;
	
	public static int FramesPerSecond;
	private static int frames;
	private static long lastFPS;
	
	private static long ms;
	private static int animMS;	
	
	private static final long baseMS = System.currentTimeMillis();

	public static float tick() {
		long now = Sys.getTime();
		elapsed = (float)((double)(now - prev) / Sys.getTimerResolution());
		pElapsed = (isPaused) ? 0 : elapsed;
		ms = (long) (pElapsed * 1000);
		prev = now;
		
		animMS = (int) Math.abs(baseMS - System.currentTimeMillis());
		
		frames++;
		if(now - lastFPS > Sys.getTimerResolution()) {
			Display.setTitle("FPS: " + frames);
			FramesPerSecond = frames;
			frames = 0;
			lastFPS = now;
		}
		return elapsed;
	}
	/** 
	 * returns the amount of time that has elapsed since the last TimeKeeper.tick() call.
	 * Frame counter is not updated, and no alerts are sent out (May be needed in the future).
	 */
	public static float silentTick() {
		long now = Sys.getTime();
		return (float)((double)(now - prev) / Sys.getTimerResolution());
	}
	
	public static long getMillis() { return ms; }
	
	/**
	 * returns the amount of time in seconds that has elapsed since the last draw cycle.
	 * returns 0 if the game is paused 
	 */
	public static float getElapsed() { return pElapsed; }
	/** 
	 * returns the amount of time in seconds that has elapsed since the last draw cycle,
	 * regardless of pause state 
	 */
	public static float getTrueElapsed() { return elapsed; }
	/**
	 * Pauses the game. 
	 */
	public static void pause() {
		isPaused = true;
	}
	/**
	 * Unpauses the game
	 */
	public static void unpause() {
		isPaused = false;
	}
	
	public static void setPaused(boolean paused) {
		isPaused = paused;
	}
	/**
	 * @return weather or not the game is currently paused
	 */
	public static boolean isPaused() { return isPaused; }
	/**
	 * A timing value used for animations. 
	 */
	public static int getAnimMS() {
		return animMS;
	}
}
