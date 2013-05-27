package com.focused.projectf.global.particles;

import java.util.Hashtable;
import java.util.Vector;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.Point;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.images.AnimatedImage;
import com.focused.projectf.resources.shaders.ShaderProgram;
import com.focused.projectf.utilities.TimeKeeper;

public final class ParticleManager {
	protected static Hashtable<String, Integer> ParticleTextureLookup 	= new Hashtable<String, Integer>();
	protected static Vector<Image> ParticleTextures						= new Vector<Image>();

	protected static Vector<Particle> ActiveParticles						= new Vector<Particle>();

	public static final int EXPLOSION_1			= 0;
	public static final int EXPLOSION_2			= 1;
	public static final int EXPLOSION_3			= 2;
	public static final int BURST_1				= 3;
	public static final int BURST_2				= 4;
	public static final int VORTEX_1				= 5;


	public static void addParticle(Point position, float lifeTime, int particleImage) {
		addParticle(position, lifeTime, particleImage, 1.0f);
	}
	public static void addParticle(Point position, float lifeTime, int particleImage, float size) {
		if(position == null)
			throw new Error("Position may not be null");
		
		Image img = ParticleTextures.get(particleImage);
		if(img instanceof AnimatedImage) 
			img = (AnimatedImage)((AnimatedImage)img).clone();
		if(lifeTime <= 0)
			lifeTime = ((AnimatedImage)img).getPlayTime();
		ActiveParticles.add(Particle.instance(position, lifeTime, img, size));
	}


	public static void attachResources() {
		for(int i = 0; i < 10; i++)
			ParticleTextures.add(null);
		
		ParticleTextures.set(EXPLOSION_1, Content.getImage("effects/animationSet1.anim/explosion1"));
		ParticleTextures.set(EXPLOSION_2, Content.getImage("effects/animationSet1.anim/explosion2"));
		ParticleTextures.set(EXPLOSION_3, Content.getImage("effects/animationSet1.anim/explosion3"));
		ParticleTextures.set(BURST_1, Content.getImage("effects/animationSet1.anim/burst1"));
		ParticleTextures.set(BURST_2, Content.getImage("effects/animationSet1.anim/burst2"));
		ParticleTextures.set(VORTEX_1, Content.getImage("effects/animationSet1.anim/vortex1"));
	}

	public static void draw() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
		GL11.glColor4f(1, 1, 1, 1);
		ShaderProgram.unbindAll();
		for(int i = 0; i < ActiveParticles.size(); i++) {
			Particle p = ActiveParticles.get(i);
			p.LifeTime -= TimeKeeper.getElapsed();
			if((p.LifeTime -= TimeKeeper.getElapsed()) < 0) {
				p.retire();
				ActiveParticles.remove(i--);
				continue;
			}			
			
			if(p.Position == null)
				continue;
			
			Image image = p.ParticleImage;
			if(image == null || image.getTexture() == null)
				image = Content.getImage("effects/burst1.png");
			
			//Canvas.drawImage(image, p.Position, -1);

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);

			float width = image.getWidth() * p.Size * 0.5f;
			float height = image.getHeight() * p.Size * 0.5f;

			image.bind();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);

			GL11.glBegin(GL11.GL_QUADS);
			; image.bindCoords(0);
			; GL11.glVertex3f(p.Position.X - width, p.Position.Y - height, -1);
			; image.bindCoords(1);
			; GL11.glVertex3f(p.Position.X - width, p.Position.Y + height, -1);
			; image.bindCoords(2);
			; GL11.glVertex3f(p.Position.X + width, p.Position.Y + height, -1);
			; image.bindCoords(3);
			; GL11.glVertex3f(p.Position.X + width, p.Position.Y - height, -1);
			GL11.glEnd();
			
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);		
	}
}