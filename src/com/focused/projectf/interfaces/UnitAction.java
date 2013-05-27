package com.focused.projectf.interfaces;

import java.util.List;

import com.focused.projectf.entities.Unit;

public interface UnitAction {
	public void runAction(List<Unit> units);
	public void runAction(Unit units);
}
