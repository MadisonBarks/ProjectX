package com.focused.projectf.screens.screens;

import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.global.CorpseDrawer;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.Input;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;
import com.focused.projectf.screens.Screen;

public class DebugScreen extends Screen {

	public boolean vissible = false;
	public boolean drawTileGrid = false;

	TTFont font = Content.getFont("Arial", 12, true, false);//TODO: , Color.fromHex("000000"), 2);

	public DebugScreen(Screen parent) {
		super(parent);
	}

	@Override
	public boolean onKeyEvent(KeyEvent event) {

		if(event.State == ButtonState.Pressed) {
			switch(event.KeyId) {
				case Keyboard.KEY_F1: vissible = !vissible; break;
				case Keyboard.KEY_F2: drawTileGrid = !drawTileGrid; break;
				default:
					return true;
			}
			return false;
		}
		return true;
	}
	@Override
	public boolean onMouseEvent(MouseEvent event) {
		return true;
	}

	@Override
	public void onFocusLost(Screen hasFocus) { }
	@Override
	public void onGainFocus(Screen lostFocus) { }

	@Override
	public String[] getRequiredResources() { return null; }
	@Override
	public boolean fillsScreen() { return false; }

	@Override
	public void update(float elapsedTime) { }

	@Override
	public void draw(float elapsedTime) {

		if(!vissible || Map.get() == null) return;

		Canvas.pushClip(new Rect(0, 0, Canvas.getWidth(), Canvas.getHeight() - GameplayScreen.BOTTOM_BAR_HEIGHT));

		font.drawText("Stored Key Event Count: " + (Input.keys.size()), new Point(30, 30), Color.WHITE);
		int i = 0;
		for(Entry<Integer, KeyEvent> entry : Input.keys.entrySet()) {
			i++;
			font.drawText("Event " + i + "Key: " + entry.getKey() + "\t\t"
					+ Keyboard.getKeyName(entry.getKey()) + "  State: " + entry.getValue().State.name(), new Point(30, 30 + 11 * i), Color.WHITE);
		}
		i = 10;
		font.drawText(" --- Cursor --- ", 30, 30 + 11 * i, Color.WHITE); i++;
		Point mouse = Input.getMousePosition();
		font.drawText("Screen:", 30, 151, Color.WHITE); 
		font.drawText("" + (int)mouse.X, 120, 151, Color.WHITE); 
		font.drawText("" + (int)mouse.X, 180, 151, Color.WHITE);

		Point gs = Canvas.toGamePoint(mouse);
		font.drawText("Gamespace:", 30, 162, Color.WHITE); 
		gs.X = Math.round(gs.X * 100) / 100f;
		gs.Y = Math.round(gs.Y * 100) / 100f;
		font.drawText("" + gs.X, 120, 162, Color.WHITE); 
		font.drawText("" + gs.Y, 180, 162, Color.WHITE);

		Point tile = Map.toTile(gs);
		font.drawText("Tile:", 30, 173, Color.WHITE); 
		font.drawText("" + (int)tile.X, 120, 173, Color.WHITE); 
		font.drawText("" + (int)tile.Y, 180, 173, Color.WHITE);

		font.drawText("" + Canvas.calcDepth(Canvas.toGamePoint(mouse).Y), 100, 200);

		font.drawText("Corpses: " + CorpseDrawer.getCount(), 30, 211);

		for(int j = 0; j < 5; j++)
			font.drawText(Mouse.getButtonName(j) + "  " + Input.getMouseButtonState(j), 400, 30 + 12 * j);

		Point p0 = Canvas.toGamePoint(0, -Map.tileHeight);
		Point p1 = Canvas.toGamePoint(Canvas.getWidth() + Map.tileWidth * 2, Canvas.getHeight() - 200 + Map.tileHeight);
		Rect clip = new Rect(p0, p1);

		// fixes an odd drawing error. TODO: figure out the cause so it doesn't cause troule elsewhere
		GL11.glColorMask(false, false, false, true);
		Canvas.fillRectangle(Canvas.getRect(), Color.WHITE);
		GL11.glColorMask(true, true, true, true);

		Canvas.enterGameplayCamera();
		{
			Point gpmp;
			gpmp = new Point(Mouse.getX(), Canvas.getHeight() - Mouse.getY());
			gpmp = Canvas.toGamePoint(gpmp);
			Point tile2 = Map.toTile(gpmp);
			Point tile3 = Map.fromTile((int)tile2.X, (int)tile2.Y);


			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glBegin(GL11.GL_QUADS); {
				Color.GREEN.bind(0.4f);
				GL11.glVertex2f(tile3.X + Map.tileHalfWidth, tile3.Y);
				GL11.glVertex2f(tile3.X, tile3.Y - Map.tileHalfHeight);
				GL11.glVertex2f(tile3.X - Map.tileHalfWidth, tile3.Y);
				GL11.glVertex2f(tile3.X, tile3.Y + Map.tileHalfHeight);
			} GL11.glEnd();

			try {
				int tileType = Map.get().getTileType((int)tile2.X, (int) tile2.Y);
				GL11.glPointSize((Map.get().isCoastLine((int)tile2.X, (int) tile2.Y))? 10 : 15);
				GL11.glBegin(GL11.GL_POINTS);	

				Color.RED.bind();
				switch(tileType) {
					case Map.SAND: Color.BLACK.bind(); break;
					case Map.GRASS: Color.WHITE.bind(); break;
					case Map.WATER: Color.BLUE.bind(); break;
				}
				tile3.bind();
				GL11.glEnd();
			} catch(Exception ex) { }



			Canvas._beginLines(2, Color.GREEN, false);
			; Canvas._line(30, -30, -30,  30);
			; Canvas._line(30, 30, -30, -30);
			; Canvas._line(0, -30000, 0, 30000);
			; Canvas._line(-30000, 0, 30000, 0);
			Canvas._end();

			float cX = (Canvas.getCenter().X % Map.tileWidth);		// Map.TILE_WIDTH;
			float cY = (Canvas.getCenter().Y % Map.tileHeight);		// Map.TILE_HEIGHT;
			float x = Canvas.getCenter().X;
			float y = Canvas.getCenter().Y;
			float sX = x - cX;
			float sY = y - cY;

			if(drawTileGrid) {						
				float hf = (clip.getHeight() / 2 + Map.tileWidth) / Canvas.getZoom();
				float range = new Point(Canvas.getWidth(), Canvas.getHeight()).length() / Canvas.getZoom();
				range -= range % Map.tileWidth;
				Canvas._beginLines(1, Color.WHITE, true);
				for(float mX = -range; mX < range; mX += Map.tileWidth) {
					GL11.glVertex2f(mX + sX + hf * Map.tileSlope, sY - hf);
					GL11.glVertex2f(mX + sX - hf * Map.tileSlope, sY + hf);
					GL11.glVertex2f(mX + sX - hf * Map.tileSlope, sY - hf);
					GL11.glVertex2f(mX + sX + hf * Map.tileSlope, sY + hf);
				}
				Canvas._end();
			}	
		}
		Canvas.exitGameplayCamera();

		Canvas.popClip();
	}
}
