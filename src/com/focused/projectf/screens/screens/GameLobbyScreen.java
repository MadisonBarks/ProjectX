package com.focused.projectf.screens.screens;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Rect;
import com.focused.projectf.TileConstants;
import com.focused.projectf.global.ChatLog;
import com.focused.projectf.global.GameSessionSettings;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.gui.DropDown;
import com.focused.projectf.gui.DropDown.OnChangeListener;
import com.focused.projectf.gui.GUIGroup;
import com.focused.projectf.gui.GUIView;
import com.focused.projectf.gui.TextBox;
import com.focused.projectf.gui.TextView;
import com.focused.projectf.gui.vButton;
import com.focused.projectf.gui.vButton.OnClickListener;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.players.Player;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;
import com.focused.projectf.screens.GUIScreen;
import com.focused.projectf.screens.Screen;
import com.focused.projectf.screens.ScreenManager;

public class GameLobbyScreen extends GUIScreen implements OnChangeListener {

	private static final float UNSET = GUIView.UNSET;

	public TTFont Text1;
	protected GUIGroup LeftPane, RightPane, TopPane, BottomPane, ChatPane;
	protected TextView MapType, MapSize, PopLimit, CPUs, Victory, Title, IP;
	protected DropDown MapTypeDD, MapSizeDD, CPUsDD, VictoryDD;
	protected vButton StartGame, CancelGame, ChatButton, Search;
	protected TextBox PopLimitBox, ChatTextBox;

	protected TextView PlayerName, PlayerTeam, PlayerColor, PlayerPing;

	protected TextView[] PlayerNames, PlayerPings;
	protected DropDown[] PlayerTeams, PlayerColors;
	
	public GameLobbyScreen(Screen parent) {
		super(parent);
	}

	public void buildGUI() {
		GUI.removeAllViews();
		TopPane 	= new GUIGroup(GUI, 5, 5, UNSET, 5, UNSET, 60, Color.fromHex("88FFFFFF"));
		LeftPane 	= new GUIGroup(GUI, 70, 5, 150, 210, UNSET, UNSET, Color.fromHex("88FFFFFF"));
		RightPane 	= new GUIGroup(GUI, 70, UNSET, 150, 5, 200, UNSET, Color.HALF_BLACK);		
		BottomPane 	= new GUIGroup(GUI, UNSET, 5, 5, 5, UNSET, 140, Color.fromHex("88FFFFFF"));

		Title = new TextView(TopPane, "Multiplayer", 0, 10, 36);
		IP = new TextView(TopPane, "IP: ", 40, 10, 12);

		Search = new vButton(TopPane, 5, UNSET, UNSET, 5, 98, 20, null, "Find Games", null);

		MapType = new TextView(RightPane, "Map Type", 10, 10, 12);		
		MapTypeDD = new DropDown(RightPane, 27.5f, 10f, UNSET, 10f, UNSET, 20);
		MapTypeDD.setList(Map.MapType.values());
		MapTypeDD.setOnChangeListener(this);

		MapSize = new TextView(RightPane, "Map Size", 10, 50, 12);
		MapSizeDD = new DropDown(RightPane, 67.5f, 10f, UNSET, 10f, UNSET, 20);
		MapSizeDD.setList(Map.MapSize.values());
		MapSizeDD.setOnChangeListener(this);

		PopLimit = new TextView(RightPane, "Population Limit", 10, 90, 12);
		PopLimitBox = new TextBox(RightPane, 107.5f, 10f, UNSET, 10, UNSET, 20);
		PopLimitBox.AllowedCharacters = "0123456789";	// restrict text to positive integer values only
		PopLimitBox.MaxChars = 4;
		PopLimitBox.setText("200");

		Victory = new TextView(RightPane, "Victory  Conditions", 10, 130, 12);
		VictoryDD = new DropDown(RightPane, 147.5f, 10f, UNSET, 10f, UNSET, 20);
		VictoryDD.setList(GameSessionSettings.VictoryCondition.values());
		VictoryDD.setOnChangeListener(this);
		StartGame = new vButton(BottomPane, 20, UNSET, 20, 20, 160, UNSET, null, "Start\nGame", new OnClickListener() {
			@Override
			public void onClick(GUIView clicked) {
				ScreenManager.pushScreen(new GameplayScreen(GameLobbyScreen.this, 
						TileConstants.MapType.values()[MapTypeDD.getElementIndex()], 
						TileConstants.MapSize.values()[MapSizeDD.getElementIndex()])); 
			}
		});

		final int MaxPlayers = 8;

		PlayerNames = new TextView[MaxPlayers]; 
		PlayerPings = new TextView[MaxPlayers]; 
		PlayerTeams = new DropDown[MaxPlayers]; 
		PlayerColors = new DropDown[MaxPlayers]; 

		for(int i = 0; i < MaxPlayers; i++) {
			float y = 30 + 25 * i;
			
			PlayerNames[i] = new TextView(LeftPane, "", y + 5, 20, GUIView.UNSET, GUIView.UNSET, 12);
			PlayerNames[i].TextFont = Canvas.Font15Bold;
			
			PlayerPings[i] = new TextView(LeftPane, "", y + 5, 360, GUIView.UNSET, GUIView.UNSET, 12);
			PlayerNames[i].TextFont = Canvas.Font15Bold;
			
			PlayerTeams[i] = new DropDown(LeftPane, y, 430, GUIView.UNSET, GUIView.UNSET, 60, 20);
			PlayerTeams[i].setList(new String[] { "Blue", "Red", "Green", "Yellow", "White", "Orange", "Purple", "Gray"});
			PlayerTeams[i].setOnChangeListener(this);
			
			PlayerColors[i] = new DropDown(LeftPane, y, 500, GUIView.UNSET, GUIView.UNSET, 60, 20	);
			PlayerColors[i].setList(new String[] { "Blue", "Red", "Green", "Yellow", "White", "Orange", "Purple", "Gray"});
			PlayerColors[i].setOnChangeListener(this);
		}

		ChatPane = new GUIGroup(BottomPane, 2, 2, 2, UNSET, 400, UNSET);
		ChatPane.fillColor = Color.HALF_BLACK;
		ChatTextBox = new TextBox(ChatPane, UNSET, 2, 2, 2, UNSET, 20);
		ChatLog.setTextBox(ChatTextBox);

		Text1 = Content.getFont("Arial", 18, true, false);
	}

	public boolean onKeyEvent(KeyEvent event) {
		boolean test = super.onKeyEvent(event);
		if(event.KeyId == Keyboard.KEY_F7 && event.State == ButtonState.Pressed)
			buildGUI();
		return test;
	}

	@Override
	public String[] getRequiredResources() { return null; }
	@Override
	public boolean fillsScreen() { return true; }
	@Override
	public void update(float elapsedTime) {
		super.update(elapsedTime);
		List<Player> players = Player.ConnectedPlayers;

		for(int i = 0; i < PlayerNames.length; i++) {
			if(players.size() > i) {
				PlayerNames[i].Text = players.get(i).Name;
				PlayerPings[i].Text = "" + players.get(i).getPing();
				PlayerTeams[i].setElementIndex(players.get(i).MyTeam.Index);
				//PlayerColors[i].setElementIndex(players.get(i).Color.Index);
			} else {
				PlayerNames[i].Text = "";
				PlayerPings[i].Text = "";
			}
		}
	}

	@Override
	public void draw(float elapsedTime) {
		GL11.glClearColor(0.5f,0.5f, 0.7f, 1);
		super.draw(elapsedTime);
		float width = LeftPane.getWidth();
		Text1.drawText("Player", 12, 75, Color.BLACK);
		Text1.drawText("Ping", width - 222, 75, Color.BLACK);
		Text1.drawText("Team", width - 152, 75, Color.BLACK);
		Text1.drawText("Color", width - 72, 75, Color.BLACK);

		Rect chatRegion = new Rect(ChatPane.getPosition().minus(1,1), ChatTextBox.getViewRegion().getTopRight().minus(0, 2));
		chatRegion.move(ChatTextBox.getParentalOffset());
		ChatLog.render(elapsedTime, chatRegion, Color.HALF_BLACK);
	}

	@Override
	public void onChange(DropDown dropDown, int elementIndex, String elementName) {
		
	}
}
