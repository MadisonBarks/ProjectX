package com.focused.projectf.resources.shaders;

import org.lwjgl.opengl.GL20;

public class VertexSubShader extends SubShader {

	public VertexSubShader(String contents) {
		super(contents, GL20.GL_VERTEX_SHADER);
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
	
	/**
	 * Because passing varriables to 
	 * @return
	 */
	public static final VertexSubShader generateForFragSubShader(String fragShaderContents, boolean textured) {
		VertexSubShader vss = null;
				
		StringBuilder sb = new StringBuilder();
		sb.append("    ");
		sb.append("    ");
		sb.append("    ");
		sb.append("    ");
		sb.append("    ");
		sb.append("void main() {");
		sb.append("    ");
		sb.append("    ");
		sb.append("    ");
		sb.append("    ");
		sb.append("}   ");
		return vss;
	}
}
