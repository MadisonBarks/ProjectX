package com.focused.projectf.screens.screens;

import com.focused.projectf.global.UserProfile;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.gui.DropDown;
import com.focused.projectf.gui.GUIGroup;
import com.focused.projectf.gui.GUIView;
import com.focused.projectf.gui.ScrollingGroup;
import com.focused.projectf.gui.Slider;
import com.focused.projectf.gui.Switch;
import com.focused.projectf.gui.TextView;
import com.focused.projectf.gui.vButton;
import com.focused.projectf.input.Input;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.screens.GUIScreen;
import com.focused.projectf.screens.Screen;
import com.focused.projectf.screens.ScreenManager;
import com.focused.projectf.utilities.TimeKeeper;

public class GameplayMenu extends GUIScreen{

	protected static final int NONE						= 0;
	protected static final int OPTIONS					= 1;
	protected static final int SAVE_GAME				= 2;
	protected static final int SAVE_GAME_THEN_QUIT		= 3;
	protected static final int GAME_INFO				= 4;
	protected static final int SAVE_BEFORE_EXITING		= 5;
	protected static final int QUIT 					= 6;

	protected static final float SLIDE_RATE = 5;

	protected GUIGroup Left, Right;
	private float SlideL = 1.0f;
	private float SlideR = 1.0f;

	private int ShowRight, nextRight = 0;
	private boolean Open = true;
	private boolean ShowLeft;

	public GameplayMenu(Screen parent, boolean justOptions) {
		super(parent);
		if(justOptions) {
			ShowRight = OPTIONS;
			ShowLeft = false;
		} else {
			ShowRight = 0;
			ShowLeft = true;
		}
	}

	@Override
	public void buildGUI() {

		Left	= new GUIGroup(GUI, 0, 0, 0, GUIView.UNSET, 250, GUIView.UNSET);
		Right	= new GUIGroup(GUI, 0, GUIView.UNSET, 0, 0, 400, GUIView.UNSET);

		Left.setPosition(-Left.getWidth(), 0);
		Right.setMargins(0, GUIView.UNSET, 0, -Right.getWidth());
		Right.setPosition(GUI.getWidth() + Right.getWidth(), 0);

		Left.borderColor = Color.CLEAR;
		Right.borderColor = Color.CLEAR;

		Left.setVissibility(ShowLeft);

		if(ShowLeft) {
			new vButton(Left, 60, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Resume Game", new vButton.OnClickListener() {
				public void onClick(GUIView clicked) { Open = false; }			
			});
			new vButton(Left, 110, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Options", new vButton.OnClickListener() {
				public void onClick(GUIView clicked) { switchRightPane(OPTIONS); }
			});
			new vButton(Left, 160, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Game Info", new vButton.OnClickListener() {
				public void onClick(GUIView clicked) { switchRightPane(GAME_INFO); }			
			});
			new vButton(Left, 210, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Save Game", new vButton.OnClickListener() {
				public void onClick(GUIView clicked) { switchRightPane(SAVE_GAME); }			
			});
			new vButton(Left, 260, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Exit Game", new vButton.OnClickListener() {
				public void onClick(GUIView clicked) { switchRightPane(SAVE_BEFORE_EXITING); }			
			});
		}
		if(ShowRight != NONE)
			createPane(ShowRight);
	}

	@Override
	public boolean fillsScreen() { return false; }

	public void draw(float elapsed) {
		elapsed = TimeKeeper.getTrueElapsed();

		if(Open) {
			if(ShowLeft && SlideL > 0.0f) { SlideL -= Math.min(elapsed * SLIDE_RATE, SlideL); }
		} else {
			ShowRight = NONE;
			if(SlideL < 1.0f || SlideR < 1.0f) { SlideL += Math.min(elapsed * SLIDE_RATE, 1.0f - SlideL); }
			else { ScreenManager.remove(this); }
		}
		if(ShowRight == NONE) {
			if(SlideR < 1.0f) { SlideR += Math.min(elapsed * SLIDE_RATE, 1.0f - SlideR); }
			else if(nextRight != NONE){
				if(nextRight == QUIT) {
					ScreenManager.remove(Parent);
					ScreenManager.remove(this);
					return;
				}
				ShowRight = nextRight;
				createPane(ShowRight);
				nextRight = NONE;
			}
		} else {
			if(SlideR > 0.0f) SlideR -= Math.min(elapsed * SLIDE_RATE, SlideR); 
		}

		Left.setPosition(Left.getWidth() * -SlideL, 0);
		Right.setPosition(GUI.getWidth() - (Right.getWidth() * (1f - SlideR)), 0);

		Canvas.fillRectectangle(Left.getViewRegion(), Color.BLACK, Color.HALF_BLACK, Color.BLACK, Color.HALF_BLACK);
		Canvas.fillRectectangle(Right.getViewRegion(), Color.HALF_BLACK, Color.BLACK, Color.HALF_BLACK, Color.BLACK);
		super.draw(elapsed);
	}

	private void switchRightPane(final int paneIndex) {

		if(ShowRight == NONE) {
			ShowRight = paneIndex;
			createPane(paneIndex);
		} else {
			if(ShowRight != paneIndex) 
				nextRight = paneIndex;
			ShowRight = 0;
		}
	}	

	private void createPane(int paneIndex) {

		Right.Children.clear();

		switch(paneIndex) {
			case OPTIONS:
				final UserProfile Prof = UserProfile.ActiveProfile;

				ScrollingGroup Scroller = new ScrollingGroup(Right, 60, 5, 50, 5);
				new TextView(Scroller, "Graphics", 10, 20, 18);

				new TextView(Scroller, "Tile Shading", 40, 65, 15);
				final Switch ShadeTiles = new Switch(Scroller, 60, GUIView.UNSET, GUIView.UNSET, 10, 60, 25, Prof.ShadeTiles);

				new TextView(Scroller, "Unit behind object render mode", 40, 90, 15);
				final DropDown unitRenderMode = new DropDown(Scroller, 90	, GUIView.UNSET, GUIView.UNSET, 10, 80, 20);
				unitRenderMode.setList("None", "Outline", "Fill");
				unitRenderMode.setElementIndex(Prof.UnitBehindSomethingShadeMode);

				new TextView(Scroller, "Sound", 10, 150, 18);

				new TextView(Scroller, "Main Volume", 40, 190, 15);
				new TextView(Scroller, "Effects Volume", 40, 220, 15);
				new TextView(Scroller, "Music Volume", 40, 250, 15);
				new TextView(Scroller, "Unit Volume", 40, 280, 15);

				final Slider MainVolume = new Slider(Scroller, 190, GUIView.UNSET, GUIView.UNSET, 10, 120, 30);
				MainVolume.setValue(Prof.MainVolume);

				final Slider FXVolume = new Slider(Scroller, 220, GUIView.UNSET, GUIView.UNSET, 10, 120, 30);
				FXVolume.setValue(Prof.FXVolume);

				final Slider MusicVolume = new Slider(Scroller, 250, GUIView.UNSET, GUIView.UNSET, 10, 120, 30);
				MainVolume.setValue(Prof.BGMusicVolume);

				final Slider UnitVolume = new Slider(Scroller, 280, GUIView.UNSET, GUIView.UNSET, 10, 120, 30);
				FXVolume.setValue(Prof.UnitChatterVolume);

				new vButton(Right, GUIView.UNSET, GUIView.UNSET, 5, 80, 87, 30, null, "Cancel", new vButton.OnClickListener() {
					public void onClick(GUIView clicked) { ShowRight = NONE; }
				});
				final vButton apply = new vButton(Right, GUIView.UNSET, GUIView.UNSET, 5, 5, 70, 30, null, "Apply", new vButton.OnClickListener() {
					public void onClick(GUIView clicked) { 
						ShowRight = NONE;

						Prof.ShadeTiles = ShadeTiles.State;
						Prof.UnitBehindSomethingShadeMode = unitRenderMode.getElementIndex();
						Prof.MainVolume = MainVolume.getValue();
						Prof.FXVolume = FXVolume.getValue();
						Prof.BGMusicVolume = MusicVolume.getValue();
						Prof.UnitChatterVolume = UnitVolume.getValue();
					}
				});

				if(!ShowLeft) {
					new vButton(Right, GUIView.UNSET, 5, 5, GUIView.UNSET, 70, 30, null, "Close", new vButton.OnClickListener() {
						public void onClick(GUIView clicked) { ShowRight = NONE; apply.OnClick.onClick(apply); }
					});
				}
				break;				


			case SAVE_GAME_THEN_QUIT:
			case SAVE_GAME:
				new TextView(Right, (paneIndex == SAVE_GAME_THEN_QUIT)? "Save Game and Quit" : "Save Game",
						60, 5, GUIView.UNSET, 5, GUIView.UNSET, 30);


				break;
			case GAME_INFO:
				new TextView(Right, "Game Info", 60, 5, GUIView.UNSET, 5, GUIView.UNSET, 30);


				break;
			case SAVE_BEFORE_EXITING:
				new TextView(Right, "Save Game before Quiting?", 60, 5, GUIView.UNSET, 5, GUIView.UNSET, 30);
				new vButton(Right, 110, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Cancle", new vButton.OnClickListener() {
					public void onClick(GUIView clicked) { ShowRight = NONE;  }			
				});
				new vButton(Right, 160, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Save First", new vButton.OnClickListener() {
					public void onClick(GUIView clicked) { ShowRight = NONE; nextRight = SAVE_GAME; }
				});
				new vButton(Right, 210, 5, GUIView.UNSET, 5, GUIView.UNSET, 40, null, "Quit Without Saving", new vButton.OnClickListener() {
					public void onClick(GUIView clicked) { 
						ShowRight = NONE;
						ShowLeft = false;
						nextRight = QUIT;
					}			
				});
				break;
			case NONE: break;	
			default: throw new Error("Invalid Right Pane number");
		}
	}

	public boolean onMouseEvent(MouseEvent event) {
		if((Input.getMouseButtonState(MouseEvent.BUTTON_LEFT).Down || Input.getMouseButtonState(MouseEvent.BUTTON_RIGHT).Down))
			if(!(Right.getMouseEventPickupRect().contains(event.Position) || Left.getMouseEventPickupRect().contains(event.Position)))
				Open = false;

		return super.onMouseEvent(event);
	}

	public String[] getRequiredResources() {
		return new String[] {};
	}
}