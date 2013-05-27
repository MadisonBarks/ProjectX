package com.focused.projectf.resources.loaders;


import com.focused.projectf.audio.WaveData;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.SoundClip;

public class SoundLoader extends ResourceLoader<SoundClip> {

	@Override
	public Object loadFromFile(String resource) {
		return null;
	}

	@Override
	public void pushToOpenGL(String resource, Object fileData, Resource placeHolder) {
		// TODO Auto-generated method stub
		SoundClip clip = (SoundClip)placeHolder;
		WaveData dat = WaveData.create(Content.getUrlForResource(resource));
		clip.fill(dat);
	}

	@Override
	public boolean canLoadFile(String resource) {
		return resource.endsWith(".wav") || resource.endsWith(".mp3");
	}

	@Override
	public SoundClip instancePlaceHolder(String file) {
		return new SoundClip();
	}
}
