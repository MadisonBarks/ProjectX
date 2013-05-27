package com.focused.projectf.audio;

import org.lwjgl.openal.AL10;

import com.focused.projectf.resources.SoundClip;

public class ActiveCue {
	public SoundClip Clip;
	public int Source;
	public float X, Y;
	public boolean Omnipresnet = false;
	public ActiveCue(SoundClip clip) {
		this(clip, SoundManager.ListenerPos.get(0), SoundManager.ListenerPos.get(1), 1, 1, false);
	}

	public ActiveCue(SoundClip clip, float x, float y, float gain, float pitch, boolean looping) {
		Clip = clip;
		Source = SoundManager.takeSource();
		AL10.alSourceUnqueueBuffers(Source);
		AL10.alSourceQueueBuffers(Source, Clip.BufferId);
		
		if(X == Float.NaN && Y == Float.NaN) {
			X = x;
			Y = y;
			AL10.alSource3f(Source, AL10.AL_POSITION, 
					SoundManager.ListenerPos.get(0), 
					SoundManager.ListenerPos.get(1), 0);
		} else {
			AL10.alSource3f(Source, AL10.AL_POSITION, X = x, Y = y, 0);
		}
		AL10.alSourcei(Source, AL10.AL_LOOPING, (looping)? AL10.AL_TRUE : AL10.AL_FALSE);
		AL10.alSourcef(Source, AL10.AL_GAIN, gain);
		AL10.alSourcef(Source, AL10.AL_PITCH, pitch);
		play();
	}

	public void play() { 
		AL10.alSourcePlay(Source); 
	}
	public void pause() { 
		AL10.alSourcePause(Source); 
	}
	public void stop() { 
		AL10.alSourceStop(Source);
		SoundManager.ActiveCues.remove(this);
		SoundManager.InactiveSources.add(Source);
	}
}
