package com.focused.projectf.screens.screens;

import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.resources.Content;
import com.focused.projectf.screens.Screen;
import com.focused.projectf.screens.ScreenManager;

public class SlidingBackgroundScreen extends Screen {

	private Image[] Backgrounds;
	
	public SlidingBackgroundScreen(Screen parent, String... textures) {
		super(parent);
		
		Backgrounds = new Image[textures.length];
		for(int i = 0; i < textures.length; i++)
			Backgrounds[i] = Content.getImage(textures[i]);
	}

	@Override
	public boolean fillsScreen() { return true; }

	@Override
	public void update(float elapsedTime) { }

	@Override
	public void draw(float elapsedTime) {
		Canvas.drawImage(Backgrounds[0], ScreenManager.getDisplayRectangle(), Color.WHITE);
	}

	@Override
	public boolean onKeyEvent(KeyEvent event) { return false; }
	@Override
	public boolean onMouseEvent(MouseEvent event) { return false; }
	
	@Override
	public String[] getRequiredResources() {
		return new String[] {
				"mapElements/allResources.png",
				"bg.jpg"
		};
	}
}