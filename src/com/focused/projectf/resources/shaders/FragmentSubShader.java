package com.focused.projectf.resources.shaders;

import org.lwjgl.opengl.GL20;

public class FragmentSubShader extends SubShader {

	public FragmentSubShader(String file) {
		super(file, GL20.GL_FRAGMENT_SHADER);
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
