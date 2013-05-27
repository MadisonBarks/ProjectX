package com.focused.projectf.ai;

import java.util.ArrayList;

import com.focused.projectf.ai.actions.IdleAction;
import com.focused.projectf.entities.Unit;
public class ActionStack {

	protected final ArrayList<Action<?>> actions;
	protected IdleAction idle;
	public final Unit Parent;

	public ActionStack(Unit parent) {
		actions = new ArrayList<Action<?>>();
		idle = new IdleAction(parent);
		Parent = parent;
	}

	public void clear() { 
		if(actions.size() > 0) {
			actions.get(0).stopAction();
			actions.clear();
		}
	}
	public void add(Action<?> action) { 
		if(action == null)
			throw new Error();
		actions.add(action);
		if(actions.size() == 1)
			action.startAction();
		else
			actions.get(1).stopAction();
	}

	public void insert(int index, Action<?> action) { 
		if(action == null)
			throw new Error();
		if(index == 0)
			action.startAction();
		actions.add(index, action);
	}

	public void insert(Action<?> action) { 
		if(action == null)
			throw new Error();
		actions.add(0, action);
		action.startAction();
	}

	public int size() { return actions.size(); }

	public void set(Action<?> action) {
		if(action == null)
			throw new Error();
		clear();

		add(action);
	}

	public void next() {
		actions.remove(0);
		if(actions.size() > 0)
			actions.get(0).startAction();
	}

	public void update(float elapsed) {
		if(actions.size() > 0)
			if(actions.get(0).isPaused())
				actions.get(0).pauseTime -= elapsed;
			else
				actions.get(0).updateUnit(elapsed);
		else
			idle.updateUnit(elapsed);
	}

	public void compleated(Action<?> action) {
		if(actions.size() > 0) {
			if(actions.get(0) == action)
				next();
			else
				actions.remove(action);
		}
	}

	public Action<?> running() {
		if(actions.size() > 0)
			return actions.get(0);

		return idle;
	}

	public boolean isIdle() { return actions.size() == 0; }
}
