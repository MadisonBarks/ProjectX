package com.focused.projectf.entities;

import java.util.Hashtable;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Action;
import com.focused.projectf.ai.ActionStack;
import com.focused.projectf.ai.Path;
import com.focused.projectf.ai.UnitStats;
import com.focused.projectf.ai.actions.BuildAction;
import com.focused.projectf.ai.actions.CollectResourceAction;
import com.focused.projectf.ai.actions.GroupAction;
import com.focused.projectf.ai.actions.MoveToAction;
import com.focused.projectf.ai.actions.RepairAction;
import com.focused.projectf.ai.actions.attack.AttackAction;
import com.focused.projectf.ai.pathfinding.UnitGroup;
import com.focused.projectf.ai.pathfinding.custom.Mover;
import com.focused.projectf.entities.collision.EllipseBounds;
import com.focused.projectf.entities.units.GroundMilitaryUnit;
import com.focused.projectf.entities.units.RangedMilitaryUnit;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.global.CorpseDrawer;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.interfaces.IDamageable;
import com.focused.projectf.interfaces.IEntity;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Player.DiplomacyState;
import com.focused.projectf.players.Selection;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.images.FlareUnitAnimation;
import com.focused.projectf.resources.shaders.ShaderProgram;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.TimeKeeper;

public class Unit extends ControllableEntity implements IDamageable, Mover {

	public static final Hashtable<UnitType, FlareUnitAnimation> Animations = new Hashtable<UnitType, FlareUnitAnimation>();
	public static ShaderProgram UnitShader;

	static {
		if(Content.isUsingResources()) {
			Animations.put(UnitType.Villager, (FlareUnitAnimation) Content.getImage("units/ArcherSpriteSheet-Flare.flare"));
			Animations.put(UnitType.Swordsman, (FlareUnitAnimation) Content.getImage("units/OrcSpriteSheet-Flare.flare"));
			Animations.put(UnitType.Archer, (FlareUnitAnimation) Content.getImage("units/ArcherSpriteSheet-Flare.flare"));
		}
	}

	protected final UnitType unitType;
	public UnitGroup Group;
	public Point Velocity = new Point(0, 0);
	public Point TVelocity = new Point(0, 0);
	public byte Direction = 0;
	public float AnimationPlayTime;
	private int Health;

	public Unit(Player owner, Point position, UnitType type) {
		super(owner, new EllipseBounds(position, type.Size, false), position);
		actions = new ActionStack(this);
		unitType = type;
		Health = ResearchManager.getStats(owner, unitType).MaxHealth;
	}

	public ActionStack getActionStack() { return actions; }

	public UnitType getType() { return unitType; }

	public UnitStats getStats() { return ResearchManager.getStats(this); }
	
	public void update(float elapsed) {
		actions.update(elapsed);
		if(this.getHealth() < 0)
			die();

		Velocity.X = (Velocity.X + TVelocity.X) / 2f;
		Velocity.Y = (Velocity.Y + TVelocity.Y) / 2f;
		position.X += Velocity.X * elapsed;	
		position.Y += Velocity.Y * elapsed;

		if(Velocity.lengthSq() > 2) {
			faceTowards(Velocity.angle());
		} else
			Direction = 6;
	}


	public byte faceTowards(Point lookAt) {
		return faceTowards(lookAt.minus(position).angle());
	}

	public byte faceTowards(float angle) {
		return Direction = (byte)(8 - (int)((angle + FMath.PI * 2 / 3) / FMath.PI * 4));
	}

	public EllipseBounds getBounds() {
		return (EllipseBounds) Bounds;
	}

	@Override
	public boolean canTransverseWater() { return getType().OverWater; }
	@Override
	public boolean airborn() { return getType().IsAirborn; }
	@Override
	public boolean canTransverseLand() { return getType().OverLand; }

	public void setGroup(UnitGroup group) {
		if(Group != null)
			group.remove(this);
		Group = group;
		Group.addUnit(this);
	}

	public void draw() {
		AnimationPlayTime += TimeKeeper.getElapsed();
		FlareUnitAnimation img = Animations.get(unitType);
		ErrorManager.GLErrorCheck();

		float depth = Canvas.calcDepth(position.Y);
		float xSize = getType().Size;
		float ySize = xSize / 2;		
		ErrorManager.GLErrorCheck();

		Canvas.fillEllipse(position, xSize, ySize, Owner.MyTeam.MainColor, depth);	
		ErrorManager.GLErrorCheck();

		actions.running().setState(img);

		ErrorManager.GLErrorCheck();

		Canvas.drawImage(img, position.X - img.getWidth() * 0.5f, position.Y - img.getHeight() * 0.75f, depth - 0.001f);
		ErrorManager.GLErrorCheck();
	}

	public void die() {
		CorpseDrawer.addCorpse(this);
		// TODO: play death sound
		Selection.remove(this);
		Map.get().removeEntity(this);
	}

	@Override
	public void remove() {
		Selection.remove(this);
		Map.get().removeEntity(this);
	}

	@Override
	public void drawSelected() {
		float depth = Canvas.calcDepth(position.Y) + 0.001f;
		float xSize = getType().Size;
		float ySize = xSize / 2;		
		GL11.glAlphaFunc(GL11.GL_ALWAYS, 0.0f);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		float invZ = 1 / Canvas.getZoom();

		Canvas.drawEllipse(position, xSize + 5 * invZ, ySize + 3 * invZ, Owner.MyTeam.DarkColor, 2.5f * invZ, depth);

		if(getActionStack().running() instanceof MoveToAction) {
			MoveToAction act = (MoveToAction) actions.running();
			if(act.path != null) {
				Color.YELLOW.bind();
				GL11.glPointSize(4);
				GL11.glLineWidth(2);
				GL11.glBegin(GL11.GL_LINE_STRIP);

				position.bind3(-1);
				for(int i = act.path.getCurrentPoint(); i < act.path.length(); i++) {
					Point pt = act.path.get(i);
					if(pt == null) {
						ErrorManager.logInfo("null point in path.");
						continue;
					}
					GL11.glVertex3f(pt.X, pt.Y, -1);
				}
				GL11.glEnd();
			}
		}
		
		
		if(getActionStack().running() instanceof GroupAction) {
			GroupAction act = (GroupAction) actions.running();
			if(act.Group.getPath() != null) {
				Color.YELLOW.bind();
				GL11.glPointSize(4);
				GL11.glLineWidth(2);
				GL11.glBegin(GL11.GL_LINE_STRIP);
				Path path = act.Group.getPath();
				
				position.bind3(-1);
				
				for(int i = path.getCurrentPoint(); i < path.length(); i++) {
					Point pt = path.get(i);
					if(pt == null) {
						ErrorManager.logInfo("null point in path.");
						continue;
					}
					GL11.glVertex3f(pt.X, pt.Y, -1);
				}
				GL11.glEnd();
			}
		}
	}

	@Override
	public int getHealth() {
		return Health; 
	}

	@Override
	public float getHealthFraction() {
		return Health / (float)ResearchManager.getStats(this).MaxHealth;
	}

	@Override
	public void damage(float attack, DamageType type) {
		Health -= (int)(attack - getStats().Defense);
		if(Health <= 0.5f)
			die();
	}

	@Override
	public Image getIcon() {
		return Content.getImage(getType().IconPath);
	}

	@Override
	public String getDisplayName() {
		return unitType.name().replace('_', ' ');
	}

	@Override
	public String[] getInfo() {
		UnitStats stats = ResearchManager.getStats(Owner, unitType);
		return new String[] {
				"Attack",		"" + stats.Attack,
				"Defense", 		"" + stats.Defense,
				"Range",		"" + stats.Range,
		};
	}

	/**
	 * Returns the action which should be applied between the supplied unit and target when
	 * the player right clicks on them. Null is returned if an acton has not yet been specified.
	 * TODO: complete this list.
	 */
	public static Action<?> getDefaultRightClickAction(Unit unit, IEntity target) {
		switch(unit.getType()) {
			// military units.
			case Archer:
			case Musketer:
			case Swordsman:
				if(target instanceof Entity) {
					ControllableEntity targ = (ControllableEntity)target;
					DiplomacyState state = targ.getOwner().getDiplomacyWith(unit.getOwner());
					switch(state) {
						case Enemy: case Neutral:	// military units attack enemy units/buildings
							return AttackAction.create(unit, targ);
						
						case Ally: case SameOwner:	// move to ally team units and units from the same player.
							return new MoveToAction(unit, targ.getPosition());
						
						default: throw new Error("Unknown diplomacy state " + state.toString());		
					}
				} 
				// just move to anything that's not an entity. 
				return new MoveToAction(unit, target.getPosition());


				
				
				// non military units
			case Villager:
				Villager vill = (Villager) unit;

				if(target instanceof ResourceElement) {
					return new CollectResourceAction(vill, (ResourceElement)target);

				} else if (target instanceof BuildingSite) {
					return new BuildAction(vill, (BuildingSite)target);
					
				} else if (target instanceof Building && ((Building)target).getHealthFraction() < 1.0f) {
					return new RepairAction(vill, (Building)target);
					
				} else if(target instanceof IEntity) {
					return new MoveToAction(vill, (IEntity)target);
				}

				break;


			case None: throw new Error("A unit should never have the type of UnitType.None");
			default:
				throw new Error("Type has not been implemented");
		}
		return null;
	}


	public static Unit spawnType(Player owner, Point pos, UnitType type) {
		switch(type) {
			case Villager: 
				return new Villager(owner, pos); 

			case Swordsman: 
				return new GroundMilitaryUnit(owner, pos, type);

			case Archer:
			case Musketer: 
				return new RangedMilitaryUnit(owner, pos, type);

			case None: 
				throw new Error("Something broke. Unit.spawnType() should never recieve the value UnitType.None");

			default:
				throw new Error("Spawning for unit type " + type + " is not yet implemented");
		}
	}
}