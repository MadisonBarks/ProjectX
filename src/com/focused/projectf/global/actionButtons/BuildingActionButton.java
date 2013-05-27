package com.focused.projectf.global.actionButtons;

import java.util.List;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.actions.BuildAction;
import com.focused.projectf.entities.BuildingSite;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.global.AlertsSystem;
import com.focused.projectf.global.AlertsSystem.AlertType;
import com.focused.projectf.global.ClickActionManager;
import com.focused.projectf.global.ClickActionManager.MouseRunnable;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.Input;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;
import com.focused.projectf.resources.Content;

public class BuildingActionButton extends ActionButton {
	public final BuildingType Type;
	public BuildingActionButton(BuildingType type) {
		super(type.iconPath);
		Type = type;
		Title = type.name().replace("_", " ");
		Desc = Title;
		if(type.WoodCost > 0) Desc += ("\nWood: " + type.WoodCost);
		if(type.StoneCost > 0) Desc += ("\nStone: " + type.StoneCost);
		if(type.GoldCost > 0) Desc += ("\nGold: " + type.GoldCost);
		if(type.FoodCost > 0) Desc += "\nFood: " + type.FoodCost;
		if(type.RadiumCost > 0) Desc += "\nRadium: " + type.RadiumCost;
	}

	@Override
	public void click() {
		Player p = Player.getThisPlayer();
		if(Type.StoneCost > p.Stone || Type.GoldCost > p.Gold || Type.WoodCost > p.Wood || Type.FoodCost > p.Food) {
			ErrorManager.logInfo("Insuficent resources");
			AlertsSystem.alert("Not enough resources", AlertType.Major, 3);
			return;
		} 

		p.Wood		-= Type.WoodCost;
		p.Stone		-= Type.StoneCost;
		p.Food		-= Type.FoodCost;
		p.Gold		-= Type.GoldCost;
		p.Radium	-= Type.RadiumCost;

		final Image bldImg = Content.getImage(Type.texturePath);

		ClickActionManager.setLeftClickAction(new MouseRunnable() {

			boolean validPosition = true;
			@Override
			public void process(MouseEvent event) {
				
				if(event.State == ButtonState.Pressed && !Input.getShift()) {
					// TODO: shift clicking
					ClickActionManager.setLeftClickAction(null);

					if(validPosition) {
						Point pos = Canvas.toGamePoint(Input.getMousePosition());
						BuildingSite site = new BuildingSite(Player.thisMachinesPlayer, pos, Type);

						Map.get().addEntity(site);
						List<Unit> units = Selection.getUnits();
						for(Unit u : units) {
							if(u instanceof Villager)
								((Villager)u).getActionStack().set(new BuildAction((Villager) u, site));
						}
					} else {
						// 
						Player p = Player.getThisPlayer();
						p.Wood		+= Type.WoodCost;
						p.Stone		+= Type.StoneCost;
						p.Food		+= Type.FoodCost;
						p.Gold 		+= Type.GoldCost;
						p.Radium 	+= Type.RadiumCost;
					}
				}
				boolean notValid = false;

				Point start = Map.toTile(Canvas.toGamePoint(Input.getMousePosition()));
				for(int x = (int) start.X; x > start.X - Type.widthInTiles; x--) 
					for(int y = (int) start.Y; y < start.Y + Type.heightInTiles; y++) 
						notValid |= Map.get().blocked(null, x, y);

				validPosition = !notValid;
			}

			@Override
			public void drawOverlay() {
				Point pos = Map.roundToTileCoord(Canvas.toGamePoint(Input.getMousePosition()));
				pos.minusEquals(bldImg.getWidth() / 2, bldImg.getHeight() - Map.tileHalfHeight);
				Color col = Color.WHITE.withAlpha(0.6f);
				if(!validPosition)
					col = Color.RED.withAlpha(0.6f);

				Canvas.drawImage(bldImg, pos, col, -1);
			}
		});
	}
}