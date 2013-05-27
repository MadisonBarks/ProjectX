package com.focused.projectf.global;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.ResourceType;
import com.focused.projectf.ai.UnitStats;
import com.focused.projectf.ai.buildings.BuildingAction;
import com.focused.projectf.entities.ActionQueue;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.BuildingType;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.SelectableEntity;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.gui.GUIGroup;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.interfaces.IDamageable;
import com.focused.projectf.players.Selection;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;

public class SelectionBar extends GUIGroup {

	protected TTFont Header1, Header2, Contents, Small;
	private float x, y;
	public SelectionBar(GUIGroup parent, float top, float left, float bottom, float right) {
		super(parent, top, left, bottom, right);

		Header1		= Canvas.Font18Bold;
		Header2		= Canvas.Font15Bold;
		Contents	= Canvas.Font12Bold;
		Small 		= Content.getFont("Arial", 10, false, false);	
	}

	private BuildingAction action;
	private Point MouseOver; 

	public void draw(float elapsed) {
		super.draw(elapsed);

		GL11.glPushMatrix();
		GL11.glTranslatef(region.getX(), region.getY(), 0);

		List<SelectableEntity> ents = Selection.getAll();
		Rect iconRegion = new Rect(10, 10, 32, 32);

		switch(ents.size()) {
			case 1:
				//Rect iconRegion = new Rect(20, 10, 32, 32);
				Canvas.fillRectangle(iconRegion, Color.BLACK);
				SelectableEntity ent = ents.get(0);
				Canvas.drawImage(ent.getIcon(), iconRegion, Color.WHITE);

				x = iconRegion.getLeft();
				y = iconRegion.getBottom() + 4;
				if(ent instanceof IDamageable) {
					IDamageable dmgbl = (IDamageable)ent;
					float div = (iconRegion.getWidth() * dmgbl.getHealthFraction());
					GL11.glLineWidth(5);
					GL11.glBegin(GL11.GL_LINES);
					Color.GRAY.bind3();
					GL11.glVertex2f(x + iconRegion.getWidth(), y);
					GL11.glVertex2f(x + div, y);
					Color.GREEN.bind3();
					GL11.glVertex2f(x + div, y);
					GL11.glVertex2f(x, y);
					GL11.glEnd();
					y += 8;
				}

				if(ent instanceof Building) {
					Building bld = (Building)ent;
					BuildingType type = bld.getType();
					BuildingStats stats = ResearchManager.getStats(bld.getOwner(), type);
					text(Header2, type.name().replace("_", " "));
					text(Contents, "Hp:", bld.getHealth() + "/" + stats.MaxHealth, 60);
					text(Contents, "Attack", "" + stats.Attack, 60);
					text(Contents, "Defense", "" + stats.Defense, 60);
					text(Contents, "Range", "" + stats.Range, 60);

					if(bld.getActionQueue().size() > 0) {
						ActionQueue queue = bld.getActionQueue();
						BuildingAction act = queue.get(0);
						Image img 			= act.getIconImage();
						Rect target 	= new Rect(180, 30, 32, 32);
						boolean hover 	= false;
						if(MouseOver != null) {
							hover = target.contains(MouseOver);
						}
						Canvas.drawImage(img, target, (hover) ? Color.YELLOW : Color.WHITE);
						if(act.getCornerText() != null) {
							float minusY = Contents.getWidth(act.getCornerText());
							Contents.drawText(act.getCornerText(), target.getRight() - minusY - 1, target.getBottom() - Small.getLineHeight() - 1);
						}

						y = target.getCenter().Y;
						float l = target.getRight() + 10;
						float width = getWidth() - 10 - l;
						GL11.glLineWidth(15);
						GL11.glBegin(GL11.GL_LINES);
						Color.RED.bind3();
						GL11.glVertex2f(l, y);
						GL11.glVertex2f(l + width * act.getProgress(), y);
						Color.YELLOW.bind3();
						GL11.glVertex2f(l + width * act.getProgress(), y);
						GL11.glVertex2f(l + width, y);
						GL11.glEnd();

						for(int i = 1; i < queue.size(); i++) {
							act = queue.get(i);
							target = new Rect(120 + (34 * i), getHeight() - 34, 32 , 32);
							img 			= act.getIconImage();
							if(MouseOver != null)
								hover = target.contains(MouseOver);
							Canvas.drawImage(img, target, (hover) ? Color.YELLOW : Color.WHITE);
							if(act.getCornerText() != null) {
								float minusY = Contents.getWidth(act.getCornerText());
								Contents.drawText(act.getCornerText(), target.getRight() - minusY - 1, target.getBottom() - Small.getLineHeight() - 1);
							}
						}
					}
				}

				if(ent instanceof Unit) {
					Unit unit = (Unit)ent;
					UnitType type = unit.getType();
					UnitStats stats = ResearchManager.getStats(unit.getOwner(), type);

					text(Header2, type.name().replace("_", ""));
					text(Contents, "Hp:", unit.getHealth() + "/" + stats.MaxHealth, 60);

					if(unit instanceof Villager) {
						Villager vill = (Villager) unit;
						if(vill.CollectedResourceType != ResourceType.None) {
							text(Contents, vill.CollectedResourceType.name(),
									vill.CollectedResourceAmount + "/" + ResearchManager.getVillagerResourceCarryingCapacity(), 60);
						}
					}

					text(Contents, "Attack", "" + (int)stats.Attack, 60);
					text(Contents, "Defense", "" + (int)stats.Defense, 60);
					text(Contents, "Range", "" + (int)stats.Range, 60);
				}

				if(ent instanceof ResourceElement) {
					ResourceElement res = (ResourceElement) ent;
					text(Header2, res.DepositType.DepositName);
					text(Contents, "" + (int)res.getResourceAmount());
				}

				break;

			default:
				if(Selection.isJustUnits()){
					Unit unit = Selection.getUnits().get(0);
					Image img = Content.getImage(unit.getType().IconPath);
					Canvas.drawImage(img, iconRegion, Color.WHITE);
					Canvas.Font15Bold.drawText(unit.getType().name().replace("_", " "), 20, 70);
					GL11.glLineWidth(5);
					GL11.glBegin(GL11.GL_LINES);
					Color.GRAY.bind3();
					y = iconRegion.getBottom() + 4;
					float div = iconRegion.getLeft() + iconRegion.getWidth() * unit.getHealthFraction();
					GL11.glVertex2f(iconRegion.getRight(), y);
					GL11.glVertex2f(div, y);
					Color.GREEN.bind3();
					GL11.glVertex2f(div, y);
					GL11.glVertex2f(iconRegion.getLeft(), y);
					GL11.glEnd();
					if(unit instanceof Villager) {
						Villager v = (Villager) unit;
						Canvas.Font12.drawText(v.CollectedResourceType.name(), 50, 88);
						Canvas.Font12.drawText(v.CollectedResourceAmount + "", 20, 88);
					}
					Canvas.Font12.drawText("Attack", 50, 103);
					Canvas.Font12.drawText("" + Math.round(unit.getStats().Attack), 20, 103);
					Canvas.Font12.drawText("Defense", 50, 118);
					Canvas.Font12.drawText("" + Math.round(unit.getStats().Defense), 20, 118);
				}

				break;

			case 0: break;
		}
		GL11.glPopMatrix();
	}

	private void text(TTFont font, String text) {
		font.drawText(text, x, y);
		y += font.getLineHeight();
	}

	private void text(TTFont font, String text, String text2, int tab) {
		font.drawText(text, x, y);
		font.drawText(text2, x + tab, y);
		y += font.getLineHeight();
	}

	public Rect Layout() {
		Rect r = super.Layout();
		DrawArea = new Rect(90, 10, getWidth() - 100, getHeight() - 20);
		return r;
	}

	public boolean onMouseEvent(MouseEvent event) {
		boolean Super = super.onMouseEvent(event);

		if(Selection.getBuilding() != null && action != null)
			if(event.Button == MouseEvent.BUTTON_LEFT && event.State == ButtonState.Pressed)
				Selection.getBuilding().getActionQueue().cancel(action);		

		if(event.Position != null)
			MouseOver = event.Position.minus(getPosition());

		return Super;
	}

	protected Rect DrawArea;

	public Rect getBuildActionDrawRectangle() {
		return DrawArea;
	}
}
