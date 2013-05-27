package com.focused.projectf.ai.actions;
import com.focused.projectf.Point;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.ai.Action;
import com.focused.projectf.ai.pathfinding.UnitGroup;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.utilities.TimeKeeper;

public class GroupAction extends Action<Unit> implements GroupActionConstants{

	protected int waypointIndex;
	
	public UnitGroup Group;
	protected Point FormationPos = new Point(0,0);
	public int Action;

	public GroupAction(Unit unit, UnitGroup group) {
		super(unit);
		Group = group;
	}

	@Override
	public void startAction() {
		FormationPos	= Group.calcGroupOffset(Unit);
		Action 			= Group.getActiveAction();
		
		switch(Action) {
			case ACT_WAIT:
			case ACT_MOVE:
				if(FormationPos.distSq(Unit.getPosition()) > 32f) 
					Action = ACT_MOVE_CATCH_UP;				
				break;

			case ACT_MOVE_CATCH_UP:
				if(FormationPos.distSq(Unit.getPosition()) < 32f) 
					Action = ACT_MOVE;
		}
	}

	@Override
	public void updateUnit(float elapsed) {
		Action = Group.getActiveAction();
		if(Action == ACT_WAIT) {
			//Action = Group.getActiveAction();
			return;
		}

		switch (Action) {
			case ACT_MOVE:
			case ACT_MOVE_CATCH_UP:
				FormationPos = Group.calcGroupOffset(Unit);

				//Point target = Group.getWaypoint().plus(FormationPos);
				Point ttarg = FormationPos.minus(Unit.getPosition());
				if(ttarg.lengthSq() > 16) {
					Unit.Velocity = ttarg.normalize(
							(ttarg.lengthSq() > 64) ? Group.getSpeed() : Unit.getStats().Speed);
				} else {
					Unit.Velocity.X = 0;
					Unit.Velocity.Y = 0;
				}
				break;
		}
	}

	@Override
	public void setState(FlareUnitAnimation img) {
		int frameLong = (TimeKeeper.getAnimMS() / 150);

		switch (Action) {
			case ACT_MOVE:
			case ACT_MOVE_CATCH_UP:		
				img.setState(1, Unit.Direction, frameLong % 6);
				break;

			case ACT_WAIT:
				img.setState(0, Unit.Direction, frameLong % 4);
				break;
		}
	}

	@Override
	public boolean holdPosition() { return false; }

	public static void start(Unit unit) {
		unit.getActionStack().set(
				new GroupAction(unit, unit.Group));
	}
}
