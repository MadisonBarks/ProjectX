package com.focused.projectf.resources.loaders;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.Texture;
import com.focused.projectf.resources.images.StaticImage;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.TextureLoader;

public class StaticImageLoader extends ResourceLoader<StaticImage>{

	@Override
	public Object loadFromFile(String resource) {

		FileData dat = new FileData();
		URL url = Content.getUrlForResource(resource);
		BufferedImage img = TextureLoader.loadImage(url);
		if(img == null)
			throw new Error("Couldn't Load file: " + url);
		dat.buffer = TextureLoader.convertImageData(img);
		dat.imgWidth = img.getWidth();
		dat.imgHeight = img.getHeight();
		dat.srcPixelFormat = GL11.GL_RGBA;
		if(img.getColorModel() != null && !img.getColorModel().hasAlpha())
			dat.srcPixelFormat = GL11.GL_RGB;
		dat.dstPixelFormat = dat.srcPixelFormat;
		return dat;
	}

	@Override
	public void pushToOpenGL(String resource, Object fileData, Resource res) {

		FileData dat = (FileData)fileData;

		Image placeHolder = (Image)res;
		Texture texture = null;
		if(placeHolder != null)
			texture = placeHolder.getTexture();
		else
			texture = new Texture(GL11.GL_TEXTURE_2D, GL11.glGenTextures());

		int texWidth	= FMath.nextPowerOf2(dat.imgWidth);
		int texHeight	= FMath.nextPowerOf2(dat.imgHeight);
		
		GL11.glBindTexture(texture.getTarget(), texture.getId()); 
		GL11.glTexParameteri(texture.getTarget(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
		GL11.glTexParameteri(texture.getTarget(), GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); 
		GL11.glTexImage2D(texture.getTarget(), 
				0, 
				dat.dstPixelFormat, 
				texWidth, 
				texHeight, 
				0, 
				dat.srcPixelFormat, 
				GL11.GL_UNSIGNED_BYTE, 
				dat.buffer);

		texture.setWidth(dat.imgWidth, texWidth);
		texture.setHeight(dat.imgHeight, texHeight);

		if(placeHolder != null)
			placeHolder.getTexture().setId(texture.getId());

		ErrorManager.GLErrorCheck();
		ErrorManager.logInfo("Resource Loaded:\t\t" + resource);
	}

	@Override
	public boolean canLoadFile(String resource) {
		boolean matches = false;
		matches |= resource.trim().endsWith(".png");
		matches |= resource.endsWith(".jpg");
		matches |= resource.endsWith(".jpeg");
		matches |= resource.endsWith(".bmp");

		return matches;
	}

	protected static class FileData {
		public int imgWidth, imgHeight;
		public int srcPixelFormat, dstPixelFormat;
		public ByteBuffer buffer;
	}

	@Override
	public StaticImage instancePlaceHolder(String file) {
		return new StaticImage();
	}
}