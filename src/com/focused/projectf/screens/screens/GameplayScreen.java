package com.focused.projectf.screens.screens;

import java.util.List;
import java.util.Vector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.MapGenerator;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.Technology;
import com.focused.projectf.TileConstants;
import com.focused.projectf.ai.IdleActionAssigner;
import com.focused.projectf.ai.pathfinding.PathFinder;
import com.focused.projectf.ai.pathfinding.UnitMovementManager;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.ControllableEntity;
import com.focused.projectf.entities.Entity;
import com.focused.projectf.entities.MapElement;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.SelectableEntity;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.entities.UnitType;
import com.focused.projectf.entities.collision.Bounding;
import com.focused.projectf.entities.units.Villager;
import com.focused.projectf.global.AlertsSystem;
import com.focused.projectf.global.ClickActionManager;
import com.focused.projectf.global.CorpseDrawer;
import com.focused.projectf.global.MiniMapDrawer;
import com.focused.projectf.global.ProjectileHandeler;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.global.SelectionBar;
import com.focused.projectf.global.UserProfile;
import com.focused.projectf.global.actionButtons.ActionButtonManager;
import com.focused.projectf.global.particles.ParticleManager;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.ResourceRendering;
import com.focused.projectf.graphics.TileRendering;
import com.focused.projectf.gui.GUIGroup;
import com.focused.projectf.gui.GUIView;
import com.focused.projectf.gui.TextView;
import com.focused.projectf.gui.vButton;
import com.focused.projectf.gui.vButton.OnClickListener;
import com.focused.projectf.input.Input;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.input.keybindings.KeyBinder;
import com.focused.projectf.interfaces.IEntity;
import com.focused.projectf.players.Player;
import com.focused.projectf.players.Selection;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;
import com.focused.projectf.resources.shaders.ShaderProgram;
import com.focused.projectf.screens.GUIScreen;
import com.focused.projectf.screens.Screen;
import com.focused.projectf.screens.ScreenManager;
import com.focused.projectf.utilities.TimeKeeper;
import com.focused.projectf.utilities.random.Chance;

public class GameplayScreen extends GUIScreen implements TileConstants {

	public static final int BOTTOM_BAR_HEIGHT = 160;

	public static KeyBinder BoundKeys;

	public TTFont ResourceFont;
	public Map map;
	public ShaderProgram Shader;

	public GUIGroup BottomBar, ResourceBar, MenuBar;
	public TextView Wood, Food, Stone, Gold, Radium, Population;

	public MiniMapDrawer MiniMap;

	private MapType mapType = MapType.Islands;
	private MapSize mapSize = MapSize.Large;

	public SelectionBar SelectionBar;

	public Vector<Ping> Pings = new Vector<Ping>();

	private Rect scissorRect;

	public GameplayScreen(Screen parent, MapType mapType, MapSize mapSize) {
		this(parent);
		this.mapType = mapType;
		this.mapSize = mapSize;
	}

	public GameplayScreen(Screen parent) {
		super(parent);
		BoundKeys = new KeyBinder();
		ResearchManager.initialize();

		Content.loadResources("effects/animationSet1.anim");
		ParticleManager.attachResources();
		ResearchManager.beginGame(Technology.Age0);
		instance = this;
	}

	public void buildGUI() {
		GUI.removeAllViews();

		TileRendering.loadResources();

		ResourceFont = Content.getFont("Arial", 16, false, false);

		BottomBar 		= new GUIGroup(GUI, GUIView.UNSET, 0, 0, 0, GUIView.UNSET, BOTTOM_BAR_HEIGHT);
		ResourceBar		= new GUIGroup(GUI, 0, 0, GUIView.UNSET, GUIView.UNSET, 300, 30);
		MenuBar 		= new GUIGroup(GUI, 0, GUIView.UNSET, GUIView.UNSET, 0, 300, 30);

		BottomBar.fillColor = Color.fromHex("225588");
		ResourceBar.fillColor = Color.fromHex("225588");
		MenuBar.fillColor = Color.fromHex("225588");

		new vButton(MenuBar, 3, GUIView.UNSET, GUIView.UNSET, 3, 60, 24, null, "Menu", new OnClickListener() {
			public void onClick(GUIView clicked) {
				ScreenManager.pushScreen(new GameplayMenu(GameplayScreen.this, false));
			}			
		});

		MapSize size = MapSize.values()[Chance.nextInt(MapSize.values().length)];
		System.out.println(size.name());
		map = MapGenerator.generateMap(mapType, mapSize);

		float actionButtonWidth = ActionButtonManager.createGUI(BottomBar, BOTTOM_BAR_HEIGHT - 10);

		MiniMap = new MiniMapDrawer(BottomBar, 5, GUIView.UNSET, 5, 5, (BOTTOM_BAR_HEIGHT - 10) * 2, GUIView.UNSET);

		SelectionBar = new SelectionBar(BottomBar, 5, actionButtonWidth + 10, 5, MiniMap.getWidth() + 10);

		Wood		= new TextView(ResourceBar, "0", 6, 3, 6, GUIView.UNSET, 47, GUIView.UNSET);
		Food		= new TextView(ResourceBar, "0", 6, 53, 6, GUIView.UNSET, 47, GUIView.UNSET);
		Gold		= new TextView(ResourceBar, "0", 6, 103, 6, GUIView.UNSET, 47, GUIView.UNSET);
		Stone		= new TextView(ResourceBar, "0", 6, 153, 6, GUIView.UNSET, 47, GUIView.UNSET);
		Radium 		= new TextView(ResourceBar, "0", 6, 203, 6, GUIView.UNSET, 47, GUIView.UNSET);
		Population	= new TextView(ResourceBar, "3 / 10", 6, 253, 6, GUIView.UNSET, 47, GUIView.UNSET);

		PathFinder.initialize(map);
	}

	@Override
	public void onFocusLost(Screen hasFocus) {
		TimeKeeper.pause();
		IdleActionAssigner.pause();
	}
	@Override
	public void onGainFocus(Screen lostFocus) {
		TimeKeeper.unpause();
		IdleActionAssigner.begin();
	}

	public void update(float elapsedTime) {
		super.update(elapsedTime);
		for(int i = map.getUnits().size() - 1; i >= 0; i--) {
			map.getUnits().get(i).update(elapsedTime);
		}

		Map.get().update();
		ProjectileHandeler.update(elapsedTime);

		Wood.Text	= "" + Player.getThisPlayer().Wood;
		Gold.Text	= "" + Player.getThisPlayer().Gold;
		Stone.Text	= "" + Player.getThisPlayer().Stone;
		Food.Text	= "" + Player.getThisPlayer().Food;
		Radium.Text	= "" + Player.getThisPlayer().Radium;

		for(int i = 0; i < Pings.size(); i++) {
			Ping ping = Pings.get(i);
			ping.TimeIn += elapsedTime;
		}
	}

	
	public void Layout() {
		super.Layout();
		scissorRect.setSize(Display.getWidth(), Display.getHeight() - BOTTOM_BAR_HEIGHT);
	}
	
	@Override
	public void draw(float elapsedTime) {
		
		if(MiniMap == null) {
			return;
		} else if(MiniMap.target == null) {
			MiniMap.initialize(Map.get().getWidthInTiles());
		}
		float z = 1f / Canvas.getZoom();
		Rect clip = new Rect(Canvas.getCenter().X - Canvas.getWidth() * z / 2f - Map.tileHalfWidth,
				Canvas.getCenter().Y - (Canvas.getHeight() - BOTTOM_BAR_HEIGHT) * z / 2f - Map.tileHalfHeight,
				Canvas.getWidth() * z + Map.tileWidth,
				(Canvas.getHeight() - BOTTOM_BAR_HEIGHT) * z + Map.tileHeight * 2);
		
		Canvas.enterGameplayCamera(); {
			
			TileRendering.renderTiles(clip);

			GL11.glDepthMask(false);
			CorpseDrawer.RenderCorpses();
			GL11.glDepthMask(true);

			renderEntities(elapsedTime, clip);	

			ProjectileHandeler.draw();

			ParticleManager.draw();

			renderOverlays();

			UnitMovementManager.update(elapsedTime);

		} Canvas.exitGameplayCamera();

		AlertsSystem.draw(elapsedTime);

		MiniMap.update();
		
		super.draw(elapsedTime);
	}

	public static ShaderProgram BehindObjectUnitShader;

	public void renderEntities(float elapsed, Rect clip) {

		ErrorManager.GLErrorCheck();
		if(BehindObjectUnitShader == null || !BehindObjectUnitShader.isLoaded()) {
			switch(UserProfile.ActiveProfile.UnitBehindSomethingShadeMode) {
				case 1: BehindObjectUnitShader = new ShaderProgram("shaders/unitBehindSomething-outline.frag"); break;
				case 2: BehindObjectUnitShader = new ShaderProgram("shaders/unitBehindSomething.frag"); break;
			}
		}

		GL11.glGetError();
		Unit.UnitShader = BehindObjectUnitShader;

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.3f);
		for(Entity e : map.getOtherEntities())
			if(e.getBounds().boundsIntersects(clip))
				if(map.isTileDiscovered(Map.toTile(e.getPosition())))
					e.draw();

		GL11.glPushAttrib(GL11.GL_DEPTH_BITS);

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.3f);
		GL11.glDepthRange(1, -1);
		GL11.glClearDepth(1);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDepthMask(true);

		for(Building b : map.getBuildings())	{		
			b.update(elapsed);
			if(clip.intersects(b.getDrawRect()))
				if(Map.get().isTileVissible(Map.toTile(b.getPosition())))
					b.draw();
		}

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.3f);
		for(MapElement b : map.getMapElements())
			if(clip.contains(b.getPosition()))
				if(map.isTileDiscovered(Map.toTile(b.getPosition())))
					b.draw();

		ResourceRendering.renderResources(clip);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.3f);
		List<Entity> entities = map.getOtherEntities();
		for(int i = 0; i < entities.size(); i++) {
			Entity b = entities.get(i);
			if(!(b instanceof ResourceElement)) {
				if(b instanceof ControllableEntity) {
					((ControllableEntity)b).update(elapsed);
				}
				if(clip.contains(b.getPosition()))
					if(map.isTileDiscovered(Map.toTile(b.getPosition())))
						b.draw();
			}
		}		

		UnitMovementManager.update(elapsed);

		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		Color.WHITE.bind();
		GL11.glLineWidth(5);
		Canvas.ellipse(GL11.GL_LINES, 0, 0,  100, 100, 0);
		ErrorManager.GLErrorCheck();
		for(int i = 0; i < Pings.size(); i++) {
			Ping ping = Pings.get(i);
			if(ping.TimeIn < 1.0) {
				float t = ping.TimeIn * 2 % 1.0f;
				GL11.glLineWidth(2);

				ping.PingColor.bind(1 - 1.25f * t);
				if(ping.Target != null) {
					ping.Target.glDraw(GL11.GL_LINE_STRIP, Canvas.calcDepth(ping.Target.getCenter().Y) + 0.03f);
					ping.PingColor.bind(0.4f - 0.65f * t);
					ping.Target.glDraw(GL11.GL_LINE_STRIP, -1);
				} else {
					
					final float radius = 15;
					final Color Y2 = ping.PingColor.withAlpha(1 - 0.9f * (t * 2 % 1));
					Point vert = ping.Point;
					GL11.glLineWidth(2);
					Canvas.drawEllipse(vert, radius * 2 * (1 - 0.9f * (t * 2 % 1)), radius * (1 - 0.9f * (t * 2 % 1)), Y2, 2);
					ErrorManager.GLErrorCheck();
				}

				ErrorManager.GLErrorCheck();				
			} else 
				Pings.remove(i--);
		}

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		ErrorManager.GLErrorCheck();

		if(UserProfile.ActiveProfile.UnitBehindSomethingShadeMode != 0) {

			GL11.glDepthMask(false);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0f);
			GL11.glDepthFunc(GL11.GL_GREATER);
			ErrorManager.GLErrorCheck();

			BehindObjectUnitShader.bind();

			if(BehindObjectUnitShader != null) {
				for(int i = map.getUnits().size() - 1; i >= 0; i--) {
					Unit e = map.getUnits().get(i); 
					e.update(elapsed);
					if(clip.contains(e.getPosition()) && (e.getOwner().equals(Player.getThisPlayer()) || Map.get().isTileVissible(Map.toTile(e.getPosition())))) {
						BehindObjectUnitShader.setUniformVec4("tint", e.getOwner().MyTeam.MainColor);
						e.draw();
					}
				}	
			}

			BehindObjectUnitShader.unbind();
			ErrorManager.GLErrorCheck();
		}

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.02f);
		GL11.glDepthMask(true);
		for(Unit e : map.getUnits()) { 
			if(clip.contains(e.getPosition()))
				if(e.getOwner().equals(Player.getThisPlayer()) || Map.get().isTileVissible(Map.toTile(e.getPosition()))) {
					e.draw();
				}
		}	
		ErrorManager.GLErrorCheck();

		List<SelectableEntity> entities2 = Selection.getAll();
		for(SelectableEntity ent : entities2) {
			ent.drawSelected();
			ErrorManager.GLErrorCheck();
		}

		GL11.glPopAttrib();	

		ErrorManager.GLErrorCheck();
	}

	@Override
	public String[] getRequiredResources() {
		return new String[] {
				"mapElements/allResources.png",
				"tiles/tile-mask.png",
				"tiles/grass1.png",
				"tiles/water1.png",
				"tiles/sand1.png",
				"units/ArcherSpriteSheet-Flare.flare",
				"buildings/house2.png"
		};
	}

	@Override
	public boolean fillsScreen() { return true; }

	public Rectangle getGameplayDrawRegion() {
		return new Rectangle(0,0, Canvas.getWidth(), Canvas.getHeight() - BOTTOM_BAR_HEIGHT);
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if(new Rect(0, 0, Display.getWidth(), Display.getHeight() - BOTTOM_BAR_HEIGHT).contains(event.Position))
			if(!ResourceBar.getAreaOnScreen().contains(event.Position))
				if(!MenuBar.getAreaOnScreen().contains(event.Position))
					ClickActionManager.process(event);

		ActionButtonManager.update();

		return super.onMouseEvent(event); 
	}

	public boolean onKeyEvent(KeyEvent event) {

		if(event.State.Down) {
			float speed = 1200 / Canvas.getZoom() * TimeKeeper.getElapsed();
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
				speed *= 3;

			switch(event.KeyId) {
				case Keyboard.KEY_UP:    Canvas.moveCenter(new Point(0, -1).times(speed)); break;
				case Keyboard.KEY_DOWN:  Canvas.moveCenter(new Point(0,  1).times(speed)); break;
				case Keyboard.KEY_LEFT:  Canvas.moveCenter(new Point(-1, 0).times(speed)); break;
				case Keyboard.KEY_RIGHT: Canvas.moveCenter(new Point( 1, 0).times(speed)); break;

				case Keyboard.KEY_F7:
					if(event.State.Change) {
						map.addEntity(Unit.spawnType(Player.getThisPlayer(), Canvas.getCenter().clone(), UnitType.Swordsman));
						map.addEntity(Unit.spawnType(Player.getDebuggingEnemyPlayer(), Canvas.getCenter().plus(30, 30).clone(), UnitType.Swordsman));
						map.addEntity(Unit.spawnType(Player.getDebuggingEnemyPlayer(), Canvas.getCenter().plus(60, 30).clone(), UnitType.Swordsman));
						map.addEntity(Unit.spawnType(Player.getDebuggingEnemyPlayer(), Canvas.getCenter().plus(90, 30).clone(), UnitType.Swordsman));
					}
					break;

				case Keyboard.KEY_F5:
					map.addEntity(new Villager(Player.getThisPlayer(), new Point(60 + Chance.randomInRange(-5, 5), 60 + Chance.randomInRange(-5, 5))));
					map.addEntity(new Villager(Player.getThisPlayer(), new Point(60 + Chance.randomInRange(-5, 5), -60 + Chance.randomInRange(-5, 5))));
					map.addEntity(new Villager(Player.getThisPlayer(), new Point(-60 + Chance.randomInRange(-5, 5), 60 + Chance.randomInRange(-5, 5))));
					map.addEntity(new Villager(Player.getThisPlayer(), new Point(-60 + Chance.randomInRange(-5, 5), -60 + Chance.randomInRange(-5, 5))));
					break;

				case Keyboard.KEY_F3:
					if(event.State.Change)
						Shader = new ShaderProgram("shaders/mapTiling.glsl");
					break;

				case Keyboard.KEY_F12: buildGUI(); break;
			}
		}
		return false;
	}




	public void renderOverlays() {

		ClickActionManager.drawOverlays(); 

		GL11.glPushAttrib(GL11.GL_DEPTH_BITS);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDepthMask(true);

		GL11.glPopAttrib();

		// map borders
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Color.RED.bind();
		GL11.glLineWidth(8);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		; GL11.glVertex2f(0, Map.get().getHeightInTiles() * Map.tileHalfHeight);
		; GL11.glVertex2f(Map.get().getWidthInTiles() * Map.tileHalfWidth, 0); 
		; GL11.glVertex2f(0, -Map.get().getHeightInTiles() * Map.tileHalfHeight);
		; GL11.glVertex2f(-Map.get().getWidthInTiles() * Map.tileHalfWidth, 0); 
		; GL11.glVertex2f(0, Map.get().getHeightInTiles() * Map.tileHalfHeight);
		GL11.glEnd();

		if(Selection.isJustBuildings()) {
			Object Target = Selection.getBuilding().UnitTarget;
			if(Target != null) {
				Point UnitTarget = null;
				GL11.glLineWidth(3);
				Color.WHITE.bind();
				GL11.glLineWidth(2);
				GL11.glBegin(GL11.GL_LINES);
				if(Target instanceof Point) {
					UnitTarget = (Point) Target;
					GL11.glVertex3f(UnitTarget.X, UnitTarget.Y, -1);
					GL11.glVertex3f(UnitTarget.X, UnitTarget.Y - 30, -1);
					GL11.glVertex3f(UnitTarget.X + 14, UnitTarget.Y + 7, -1);
					GL11.glVertex3f(UnitTarget.X - 14, UnitTarget.Y - 7, -1);
					GL11.glVertex3f(UnitTarget.X + 14, UnitTarget.Y - 7, -1);
					GL11.glVertex3f(UnitTarget.X - 14, UnitTarget.Y + 7, -1);
					GL11.glEnd();
				} else {
					UnitTarget = ((Entity)Target).getPosition();
					GL11.glVertex3f(UnitTarget.X, UnitTarget.Y, -1);
					GL11.glVertex3f(UnitTarget.X, UnitTarget.Y - 30, -1);
					GL11.glEnd();
					((Entity)Target).getBounds().glDraw(GL11.GL_LINE_STRIP, -1);
				}
			}
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_I)) {
			Point center = Map.toTile(new Point(0,0));
			if(Selection.getUnits().size() > 0)
				center = Map.toTile(Selection.getUnits().get(0).getPosition());

			Point mouse = Map.toTile(Canvas.toGamePoint(Input.getMousePosition()));
			drawLine((int)center.X, (int)center.Y, (int)mouse.X, (int)mouse.Y);

			// Nearest enterable tile 

			if(Selection.getUnits().size() > 0) {
				map = Map.get();

				int x = (int)mouse.X;
				int y = (int)mouse.Y;

				Point ret = null;
				Unit unit = Selection.getUnits().get(0);
				Point unitPos = unit.getPosition();

				for(int r = 1; r < 30; r++) {
					for(int xo = -r; xo < r; xo++) {
						if(map.safeblocked(unit, x + xo, y + r))
							plot(x + xo, y + r, Color.YELLOW.withAlpha(0.5f));
						else if(ret == null || unitPos.distSq(ret) > unitPos.distanceSq(x + xo, y + r))
							ret = new Point(x + xo, y + r);

						if(map.safeblocked(unit, x + xo + 1, y - r)) 
							plot(x + xo + 1, y - r, Color.YELLOW.withAlpha(0.5f));
						else if(ret == null || unitPos.distSq(ret) > unitPos.distanceSq(x + xo + 1, y - r))
							ret = new Point(x + xo + 1, y - r);
					}

					if(ret != null) {
						plot((int)ret.X, (int) ret.Y, Color.GREEN.withAlpha(0.5f));
						break;
					}

					for(int yo = -r; yo < r; yo++) {
						if(map.safeblocked(unit, x + r, y + yo + 1)) 
							plot(x + r, y + yo + 1, Color.YELLOW.withAlpha(0.5f));
						else if(ret == null || unitPos.distSq(ret) > unitPos.distanceSq(x + r, y + yo + 1))
							ret = new Point(x + r, y + yo + 1);

						if(map.safeblocked(unit, x - r, y + yo)) 
							plot(x - r, y + yo, Color.YELLOW.withAlpha(0.5f));
						else if(ret == null || unitPos.distSq(ret) > unitPos.distanceSq(x - r, y + yo))
							ret = new Point(x - r, y + yo);
					}
				}
			}
		}
	}



	public static class Ping {
		public Bounding Target;
		public Point Point;
		public float TimeIn;
		public Color PingColor;
		public float PingRadius;
		
		public Ping(IEntity atPoint) {
			this(atPoint, Color.GREEN, 20f);
		}
		public Ping(IEntity target, Color color, float radius) {
			Target = target.getBounds();
			TimeIn = 0;
			PingColor = color;
			PingRadius = radius;
		}
		public Ping(Point target) {
			this(target, Color.GREEN, 20f);
		}
		public Ping(Point target, Color color, float radius) {
			Point = target.clone();
			TimeIn = 0;
			PingColor = color;
			PingRadius = radius;
		}
		public Ping(Point target, Color color, float radius, float showTime) {
			Point = target.clone();
			TimeIn = 1f - showTime;
			PingColor = color;
			PingRadius = radius;
		}
	}

	void plot(int x, int y, Color col) {
		Point p = Map.fromTile(x, y);
		GL11.glBegin(GL11.GL_QUADS);
		; col.bind();
		; GL11.glVertex2f(p.X - Map.tileHalfWidth, p.Y);
		; GL11.glVertex2f(p.X, p.Y + Map.tileHalfHeight);
		; GL11.glVertex2f(p.X + Map.tileHalfWidth, p.Y);
		; GL11.glVertex2f(p.X, p.Y - Map.tileHalfHeight);
		GL11.glEnd();
	}
	int ipart(float x) { return (int) Math.floor(x); }
	int round(float x) { return (int)x; }
	float fpart(float x) { return x - ipart(x); }
	float rfpart(float x) { return 1 - fpart(x); }
	boolean blocked = false;
	private boolean drawLine(int x0, int y0, int x1, int y1) {

		blocked = false;
		boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);

		int temp = 0;

		if(steep) {
			temp = x0; x0 = y0; y0 = temp;
			temp = x1; x1 = y1; y1 = temp;
		}
		if (x0 > x1) {
			temp = x0; x0 = x1; x1 = temp;
			temp = y0; y0 = y1; y1 = temp;
		}
		float dx = x1 - x0;
		float dy = y1 - y0;
		float gradient = dy / dx;

		// handle first endpoint
		int xend = (int)x0;
		int yend = (int) (y0 + gradient * (xend - x0));
		float xgap = rfpart(x0 + 0.5f);
		int xpxl1 = xend;   //this will be used in the main loop
		int ypxl1 = ipart(yend);
		if (steep) {
			plot(ypxl1,   xpxl1, Color.RED.withAlpha(rfpart(yend) * xgap));
			plot(ypxl1+1, xpxl1,  Color.RED.withAlpha(fpart(yend) * xgap));
		} else {
			plot(xpxl1, ypxl1  , Color.RED.withAlpha(rfpart(yend) * xgap));
			plot(xpxl1, ypxl1+1,  Color.RED.withAlpha(fpart(yend) * xgap));
		}	
		float intery = yend + gradient; // first y-intersection for the main loop

		// handle second endpoint

		xend = round(x1);
		yend = (int) (y1 + gradient * (xend - x1));
		xgap = fpart(x1 + 0.5f);
		int xpxl2 = xend; //this will be used in the main loop
		int ypxl2 = ipart(yend);
		if (steep) {
			plot(ypxl2  , xpxl2, Color.RED.withAlpha(rfpart(yend) * xgap));
			plot(ypxl2 + 1, xpxl2,  Color.RED.withAlpha(fpart(yend) * xgap));
		} else {
			plot(xpxl2, ypxl2, Color.RED.withAlpha(rfpart(yend) * xgap));
			plot(xpxl2, ypxl2 + 1, Color.RED.withAlpha(fpart(yend) * xgap));
		}
		// main loop

		if(steep) {
			for (int x = xpxl1 + 1; x < xpxl2; x++) {
				plot(ipart(intery)  , x, Color.RED.withAlpha(rfpart(intery)));
				plot(ipart(intery) + 1, x,  Color.RED.withAlpha(fpart(intery)));
				intery += gradient;
			}
		} else {
			for (int x = xpxl1 + 1; x < xpxl2; x++) {
				plot(x, ipart(intery),  Color.RED.withAlpha(rfpart(intery)));
				plot(x, ipart(intery) + 1, Color.RED.withAlpha(fpart(intery)));
				intery = intery + gradient;
			}
		}

		return blocked;
	}


	public static GameplayScreen instance;
	public static GameplayScreen get() { return instance; }
}