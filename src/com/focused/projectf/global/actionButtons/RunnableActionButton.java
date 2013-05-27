package com.focused.projectf.global.actionButtons;

import com.focused.projectf.ai.UnitRunnable;
import com.focused.projectf.players.Selection;

public class RunnableActionButton extends ActionButton {

	public final UnitRunnable Runnable;
	
	public RunnableActionButton(String imageSrc, UnitRunnable runnable) {
		super(imageSrc);
		Runnable = runnable;
	}

	@Override
	public void click() {
		Runnable.run(Selection.getUnits());
	}
}
