package com.focused.projectf.resources.loaders;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Texture;
import com.focused.projectf.utilities.FMath;

public class TextureLoader {

	public static ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[] {8,8,8,8},
			true,
			false,
			ComponentColorModel.TRANSLUCENT,
			DataBuffer.TYPE_BYTE);

	public static ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[] {8,8,8,0},
			false,
			false,
			ComponentColorModel.OPAQUE,
			DataBuffer.TYPE_BYTE);

	public static BufferedImage loadImage(String ref) { 
		try {
			URL url = Content.getUrlForResource(ref);
			BufferedImage bufferedImage = ImageIO.read(url); 
			if(bufferedImage == null)
				ErrorManager.logWarning("Unable to load image: " + url.toString(), null);
			return bufferedImage;
		} catch(Exception ex) {
			ErrorManager.logWarning("Unable to load image: " + ref, ex);
			return null;
		}
	}

	public static ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture) { 
		int texWidth	= FMath.nextPowerOf2(bufferedImage.getWidth());
		int texHeight	= FMath.nextPowerOf2(bufferedImage.getHeight());
		texture.setWidth(bufferedImage.getWidth(), texWidth);
		texture.setHeight(bufferedImage.getHeight(), texHeight);
		return convertImageData(bufferedImage);
	} 

	public static ByteBuffer convertImageData(BufferedImage bufferedImage) {
		ByteBuffer imageBuffer = null; 
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth	= FMath.nextPowerOf2(bufferedImage.getWidth());
		int texHeight	= FMath.nextPowerOf2(bufferedImage.getHeight());

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
		g.translate(0, texHeight);
		AffineTransform t = AffineTransform.getScaleInstance(1,-1);
		g.drawImage(bufferedImage, t, null);

		// build a byte buffer from the temporary image that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 

		imageBuffer = ByteBuffer.allocateDirect(data.length); 
		imageBuffer.order(ByteOrder.nativeOrder()); 
		imageBuffer.put(data, 0, data.length); 
		imageBuffer.flip();

		return imageBuffer; 
	}
}
