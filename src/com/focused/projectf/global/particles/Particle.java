package com.focused.projectf.global.particles;

import java.util.Stack;

import com.focused.projectf.Point;
import com.focused.projectf.graphics.Image;

public class Particle {

	private static Stack<Particle> InactiveParticles = new Stack<Particle>();

	public Point Position;
	public float LifeTime;
	public Image ParticleImage;
	public float Size;

	private Particle(Point pos, float life, Image image) {
		this(pos, life, image, 1.0f);
	}
	private Particle(Point pos, float life, Image image, float size) {
		Position = pos;
		LifeTime = life;
		ParticleImage = image;
		Size = size;
	}
	public void draw() {

	}
	
	public void retire() {
		InactiveParticles.push(this);
	}

	public static Particle instance(Point pos, float life, Image image) {
		return instance(pos, life, image, 1.0f);
	}
	
	public static Particle instance(Point pos, float life, Image image, float size) {
		if(InactiveParticles.size() == 0) 
			return new Particle(pos, life, image, size);
		Particle ret = InactiveParticles.pop();
		ret.Position = pos;
		ret.LifeTime = life;
		ret.ParticleImage = image;
		ret.Size = size;
		return ret;
	}
}
