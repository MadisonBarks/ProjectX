package com.focused.projectf.screens.screens;

import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.screens.Screen;

public class PauseGameScreen extends Screen {

	public PauseGameScreen(Screen parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onFocusLost(Screen hasFocus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGainFocus(Screen lostFocus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getRequiredResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fillsScreen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(float elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(float elapsedTime) {
		Canvas.fillRectangle(
				Canvas.getCenter().X - 100,
				Canvas.getCenter().Y - 50,
				Canvas.getCenter().X + 100,
				Canvas.getCenter().Y + 50,
				Color.fromHex("EEEE66"));
	}


	@Override
	public boolean onKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onMouseEvent(MouseEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
