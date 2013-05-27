package com.focused.projectf.screens.screens;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.audio.SoundManager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.resources.Content;
import com.focused.projectf.screens.GUIScreen;
import com.focused.projectf.screens.Screen;
import com.focused.projectf.screens.ScreenManager;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.TimeKeeper;

/**
 * A small popup window to show when lots of loading or processing need to be done before continuing.
 */
public class LoadingScreen extends GUIScreen {

	public float LoadedAmmount = 0;
	public float Transition;
	public boolean MoveUp;

	private float TotalElapsed = 0;
	
	public LoadingScreen(Screen parent) {
		super(parent);
	}

	@Override
	public boolean fillsScreen() { 
		return MoveUp && Content.isLoading();
	}

	@Override
	public void update(float elapsedTime) {
		if(MoveUp && !Content.isLoading()) {
			ScreenManager.remove(this);
			TimeKeeper.unpause();
		}
	}

	@Override
	public void draw(float elapsedTime) {
		super.draw(elapsedTime);
		TotalElapsed += TimeKeeper.getTrueElapsed();

		final float cX = Canvas.getWidth() / 2;
		final float cY = Canvas.getHeight() / 2;

		Rect drawRect = new Rect(cX - 120, cY - 60, 240, 120);
		Point textPos = drawRect.getCenter();
		if(MoveUp) {
			int state = SoundManager.getBackgroundMusicState();
			if(state != SoundManager.STOPPING && state != SoundManager.STOPPED)
				SoundManager.stopBackgroundMusic(0.5f);
			
			Transition += FMath.min(TimeKeeper.getTrueElapsed() * 3, 1 - Transition);
		} else if(!Content.isLoading()) {
			Transition -= FMath.min(TimeKeeper.getTrueElapsed() * 3, 1 - (Transition + 0.35f));
			Transition = FMath.max(Transition, -(80f / Display.getHeight()));
		} else {
			Transition -= FMath.min(TimeKeeper.getTrueElapsed() * 3, Transition);
		}

		if(ScreenManager.getTopmost() instanceof GameplayScreen && Content.isLoading()) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		}
		drawRect = new Rect(
				cX - 100 - 50 * Transition, 
				Display.getHeight() - (Display.getHeight() * 0.5f + 40) * Transition 
				- 40 * (1 - Transition),
				200 + 100 * Transition,
				40 + 40 * Transition);

		textPos = drawRect.getTopLeft().plus(drawRect.getHeight(), drawRect.getHeight() / 2 - Canvas.Font12Bold.getLineHeight());

		Canvas.fillRectangle(drawRect, Color.fromHex("FFCC6600"));
		Canvas.Font12Bold.drawText("Loading Resources" + "\n" + Content.LoadingQueue.size() + " left", textPos.X, textPos.Y);

		Point dir = Point.fromAngle(TotalElapsed * FMath.PI * 1.5f, drawRect.getHeight() / 3);
		Point vertex = drawRect.getTopLeft().plus(drawRect.getHeight() / 2, drawRect.getHeight() / 2);
		Canvas.drawLine(vertex, vertex.plus(dir), 4, Color.WHITE);
	}

	@Override
	public void buildGUI() {


	}

	@Override
	public void onFocusLost(Screen hasFocus) { 
		TimeKeeper.unpause();
	}
	@Override
	public void onGainFocus(Screen lostFocus) {
		TimeKeeper.pause();
	}
}