package com.focused.projectf.resources.images;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Texture;

public class StaticImage extends Image  {

	protected Texture texture;

	public StaticImage(Texture texture) {
		this.texture = texture;
		if(texture == null)
			throw new Error();
	}

	public StaticImage() {
		try {
			texture = new Texture(GL11.GL_TEXTURE_2D, GL11.glGenTextures());
		} catch(Exception ex) {
			texture = new Texture(GL11.GL_TEXTURE_2D, Content.popTextureId());
		}
	}

	@Override
	public int getWidth() { return texture.getWidth(); }
	@Override
	public int getHeight() { return texture.getHeight(); }


	@Override
	public Texture getTexture() { return texture; }


	@Override
	public float getWidthRatio() { return texture.getWidthRatio(); }
	@Override
	public float getHeightRatio() { return texture.getHeightRatio(); }

	public void setTexture(Texture texture2) {
		texture = texture2;
	}

	@Override
	public boolean dispose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getID() { return texture.getId(); }

	@Override
	public boolean isLoaded() {
		return (texture.getWidth() != 0 && texture.getHeight() != 0);
	}
}
