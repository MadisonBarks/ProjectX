package com.focused.projectf.resources.images;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.resources.Texture;

public class FlareUnitAnimation extends StaticImage {

	public FlareAction[] Actions;
	protected int[] frameIndex;
	public int FramesWide = -1, FramesTall = -1;

	public FlareUnitAnimation(Texture texture, int framesWide, int framesTall, FlareAction[] actions) {
		super(texture);
		params(framesWide, framesTall, actions);
	}

	public FlareUnitAnimation() {
		super();
	}

	public int getWidth() { return (int) (texture.getWidth() / FramesWide * 0.75f); }
	public int getHeight() { return (int) (texture.getHeight() / FramesTall * 0.75f); }


	public void params(int framesWide, int framesTall,  FlareAction[] actions) {
		Actions = new FlareAction[actions.length];
		for(int i = 0; i < actions.length; i++)
			Actions[i] = actions[i];
		FramesWide = framesWide;
		FramesTall = framesTall;
	}

	public void bind() {
		super.bind();
		//GameplayScreen.BehindObjectUnitShader.setAttribInts("widthInPixles", getWidth());
		//GameplayScreen.BehindObjectUnitShader.setAttribInts("heightInPixles", getHeight());
	}

	public void setStateTime(int act, int dir, float t) {
		action = act;
		direction = (FramesTall + dir) % FramesTall;
		float time = t;
		if(Actions != null && Actions.length > action)
			while(time > Actions[action].PlayTime)
				time -= Actions[action].PlayTime;
		frame = ((int)(time / Actions[action].FrameTime));
	}

	public void setState(int act, int dir, int f) {
		action = act;
		if(FramesTall != 0)
			direction = (FramesTall + dir) % FramesTall;
		frame = f;
		if(Actions != null && Actions.length > act)
			frame %= Actions[act].FramesLong;
	}
	
	private int action, direction, frame;

	public boolean isLoaded() {
		return Actions != null && super.isLoaded();
	}
	
	public void bindCoords(int corner) {
		if(FramesTall != -1 && FramesWide != -1) {
			float fx = Actions[action].FrameStart + frame;
			float fy = direction;
			fx = (fx + xCoords[corner]) / (float)(FramesWide);
			fy = (fy + yCoords[corner]) / (float)(FramesTall);
			fx = fx * texture.getWidthRatio();
			fy = fy * texture.getHeightRatio();
			GL11.glTexCoord2f(fx, fy);
		} else
			GL11.glTexCoord2f(0, 0);

	}

	public static class FlareAction {
		public final String Name;
		public final int FrameStart, FrameEnd;
		public final int FramesLong;
		public final float PlayTime;
		public final float FrameTime;
		public FlareAction(String line) {
			line = line.replace("\t", " ");
			line = line.replace("  ", " ");
			String[] parts = line.split(" ");
			Name 		= parts[0];
			FrameStart 	= Integer.parseInt(parts[1]);
			FrameEnd 	= Integer.parseInt(parts[2]);
			FrameTime	= Float.parseFloat(parts[3]);
			FramesLong	= FrameEnd - FrameStart + 1;
			PlayTime	= FramesLong * FrameTime;
		}
	}
}
