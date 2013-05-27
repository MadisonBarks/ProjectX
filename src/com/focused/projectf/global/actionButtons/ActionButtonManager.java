package com.focused.projectf.global.actionButtons;

import com.focused.projectf.graphics.Color;
import com.focused.projectf.gui.GUIGroup;
import com.focused.projectf.gui.GUIView;
import com.focused.projectf.gui.TextView;
import com.focused.projectf.gui.vButton;
import com.focused.projectf.gui.vButton.OnClickListener;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.players.Selection;

public class ActionButtonManager {

	protected static GUIGroup Panel;
	protected static vButton[][] Buttons;
	protected static GUIGroup DescriptionBox;
	protected static TextView Title, Details;

	public static final int BUTTONS_WIDE = 5;
	public static final int BUTTONS_TALL = 4;

	public static final int BUTTON_PADDING = 0;

	protected static ActionButtonSet Using = null;
	
	private static OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(GUIView clicked) {
			ActionButtonManager.onClick((vButton)clicked);
			updateGUI();
		}
	};

	public static float createGUI(GUIGroup gui, int height) {

		float buttonSize = (height - (BUTTON_PADDING * (BUTTONS_TALL + 1))) / (float)BUTTONS_TALL;
		float width = buttonSize * BUTTONS_WIDE + BUTTON_PADDING * (BUTTONS_WIDE + 1);

		Panel = new GUIGroup(gui, GUIView.UNSET, 5, 5, GUIView.UNSET, width, height) {
			public boolean onMouseEvent(MouseEvent event) {
				boolean ret = super.onMouseEvent(event);
				DescriptionBox.setVissibility(false);
				if(ret) {
					for(int i = Children.size() - 1; i >= 0; i--) {
						GUIView view = Children.get(i);
						if(view.getMouseEventPickupRect().contains(event.Position) && Using != null) {
							ActionButton button = Using.Buttons[i / BUTTONS_TALL][i % BUTTONS_TALL];
							if(button != null && button.shouldBeVissible()) {
								Title.Text = button.getTitle();
								Details.Text = button.getTitle();
								
								DescriptionBox.setVissibility(true);
							}
						}
					}
				}
				return ret;
			}
		};
		Buttons = new vButton[BUTTONS_WIDE][BUTTONS_TALL];

		DescriptionBox	= new GUIGroup(gui, GUIView.UNSET, 0, height + 10, GUIView.UNSET, width, 100);
		DescriptionBox.setVissibility(false);
		DescriptionBox.fillColor = Color.HALF_BLACK;
		Title 			= new TextView(DescriptionBox, "Description", 5, 5, 16);
		Details 		= new TextView(DescriptionBox, "Details", 25, 5, 5, 5, 12);
		float xStep = (float)(width - BUTTON_PADDING) / BUTTONS_WIDE;
		float yStep = (float)(height - BUTTON_PADDING) / BUTTONS_TALL;
		for(int x = 0; x < BUTTONS_WIDE; x++)
			for(int y = 0; y < BUTTONS_TALL; y++) {
				Buttons[x][y] = new vButton(Panel, 
						BUTTON_PADDING + yStep * y,
						BUTTON_PADDING + xStep * x,
						GUIView.UNSET,
						GUIView.UNSET,
						buttonSize,
						buttonSize,
						null, ("" + x) + y, listener);
				Buttons[x][y].Vissible = false;
			}

		return width;
	}

	public static void selectionChanged() {
		for(int x = 0; x < BUTTONS_WIDE; x++)
			for(int y = 0; y < BUTTONS_TALL; y++) {
				Buttons[x][y].Vissible = false;
				Buttons[x][y].Background = null;
			}

		if(Selection.size() == 0)
			Using = null;

		if(Selection.isJustBuildings() && Selection.getBuildingType() != null) {
			Using = Selection.getBuildingType().ActionButtons;
		} else {
			Using = Selection.getDominantUnitType().ActionButtons;
		}
		updateGUI();
	}

	public static void update() {
		if(DescriptionBox != null)
			DescriptionBox.setVissibility(false);
	}

	public static void updateGUI() {
		if(Using == null) {
			for(int x = 0; x < BUTTONS_WIDE; x++)
				for(int y = 0; y < BUTTONS_TALL; y++) {
					Buttons[x][y].Background = null;
					Buttons[x][y].Vissible = false;
				}

		} else {
			for(int x = 0; x < BUTTONS_WIDE; x++)
				for(int y = 0; y < BUTTONS_TALL; y++)
					if(Using.Buttons[x][y] != null && Using.Buttons[x][y].shouldBeVissible()) {
						Buttons[x][y].Background = Using.Buttons[x][y].getImage();
						Buttons[x][y].Vissible = true;
						Buttons[x][y].Text = "";
					} else {
						Buttons[x][y].Vissible = false;
						Buttons[x][y].Background = null;
						Buttons[x][y].BottomColor = Color.CLEAR;
						Buttons[x][y].Text = x + "" + y;						
					}
		}
	}

	protected static void onClick(vButton clicked) {
		if(Using != null)
			for(int x = 0; x < Buttons.length; x++)
				for(int y = 0; y < Buttons[x].length; y++)
					if(Buttons[x][y] == clicked)
						if(Using.Buttons[x][y] != null)
							Using.Buttons[x][y].click();
	}

	public static void setUsing(ActionButtonSet subSet) {
		Using = subSet;
		updateGUI();
	}
}
