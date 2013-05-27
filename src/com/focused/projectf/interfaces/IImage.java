package com.focused.projectf.interfaces;

import com.focused.projectf.resources.Texture;

public interface IImage {
	public Texture getTexture();
	public int getWidth();
	public int getHeight();
	public float getAspectRatio();
}
