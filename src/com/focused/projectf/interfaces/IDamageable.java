package com.focused.projectf.interfaces;

import com.focused.projectf.Point;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.players.Player;


public interface IDamageable extends IInteractable{

	public int getHealth();
	public void damage(float points, DamageType type);
	public float getHealthFraction();
	public Player getOwner();
	public Point getPosition();
	public Bounding getBounds();
	
	public static enum DamageType {
		Knife				(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		Sword				(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		Arrow				(1.00f, 0.90f, 1.00f, 1.00f, 1.00f, 1.00f),
		Explosive			(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		Cannon				(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		Gun					(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		Siege				(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		BatteringRam		(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		Fist				(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),	
		/** 
		 * Basically when the game decides a unit should take damage rather than another unit
		 *  inflicting it, or for when God really does intervene. 
		 */
		God					(), 
		Unspecified			(0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f), 
		Repair				(1.00f, 1.00f, 1.00f, 1.00f, 1.00f, 1.00f),
		;
		public final float InfantryMultiplier;
		public final float CalveryMultiplier;
		public final float BuildingMultiplier;
		public final float MachineMultiplier;
		public final float AircraftMultiplier;
		public final float ShipMultiplier;
		DamageType() {
			InfantryMultiplier 		= 1;
			CalveryMultiplier 		= 1;
			BuildingMultiplier 		= 1;
			MachineMultiplier 		= 1;
			AircraftMultiplier 		= 1;
			ShipMultiplier 			= 1;
			//Multiplier 		= 1;
		}
		DamageType(float infantry, float cavelry, float building, float machine, float aircraft, float ship) {
			InfantryMultiplier 		= infantry;
			CalveryMultiplier 		= cavelry;
			BuildingMultiplier 		= building;
			MachineMultiplier 		= machine;
			AircraftMultiplier 		= aircraft;
			ShipMultiplier 			= ship;
		}
	}
}
