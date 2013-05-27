package com.focused.projectf.resources.shaders;

import org.lwjgl.opengl.GL32;

public class GeometrySubShader extends SubShader {

	public GeometrySubShader(String contents) {
		super(contents, GL32.GL_GEOMETRY_SHADER);
		System.err.print("Waringing: Geometry shader implementation is untested. " + 
				"They probably won't work correctly");
	}

	@Override
	public void setUniformFloats(String name, float... vals) {
	}
	@Override
	public void setUniformInts(String name, int... vals) {
	}
	@Override
	public void setFloats(String name, float... vals) {
	}
	@Override
	public void setInts(String name, int... vals) {
	}
	@Override
	public void setBoolean(String name, boolean val) {
	}
}