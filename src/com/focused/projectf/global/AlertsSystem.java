package com.focused.projectf.global;

import org.lwjgl.opengl.Display;

import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.resources.TTFont;
import com.focused.projectf.screens.screens.GameplayScreen;

public class AlertsSystem {

	public static enum AlertType {
		Minor		(Color.WHITE),
		Normal		(Color.YELLOW),
		Major		(Color.RED)
		
		;
		public Color TextColor;
		AlertType(Color color) {
			TextColor = color;
		}
	}

	private static String CurrentMessage;
	private static float width;
	private static AlertType CurrentType;
	private static int ShowTime;
	private static float timeShown;

	public static void draw(float elapsed) {
		timeShown += elapsed;
		if(timeShown < ShowTime) {
			TTFont font = null;
			switch(CurrentType) {
				case Major:		font = Canvas.Font15Bold; break;
				case Minor:		font = Canvas.Font12; break;
				case Normal:	font = Canvas.Font12Bold; break;
			}
			if(width == 0)
				width = font.getWidth(CurrentMessage);
			
			font.drawText(CurrentMessage, (Display.getWidth() - width) / 2f, Display.getHeight() - GameplayScreen.BOTTOM_BAR_HEIGHT - 40, CurrentType.TextColor);
		}
	}

	public static void alert(String message, AlertType type, int showTime) {
		CurrentMessage = message;
		CurrentType = type;
		ShowTime = showTime;
		timeShown = 0;
		width = 0;
	}
}
