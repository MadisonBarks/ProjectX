package com.focused.projectf.gui;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.MouseEvent;
import com.focused.projectf.resources.TTFont;

public class DropDown extends GUIView {

	private boolean hover;
	private String[] Elements;
	private int ElementIndex;
	private boolean showingDropDown;
	private int hoverIndex;
	private OnChangeListener Listener;
	public TTFont Font;

	public DropDown(GUIGroup parent, float top, float left, float bottom, float right, float width, float height) {
		super(parent, top, left, bottom, right, width, height);
		Font = Canvas.Font15Bold;
	}

	@Override
	public void draw(float time) {
		Canvas.fillRectangle(region, (hover)? Color.GREEN : Color.WHITE);
		Canvas.drawRectangle(region, 2, Color.BLACK);
		if(Elements != null)
			Font.drawText(Elements[ElementIndex], region.getTopLeft().plus(2, 2), Color.BLACK);
		Rect ddb = new Rect(region.getBottomRight(), region.getTopRight().minus(20, 0));
		Canvas.drawRectangle(ddb, 2, Color.BLACK);
		
		GL11.glLineWidth(2);
		Color.BLACK.bind();

		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex2f(ddb.getX() + 4f, ddb.getY() + 4f);
		GL11.glVertex2f(ddb.getRight() - 4f, ddb.getY() + 4f);
		float dif = ddb.getWidth() / 2;
		GL11.glVertex2f(ddb.getX() + dif, ddb.getY() + dif);
		GL11.glVertex2f(ddb.getX() + 4, ddb.getY() + 4);
		GL11.glEnd();	

		if(showingDropDown) {
			float height = Font.getLineHeight() * Elements.length;
			Rect ddr = new Rect(region.getX(), region.getBottom(), region.getWidth(), height);
			Canvas.fillRectangle(ddr, Color.WHITE);
			if(hoverIndex >= 0)
				Canvas.fillRectangle(ddr.getX(), 
						region.getBottom() + hoverIndex * Canvas.Font15.getLineHeight(), 
						ddr.getWidth(),
						Font.getLineHeight(),
						Color.fromHex("8888FF"));
			Canvas.drawRectangle(ddr, 2, Color.BLACK);

			for(int i = 0; i < Elements.length; i++)
				Font.drawText(Elements[i], 
						region.getX() + 5,
						region.getBottom() + Font.getLineHeight() * i,
						Color.BLACK);
		}		
		hover = false;
	}

	public void onFocusLost(GUIView whoHasFocusNow) {
		showingDropDown = false;
	}

	@Override
	public boolean onMouseEvent(MouseEvent event) {
		if(getMouseEventPickupRect().contains(event.Position))
			hover = true;

		if(region.contains(event.Position)) {
			if(event.Button == MouseEvent.BUTTON_LEFT && event.State == ButtonState.Pressed) {
				showingDropDown = !showingDropDown;
				if(showingDropDown)
					getFocus();
				Parent.makeLastRendered(this);
				return true;
			}
		}

		if(showingDropDown) {
			hoverIndex = (int)((event.Position.Y - region.getBottom()) / Font.getLineHeight());
			if(event.Button == MouseEvent.BUTTON_LEFT && event.State == ButtonState.Pressed) {
				if(hoverIndex >= 0) {
					ElementIndex = hoverIndex;
					showingDropDown = false;
					if(Listener != null)
						Listener.onChange(this, ElementIndex, Elements[ElementIndex]);
				}
				return true;
			}			
		}
		return false;
	}

	public Rect getMouseEventPickupRect() { 
		Rect rect = getViewRegion().clone();
		if(showingDropDown && Elements != null && Font != null) 
			rect.setSize(rect.getWidth(), rect.getHeight() + Font.getLineHeight() * Elements.length);

		return rect;
	}

	@Override
	public boolean canTakeFocus() { return true; }

	public void setList(Enum<?>[] elements) {
		Elements = new String[elements.length];
		for(int i = 0; i < elements.length; i++)
			Elements[i] = elements[i].name();
	}

	public void setOnChangeListener(OnChangeListener listener) {
		Listener = listener;
	}

	public void setList(String... strings) {
		Elements = strings;
		ElementIndex = 0;
	}

	public String getElement() { return Elements[ElementIndex]; }
	public int getElementIndex() { return ElementIndex; }
	public void setElementIndex(int index) { ElementIndex = Math.min(index, Elements.length - 1); }

	public interface OnChangeListener {
		void onChange(DropDown dropDown, int elementIndex, String string);
	}
}