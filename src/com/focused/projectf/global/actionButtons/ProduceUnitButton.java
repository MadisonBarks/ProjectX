package com.focused.projectf.global.actionButtons;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.ai.buildings.BuildingAction;
import com.focused.projectf.ai.buildings.SpawnUnitAction;
import com.focused.projectf.entities.ActionQueue;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;

public class ProduceUnitButton extends ActionButton {

	public final UnitType Type;

	public ProduceUnitButton(UnitType type) {
		super(type.IconPath);
		Type = type;
		Title = type.name().replace("_", " ");
		Desc = Title;
		if(type.WoodCost > 0)	Desc += ("\nWood:" + type.WoodCost);
		if(type.StoneCost > 0)	Desc += ("\nStone:\t " + type.StoneCost);
		if(type.GoldCost > 0)	Desc += ("\nGold:" + type.GoldCost);
		if(type.FoodCost > 0)	Desc += ("\nFood:" + type.FoodCost);
		if(type.RadiumCost > 0)	Desc += ("\nRadium:" + type.RadiumCost);
	}

	@Override
	public void click() {

		Player p = Player.getThisPlayer();
		if(Type.StoneCost > p.Stone || Type.GoldCost > p.Gold || Type.WoodCost > p.Wood || Type.FoodCost > p.Food) {
			ErrorManager.logInfo("Insuficent resources");
			return;
		} 

		p.Wood -= Type.WoodCost;
		p.Stone -= Type.StoneCost;
		p.Food -= Type.FoodCost;
		p.Gold -= Type.GoldCost;
		p.Radium -= Type.RadiumCost;
		
		Building target = Selection.getBuilding();
		if(target != null) {
			ActionQueue queue = target.getActionQueue();
			for(int i = 0; i < queue.size(); i++) {
				BuildingAction act = queue.get(i);
				if(act instanceof SpawnUnitAction) {
					SpawnUnitAction act2 = (SpawnUnitAction)act;
					if(act2.Type == this.Type && act2.append())
						return;
				}
			}
			target.getActionQueue().addAction(new SpawnUnitAction(Type, target));
		} else 
			throw new Error("Could not get the building to pass this creation too. try again");
	}
}
