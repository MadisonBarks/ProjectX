package com.focused.projectf.ai.pathfinding;

import java.util.List;
import java.util.Vector;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.ai.Action;
import com.focused.projectf.ai.actions.MoveToAction;
import com.focused.projectf.entities.Building;
import com.focused.projectf.entities.Entity;
import com.focused.projectf.entities.Unit;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.utilities.FMath;

public class UnitMovementManager {

	public static Vector<UnitGroup> Groups = new Vector<UnitGroup>();

	public static int[] directions = { 1, 1, -1, -1, 1, 1, -1, -1 };

	public static void update(float elapsed) {
		
		if(elapsed < 5)
			return;
		
		GL11.glDepthFunc(GL11.GL_ALWAYS);
		Map map = Map.get();
		List<Unit> units = map.getUnits();

		for(int a = 0; a < units.size(); a++) {
			Unit u1 = units.get(a);
			float velAngle1 = u1.Velocity.angle();
			//float testCenter = velAngle1;
			//Canvas.drawLine(u1.getPosition(), u1.getPosition().plus(u1.Velocity), 3, Color.GREEN, -1);
			//Canvas.drawLine(u1.getPosition(), u1.getPosition().plus(Point.fromAngle(testCenter + FMath.PI / 3, 60)), 3, Color.WHITE, -1);
			//Canvas.drawLine(u1.getPosition(), u1.getPosition().plus(Point.fromAngle(testCenter - FMath.PI / 3, 60)), 3, Color.WHITE, -1);

			float u1Size = u1.getType().Size;
			for(int b = 0; b < units.size(); b++) {
				if(a == b)
					continue;
				Unit u2 = units.get(b);
				float xdif = u1.getPosition().X - u2.getPosition().X;
				float ydif = u1.getPosition().Y * 2 - u2.getPosition().Y * 2;
				float distSq = xdif * xdif + ydif * ydif;

				if(distSq < 9 * (u1Size * 4 * u1Size)) {
					float velAngle2 = u2.Velocity.angle();
					float u1ToU2Angle = u2.getPosition().minus(u1.getPosition()).angle();

					if(FMath.abs(velAngle1 - u1ToU2Angle) < FMath.HALF_PI) {	// if u2 is in front of u1
						Point velShift = new Point(0, 0);

						//Point velDiff = u1.Velocity.minus(u2.Velocity);
						if(FMath.abs(u1.Velocity.angle() - u2.Velocity.angle()) < FMath.PI / 5) {
							velShift = Point.fromAngle((velAngle1 - velAngle2) - FMath.PI / 3, FMath.sqrt(FMath.sqrt(distSq * 6)));
						}
						Canvas.drawLine(u1.getPosition(), u1.getPosition().plus(velShift), 3, Color.WHITE, -1);

						u1.Velocity.X = (u1.Velocity.X * 4 + velShift.X) / 5f;
						u1.Velocity.Y = (u1.Velocity.Y * 4 + velShift.Y) / 5f;
					}
					if(distSq * 0.8f < (u1Size + u2.getType().Size) * (u1Size + u2.getType().Size)) {
						Point normal = u1.getPosition().minus(u2.getPosition());
						normal = normal.normalize(FMath.sqrt(distSq) - (u1Size + u2.getType().Size)).times(-0.4f);
						u1.move(normal);
						if(!u2.getActionStack().running().holdPosition())
							u2.Velocity.plusEquals(normal.timesEquals(-1));
					}
				}
			}

			Point tile = Map.toTile(u1.getPosition());
			if(map.blocked(u1, u1.getPosition())) {
				Point normal = u1.getPosition().minus(Map.fromTile(tile));
				normal = normal.normalize(10f / normal.length());
				u1.move(normal);
			}
		}
	}


	protected static void nonUnitOverlap(Unit e1, Entity e2) {
		if(e2 instanceof Building) {
			if(e1.getBounds().collides(e2.getBounds())) {
				e1.move(e1.getBounds().calcNormal(e2.getBounds()));
			}
		}
	}

	public static void startMovement(List<Unit> units, Point target) {
		UnitGroup group = null;
		if(units.size() > 1) {
			group = UnitGroup.instance();

			Point center = new Point(0, 0);
			int count = 0;
			for(Unit u : units) {
				count++;
				center.plusEquals(u.getPosition());
			}
			center.times(1.0f / count);

			for(Unit u : units) {
				UnitGroup prev = u.Group;
				u.setGroup(group);
				if(prev != null)
					if(prev.size() == 0)
						prev.recycle();
			}

			group.setMoveTo(target);
			return;
		} 

		units.get(0).getActionStack().set(new MoveToAction(units.get(0), target));
	}

	public static void checkUnits(Unit u1, Unit u2) {

		if(!u1.getBounds().softCollides(u2.getBounds()))
			return; 
		boolean hardCollision = u1.getBounds().collides(u2.getBounds());
		System.out.println("Soft Collision");

		Point norm = u1.getBounds().calcNormal(u2.getBounds());
		Point normal = norm.normalize();
		if(!hardCollision)
			normal.timesEquals(-0.5f);

		notMovingInGroup:
			if(u1.Group == u2.Group && u1.Group != null) {
				MoveToAction action;
				Action<?> act = u1.getActionStack().running();
				if(act instanceof MoveToAction)
					action = (MoveToAction) act;
				else {
					act = u2.getActionStack().running();
					if(act instanceof MoveToAction)
						action = (MoveToAction) act;
					break notMovingInGroup;
				}
				if(action.path != null & u1.Group != null)
					if(u1.getPosition().distance(action.path.getTarget()) < u1.Group.size() * u1.getType().Size / 3)
						u1.getActionStack().compleated(action);
			}


		u1.move(normal);
		Point norm2 = u2.getBounds().calcNormal(u1.getBounds());
		u2.move(normal.times(-1, 1));
		System.out.println(normal.toString() + "    " + norm2.toString());

	}
}


















/*



//wandering particle engine
class DParticleEngine{
	ArrayList partArray;


	DParticleEngine(){
		partArray=new ArrayList();
	}

	void update(){
		for(int i=partArray.size()-1;i>=0;i--){
			Unit currentPart=(Unit) partArray.get(i);
			updateParticle(currentPart);
		}
	}

	void display(){
		for(int i=partArray.size()-1;i>=0;i--){


			Unit currentPart=(Unit) partArray.get(i);
			displayParticle(currentPart);
		}
	}


	void updateParticle(Unit pParticle){
		//  if(!pParticle.arrived){
		pParticle.setSpeedRange(0,2);
		pParticle.setSenseDistance(50);

		ArrayList nearFlockmates=getNearFlockmates(pParticle,partArray);

		Point prevVelocity=pParticle.Velocity;
		Point wanderVelocity=wander(pParticle.wanderDirection,0.5,1);

		//pParticle.setChase(mouseX,mouseY);
		//Point chaseVelocity=chase(pParticle,1);

		Point avoidVelocity=avoid(pParticle,nearFlockmates,1);
		Point cohesionVelocity=cohesion(pParticle,nearFlockmates,1);
		Point alignmentVelocity=alignment(pParticle,nearFlockmates,1);

		pParticle.Velocity=Point.add(prevVelocity,wanderVelocity);
		pParticle.Velocity.add(avoidVelocity);
		pParticle.Velocity.add(cohesionVelocity);
		pParticle.Velocity.add(alignmentVelocity);

		//pParticle.Velocity.add(chaseVelocity);


		float magnitude=pParticle.Velocity.mag();
		magnitude=constrain(magnitude,pParticle.minSpeed,pParticle.maxSpeed);

		pParticle.Velocity.normalize();
		pParticle.Velocity.mult(magnitude);

		//pParticle.setTarget(noise(pParticle.id,frameCount/50.0)*width,noise(pParticle.id,frameCount/50.0+1)*height);
		//pParticle.setTarget(noise(pParticle.id,0)*width,noise(pParticle.id,1)*height);

		//Point arriveVelocity=arrive(pParticle,0.5,2);
		//pParticle.Velocity.add(arriveVelocity);

		//pParticle.Velocity.add(chaseVelocity);//para quitarle prioridad a que queden fijos se pueden
		//cambiar las cosas en el stack jojojo

		pParticle.x=(pParticle.x+pParticle.Velocity.x)%width;
		pParticle.y=(pParticle.y+pParticle.Velocity.y)%height;
		//}
		if(pParticle.x<0){
			pParticle.x=width-pParticle.x;
		}

		if(pParticle.y<0){
			pParticle.y=height-pParticle.y;
		}

		if(!pParticle.arrived){
			pParticle.direction=atan2(pParticle.Velocity.x,-pParticle.Velocity.y);
		}

	}

	void displayParticle(Unit pParticle){
		//point(pParticle.x, pParticle.y);
		//stroke(255);
		//point(pParticle.target.x,pParticle.target.y);

		pushMatrix();
		translate(pParticle.x,pParticle.y);
		scale(0.25);
		rotate(pParticle.direction);
		//translate(10,15);
		//fill(255,32);
		noStroke();
		triangle(-10,15,0,-15,10,15);
		popMatrix();
	}


	Point circleRand(float pCenterX,float pCenterY, float pRadius){
		float theta=random(TWO_PI);
		float randX=pRadius*sin(theta);
		float randY=pRadius*cos(theta);
		Point randomVector=new Point(randX,randY);
		return randomVector;
	}

	Point wander(Point pWanderDirection, float pWeight, float pRate){
		Point forwardVector=new Point(pWanderDirection.x,pWanderDirection.y);
		forwardVector.normalize();
		Point circleRand=circleRand(forwardVector.x,forwardVector.y,pRate);
		forwardVector.add(circleRand);
		forwardVector.normalize();
		forwardVector.mult(pWeight);
		//stroke(255);
		//point(forwardVector.x*600+width/2,forwardVector.y*600+height/2);
		pWanderDirection=forwardVector;
		return forwardVector;
	}

	Point chase(Unit pParticle, float pWeight){
		Point position=new Point(pParticle.x,pParticle.y);
		if(Point.dist(position,pParticle.chase)<pParticle.senseDistance){
			Point forwardVector=pParticle.chase;
			forwardVector.sub(position);
			forwardVector.normalize();
			forwardVector.mult(pWeight);
			return forwardVector;
		}
		else {
			return new Point(0,0);
		}
	}


	Point run(Unit pParticle, float pWeight){
		Point position= pParticle.getPosition().clone();
		if(Point.dist(position,pParticle.target)<pParticle.senseDistance){
			Point forwardVector=pParticle.target;
			forwardVector.sub(position);
			forwardVector.normalize();
			forwardVector.mult(pWeight);
			return forwardVector;
		}
		else {
			return new Point(0,0);
		}
	}

	Point arrive(Unit pParticle, float pWeight, float slowdown){
		if(slowdown<1){
			slowdown=1;
		}
		Point position = pParticle.getPosition().clone();
		Point forwardVector=new Point(pParticle.target.x,pParticle.target.y);
		Point backwardVector = pParticle.Velocity.clone();
		float distance = position.distance(forwardVector);

		if(distance>1){
			forwardVector.minus(position);
			forwardVector.normalize();
			forwardVector.times(pWeight);
			pParticle.arrived=false;

		}
		else{
			forwardVector.times(0);
			pParticle.arrived=true;
		}

		backwardVector.times(1/((distance/slowdown)+1));
		forwardVector.minus(backwardVector);

		//println(forwardVector);
		return forwardVector;
	}

	Point avoid(Unit pParticle, ArrayList pArray,float weight){
		Point cummulative=new Point(0,0);
		Point partPosition=new Point(pParticle.x,pParticle.y);

		for(int i=pArray.size()-1;i>=0;i--){
			Unit currentPart=(Unit) pArray.get(i);
			Point currentPosition=new Point(currentPart.x,currentPart.y);
			//aqui iria hipotéticamente el quadtree
			float localDist=partPosition.distance(currentPosition);
			Point localVector= currentPosition.minus(partPosition);
			localVector.normalize();
			localVector.times(1/(localDist));
			cummulative.minus(localVector);

		}
		cummulative.normalize();
		cummulative.times(weight);
		return cummulative;
	}

	Point cohesion(Unit pParticle, ArrayList pArray,float weight){
		Point cummulative=new Point(0,0);
		Point partPosition=new Point(pParticle.x,pParticle.y);

		for(int i=pArray.size()-1;i>=0;i--){
			Unit currentPart=(Unit) pArray.get(i);
			Point currentPosition=new Point(currentPart.x,currentPart.y);
			//aqui iria hipotéticamente el quadtree
			Point localVector=Point.minus(currentPosition,partPosition);
			//localVector.normalize();
			//localVector.mult(1/(localDist));
			cummulative.plus(localVector);

		}
		cummulative.normalize();
		cummulative.times(weight);
		return cummulative;
	}

	Point alignment(Unit pParticle, ArrayList pArray,float weight){
		Point cummulative=new Point(0,0);
		Point partPosition=new Point(pParticle.getPosition().X ,pParticle.getPosition().Y);

		for(int i=pArray.size()-1;i>=0;i--){
			Unit currentPart=(Unit) pArray.get(i);
			cummulative.plus(currentPart.Velocity);
		}
		cummulative.normalize();
		cummulative.times(weight);
		return cummulative;
	}

	ArrayList getNearFlockmates(Unit pParticle, ArrayList pArray){
		ArrayList nearFlockmates=new ArrayList();
		Point partPosition = pParticle.getPosition().clone();//new Point(pParticle.x, pParticle.y);

		for(int i=pArray.size()-1;i>=0;i--){
			Unit currentPart=(Unit) pArray.get(i);
			Point currentPosition=new Point(currentPart.x,currentPart.y);

			//aqui iria hipotéticamente el quadtree
			float localDist=Point.distance(partPosition,currentPosition);
			if(localDist<pParticle.senseDistance&&localDist>0){
				nearFlockmates.add(currentPart);
			}
		}
		return nearFlockmates;
	}
}
 */