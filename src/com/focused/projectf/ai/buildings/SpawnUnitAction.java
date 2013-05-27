package com.focused.projectf.ai.buildings;


import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.actions.MoveToAction;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.entities.collision.TileBounds;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.interfaces.IEntity;
import com.focused.projectf.resources.Content;
import com.focused.projectf.utilities.TimeKeeper;
import com.focused.projectf.utilities.random.Chance;

public class SpawnUnitAction extends BuildingAction {

	float TimeLeft;
	public int UnitsToSpawn = 1;
	public UnitType Type;
	public Image Icon;
	public Building Owner;
	public SpawnUnitAction(UnitType type, Building owner) {
		Owner = owner;
		Type = type;
		Icon = Content.getImage(type.IconPath);
		TimeLeft = 10; 
	}
	@Override
	public void begin() {
		// TODO: check pop limit before proceeding
	}
	@Override
	public void cancel() {
		// TODO: return resources
	}

	@Override
	public boolean update() {
		TimeLeft -= TimeKeeper.getElapsed() / (UnitsToSpawn);
		if(TimeLeft <= 0) {
			TileBounds b = ((TileBounds)Owner.getBounds());
			for(; UnitsToSpawn > 0; UnitsToSpawn--) {
				Point position = b.corners[0].clone(); // TODO: find an open spot along the edge.
				Unit u = Unit.spawnType(Owner.getOwner(), position, Type);

				if(Owner.UnitTarget != null) {
					
					if(Owner.UnitTarget instanceof IEntity) // covers buildings, resource deposits, and other such stuff
						u.getActionStack().add(Unit.getDefaultRightClickAction(u, (IEntity)Owner.UnitTarget));
					
					else if(Owner.UnitTarget instanceof Point)
						u.getActionStack().add(new MoveToAction(u, (Point) Owner.UnitTarget));
					
					else
						throw new Error("Unimplemented UnitTarget type for building.");
					
				} else {
					Owner.getBounds().getBorderingPoint(
							Owner.getBounds().getCenter().plus(Chance.rnd.nextFloat() - 0.5f, Chance.rnd.nextFloat() - 0.5f),
							u.getType().Size * 0.75f);
				}
				
				Map.get().addEntity(u);
			}	
			return true;
		}
		return false;
	}

	public boolean append() {
		if(UnitsToSpawn < 5) { // TODO: Type.SpawnGroupSize;
			UnitsToSpawn++;
			return true;
		}
		return false;
	}

	@Override
	public float getProgress() { return 1f - (TimeLeft / 10.0f); } // TODO: / Type.TimeToSpawn

	public String getCornerText() {
		return "" + UnitsToSpawn;
	}
	
	@Override
	public Image getIconImage() {
		return Icon;
	}
}
