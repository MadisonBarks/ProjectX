package com.focused.projectf.global;

import java.util.Stack;
import java.util.Vector;

import com.focused.projectf.Point;
import com.focused.projectf.Point3;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.interfaces.IDamageable.DamageType;

public class ProjectileHandeler {

	public static Point3 ProjectileGravity = new Point3(0, 0, -90); 
	//... new Point3(0, -6, -9); // Something like this when arcs can be calculated

	public static final int ARROW_NORM = 0;

	public static Vector<Projectile> ActiveProjectiles = new Vector<Projectile>();

	public static Image[] projectileImages;

	public static void update(float elapsedTile) {
		int Steps = 1;
		ProjectileGravity = new Point3(0, 0, -1000);
		//List<Entity> entities = Map.get().getEntities();

		for(int step = 0; step < Steps; step++) {
			float elapsed = elapsedTile / Steps;
			Point3 gravityTime = ProjectileGravity.times(elapsed);

			for(int p = 0; p < ActiveProjectiles.size(); p++) {
				Projectile proj = ActiveProjectiles.get(p);

				if(proj.Position.Z < 0) { // TODO: also check for aircrafts.
					proj.recycle();
					p--;
				}	
				proj.Position.plusEquals(proj.Velocity.times(elapsed));
				proj.Velocity.plusEquals(gravityTime);
				System.out.println(proj.Position.Z);

			}
		}
	}

	public static void draw() {
		for(int p = 0; p < ActiveProjectiles.size(); p++) {
			Projectile proj = ActiveProjectiles.get(p);
			float x0 = proj.Position.X;
			float y0 = proj.Position.Y - proj.Position.Z;
			float velLen = 1 / proj.Velocity.length();
			float x1 = x0 +  proj.Velocity.X * velLen * 20;
			float y1 = y0 + proj.Velocity.Y * velLen * 20 - proj.Velocity.Z / 15;
			
			Canvas.drawLine(x0, y0, x1, y1, 3, Color.YELLOW);
		}
	}

	public static Point3 calculateVelocityFromTarget(Point3 pos, Point3 target, int type) {
		float velX, velY, velZ;
		float time = 1f; // TODO: calculate time by distance and type speed.
		velX = (target.X - pos.X) / time;
		velY = (target.Y - pos.Y) / time;
		velZ = (target.Z - pos.Z) / time - ProjectileGravity.Z * time / 2;
		return new Point3(velX, velY, velZ);
	}

	public static void makeNew(Point start, Point aim, Unit firer) {
		ActiveProjectiles.add(Projectile.instance(start.z(20), aim.z(0), ARROW_NORM, 20));
	}
	public static void makeNew(Point start, Point aim, Building firer) {
		ActiveProjectiles.add(Projectile.instance(start.z(20), aim.z(0), ARROW_NORM, 30));
	}



	public static class Projectile {

		public int HeldTime;
		public boolean isDead;
		public static Stack<Projectile> Stored = new Stack<Projectile>();

		public Point3 Position, Velocity;
		public int Type;
		public int Damage;

		private Projectile(Point3 pos, Point3 target, int type, int damage) {
			Position = pos;
			Type = type;
			Damage = damage;
			Velocity = calculateVelocityFromTarget(pos, target, type);
		}

		public DamageType getDamageType() {
			switch(Type) {
				case ARROW_NORM:
					return DamageType.Arrow;
				default:
					return DamageType.Unspecified;
			}
		}

		public Point calc2DPosition() {
			return new Point(Position.X, Position.Y);
		}

		public void recycle() { 
			Stored.push(this); 
			ActiveProjectiles.remove(this);
			Position.set(0, 0, 0);
			Velocity.set(0, 0, 0);
			Type = -1;
			Damage = 0;
			isDead = false;
			HeldTime = 0;
			
		}

		public static Projectile instance(Point3 pos, Point3 target, int type, int damage) {
			Projectile ret;
			if(Stored.size() > 0) {
				ret = Stored.pop();
				ret.Position = pos;
				ret.Velocity = calculateVelocityFromTarget(pos, target, type);
			} else 
				ret = new Projectile(pos, target, type, damage);

			return ret;
		}
	}
}