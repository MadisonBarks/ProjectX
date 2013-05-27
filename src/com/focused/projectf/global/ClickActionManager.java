package com.focused.projectf.global;

import java.util.ArrayList;
import java.util.List;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.actions.BuildAction;
import com.focused.projectf.ai.actions.CollectResourceAction;
import com.focused.projectf.ai.actions.MoveToAction;
import com.focused.projectf.ai.actions.RepairAction;
import com.focused.projectf.ai.actions.attack.AttackAction;
import com.focused.projectf.ai.pathfinding.UnitGroup;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.BuildingSite;
import com.focused.projectf.entities.Entity;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.SelectableEntity;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.Input;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.interfaces.IDamageable;
import com.focused.projectf.interfaces.IEntity;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;
import com.focused.projectf.screens.screens.GameplayScreen;
import com.focused.projectf.screens.screens.GameplayScreen.Ping;

/**
 * Handles mouse related events and routing right clicks to the appropriate actions.
 * (Unit and unit state dependent)
 */
public class ClickActionManager {
	public interface MouseRunnable {
		public void process(MouseEvent event);
		public void drawOverlay();
	}

	public static void process(MouseEvent event) {

		switch(event.Button) {
			default:
			case MouseEvent.BUTTON_NONE:
				break;

			case MouseEvent.BUTTON_RIGHT:
				RightClickReciever.process(event);
				break;

			case MouseEvent.BUTTON_LEFT:
				LeftClickReciever.process(event);
				break;

			case MouseEvent.BUTTON_CENTER:
				switch(event.State) {					
					default: break;
					case Released:
						if(Canvas.getZoom() == 1.0f)		
							Canvas.setZoom(0.85f);
						else if(Canvas.getZoom() >= 0.85f)	
							Canvas.setZoom(0.75f);
						else if(Canvas.getZoom() >= 0.75f)	
							Canvas.setZoom(0.65f);
						else if(Canvas.getZoom() >= 0.65f)		
							Canvas.setZoom(0.5f);
						else								
							Canvas.setZoom(1f);
						break;
				}
				break;
		}
		if(event.ScrollWheel != 0) {
			int change = event.ScrollWheel / 120;
			while(change > 0) {
				Canvas.setZoom(Canvas.getZoom() * 1.125f);
				change--;
			}
			while(change < 0) {
				Canvas.setZoom(Canvas.getZoom() / 1.125f);
				change++;
			}
		}
	}

	public static void drawOverlays() {
		LeftClickReciever.drawOverlay();
		RightClickReciever.drawOverlay();
	}
	public static void setRightClickAction(MouseRunnable onClick) {
		if(onClick == null)
			RightClickReciever = DefaultRightClickReciever;
		else
			RightClickReciever = onClick;
	}

	public static void setLeftClickAction(MouseRunnable onClick) {
		if(onClick == null)
			LeftClickReciever = DefaultLeftClickReciever;
		else
			LeftClickReciever = onClick;
	}

	public static final MouseRunnable DefaultRightClickReciever = new MouseRunnable() {

		@Override
		public void process(MouseEvent event) {

			if(event.State != ButtonState.Released)
				return;
			if(Input.getMouseButtonDownTime(event.Button) > MouseEvent.CLICK_TIME || Selection.size() == 0) 
				return;

			Point point = Canvas.toGamePoint(event.Position);
			IEntity atPoint = null;
			if(Map.get().isTileDiscovered(Map.toTile(point)))
				atPoint = Map.get().getEntityAtPoint(point);

			if(atPoint instanceof Unit && ((Unit) atPoint).getOwner() == Player.getThisPlayer())
				atPoint = null;

			GameplayScreen.get().Pings.add((atPoint != null) ? 
					new Ping(atPoint) :
						new Ping(point.clone()));


			if(Selection.isJustBuildings()) {
				if(atPoint != null)	Selection.getBuilding().UnitTarget = atPoint;
				else 				Selection.getBuilding().UnitTarget = point.clone();
				return;
			} 

			if(Selection.isJustUnits()) { // I can control them

				List<Unit> units = Selection.getUnits();

				if(units.size() > 1) {
					UnitGroup group = UnitGroup.instance();
					group.begin(units);

					group.startRegroup();

					if(atPoint == null)
						group.setMoveTo(point);
					else
						group.setMoveTo(atPoint);

					return;
				}

				Unit u = units.get(0);

				if(atPoint == null)  {
					u = units.get(0);
					if(u.Group != null)
						u.Group.remove(u);
					u.Group = null;	
					u.getActionStack().set(new MoveToAction(u, Canvas.toGamePoint(event.Position)));
					return;
				}

				if(u instanceof Villager) {
					if(atPoint instanceof ResourceElement) 
						u.getActionStack().set(new CollectResourceAction((Villager)u, (ResourceElement)atPoint));

					else if(atPoint instanceof BuildingSite)
						u.getActionStack().set(new BuildAction((Villager)u, (BuildingSite)atPoint));

					else if(atPoint instanceof Building) {
						if(((Building) atPoint).getHealthFraction() < 1.0)
							u.getActionStack().set(new RepairAction((Villager)u, (Building)atPoint));

						else 
							u.getActionStack().set(new MoveToAction((Villager)u, (Building)atPoint));
					}
				}


				else if (atPoint instanceof IDamageable) {
					if(((IDamageable)atPoint).getOwner().getDiplomacy().CanAttack) 
						u.getActionStack().set(AttackAction.create(u, (IDamageable)atPoint));


				} else {
					u.getActionStack().set(new MoveToAction(u, atPoint));

				}
			}
		}

		@Override
		public void drawOverlay() { }
	};

	public static final MouseRunnable DefaultLeftClickReciever = new MouseRunnable() {

		public Point dragBegin, dragEnd;

		@Override
		public void process(MouseEvent event) {

			if(event.State.Down)
				dragEnd = Canvas.toGamePoint(event.Position);

			switch(event.State) {
				case Held: dragEnd = Canvas.toGamePoint(event.Position); break;

				case Pressed:
					dragBegin = Canvas.toGamePoint(event.Position);
					if(!Input.getShift())
						Selection.clear();
					break;

				case Released:
					List<SelectableEntity> selection = null;

					if(dragEnd == null || dragBegin == null || dragBegin.distSq(dragEnd) < 16) {
						Entity e = Map.get().getEntityAtPoint(Canvas.toGamePoint(event.Position));
						selection = new ArrayList<SelectableEntity>();

						if(e != null) {
							if(e instanceof Unit && ((Unit)e).Group != null && Input.getCtrl())
								selection.addAll(((Unit)e).Group.getUnits());

							else
								selection.add((SelectableEntity) e);
						}
					} else {
						if(Input.getCtrl())	selection = Map.get().getEntitiesNear(dragBegin, dragBegin.distSq(dragEnd));
						else				selection = Map.get().getEntitiesInRegion(dragBegin, dragEnd);
					}

					if(Input.getShift()) 	Selection.add(selection);
					else					Selection.set(selection);
					dragEnd = dragBegin = null;
					Selection.prune();
					break;

				case Depressed:
					Selection.prune();
					break;			
			}
		}

		@Override
		public void drawOverlay() {
			if(dragBegin != null && dragEnd != null) {
				if(Input.getCtrl()) 
					Canvas.drawCircle(dragBegin, dragBegin.distance(dragEnd), Color.WHITE, 2);
				else
					Canvas.drawRectangle(dragBegin, dragEnd, 2.0f, Color.WHITE);
			}
		}
	};

	private static MouseRunnable RightClickReciever = DefaultRightClickReciever;
	private static MouseRunnable LeftClickReciever = DefaultLeftClickReciever;

}