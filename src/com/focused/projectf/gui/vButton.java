package com.focused.projectf.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Point;
import com.focused.projectf.audio.SoundManager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.Input;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;

public class vButton extends GUIView {

	public static interface OnClickListener {
		public void onClick(GUIView clicked);
	}

	public static final Color TOP_NORM 			= new Color(0.5f, 0.5f, 0.5f);
	public static final Color BOTTOM_NORM 		= new Color(1f,1f,1f,1f);
	public static final Color TOP_CLICK 			= new Color(1f, 0.175f, 0.175f, 0.75f);
	public static final Color BOTTOM_CLICK 		= new Color(0.125f, 0.175f, 0.175f, 0.75f);
	public static final Color TOP_HOVER 			= new Color(1f, 0.175f, 0.175f, 0.75f);
	public static final Color BOTTOM_HOVER		= new Color(0.325f, 0.175f, 0.175f, 0.75f);
	public static final Color HOVER_TINT			= new Color("55FF9500");
	public static final Color CLICK_TINT			= new Color(0x88, 0, 0, 0);

	public String HoverCueName = null;
	public String ClickCueName	= "menu-button-click";

	public Color TopColor, BottomColor;
	public Color TopHoverColor, BottomHoverColor;
	public Color TopClickColor, BottomClickColor;

	public Image Background, Hover;
	public String Text;
	public TTFont Font;
	public boolean pressed = false;
	public OnClickListener OnClick;
	private boolean hover;
	public boolean Vissible = true;
	public vButton(GUIGroup parent, Point position, float width, float height, Image img, String text, OnClickListener onClick) {
		this(parent, position.X, position.Y, UNSET, UNSET, width, height, img, text, onClick);
	}

	public vButton(GUIGroup parent, float top, float left, float bottom, float right, float width, float height,
			Image img, String text, OnClickListener onClick) {
		super(parent, top, left, bottom, right, width, height);
		Background = img;
		Hover = img;
		Text = text;
		OnClick = onClick;
		content(getHeight());
	}

	private void content(float height) {
		int lines = 1;
		for(int i = 0; i < Text.length(); i++)
			if(Text.charAt(i) == '\n')
				lines++;
		Font = Content.getFont("Arial", (int)((height * 0.8f) / lines), false, false);
		TopColor 			= TOP_NORM.clone();
		TopClickColor 		= TOP_CLICK.clone();
		TopHoverColor 		= TOP_HOVER.clone();
		BottomColor			= BOTTOM_NORM.clone();
		BottomClickColor	= BOTTOM_CLICK.clone();
		BottomHoverColor	= BOTTOM_HOVER.clone();
	}

	@Override
	public void draw(float time) {
		if(!Vissible) return;
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		hover = getViewRegion().contains(Input.getMousePosition().minus(getParentalOffset()));
		
		ErrorManager.GLErrorCheck();
		if(Background != null) {
			if(!hover) {
				Canvas.drawImage(Background, region, Color.WHITE);
			} else {
				if(Hover != null)	Canvas.drawImage(Hover, region, Color.WHITE);
				else				Canvas.drawImage(Background, region, Color.WHITE);
				if(pressed)		Canvas.fillRectangle(region, CLICK_TINT.withAlpha(0.2f));
				else			Canvas.fillRectangle(region, HOVER_TINT.withAlpha(0.2f));
			} 
		} else {
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			GL11.glBegin(GL11.GL_QUADS); {
				if(pressed) 	TopClickColor.bind(); 
				else if(hover)	TopHoverColor.bind(); 
				else 			TopColor.bind();
				GL11.glVertex2f(region.getRight(), region.getY());
				GL11.glVertex2f(region.getX(), region.getY());

				if(pressed) 	BottomClickColor.bind(); 
				else if(hover)	BottomHoverColor.bind(); 
				else 			BottomColor.bind();
				GL11.glVertex2f(region.getX(), region.getBottom());
				GL11.glVertex2f(region.getRight(), region.getBottom());
			} GL11.glEnd();

			ErrorManager.GLErrorCheck();
		}
		Font.drawText(Text, getPosition().plus(5, 0), Color.WHITE);
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if(event.Button == MouseEvent.BUTTON_LEFT && event.State == ButtonState.Pressed) {
			if(ClickCueName != null) 
				SoundManager.playCue(ClickCueName);
			onClick();
			pressed = true;
		} else {
			pressed = false;
		}
		
		if(hover ==	false) {
			if(HoverCueName != null) 
				SoundManager.playCue(HoverCueName);
			hover = true;
		}

		return true;
	}

	@Override
	public void onKeyEvent(KeyEvent event) {
		if(event.KeyId == Keyboard.KEY_RETURN)
			this.onClick();
	}

	private void onClick() {
		if(OnClick != null)
			OnClick.onClick(this);
	}

	public void update(float elapsed) { }

	@Override
	public boolean canTakeFocus() { return true; }
}
