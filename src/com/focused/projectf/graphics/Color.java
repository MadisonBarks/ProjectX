package com.focused.projectf.graphics;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.utilities.FMath;

public class Color {

	public static final Color CLEAR 		= new Color("#00000000");

	public static final Color RED 			= new Color("#FFFF0000");
	public static final Color GREEN 		= new Color("#FF00FF00");
	public static final Color YELLOW 		= new Color("#FFFFFF00");
	public static final Color BLUE 			= new Color("#FF0000FF");
	public static final Color WHITE 		= new Color("#FFFFFFFF");
	public static final Color BLACK 		= new Color("#FF000000");
	/** black with alpha=50% */
	public static final Color HALF_BLACK	= new Color("#88000000");
	/** white with alpha=50% */
	public static final Color HALF_WHITE 	= new Color("#88FFFFFF");
	public static final Color HALF_COLOR	= new Color("#FF888888");

	public static final Color GRAY 		= Color.fromHex("#FF888888");

	private final int val;

	public Color(String hex) {
		hex = hex.replace("#", "").replace("0x", "");
		long v = Long.parseLong(hex, 16);
		if(hex.length() == 6)
			v |= 0xFF000000;
		val = (int)v;
	}

	public Color(int r, int g, int b) {
		val = 0xFF000000 | (r << 16) | (g << 8) | b;
	}
	public Color(int a, int r, int g, int b) {
		val = (a << 24) | (r << 16) | (g << 8) | b;
	}
	public Color(float alpha, float red, float green, float blue) {
		int a = (int) (alpha * 255) & 0xFF;
		int r = (int) (red * 255)   & 0xFF;
		int g = (int) (green * 255) & 0xFF;
		int b = (int) (blue * 255)  & 0xFF;

		val = (a << 24) | (r << 16) | (g << 8) | b;
	}
	public Color(float red, float green, float blue) {
		int r = (int) (red * 255f);
		int g = (int) (green * 255f);
		int b = (int) (blue * 255f);

		val = 0xFF000000 | (r << 16) | (g << 8) | b;
	}
	public Color(int argb) {
		val = argb;
	}

	public Color(Color col, float alpha) {
		val = (col.val & 0xFFFFFF) | (((int)(alpha * 255f)) << 24);
	}

	public void bind3() { 
		GL11.glColor3ub(red(), green(), blue());
	}
	public void bind(float alpha) { 
		GL11.glColor4f(glRed(), glGreen(), glBlue(), alpha);
	}
	public void bind() { 
		GL11.glColor4ub(red(), green(), blue(), alpha());
	}

	public byte alpha() 	{ return (byte) ((val >>> 24) & 0xFF); }	
	public byte red()		{ return (byte) ((val >>> 16) & 0xFF); }	
	public byte green() 	{ return (byte) ((val >>> 8 ) & 0xFF); }	
	public byte blue() 		{ return (byte) (val & 0xFF); }

	public float glAlpha()	{ return (((val >>> 16) & 0xFF00) >>> 8) / 255f; }
	public float glRed() 	{ return ((val >>> 16) & 0xFF) / 255f; }
	public float glGreen()	{ return ((val >>> 8)  & 0xFF) / 255f; }
	public float glBlue() 	{ return (val          & 0xFF) / 255f; }

	public int asInt() { return val; }

	public static final void bindWhite() { WHITE.bind(); }

	public static final Color multiply(Color a, Color b) {
		return new Color(
				a.glAlpha() * b.glAlpha(),
				a.glRed() * b.glRed(),
				a.glGreen() * b.glGreen(),
				a.glBlue() * b.glBlue());
	}
	public java.awt.Color toAWT() {
		return new java.awt.Color(glRed(), glGreen(), glBlue(), glAlpha());
	}
	public String toHex() {
		long l = val & 0x7FFFFFFFl;
		if(l != val) l |= 0x80000000l;
		return Long.toHexString(new Long(l));
	}
	public org.newdawn.slick.Color toSlickColor() {
		return new org.newdawn.slick.Color(val | 0xFF000000);
	}

	public static Color fromHex(String hex) { return new Color(hex); }

	/** 
	 * passes the color represented by the given hex string to openGL
	 */
	public static void bind(String hex) {
		hex = hex.replace("#", "").replace("0x", "");
		int val = Integer.parseInt(hex, 16);
		if(hex.length() == 6)
			val |= 0xFF000000;

		GL11.glColor4ub(
				(byte)((val >>> 16) & 0xFF),
				(byte)((val >>> 8) & 0xFF),
				(byte)(val & 0xFF),
				(byte)((val >>> 24) & 0xFF));
	}

	public Color clone() { return new Color(val); }

	public Color darken() {
		return blend(this, BLACK, 0.9f);
	}

	public static Color blend(Color c1, Color c2, float factor) {
		float inv = 1.0f - factor;
		Color col =  new Color(
				c1.glAlpha() * factor + c2.glAlpha() * inv,
				c1.glRed() * factor + c2.glRed() * inv,
				c1.glGreen() * factor + c2.glGreen() * inv,
				c1.glBlue() * factor + c2.glBlue() * inv);
		;
		return col;
	}

	public Color withAlpha(float a) {
		return new Color(this, FMath.clamp(a, 1.0f, 0.0f));
	}

	public String toString() {
		return toHex();
	}
}