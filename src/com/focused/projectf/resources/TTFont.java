package com.focused.projectf.resources;

import java.awt.Font;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ShadowEffect;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Color;

public class TTFont implements Resource {

	public UnicodeFont font;
	public boolean Bold, Italic;
	public String FontFamily;
	public int Size;
	public boolean requiresScaling;
	
	@SuppressWarnings("unchecked")
	public TTFont(String fontName, boolean bold, boolean italic, int size) {
		int style = (bold) ? Font.BOLD : 0;
		if(italic) style |= Font.ITALIC;
		FontFamily = fontName;
		try{
			font = new UnicodeFont(new Font(fontName, style, size));
			font.addNeheGlyphs();
			font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
			font.loadGlyphs(); 
		} catch(SlickException e) {
			e.printStackTrace();
		}
		Bold = bold;
		Italic = italic;
	}

	@SuppressWarnings("unchecked")
	public TTFont(String fontName, boolean bold, boolean italic, int size, Color outlineColor, int outlineWidth) {
		int style = (bold) ? Font.BOLD : 0;
		if(italic) style |= Font.ITALIC;
		FontFamily = fontName;
		try{
			font = new UnicodeFont(new Font(fontName, style, size));
			font.addNeheGlyphs();
			
	        font.getEffects().add(new ShadowEffect(outlineColor.toAWT(), outlineWidth, outlineWidth, 1));
		    font.getEffects().add(new ShadowEffect(outlineColor.toAWT(), -outlineWidth, outlineWidth, 1f));
	        font.getEffects().add(new ShadowEffect(outlineColor.toAWT(), outlineWidth, -outlineWidth, 1f));
	        font.getEffects().add(new ShadowEffect(outlineColor.toAWT(), -outlineWidth, -outlineWidth, 1));
	        
	        font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));

			font.loadGlyphs(); 
		} catch(SlickException e) {
			e.printStackTrace();
		}
		Bold = bold;
		Italic = italic;
	}

	public TTFont() {
	}

	public TTFont(String resource) {
		this(resource.split("[-\\.]")[0],
				resource.contains("-b"),
				resource.contains("-u"),
				Integer.parseInt(resource.split("[-\\.]")[1]));
	}

	public void drawText(String text, Point pos, Color color) {
		drawText(text, pos.X, pos.Y, color);
	}

	public void drawText(String text, float x, float y, Color color) {
		if(text == null || font == null)
			return;
		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		
		if(requiresScaling) {
			GL11.glPushMatrix();
			float scale = Size / font.getLineHeight(); 
			GL11.glScalef(scale, scale, 1);
			font.drawString(x, y, text, color.toSlickColor());
			GL11.glPopMatrix();
		} else {
			font.drawString(x, y, text, color.toSlickColor());
		}
		GL11.glPopAttrib();
	}
	public void drawText(String text, float x, float y) {
		drawText(text, x, y, Color.WHITE);
	}

	/** 
	 * Draws lines of text no wider than areaWidth. 
	 * @return A rectangle containing the area which the rendered text covers.
	 */
	public Rect drawMultiLineText(String text, Point pos, float areaWidth, Color color) {
		if(font == null || text == null)
			return new Rect(pos.X, pos.Y, 0, 0);

		int lines = 0;
		float intoLine = 0;
		String[] words = text.replaceAll("\\n", " \n").split(" ");
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for(int i = 0; i < words.length; i++) {
			while(words[i].startsWith("\n")) {
				words[i] = words[i].substring(1, words[i].length());
				lines++;
				intoLine = 0;
			}
			
			float partWidth = font.getWidth(words[i]);
			if(intoLine == 0 || intoLine + partWidth < areaWidth) {
				font.drawString(pos.X + intoLine, 
						pos.Y + lines * font.getLineHeight(),
						words[i], color.toSlickColor());
				intoLine += partWidth + font.getSpaceWidth();
			} else {
				i--;
				intoLine = 0;
				lines++;
			}
		}
		
		/*
		int lines = 1, decend = 0;
		float amountIntoLine = 0;
		String[] parts = text.split(" ");

		for(int i = 0; i < parts.length; i++) {
			float partWidth = font.getWidth(parts[i]);
			if(amountIntoLine == 0) {
				if(amountIntoLine + partWidth < areaWidth) {
					font.drawString(pos.X + amountIntoLine, pos.Y + decend , parts[i], color.toSlickColor());
					amountIntoLine += partWidth + font.getSpaceWidth();
				} else {
					int chars = (int)(areaWidth / 3f / font.getSpaceWidth());
					float width = font.getWidth(parts[i].substring(0, chars));
					for(; chars < parts[i].length(); chars++) {
						width += font.getWidth(parts[i].charAt(chars) + "");
						if(width >= areaWidth) {
							font.drawString(pos.X, pos.Y + decend, parts[i].substring(0, Math.max(0, chars - 1)), color.toSlickColor());
							amountIntoLine = 0;
							decend = decend(lines++);
							parts[i] = parts[i].substring(FMath.clamp(chars , parts[i].length(), 1) - 1);
							break;
						}
					}
				}	
			} else {
				if(partWidth + amountIntoLine < areaWidth) {
					font.drawString(pos.X + amountIntoLine, pos.Y + decend, parts[i], color.toSlickColor());
					amountIntoLine += partWidth + font.getSpaceWidth();
				} else {
					i--;
					amountIntoLine = 0;
					decend = decend(lines++);
				}		
			}
		}
		 */
		return new Rect(pos.X, pos.Y, areaWidth,
				font.getLineHeight() * lines + font.getLeading() * (lines - 1));
	}

	public int getLineHeight() { return font.getLineHeight() + font.getLeading(); }

	@Override
	public boolean dispose() { return false; }

	@Override
	public int getID() { return -1; }

	public float getWidth(String text) {
		if(font != null)
			return font.getWidth(text);
		// make a guestimateion 
		return text.length() * 30;
	}

	public boolean isLoaded() {
		return font != null;
	}
}
