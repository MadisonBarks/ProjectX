package com.focused.projectf.audio;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALCcontext;
import org.lwjgl.openal.ALCdevice;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Point;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.SoundClip;
import com.focused.projectf.utilities.IOUtils;
import com.focused.projectf.utilities.TimeKeeper;

public final class SoundManager {

	private static Hashtable<String, SoundCue> SoundCues;
	public static Vector<ActiveCue> ActiveCues;
	public static Vector<Integer> InactiveSources;

	public static ALCdevice Device;
	public static ALCcontext Context;

	protected static int BGMSourceId;
	private static float BGMFadeRate = 0;
	private static float BGMFadeVolume = 0;
	private static int BGMTargetState = -1;
	private static float BGMTargetVolume = 1;

	protected static FloatBuffer ListenerPos;
	protected static FloatBuffer ListenerVel;  
	protected static FloatBuffer ListenerOri;

	public static final int PAUSED 				= AL10.AL_PAUSED;
	public static final int PLAYING 				= AL10.AL_PLAYING;
	public static final int STOPPED 				= AL10.AL_STOPPED;
	public static final int STOPPING 				= AL10.AL_STOPPED + 1;
	public static final int PAUSING 				= AL10.AL_STOPPED + 2;

	public static void initialize() {
		try {
			AL.create();
			Device = AL.getDevice();
			Context = AL.getContext();

			SoundCues = new Hashtable<String, SoundCue>();
			ActiveCues = new Vector<ActiveCue>();
			InactiveSources = new Vector<Integer>();
			ListenerPos = FloatBuffer.wrap(new float[] { 0.0f, 0.0f, 0.0f });
			ListenerVel = FloatBuffer.wrap(new float[] { 0.0f, 0.0f, 0.0f });
			ListenerOri = FloatBuffer.wrap(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		BGMSourceId = AL10.alGenSources();
	}

	/**
	 * Loads a set of sound clips. File should end with .scs
	 * @param soundClipDefFile
	 */
	public static void loadClipSet(URL soundClipDefFile) {
		BufferedReader reader = IOUtils.openBufferedReader(soundClipDefFile);
		try { 
			String cueName = null;
			ArrayList<String> SoundClipFiles = new ArrayList<String>();
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("cue:")) {
					if(cueName != null) {
						SoundClip[] clips = new SoundClip[SoundClipFiles.size()];
						for(int i = 0; i < clips.length; i++)
							clips[i] = new SoundClip(Content.getUrlForResource(SoundClipFiles.get(i)));

						SoundClipFiles.clear();
						SoundCues.put(cueName, new SoundCue(clips));
					}
					cueName = line.split(":")[1].trim();
				} else if(line.startsWith("\t[")) {
					// TODO: cue varriables
				} else if(line.startsWith("\t")) {
					SoundClipFiles.add(line.trim());
				} else {
					continue;
				}
			}

			if(cueName != null) {
				SoundClip[] clips = new SoundClip[SoundClipFiles.size()];
				for(int i = 0; i < clips.length; i++)
					clips[i] = new SoundClip(Content.getUrlForResource(SoundClipFiles.get(i)));

				SoundClipFiles.clear();
				SoundCues.put(cueName, new SoundCue(clips));
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return;
	}

	public void pauseAll() {
		for(ActiveCue cue : ActiveCues)
			cue.pause();
	}

	public void resumeAll() {
		for(ActiveCue cue : ActiveCues)
			cue.play();
	}

	public void stopAll() {
		for(ActiveCue cue : ActiveCues)
			cue.stop();
	}

	public static void update() {
		float elapsed = TimeKeeper.getTrueElapsed();

		ListenerPos.put(0, Canvas.getCenter().X);
		ListenerPos.put(1, Canvas.getCenter().Y);

		for(int i = 0; i < ActiveCues.size(); i++) {
			ActiveCue cue = ActiveCues.get(i);
			if(AL10.alGetSourcei(cue.Source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED)
				ActiveCues.remove(i--);
		}

		if(BGMFadeRate != 0.0f) {
			BGMFadeVolume += BGMFadeRate * elapsed;
			if(BGMFadeVolume <= 0) {
				BGMFadeVolume = 0;
				BGMFadeRate = 0;

				if(BGMTargetState == AL10.AL_STOPPED)
					AL10.alSourceStop(BGMSourceId);
				else if(BGMTargetState == AL10.AL_PAUSED)
					AL10.alSourcePause(BGMSourceId);
			} else if(BGMFadeVolume >= BGMTargetVolume) {
				BGMFadeVolume = BGMTargetVolume;
				BGMFadeRate = 0;
			}
			AL10.alSourcef(BGMSourceId, AL10.AL_GAIN, BGMFadeVolume);
		}
	}

	public static ActiveCue playCue(String cueName, float x, float y, float gain, float pitch, boolean looping) {
		SoundCue cue = SoundCues.get(cueName);
		if(cue == null) {
			ErrorManager.logInfo("Unable to play cue \'" + cueName + "\'");
			return null;
		}
		ActiveCue aCue = cue.create(x, y, gain, pitch, looping);
		ActiveCues.add(aCue);
		return aCue;
	}
	/**
	 * 
	 */
	public static ActiveCue playCue(String cueName) {
		return playCue(cueName, 1, 1);
	}
	public static ActiveCue playCue(String cueName, float gain, float pitch) {
		return playCue(cueName, Float.NaN, Float.NaN, gain, pitch, false);
	}
	public static ActiveCue playCue(String cueName, Point pos) {
		return playCue(cueName, pos.X, pos.Y, 1.0f, 1.0f, false);
	}


	public static boolean startBackgroundMusic(URL fileToStream, boolean loop, float volume, float fadeInTime) {
		BGMTargetVolume = volume;
		BGMFadeVolume = 0;
		BGMFadeRate = volume / fadeInTime;
		BGMTargetState = AL10.AL_PLAYING;

		try {
			SoundClip sc = new SoundClip(fileToStream);
			AL10.alSourceQueueBuffers(BGMSourceId, sc.BufferId);
			AL10.alSourcePlay(BGMSourceId);
			AL10.alSourcei(BGMSourceId, AL10.AL_LOOPING, (loop) ? AL10.AL_TRUE : AL10.AL_FALSE);
			AL10.alSourcef(BGMSourceId, AL10.AL_GAIN, BGMFadeVolume);
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public static void stopBackgroundMusic(float fadeOutTime) {
		BGMFadeRate = -BGMTargetVolume / fadeOutTime;
		BGMTargetState = AL10.AL_STOPPED;
	}
	public static void setBackgroundMusicVolume(float volume, float fadeTime) {
		BGMFadeRate = (BGMTargetVolume - volume) / fadeTime;
		BGMTargetVolume = volume;
	}
	public static void playBackgroundMusic(float fadeInTime) {
		BGMFadeRate = BGMTargetVolume / fadeInTime;
		BGMTargetState = AL10.AL_PLAYING;
		AL10.alSourcePlay(BGMSourceId);
	}
	public static void pauseBackgroundMusic(float fadeOutTime) {
		BGMFadeRate = -BGMTargetVolume / fadeOutTime;
		BGMTargetState = AL10.AL_PAUSED;
	}
	public static int getBackgroundMusicState() {
		int state = AL10.alGetSourcei(BGMSourceId, AL10.AL_SOURCE_STATE);
		if(state == AL10.AL_PLAYING) {
			if(BGMTargetState == AL10.AL_STOPPED)
				return STOPPING;
			else if(BGMTargetState == AL10.AL_PAUSED)
				return PAUSING;
			else if(BGMTargetState == AL10.AL_PLAYING)
				return AL10.AL_PLAYING;
			else
				throw new Error("Something fucked up");
		}
		return state;
	}


	public static int takeSource() {
		if(InactiveSources.size() > 0)
			return InactiveSources.remove(InactiveSources.size() - 1);
		return AL10.alGenSources();
	}

	public static void shutDown() {
		while(ActiveCues.size() > 0)
			ActiveCues.get(0).stop();
		for(int i = 0; i < InactiveSources.size(); i++) {
			AL10.alDeleteSources(InactiveSources.get(i));
		}
		Enumeration<String> keys = SoundCues.keys();
		while(keys.hasMoreElements()) {
			SoundCue cue = SoundCues.get(keys.nextElement());
			for(int i = 0; i < cue.AssociatedClips.length; i++) {
				cue.AssociatedClips[i].dispose();
			}
		}
		AL.destroy();
	}
}