package com.focused.projectf.ai.actions;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Action;
import com.focused.projectf.ai.Path;
import com.focused.projectf.ai.pathfinding.PathFinder;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.global.Threading;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.interfaces.IEntity;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.screens.screens.GameplayScreen;
import com.focused.projectf.screens.screens.GameplayScreen.Ping;
import com.focused.projectf.utilities.TimeKeeper;

public class MoveToAction extends Action<Unit> implements Runnable {

	public static final int DO_NOTHING 			= 0;
	public static final int DROP_OFF_RESOURCES 	= 1;
	public static final int ENTER_BUILDING		= 2;
	public static final int TRY_NEW_TARGET		= 3;

	public Point Target;
	public IEntity TrueTarget;
	protected float SpeedCap, WaypointTick;
	public Path path;

	protected int ArivalAction;

	protected Point wVel = new Point(0,0);

	public MoveToAction(Unit unit, Point target, float speedCap) {
		super(unit);
		Target = target.clone();
		ArivalAction = 0;
		SpeedCap = speedCap;
	}
	public MoveToAction(Unit unit, Point target) {
		this(unit, target, 0);
	}

	public MoveToAction(Unit unit, IEntity target) {
		this(unit, target, getAction(unit, target));
	}

	public void stopAction() {
		Unit.TVelocity.X = 0;
		Unit.TVelocity.Y = 0;
	}
	
	public MoveToAction(Unit unit, IEntity target, int arivalAction) {
		super(unit);

		float shiftOut = Map.tileHalfHeight;
		
		if(target instanceof Building) {
			Building bld = (Building) target;
			shiftOut = bld.getBounds().getMinRadius() * 0.95f;
		}
		
		
		Target = target.getBounds().getCenter();
		Target.plusEquals(unit.getPosition().minus(target.getBounds().getCenter()).normalize(shiftOut));

		GameplayScreen.get().Pings.add(new Ping(target.getBounds().getCenter().clone(), Color.YELLOW, 30, 0.25f));
		GameplayScreen.get().Pings.add(new Ping(Target.clone(), Color.RED, 10, 0.25f));

		TrueTarget = target;
		ArivalAction = arivalAction;
	}

	private static int getAction(Unit unit, IEntity target) {
		if(target instanceof Building) {
			Building bld = (Building) target;
			if(unit instanceof Villager) {
				Villager vill = (Villager)unit;
				if(bld.getType().AcceptsResources() && vill.CollectedResourceAmount > 0)
					return DROP_OFF_RESOURCES;
			}

			if(bld.getType().MaxGarrison > 0)
				return ENTER_BUILDING;
		} 
		return DO_NOTHING;
	}

	public void updateUnit(float elapsed) {
		if(path == null || path.length() == 0) 
			return;
		float distSqToNextPathWaypoint = Unit.getPosition().distSq(path.getPoint());

		WaypointTick += 6 / distSqToNextPathWaypoint;
		wVel = path.getPoint().minus(Unit.getPosition()).normalize((SpeedCap == 0) ? Unit.getStats().Speed : SpeedCap);
		if(WaypointTick >= 1 || distSqToNextPathWaypoint < 16f) {
			if(path.next()) {
				wVel = path.getPoint().minus(Unit.getPosition()).normalize((SpeedCap == 0) ? Unit.getStats().Speed : SpeedCap);
				WaypointTick = 0;

			} else {
				onArrive();
				return;
			}
		}

		Unit.TVelocity.X = (Unit.Velocity.X * 3 + wVel.X) / 4f;
		Unit.TVelocity.Y = (Unit.Velocity.Y * 3 + wVel.Y) / 4f;
	}

	public void onArrive() {
		Unit.getActionStack().compleated(this);
		Unit.TVelocity.X = 0;
		Unit.TVelocity.Y = 0;

		switch(ArivalAction) {
			case DO_NOTHING: 
				break;

			case DROP_OFF_RESOURCES:
				((Villager)Unit).dropOffResources();
				break;

			case ENTER_BUILDING:
				((Building)TrueTarget).garison(Unit);
				break;

			case TRY_NEW_TARGET:	// this is a note for external systems. Ignore this.
				break;

			default:
				throw new Error("MoveToAction doesn't know what to do for ArrivalAciton " + ArivalAction);
		}
	}
	
	@Override
	public void startAction() {
		Threading.pushOperation(this);
	}

	public int getArriveAction() { 
		return ArivalAction;
	}

	@Override
	public void draw(Unit unit) { }

	@Override
	public void run() {
		SpeedCap = Unit.getStats().Speed;
		
		if(TrueTarget != null) {
			Point nTarg = TrueTarget.getBounds().getBorderingPoint(Target, 15);
			if(PathFinder.testLine(Unit.getPosition(), nTarg, Unit))
				path = new Path(Unit.getPosition(), nTarg);
		} 
		
		if(path == null)
			path = PathFinder.computePath(Unit.getPosition(), Target, Unit);

		if(path == null)
			Unit.getActionStack().clear();

		if(path.length() == 0) {
			if(TrueTarget != null) {
				Point nTarg = TrueTarget.getBounds().getBorderingPoint(Target, 5);
				path.insert(nTarg);
				path.add(Unit.getPosition());
			} else {
				//path.add(Target);
				path.add(Unit.getPosition());
			}
			return;
		} 	

		path.add(Unit.getPosition());

		if(TrueTarget != null) {
			Point nTarg = TrueTarget.getBounds().getBorderingPoint(path.getTarget(), 15);
			if(nTarg != null && !Map.get().blocked(Unit, nTarg) && nTarg.distSq(path.getTarget()) < Map.tileWidth * Map.tileWidth) {
				path.insert(nTarg);
			} else {				
				ArivalAction = TRY_NEW_TARGET;
			}
		}

		path.simplify();

		wVel = path.getPoint().minus(Unit.getPosition()).normalize(Unit.getStats().Speed);
	}

	@Override
	public boolean holdPosition() { return false; }

	@Override
	public void setState(FlareUnitAnimation img) {		
		int frame = (TimeKeeper.getAnimMS() / 100) % 8;
		img.setState(1, Unit.Direction, frame);
	}
}
