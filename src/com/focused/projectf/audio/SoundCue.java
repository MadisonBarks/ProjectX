package com.focused.projectf.audio;

import com.focused.projectf.resources.SoundClip;
import com.focused.projectf.utilities.random.Chance;

public class SoundCue {

	public final SoundClip[] AssociatedClips;
	public final float[] Probabilities;

	public SoundCue(SoundClip[] clips) {
		AssociatedClips = clips;
		Probabilities = null;
	}

	public SoundCue(SoundClip[] clips, float[] probs) {
		if(clips.length != probs.length)
			throw new Error();
		AssociatedClips = clips;
		Probabilities = probs;
	}

	public ActiveCue create(float x, float y, float gain, float pitch, boolean looping) {

		SoundClip clip;
		if(Probabilities == null)
			clip = Chance.random(AssociatedClips);
		else
			clip = Chance.random(AssociatedClips, Probabilities);
		ActiveCue cue = new ActiveCue(clip, x, y, gain, pitch, looping);
		return cue;
	}

	public ActiveCue create(float x, float y) {
		return create(x, y, 1, 1, false);
	}
	
	public ActiveCue create() {

		SoundClip clip;
		if(Probabilities != null)
			clip = Chance.random(AssociatedClips);
		else
			clip = Chance.random(AssociatedClips, Probabilities);
		ActiveCue cue = new ActiveCue(clip);
		return cue;
	}
}
