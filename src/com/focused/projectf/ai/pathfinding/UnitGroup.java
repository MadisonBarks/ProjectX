package com.focused.projectf.ai.pathfinding;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Path;
import com.focused.projectf.ai.actions.GroupAction;
import com.focused.projectf.ai.actions.GroupActionConstants;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.global.Threading;
import com.focused.projectf.interfaces.IEntity;
import com.focused.projectf.utilities.FMath;

public class UnitGroup implements Runnable, GroupActionConstants {

	/** used */
	private static Stack<UnitGroup> InactiveGroups = new Stack<UnitGroup>();
	
	protected Vector<Unit> Units;
	protected Vector<Point> UnitOffsets;

	protected Path path;
	protected Point Target;
	protected IEntity TrueTarget;
	
	protected int activeAction;
	
	protected Unit Commander;
	
	protected float slowestSpeed;
	
	private UnitGroup() {
		Units = new Vector<Unit>();
		UnitOffsets = new Vector<Point>();
	}

	public void begin(final List<Unit> units) {
		if(Units.size() == 0)
			Commander = units.get(0);
		Units.addAll(units);
		
		for(int i = 0; i < units.size(); i++) {
			units.get(i).setGroup(this);
			GroupAction.start(units.get(i));
			UnitOffsets.add(new Point(0, 0));
		}
	}

	public int size() {
		return Units.size();
	}
	public void addUnit(Unit u) {
		Units.add(u);
		if(Units.size() == 1)
			Commander = Units.get(0);
		
		slowestSpeed = Math.min(slowestSpeed, u.getStats().Speed);
		UnitOffsets.add(new Point(0, 0));
	}
	public Unit getUnit(int index) {
		return Units.get(index);
	}
	public boolean remove(Unit u) {
		int index = Units.indexOf(u);
		if(index == -1)
			return false;
		if(u == Commander) {
			if(Units.size() != 0) {
				Commander = Units.get(0);
				startRegroup();
			} else
				recycle();
		}
		return Units.remove(u);
	}



	public Path getPath() {
		return path;
	}

	public int getActiveAction() {
		return activeAction;
	}
	
	public Point getTargetPosition() {
		return Target;
	}
	public IEntity getTrueTarget() {
		return TrueTarget;
	}
	
	/**
	 * Realigns unit target to near each other
	 */
	public void startRegroup() {
		activeAction = ACT_MOVE_CATCH_UP;
	}
	
	public void setMoveTo(Point target) {
		TrueTarget = null;
		Target = target;
		startRegroup();
		Threading.pushOperation(this);
	}
	public void setMoveTo(IEntity target) {
		TrueTarget = target;
		Target = TrueTarget.getBounds().getBorderingPoint(Commander.getPosition(), 10);
		startRegroup();
		Threading.pushOperation(this);
	}

	public Point calcGroupOffset(Unit unit) {
		
		if(unit == Commander)
			return getWaypoint();
		
		int index = Units.indexOf(unit);
		if(index < 0) {
			Units.add(unit);
			index = Units.size() - 1;
		}
		/*
		float angleAdd = (FMath.PI / 3) * index;
		float length = (int)(Math.floor(index / 6f) + 2) * 20f;
		float angle = Commander.Velocity.angle();
		*/
		final float turn = FMath.PI / 3f;
		float angle = 0;//Commander.Velocity.angle();
		float angleAdd = turn * (index % 6);
		float length = 45.0f * (1 + (int)Math.floor(index / 6f));
		return Commander.getPosition().minus(Point.fromAngle(angle + angleAdd, length));
	}

	public float getSpeed() {
		return (slowestSpeed == Float.POSITIVE_INFINITY || slowestSpeed == 0) ? 100 : slowestSpeed;
	}

	public Point getWaypoint() {
		if(path != null) {
			
			if(Commander.getPosition().distSq(path.getPoint()) < 32) {
				if(!path.next()) {
					path = null;
					activeAction = ACT_WAIT;
					return Commander.getPosition();
				}
				return path.getPoint();
		
			} else 
				return path.getPoint();
		}
		return Commander.getPosition();
	}	
	
	public void quarterUpdate() {
		
	}
	
	public void run() {
	
		path = PathFinder.computePath(Commander.getPosition(), Target, Commander);

		if(path == null)
			for(Unit u : Units)
				u.getActionStack().next();

		if(path.length() == 0) {
			if(TrueTarget != null) {
				Point nTarg = TrueTarget.getBounds().getBorderingPoint(Target, 5);
				path.insert(nTarg);
				path.add(Commander.getPosition());
			} else {
				path.add(Target);
				path.add(Commander.getPosition());
				ErrorManager.logWarning("Poorly formed path passed. if a unit runs through somehting this is why", null);
			}
			return;
		} else 
			path.add(Commander.getPosition());
		/*
		if(TrueTarget != null) {
			Point nTarg = TrueTarget.getBounds().getBorderingPoint(path.getTarget(), 15);
			if(nTarg != null && !Map.get().blocked(Commander, nTarg) && nTarg.distSq(path.getTarget()) < Map.tileWidth * Map.tileWidth) {
				path.insert(nTarg);
			} else {				
				throw new Error();
			}
		}
		 */
		if(path.length() > 2)
			path.simplify();

		path.expand();
	}
	

	public void recycle() {
		Units.clear();
		UnitMovementManager.Groups.remove(this);
		InactiveGroups.push(this);
		slowestSpeed = Float.POSITIVE_INFINITY;
	}

	public static UnitGroup instance() {
		if(InactiveGroups.size() == 0)
			return new UnitGroup();

		return InactiveGroups.pop();
	}

	public List<Unit> getUnits() {
		return Units;
	}
}
