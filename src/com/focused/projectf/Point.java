package com.focused.projectf;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.utilities.FMath;

public class Point {
	
	public static final Point ZERO = new Point(0, 0);
	
	public float X, Y;
	
	public Point(float x, float y) {
		X = x;
		Y = y;
	}
	
	public Point() { }

	public boolean equals(Point p) {
		return (X == p.X) & (Y == p.Y);
	}
	
	public void bind() { GL11.glVertex2f(X, Y); }
	public void bind3(float Z) { GL11.glVertex3f(X, Y, Z); }
	
	public float length() { return (float) Math.sqrt((X * X) + (Y * Y)); }
	public float lengthSq() { return (X * X) + (Y * Y); }

	public float distance(Point pt) {
		Point p = new Point(X - pt.X, Y - pt.Y);
		return p.length();
	}
	public float distance(float x, float y) {
		float s1 = X - x;
		float s2 = Y - y;
		s1 *= s1;
		s2 *= s2;		
		float d = FMath.sqrt(s1 + s2);
		return d;
	}
	public float distSq(Point pt) {
		return  (X - pt.X) * (X - pt.X) + (Y - pt.Y) * (Y - pt.Y);
	}
	public float distanceSq(float x, float y) {
		float s1 = X - x;
		float s2 = Y - y;
		s1 *= s1;
		s2 *= s2;		
		return s1 + s2;
	}

	public Point minus(Point position) {
		return new Point(X - position.X, Y - position.Y);
	}
	public Point minus(float x, float y) {
		return new Point(X - x, Y - y);
	}
	/** subtracts to this point and returns it */
	public Point minusEquals(float pX, int pY) { 
		X -= pX; 
		Y -= pY; 
		return this;
	}
	public Point minusEquals(Point sub) {
		X -= sub.X;
		Y -= sub.Y;
		return this;
	}
	
	public Point clone() {
		return new Point(X, Y);
	}

	/**
	 * returns a new Point with X and Y multiplies by 'a'
	 */
	public Point times(float a) {
		return new Point(X * a, Y * a);
	}
	/**
	 * returns a new point with it's X and Y multiplied by those of 'pt'
	 */
	public Point times(Point pt) { 
		return new Point(X * pt.X, Y * pt.Y);
	}
	public Point times(float tx, float ty) {
		return new Point(X * tx, Y * ty);
	}

	
	/**
	 * returns this with X and Y multiplies by 'a'
	 */
	public Point timesEquals(float a) {
		X *= a; Y *= a; return this;
	}
	/**
	 * returns this point with it's X and Y multiplied by those of 'pt'
	 */
	public Point timesEquals(Point pt) {
		X *= pt.X;
		Y *= pt.Y;
		return this;
	}
	public Point timesEquals(float tx, float ty) {
		X *= tx;
		Y *= ty;
		return this;
	}

	public static Point getIdentity() { return new Point(0, 0); }

	/** returns a new point with the given values added */
	public Point plus(float x, float y) { return new Point(X + x, Y + y); }
	public Point plus(Point add) {
		return new Point(X + add.X, Y + add.Y);
	}
	/** adds to this point and returns it */
	public Point plusEquals(float pX, float pY) { 
		X += pX; 
		Y += pY; 
		return this;
	}
	public Point plusEquals(Point add) {
		X += add.X;
		Y += add.Y;
		return this;
	}
	
	public Point normalize() {
		float l = length();
		if(l == 0)
			return clone();
		float len = 1.0f / length();
		return new Point(X * len, Y * len);
	}
	public Point normalize(float i) {
		float l = length();
		if(l == 0)
			return clone();
		float len = i / l;
		return new Point(X * len, Y * len);
	}
	public Point normalizeEquals() {
		float len = 1.0f / length();
		X *= len;
		Y *= len;
		return this;
	}

	public Point normalizeEquals(float i) {
		float l = length();
		if(l == 0) return clone();
		float len = i / l;
		X *= len;
		Y *= len;
		return this;
	}
	
	public String toString() {
		return "(" + X + ", " + Y + ")";
	}

	public boolean isInvalid() {
		return Float.isNaN(X) || Float.isNaN(Y);
	}

	public Point3 z(float z) {
		return new Point3(X, Y, z);
	}

	public float angle() {
		return (float)Math.atan2(X, Y);
	}

	public static Point fromAngle(float theta, float lengthX, float lengthY) {
		//theta = FMath.wrap(theta, -FMath.PI, FMath.PI);
		Point p = new Point(
				(float)Math.sin(theta) * lengthX,
				(float)Math.cos(theta) * lengthY);
		return p;
	}
	public static Point fromAngle(float theta, float length) {
		//theta = FMath.wrap(theta, -FMath.PI, FMath.PI);
		Point p = new Point(
				(float)Math.sin(theta) * length,
				(float)Math.cos(theta) * length);
		return p;
	}
	/*
	public static Point fromAngle(float theta, float length) {
		return new Point(
				FMath.sin(FMath.wrap(theta, -FMath.PI, FMath.PI)) * length,
				FMath.cosF(FMath.wrap(theta, -FMath.HALF_PI * 3, FMath.HALF_PI)) * length);
	}
	 */
	public Point plus(Point add, float times) {
		return new Point(X + add.X * times, Y + add.Y * times);
	}

	public void bindDepth() {
		bind3(Canvas.calcDepth(Y));
	}

}
