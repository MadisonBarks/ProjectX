package com.focused.projectf;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.utilities.FMath;

public class Point3 {
	
	public static final Point3 ZERO = new Point3(0, 0, 0);
	
	public float X, Y, Z;
	
	public Point3(float x, float y, float z) {
		X = x;
		Y = y;
		Z = z;
	}
	
	public Point3() { }

	public void bind() { GL11.glVertex3f(X, Y, Z); }
	public void bind2() { GL11.glVertex2f(X, Y); }
	
	public float length() { return (float) Math.sqrt((X * X) + (Y * Y) + (Z * Z)); }
	public float lengthSq() { return (X * X) + (Y * Y) + (Z * Z); }

	public float distance(Point3 pt) {
		Point3 p = new Point3(X - pt.X, Y - pt.Y, Z - pt.Z);
		return p.length();
	}
	public float distance(float x, float y, float z) {
		float s1 = X - x;
		float s2 = Y - y;
		float s3 = Y - y;
		return FMath.sqrt(s1 * s1 + s2 * s2 + s3 * s3);
	}
	public float distanceSq(Point3 pt) {
		Point3 p = new Point3(X - pt.X, Y - pt.Y, Z - pt.Z);
		return p.lengthSq();
	}
	public float distanceSq(float x, float y, float z) {
		float s1 = X - x;
		float s2 = Y - y;
		float s3 = Z - z;
		return s1 * s1 + s2 * s2 + s3 * s3;
	}

	public Point3 minus(Point3 position) {
		return new Point3(X - position.X, Y - position.Y, Z - position.Z);
	}
	public Point3 minus(float x, float y, float z) {
		return new Point3(X - x, Y - y, Z - z);
	}
	/** subtracts to this point and returns it */
	public Point3 minusEquals(float pX, float pY, float pZ) { 
		X -= pX; 
		Y -= pY; 
		Z -= pZ;
		return this;
	}
	public Point3 minusEquals(Point3 sub) {
		X -= sub.X;
		Y -= sub.Y;
		Z -= sub.Z;
		return this;
	}
	
	public Point3 clone() {
		return new Point3(X, Y, Z);
	}

	/**
	 * returns a new Point with X and Y multiplies by 'a'
	 */
	public Point3 times(float a) {
		return new Point3(X * a, Y * a, Z * a);
	}
	/**
	 * returns a new point with it's X and Y multiplied by those of 'pt'
	 */
	public Point3 times(Point3 pt) { 
		return new Point3(X * pt.X, Y * pt.Y, Z * pt.Z);
	}
	public Point3 times(float tx, float ty, float tz) {
		return new Point3(X * tx, Y * ty, Z * tz);
	}

	
	/**
	 * returns this with X and Y multiplies by 'a'
	 */
	public Point3 timesEquals(float a) {
		X *= a; Y *= a; Z *= a; return this;
	}
	/**
	 * returns this point with it's X and Y multiplied by those of 'pt'
	 */
	public Point3 timesEquals(Point3 pt) {
		X *= pt.X;
		Y *= pt.Y;
		Z *= pt.Z;
		return this;
	}
	public Point3 timesEquals(float tx, float ty, float tz) {
		X *= tx;
		Y *= ty;
		Z *= tz;
		return this;
	}

	public static Point3 getIdentity() { return new Point3(0, 0, 0); }

	/** returns a new point with the given values added */
	public Point3 plus(float x, float y, float z) { return new Point3(X + x, Y + y, Z + z); }
	public Point3 plus(Point3 add) {
		return new Point3(X + add.X, Y + add.Y, Z + add.Z);
	}
	/** adds to this point and returns it */
	public Point3 plusEquals(float pX, float pY, float pZ) { 
		X += pX; 
		Y += pY; 
		Z += pZ;
		return this;
	}
	public Point3 plusEquals(Point3 add) {
		X += add.X;
		Y += add.Y;
		Z += add.Z;
		return this;
	}
	
	public Point3 normalize() {
		float len = 1.0f / length();
		return new Point3(X * len, Y * len, Z * len);
	}
	public Point3 normalizeEquals() {
		float len = 1.0f / length();
		X *= len;
		Y *= len;
		Z *= len;
		return this;
	}
	
	public String toString() { return "(" + X + ", " + Y + ", " + Z + ")"; }
	public boolean isInvalid() { return Float.isNaN(X) || Float.isNaN(Y) || Float.isNaN(Z); }

	public Point xy() { return new Point(X, Y); }
	public Point xz() { return new Point(X, Z); }
	public Point yz() { return new Point(Y, Z); }

	public void set(float x, float y, float z) {
		X = x;
		Y = y; 
		Z = z;
	}
}
