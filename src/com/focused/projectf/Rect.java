package com.focused.projectf;

import java.io.Serializable;

import org.lwjgl.util.Rectangle;

public class Rect implements Serializable {

	private static final long serialVersionUID = -1061101539505845841L;

	protected float X, Y, Width, Height;

	public Rect(float x, float y, float width, float height) {
		X = x;
		Y = y;
		Width = Math.abs(width);
		Height = Math.abs(height);
	}

	public Rect(Point corner1, Point corner2) {
		X = Math.min(corner1.X, corner2.X);
		Y = Math.min(corner1.Y, corner2.Y);
		Width = Math.abs(corner1.X - corner2.X);
		Height = Math.abs(corner1.Y - corner2.Y);
	}

	public Rect() { }

	public void translate(Point to) {
		X = to.X;
		Y = to.Y;
	}
	public void translate(float newX, float newY) {
		X = newX;
		Y = newY;
	}
	public void move(Point shift) {
		X += shift.X;
		Y += shift.Y;
	}
	public void move(float shiftX, float shiftY) {
		X += shiftX;
		Y += shiftY;
	}

	public float getX() { return X; }
	public float getY() { return Y; }
	public float getWidth() { return Width; }
	public float getHeight() { return Height; }

	public int getXi() { return (int)X; }
	public int getYi() { return (int)Y; }
	public int getWidthi() { return (int)Width; }
	public int getHeighti() { return (int)Height; }

	public Rect shrink(float amount) {
		return new Rect(X + amount, Y + amount, Width - 2 * amount, Height - 2 * amount);
	}
	public Rect shrink(float widthChange, float heightChange) {
		return new Rect(X + widthChange, Y + heightChange, Width - 2 * widthChange, Height - 2 * heightChange);
	}
	public Rect grow(float amount) {
		return new Rect(X - amount, Y - amount, Width + 2 * amount, Height + 2 * amount);
	}
	public Rect grow(float widthChange, float heightChange) {
		return new Rect(X - widthChange, Y - heightChange, Width + 2 * widthChange, Height + 2 * heightChange);
	}

	public boolean contains(Point point) {
		return (point.X >= X && point.X <= X + Width) &&
				(point.Y >= Y && point.Y <= Y + Height);
	}
	public boolean contains(float x, float y) {
		return x >= X && x <= X + Width && y >= Y && y <= Y + Height;
	}
	public Point getCenter() { return new Point(X + Width / 2, Y + Height / 2); }

	public Point getTopLeft() { return new Point(X, Y); }
	public Point getTopRight() { return new Point(X + Width, Y); }
	public Point getBottomLeft() { return new Point(X, Y + Height); }
	public Point getBottomRight() { return new Point(X + Width, Y + Height); }


	public Rectangle toLWJGL() {
		return new Rectangle((int)X, (int)Y, (int)Width, (int)Height);
	}

	public void setSize(float width, float height) {
		Width = width;
		Height = height;
	}

	public boolean intersects(Rect rect) {
		return (X < (rect.X + rect.Width) && (X + Width) > rect.X &&
				Y < (rect.Y + rect.Height) && (Y + Height) > rect.Y);
	}
	
	public Rect clone() {
		return new Rect(X, Y, Width, Height);
	}

	public float getTop() { return Y; }
	public float getBottom() { return Y + Height; }
	public float getLeft() { return X; }
	public float getRight() { return X + Width; }

	public Rect moved(Point move) {
		return new Rect(X + move.X, Y + move.Y, Width, Height);
	}

	public Rect plus(Point add) {
		return new Rect(X + add.X, Y + add.Y, Width, Height);
	}

	public void setCenter(Point center) {
		X = center.X - Width / 2f;
		Y = center.Y - Height / 2f;	
	}

	public void setX(float x) { X = x; }
	public void setY(float y) { Y = y; }
}
