package com.focused.projectf.resources;

import java.net.URL;

import org.lwjgl.openal.AL10;

import com.focused.projectf.audio.WaveData;

public class SoundClip implements Resource{
	public WaveData Data;
	public int BufferId;

	public SoundClip() {
		BufferId = AL10.alGenBuffers();
	}

	public SoundClip(URL file) {
		BufferId = AL10.alGenBuffers();
		try {
			Data = WaveData.create(file);
			AL10.alBufferData(BufferId, Data.format, Data.data, Data.samplerate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean dispose() { 
		AL10.alDeleteBuffers(BufferId);
		Data.dispose();
		return true;
	}

	@Override
	public int getID() {
		return BufferId;
	}

	public void fill(WaveData dat) {
		Data = dat;
	}

	@Override
	public boolean isLoaded() { 
		return Data != null;
	}
}
