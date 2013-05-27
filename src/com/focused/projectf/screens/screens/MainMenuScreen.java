package com.focused.projectf.screens.screens;

import com.focused.projectf.Point;
import com.focused.projectf.audio.SoundManager;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.gui.GUIView;
import com.focused.projectf.gui.vButton;
import com.focused.projectf.gui.vButton.OnClickListener;
import com.focused.projectf.multiplayer.NetworkingManager;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;
import com.focused.projectf.screens.GUIScreen;
import com.focused.projectf.screens.Screen;
import com.focused.projectf.screens.ScreenManager;

public class MainMenuScreen extends GUIScreen {

	public TTFont Font;
	
	public MainMenuScreen(Screen parent, NetworkingManager netManager) {
		super(parent);
		Font = Content.getFont("Arial", 24, false, false);
		SoundManager.startBackgroundMusic(Content.getUrlForResource("audio/bg.wav"), true,  0.25f, 2f);
	}

	public void buildGUI() {
		GUI.empty();
		new vButton(GUI, new Point(100, 50), 200, 40, null, "Single Player", new OnClickListener() {
			public void onClick(GUIView clicked) {
				ScreenManager.pushScreen(new GameplayScreen(MainMenuScreen.this));
			}
		});
		new vButton(GUI, new Point(150, 50), 200, 40, null, "Multiplayer", new OnClickListener() {
			public void onClick(GUIView clicked) {
				ScreenManager.pushScreen(new GameLobbyScreen(MainMenuScreen.this));
			}
		});
		new vButton(GUI, new Point(200, 50), 200, 40, null, "Options", new OnClickListener() {
			public void onClick(GUIView clicked) {
				if(ScreenManager.getTopmost() instanceof GameplayMenu) 
					ScreenManager.getTopmost().close();
				else	
					ScreenManager.pushScreen(new GameplayMenu(MainMenuScreen.this, true));
			}
		});
		new vButton(GUI, new Point(250, 50), 200, 40, null, "Exit",  new OnClickListener() {
			public void onClick(GUIView clicked) {
				//MainLoop.exit();
			}
		});
	}

	@Override
	public boolean fillsScreen() { return false; }

	@Override
	public void draw(float elapsedTime) {
		super.draw(elapsedTime);
		Font.drawText("Main Menu", new Point(50, 50), Color.WHITE);
	}

	@Override
	public String[] getRequiredResources() {
		return new String[] { "bg.jpg" };
	}

	public String[] getQueryResources() {
		return new String[] { 
				"units/ArcherSpriteSheet-Flare.flare",
				"tiles/grass1.png",
				"tiles/sand1.png",
				"tiles/water1.png",};
	}
}
