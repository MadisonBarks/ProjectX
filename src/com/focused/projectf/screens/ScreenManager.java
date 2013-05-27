package com.focused.projectf.screens;

import java.util.Vector;

import org.lwjgl.opengl.Display;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Rect;
import com.focused.projectf.global.Threading;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.interfaces.IInputReciever;
import com.focused.projectf.resources.Content;
import com.focused.projectf.screens.screens.DebugScreen;
import com.focused.projectf.utilities.TimeKeeper;

public class ScreenManager {

	private static final int HOLD_ON			= 0;
	private static final int HOLD_OFF			= 1;
	private static final int GOTO_OFF 			= 2;
	private static final int GOTO_ON 			= 3;

	private static float FadeRate 		= 3;
	private static float FadeAlpha		= 0;
	private static int FadeMode 		= HOLD_OFF;

	private static Vector<Screen> Screens;
	public static Vector<Screen> AlwaysOnTop;
	private static Color FadeColor;

	private static IInputReciever Reciever;

	public static void initialize() {
		Screens = new Vector<Screen>();
		AlwaysOnTop = new Vector<Screen>();
		AlwaysOnTop.add(new DebugScreen(null));
		
		Reciever = new IInputReciever() {
			public boolean onKeyEvent(KeyEvent event) { return ScreenManager.onKeyEvent(event); }
			public boolean onMouseEvent(MouseEvent event) { return ScreenManager.onMouseEvent(event); }
		};

		FadeAlpha = 1.0f;
		FadeColor = Color.BLACK.clone();
		FadeMode = HOLD_ON;
	}

	public static void setFadeAlpha(float fade) { FadeAlpha = fade; }
	public static float getFadeAlpha() { return FadeAlpha; }

	public static void setFadeColor(Color col) { FadeColor = col; }
	public static Color getFadeColor() { return FadeColor; }

	public static void draw(float elapsedTime) {

		if(Screens.size() > 0) {
			int startAt = 0;
			for(startAt = Screens.size() - 1; startAt >= 0; startAt--) {
				Screen s = Screens.get(startAt);
				if(s.Vissible && s.Active && s.fillsScreen()) 
					break;
			}

			if(startAt >= 0) {
				for(int s = startAt; s < Screens.size(); s++) {
					Screen screen = Screens.get(s);
					if(screen.Active && screen.Vissible)
						screen.draw(elapsedTime);
					ErrorManager.GLErrorCheck();
				}
			} else 
				FadeAlpha = 1.0f;
		}

		for(Screen screen : AlwaysOnTop) 
			screen.draw(elapsedTime);

		if(FadeAlpha > 0)
			Canvas.fillRectangle(getDisplayRectangle(), FadeColor.withAlpha(FadeAlpha));

		ErrorManager.GLErrorCheck();
	}

	public static void update(float elapsedTime) {		

		switch(FadeMode) {
			default:
			case HOLD_ON:
			case HOLD_OFF:
				break;

			case GOTO_ON:
				FadeAlpha = Math.min(1, FadeAlpha + TimeKeeper.getTrueElapsed() * FadeRate);
				if(FadeAlpha == 1)
					FadeMode = HOLD_ON;
				break;

			case GOTO_OFF:
				FadeAlpha = Math.max(0, FadeAlpha - TimeKeeper.getTrueElapsed() * FadeRate);
				if(FadeAlpha == 0)
					FadeMode = HOLD_OFF;
				break;

		}

		for(int s = Screens.size() - 1; s >= 0; s--) {
			Screen screen = Screens.get(s);
			if(screen.Active)
				screen.update(elapsedTime);
			else 
				screen.Active = screen.isReadyToShow();
		}

		for(Screen screen : AlwaysOnTop) 
			screen.update(elapsedTime);
	}

	public static void pushScreen(final Screen screen) {
		if(Screens.size() > 0) 
			Screens.lastElement().onFocusLost(screen);

		if(screen.fillsScreen())
			FadeMode = GOTO_ON;

		Screens.add(screen);
		screen.Active = false;

		Threading.pushOperation(new Runnable() {
			public void run() {
				Content.loadResources(screen.getRequiredResources());
				while(!Content.areLoaded(screen.getRequiredResources()))
					try { Thread.sleep(100); } catch (InterruptedException e) { }

				while(Content.isLoading())
					try { Thread.sleep(100); } catch (InterruptedException e) { }

				if(screen instanceof GUIScreen)
					((GUIScreen) screen).build();

				screen.Active = true;
				if(Screens.size() > 0)
					screen.onGainFocus(Screens.lastElement());
				else
					screen.onGainFocus(null);
				FadeMode = GOTO_OFF;
			}
		});
	}

	public static Rect getDisplayRectangle() {
		return new Rect(0, 0, Display.getWidth(), Display.getHeight());
	}

	public static void remove(Screen screen) {
		if(Screens.lastElement() == screen) {
			if(Screens.size() > 1) {
				Screen below = Screens.get(Screens.size() - 2);
				screen.onFocusLost(below);
				below.onGainFocus(screen);
			}
		}
		Screens.remove(screen);
		screen.onFocusLost(Screens.lastElement());
	}

	public static boolean onKeyEvent(KeyEvent event) {

		for(Screen screen : AlwaysOnTop) 
			screen.onKeyEvent(event);

		for(int i = Screens.size() - 1; i >= 0; i--) {
			Screen s = Screens.get(i);
			if(!s.Active) return false;
			if(!s.onKeyEvent(event) || s.fillsScreen())
				return true;
		}

		return false;
	}

	public static boolean onMouseEvent(MouseEvent event) {
		for(Screen screen : AlwaysOnTop) 
			screen.onMouseEvent(event);

		for(int i = Screens.size() - 1; i >= 0; i--) {
			Screen s = Screens.get(i);
			if(!s.Active) return false;
			if(s.onMouseEvent(event) || s.fillsScreen())
				return true;
		}
		return false;
	}

	public static void LayoutGUI() {
		for(Screen s : Screens) 
			if(s.Active && s instanceof GUIScreen) 
				((GUIScreen)s).Layout();
	}

	public static Screen getTopmost() {
		return Screens.lastElement();
	}

	public static IInputReciever getInputReciever() {
		return Reciever;
	}

	public static void makeTopMost(Screen newTop) {
		Screens.remove(newTop);
		Screens.add(newTop);
	}

	public static int getDepthOf(Screen parent) {
		int index = Screens.indexOf(parent);
		return Screens.size() - 1 - index;
	}
}
