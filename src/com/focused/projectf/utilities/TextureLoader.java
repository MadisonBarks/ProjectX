package com.focused.projectf.utilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Texture;

/**
 * A utility class to load textures for LWJGL. This source is based
 * on a texture that can be found in the Java Gaming (www.javagaming.org)
 * Wiki. 
 * 
 * OpenGL uses a particular image format. Since the images that are 
 * loaded from disk may not match this format this loader introduces
 * a intermediate image which the source image is copied into. In turn,
 * this image is used as source for the OpenGL texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class TextureLoader {

	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	/** The color model including alpha for the GL image */
	private static ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[] {8,8,8,8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE);


	/** The color model for the GL image */
	private static ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[] {8,8,8,0},
			false,
			false,
			ComponentColorModel.OPAQUE,
			DataBuffer.TYPE_BYTE);

	/**
	 * Load a texture
	 *
	 * @param resourceName The location of the resource to load
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(String resourceName) {
		ErrorManager.logInfo("Loading texture: " + resourceName);
		Texture tex = getTexture(resourceName,
				GL11.GL_TEXTURE_2D, // target
				GL11.GL_RGBA,     // dst pixel format
				GL11.GL_LINEAR, // min filter (unused)
				GL11.GL_NEAREST);

		return tex;
	}
	/**
	 * @param mipmap if true, mipmaps will be generated for the loaded texture 
	 * if OpenGL 3.0 is supported on this machine. Otherwise, it will be ignored
	 */
	public Texture getTexture(String resourceName, boolean mipmap) {
		ErrorManager.logInfo("Loading texture: " + resourceName);
		Texture tex = getTexture(resourceName,
				GL11.GL_TEXTURE_2D, // target
				GL11.GL_RGBA,     // dst pixel format
				GL11.GL_LINEAR, // min filter (unused)
				GL11.GL_NEAREST);
		if(mipmap) {
			if(GLContext.getCapabilities().OpenGL30) {
				GL30.glGenerateMipmap(tex.getId());
			} else {
				ErrorManager.logDebug("Cannot generate mipmaps. OpenGL 3.0 is not supported");
			}
		}
		return tex;
	}
	/**
	 * Load a texture into OpenGL from a image reference on
	 * disk.
	 *
	 * @param resourceName The location of the resource to load
	 * @param target The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter The minimizing filter
	 * @param magFilter The magnification filter
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(String resourceName,	int target, int dstPixelFormat, int minFilter, int magFilter) { 

		int textureID = GL11.glGenTextures(); 
		Texture texture = new Texture(target, textureID); 
		GL11.glBindTexture(target, textureID); 

		try {
			BufferedImage bufferedImage = loadImage(new URL(resourceName)); 
			if(bufferedImage == null)
				throw new Error();

			int srcPixelFormat = GL11.GL_RGBA;
			if(bufferedImage.getColorModel() != null && !bufferedImage.getColorModel().hasAlpha()) 
				srcPixelFormat = GL11.GL_RGB;

			ByteBuffer textureBuffer = convertImageData(bufferedImage, texture); 

			if (target == GL11.GL_TEXTURE_2D)  { 
				GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter); 
				GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter); 
			} 

			GL11.glTexImage2D(target, 
					0, 
					dstPixelFormat, 
					get2Fold(bufferedImage.getWidth()), 
					get2Fold(bufferedImage.getHeight()), 
					0, 
					srcPixelFormat, 
					GL11.GL_UNSIGNED_BYTE, 
					textureBuffer ); 

		} catch (Exception ex) {
			ex.printStackTrace();        	
		}
		textures.put(resourceName, texture);
		return texture; 
	} 

	public static Texture makeTexture(ByteBuffer textureBuffer, int target, int srcPixelFormat,
			int dstPixelFormat, int minFilter, int magFilter, int imageWidth, int imageHeight, int bufferDataType) {

		int textureID = GL11.glGenTextures(); 
		Texture texture = new Texture(target, textureID); 

		GL11.glBindTexture(target, textureID); 
		GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter); 
		GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter); 
		GL11.glTexImage2D(target, 
				0, 
				dstPixelFormat, 
				get2Fold(imageWidth), 
				get2Fold(imageHeight), 
				0, 
				srcPixelFormat, 
				bufferDataType, 
				textureBuffer); 

		return texture;
	}

	public static Texture makeTexture(String resourceName, ByteBuffer textureBuffer, int target, int srcPixelFormat,
			int dstPixelFormat, int minFilter, int magFilter, int imageWidth, int imageHeight, int bufferDataType) {

		Texture texture = makeTexture(resourceName, textureBuffer, target, srcPixelFormat, dstPixelFormat, minFilter,
				magFilter, imageWidth, imageHeight, bufferDataType);
		textures.put(resourceName, texture);
		return texture;
	}

	/**
	 * Get the closest greater power of 2 to the fold number
	 * 
	 * @param fold The target number
	 * @return The power of 2
	 */
	public static int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	} 

	/**
	 * Convert the buffered image to a texture and writes size info into the provided texture.
	 *
	 * @param bufferedImage The image to convert to a texture
	 * @param texture The texture to store the data into
	 * @return A buffer containing the data
	 */
	public static ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture) { 
		int texWidth, texHeight;
		if(GLContext.getCapabilities().GL_ARB_texture_non_power_of_two) {
			texWidth	= FMath.nextPowerOf2(bufferedImage.getWidth());
			texHeight	= FMath.nextPowerOf2(bufferedImage.getHeight());
		} else {
			texWidth	= bufferedImage.getWidth();
			texHeight	= bufferedImage.getHeight();			
		}
		texture.setWidth(bufferedImage.getWidth(), texWidth);
		texture.setHeight(bufferedImage.getHeight(), texHeight);
		return convertImageData(bufferedImage);
	} 

	public static ByteBuffer convertImageData(BufferedImage bufferedImage) {
		ByteBuffer imageBuffer = null; 
		WritableRaster raster;
		BufferedImage texImage;
		int texWidth, texHeight;
		if(Content.NonPowersOf2TextureSizesSupported) {
			texWidth	= FMath.nextPowerOf2(bufferedImage.getWidth());
			texHeight	= FMath.nextPowerOf2(bufferedImage.getHeight());
		} else {
			texWidth	= bufferedImage.getWidth();
			texHeight	= bufferedImage.getHeight();			
		}

		// create a raster that can be used by OpenGL as a source for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false, null);
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false, null);
		}

		// copy the source image into the produced image
		Graphics2D g = (Graphics2D) texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 

		imageBuffer = ByteBuffer.allocateDirect(data.length); 
		imageBuffer.order(ByteOrder.nativeOrder()); 
		imageBuffer.put(data, 0, data.length); 
		imageBuffer.flip();

		return imageBuffer; 
	}

	/** 
	 * Load a given resource as a buffered image
	 * 
	 * @param ref The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException Indicates a failure to find a resource
	 */
	public static BufferedImage loadImage(URL url) { 
		try {
			BufferedImage bufferedImage = ImageIO.read(url); 
			if(bufferedImage == null)
				ErrorManager.logWarning("Unable to load image: " + url.toString(), null);
			return bufferedImage;
		} catch(Exception ex) {
			ErrorManager.logWarning("Unable to load image: " + url.getFile(), ex);
			return null;
		}
	}

	/**
	 * Creates an integer buffer to hold specified ints
	 * - strictly a utility method
	 *
	 * @param size how many int to contain
	 * @return created IntBuffer
	 */
	protected IntBuffer createIntBuffer(int size) {
		ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());

		return temp.asIntBuffer();
	}    
}
