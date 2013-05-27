package com.focused.projectf.entities;

import java.util.Vector;

import com.focused.projectf.ai.buildings.BuildingAction;

public class ActionQueue {

	public static final int MAX_ACTIONS_TO_QUEUE = 3;

	public final Building Parent;
	protected Vector<BuildingAction> Actions;

	public ActionQueue(Building parent) {
		Parent = parent;
		Actions = new Vector<BuildingAction>();
	}

	public void addAction(BuildingAction act) {
		Actions.add(act);
		if(Actions.size() == 1)
			act.begin();
	}

	public void update() {
		if(Actions.size() == 0) return;
		BuildingAction act = Actions.get(0);
		if(act.update()) {
			Actions.remove(0);
			if(Actions.size() > 0)
				Actions.get(0).begin();
		}
	}

	public BuildingAction getCurrent() {
		if(Actions.size() != 0)
			return Actions.get(0);
		return null;
	}

	public int size() { return Actions.size(); }
	public BuildingAction get(int i) { return Actions.get(i); }

	public void insert(BuildingAction act) {
		Actions.add(0, act);
		if(Actions.size() == 1)
			act.begin();
	}

	public void cancel(BuildingAction action) {
		if(Actions.remove(action)) {
			action.cancel();
		}
	}
}
