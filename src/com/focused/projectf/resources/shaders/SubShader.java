package com.focused.projectf.resources.shaders;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.focused.projectf.ErrorManager;

public abstract class SubShader {

	public final int ShaderId;
	protected boolean Loaded = false;
	
	protected SubShader(String contents, int type) {
		ErrorManager.GLErrorCheck();
		//ErrorManager.logWarning("GLSL shader type " + this.getClass().getSimpleName() + " has contents:\n" + contents, null);
		ShaderId = GL20.glCreateShader(type);
		if(ShaderId == 0)  {
			Loaded = false;
			throw new RuntimeException("Error Creating Subshader: " + getLog(ShaderId));
		}
		
		ErrorManager.GLErrorCheck();
		GL20.glShaderSource(ShaderId, contents);
		ErrorManager.GLErrorCheck();
		GL20.glCompileShader(ShaderId);
		ErrorManager.GLErrorCheck();
		Loaded = true;
		if (ARBShaderObjects.glGetObjectParameteriARB(ShaderId, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
			Loaded = false;
			ErrorManager.logWarning(getLog(ShaderId), null);
		}
		ErrorManager.GLErrorCheck();
	}

	protected static final String getLog(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	public abstract void setUniformFloats(String name, float... vals);
	public abstract void setUniformInts(String name, int... vals);
	public abstract void setFloats(String name, float... vals);
	public abstract void setInts(String name, int... vals);
	public abstract void setBoolean(String name, boolean val);

	/**
	 * Unloads this <code>SubShader</code>. If a <code>ShaderProgram</code> 
	 * is still using this <code>SubShader</code>, problems will occur
	 */
	public void dispose() {
		try {
		GL20.glDeleteShader(ShaderId);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean isLoaded() {
		return Loaded;
	}
}
