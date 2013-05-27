package com.focused.projectf.gui;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;

public class TextView extends GUIView {

	public TTFont TextFont;
	public String Text;
	public Color TextColor = Color.WHITE.clone();
	
	public TextView(GUIGroup parent, String text, float[] margins, float width, float height) {
		super(parent, margins, width, height);
		Text = text;
		internal(region.getHeight());
	}	
	public TextView(GUIGroup parent, String text, float top, float left, float bottom, float right) {
		super(parent, top, left, bottom, right, 0, 0);
		Text = text;
		internal(region.getHeight());
	}
	public TextView(GUIGroup parent, String text, float x, float y, int fontSize) {
		super(parent, y, x, UNSET, 1, UNSET, fontSize * 1.1f);
		Text = text;
		TextFont = Content.getFont("Arial", fontSize, false, false);
	}
	public TextView(GUIGroup parent, String text, float top, float left, float bottom, float right, float width, float height) {
		super(parent, top, left, bottom, right, width, height);
		Text = text;
		internal(region.getHeight());
	}
	public TextView(GUIGroup parent, String text, float top, float left, float bottom, float right, int fontSize) {
		super(parent, top, left, bottom, right, 1, 1);
		Text = text;
		TextFont = Content.getFont("Arial", fontSize, false, false);
	}
	public TextView(GUIGroup parent, String text, float x, float y, float height) {
		super(parent, x, y, 10, height);
		Text = text;
		internal(height);
	}

	private void internal(float height) {
		if(height < 2)
			height = 14;
		
		int lines = 1;
		for(int i = 0; i < Text.length(); i++)
			if(Text.charAt(i) == '\n')
				lines++;
		int fontSize = (int)Math.max(5, ((height * 0.8f) / lines));
		TextFont = Content.getFont("Arial", fontSize, false, false);
	}
	
	@Override
	public void draw(float time) {
		if(!Float.isNaN(getWidth()) && getWidth() > 5)
			TextFont.drawMultiLineText(Text, getPosition(), getWidth(), TextColor);
		else
			TextFont.drawText(Text, getPosition(), TextColor);
		TextColor.toHex();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		return false;
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
	}

	@Override
	public boolean canTakeFocus() { return false; }
}