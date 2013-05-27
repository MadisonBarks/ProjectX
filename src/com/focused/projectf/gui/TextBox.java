package com.focused.projectf.gui;


import org.lwjgl.input.Keyboard;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.resources.TTFont;
import com.focused.projectf.utilities.FMath;

public class TextBox extends GUIView {

	public TTFont Font;
	public String AllowedCharacters = null;

	protected String Text = "";
	protected int cursorPosition;
	public int MaxChars = 2000;
	public Color TextColor			= Color.BLACK.clone();
	public Color BorderColor		= Color.BLACK.clone();
	public Color BackgroundColor	= Color.WHITE.clone();

	public KeyPressListener KPListener;

	float tick = 0;

	public TextBox(GUIGroup parent, float top, float left, float bottom, float right) {
		this(parent, top, left, bottom, right, UNSET, 15);
	}
	public TextBox(GUIGroup parent, float[] margins, float width, float height) {
		super(parent, margins, width, height);
		Font = Canvas.Font15Bold;//Content.getFont("Arial", 16, false, false);
	}
	public TextBox(GUIGroup parent, Point point, float width, float height) {
		super(parent, point, width, height);
		Font = Canvas.Font15Bold;//Content.getFont("Arial", 16, false, false);
	}
	public TextBox(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		super(parent, top, left, bottom, right, width, height);
		Font = Canvas.Font15Bold;//Content.getFont("Arial", 16, false, false);
	}	

	@Override
	public void draw(float time) {
		Canvas.fillRectangle(region, BackgroundColor);
		Canvas.drawRectangle(region, 2f, BorderColor);
		float move = Math.max(0, Font.getWidth(Text) - region.getWidth() + 6);
		Rect offset = region.moved(getParentalOffset());
		
		Canvas.pushClip(offset);
		
		Font.drawText(Text, getPosition().plus(3 - move, 3), TextColor);
		tick -= time;
		if(hasFocus()) {
			if(tick > 0.5f) {
				float beforeCursor = Font.font.getWidth(Text.substring(0, cursorPosition)) - move;
				Canvas.drawLine(
						region.getX() + 4 + beforeCursor,
						region.getY() + 3,
						region.getX() + 4 + beforeCursor,
						region.getBottom() - 3,
						1, TextColor);
			} else {
				if(tick < 0)
					tick += 1f;
			}
		}
		
		Canvas.popClip();
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if(event.Button == MouseEvent.BUTTON_LEFT && event.State.Down) {
			getFocus();
			tick = 1f;
			return true;
		}
		return false;
	}

	@Override
	public void onKeyEvent(KeyEvent event) {

		if(KPListener != null)
			if(!KPListener.onKeyPressed(event))
				return;

		if(event.State == ButtonState.Pressed) {
			switch(event.KeyId) {
				case Keyboard.KEY_UP: cursorPosition = 0; break;
				case Keyboard.KEY_DOWN: cursorPosition = Text.length() - 1; break;
				case Keyboard.KEY_LEFT: cursorPosition = FMath.max(cursorPosition - 1, 0); break;
				case Keyboard.KEY_RIGHT: cursorPosition = FMath.min(cursorPosition + 1, Text.length()); break;
				case Keyboard.KEY_BACK:	// backspace 
					if(cursorPosition == 0) break;
					Text = Text.substring(0, cursorPosition - 1) + Text.substring(cursorPosition);
					cursorPosition--;
					break;
				case Keyboard.KEY_DELETE: 
					if(cursorPosition == Text.length()) break;
					Text = Text.substring(0, cursorPosition) + Text.substring(cursorPosition + 1);
					break;

				default:
					if(cursorPosition >= MaxChars || Text.length() >= MaxChars)
						break;
					if(!Character.isIdentifierIgnorable(event.KeyChar)) {
						if(AllowedCharacters != null)
							if(!AllowedCharacters.contains("" + event.KeyChar))
								break;
						if(cursorPosition >= MaxChars)
							break;
						if(Text.length() == 0) {
							Text = "" + event.KeyChar;
						} else {
							Text = Text.substring(0, cursorPosition) + event.KeyChar +
									Text.substring(cursorPosition);
						}
						cursorPosition++;
					}
					break;
			}
		}
	}

	@Override
	public boolean canTakeFocus() { return true; }
	public void setText(String text) {
		Text = text;
		cursorPosition = text.length();
		MaxChars = FMath.max(MaxChars, text.length());
	}

	public String getText() { return Text; }

	public void setKeypressListener(KeyPressListener listener) {
		KPListener = listener;
	}

	public static interface KeyPressListener {
		public boolean onKeyPressed(KeyEvent event);
	}
}