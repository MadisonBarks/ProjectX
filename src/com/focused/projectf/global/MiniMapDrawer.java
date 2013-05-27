package com.focused.projectf.global;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.TileConstants;
import com.focused.projectf.entities.Entity;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.FrameBuffer;
import com.focused.projectf.gui.GUIGroup;
import com.focused.projectf.gui.GUIView;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.screens.screens.GameplayScreen;
import com.focused.projectf.utilities.FMath;
import com.focused.projectf.utilities.TimeKeeper;

public class MiniMapDrawer extends GUIView {

	public FrameBuffer target;

	public MiniMapDrawer(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		super(parent, top, left, bottom, right, width, height);
	}
	public MiniMapDrawer(GUIGroup parent, float top, float left, float bottom, float right) {
		super(parent, top, left, bottom, right);
	}

	public Rect Layout() {
		return super.Layout();
	}

	public void initialize(int mapSize) {		
		try {
			ErrorManager.GLErrorCheck();
			target = new FrameBuffer(mapSize, mapSize, GL11.GL_R3_G3_B2);
			ErrorManager.GLErrorCheck();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public float sinceLastUpdate = 1;
	public void update() {
		sinceLastUpdate += TimeKeeper.getElapsed();

		ErrorManager.GLErrorCheck();
		if(sinceLastUpdate > 1 && target != null) {
			Map map = Map.get();
			target.makeTarget();			
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glPushMatrix();
			// solves a really weird translation problem. Don't ask
			GL11.glTranslatef(0, Display.getHeight() - Map.get().Size.Size, 0); 			
			GL11.glPointSize(1); 
			GL11.glBegin(GL11.GL_POINTS);
			ErrorManager.GLErrorCheck();

			int minX = 10000, minY = 10000, maxX = 0, maxY = 0;

			for(int x = 0; x < map.getWidthInTiles(); x++) {
				for(int y = 0; y < map.getHeightInTiles(); y++) {
					if(map.isTileDiscovered(x, y)) {
						tileColors[map.getTileType(x, y)].bind();
						GL11.glVertex2f(x, y);
						minX = FMath.min(minX, x);
						maxX = FMath.max(maxX, x);
						minY = FMath.min(minY, y);
						maxY = FMath.max(maxY, y);
					}
				}
			}
			GL11.glEnd(); GL11.glGetError();

			GL11.glPointSize(Map.get().Size.UnitPointSize);
			ErrorManager.GLErrorCheck();
			GL11.glBegin(GL11.GL_POINTS);

			for(Unit u : map.getUnits()) {
				Point tile = Map.toTile(u.getPosition());
				if(!u.getOwner().OnThisMachine() && !Map.get().isTileVissible((int)tile.X, (int)tile.Y))
					continue;
				u.getOwner().MyTeam.MainColor.bind();
				GL11.glVertex2f(tile.X, tile.Y);				
			}

			for(Entity u : map.getOtherEntities()) {

				if(u instanceof ResourceElement) {
					Point tile = Map.toTile(u.getPosition());
					if(!Map.get().isTileDiscovered(tile))
						continue;
					//((ResourceElement)u).ResourceType.MiniMapColor.bind();
					switch(((ResourceElement)u).DepositType) {
						case Food: break;
						case Gold: Color.YELLOW.bind(); break;
						case Radium: Color.GREEN.bind(); break;
						case Stone: Color.fromHex("666666").bind(); break;
						case Wood: Color.fromHex("006600").bind(); break;
						default:continue;
					}
					GL11.glVertex2f(tile.X, tile.Y);				
				}
			}

			GL11.glEnd();
			GL11.glPopMatrix();
			ErrorManager.GLErrorCheck();
			target.revertTarget();
			sinceLastUpdate -= 1;
		}
	}
	@Override
	public void draw(float time) { 

		Point center = getViewRegion().getCenter();

		float h = Math.min(getWidth() / 2, getHeight()) / 2f;
		float hWidth = h * 2;
		float hHeight = h;
		Canvas.drawRectangle(getViewRegion(), 1, Color.GREEN);
		ErrorManager.GLErrorCheck();

		if(target == null)
			return;

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		target.bindAsTexture();
		Color.WHITE.bind();
		ErrorManager.GLErrorCheck();


		GL11.glBegin(GL11.GL_QUADS);
		ErrorManager.GLErrorCheck();
		;  target.bindCoorner(2);
		;  GL11.glVertex2f(center.X + hWidth, center.Y);		
		ErrorManager.GLErrorCheck();
		;  target.bindCoorner(1);
		;  GL11.glVertex2f(center.X, center.Y - hHeight);		
		ErrorManager.GLErrorCheck();
		;  target.bindCoorner(0);
		;  GL11.glVertex2f(center.X - hWidth, center.Y);		
		ErrorManager.GLErrorCheck();
		;  target.bindCoorner(3);
		;  GL11.glVertex2f(center.X, center.Y + hHeight);
		ErrorManager.GLErrorCheck();
		GL11.glEnd();

		GL11.glGetError();	// clears an error created by GL11.glEnd() for no apparent reason

		ErrorManager.GLErrorCheck();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		Rect region = getViewRegion();

		Point offset = new Point(Canvas.getWidth() / 2 / Canvas.getZoom(),  (Canvas.getHeight() - GameplayScreen.BOTTOM_BAR_HEIGHT) / 2 / Canvas.getZoom());

		ErrorManager.GLErrorCheck();
		Point ul = Canvas.getCenter().minus(offset);
		Point br = Canvas.getCenter().plus(offset);
		ul.timesEquals(region.getWidth() / (Map.get().getWidthInTiles() * Map.tileWidth));
		br.timesEquals(region.getWidth() / (Map.get().getWidthInTiles() * Map.tileWidth));
		Point mmUL = region.getCenter().plus(ul);
		Point mmBR = region.getCenter().plus(br);

		ErrorManager.GLErrorCheck();
		Canvas.drawRectangle(new Rect(mmUL,  mmBR), 1, Color.WHITE);
		ErrorManager.GLErrorCheck();

	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		Point center = getViewRegion().getCenter();
		float X = event.Position.X;
		float Y = event.Position.Y;
		
		Point miniOffset = center.minus(X, Y);			
		Point realMapSpace = miniOffset.times(Map.tileWidth * Map.get().getWidthInTiles() / -getWidth());

		switch(event.Button) {
			case MouseEvent.BUTTON_LEFT:
				if(event.State.Down) {
					Canvas.setCenter(realMapSpace);
					return true;
				}
				return false;

			case MouseEvent.BUTTON_RIGHT:
				if(event.State == ButtonState.Pressed) {
					event.Position = Canvas.fromGamePoint(realMapSpace);
					ClickActionManager.process(event);
				}
			default:
				return false;
		}
	}

	@Override
	public boolean canTakeFocus() { return false; }


	public static final Color[] tileColors = new Color[100];
	static {
		tileColors[TileConstants.GRASS] 	= Color.fromHex("138900");
		tileColors[TileConstants.SAND] 		= Color.fromHex("FFFF73");
		tileColors[TileConstants.WATER] 	= Color.fromHex("3C9DD0");

		for(int i = 0; i < tileColors.length; i++)
			if(tileColors[i] == null)
				tileColors[i] = Color.RED;
	};
}
