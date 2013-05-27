package com.focused.projectf.resources.shaders;

import java.io.BufferedReader;
import java.util.Vector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.graphics.Image;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.utilities.IOUtils;
/**
 * Multiple subshaders may now be defined in the same .glsl file. 
 * Use [VERTEX SHADER] and [FRAGMENT SHADER] before each
 */
public class ShaderProgram implements Resource {

	public int ShaderProgramId;
	public Vector<SubShader> SubShaders;

	public ShaderProgram() {
		SubShaders = new Vector<SubShader>();
	}

	public ShaderProgram(String... files) {
		if(!Content.ShadersSupported)
			throw new Error("Shaders require OpenGL version 2.0 or greater to function.");

		ShaderProgramId = GL20.glCreateProgram();
		SubShaders = new Vector<SubShader>();

		if(files.length == 1 && files[0].endsWith(".glsl")) {
			BufferedReader reader = IOUtils.openBufferedReader(IOUtils.ResourceDirectory + files[0]);
			String line = "";
			StringBuilder sb = null;
			char ntype = (char) -2, type = 0;

			try { line = reader.readLine(); } catch(Exception ex) { }

			while(true) {
				if(line != null) {
					if(line.startsWith("[VERTEX SHADER]") || line.startsWith("[FRAGMENT SHADER]") || line.startsWith("[GEOMETRY SHADER]")) {
						ntype = line.charAt(1);
						line = null;
					} else if(sb != null)
						sb.append(line + "\n");
				}
				if(line == null) {
					if(sb != null) {
						if(sb.length() == 0)
							break;
						SubShader sub = null;
						switch(type) {
							case 'V': SubShaders.add(sub = new VertexSubShader(sb.toString())); break;
							case 'F': SubShaders.add(sub = new FragmentSubShader(sb.toString())); break;
							case 'G': SubShaders.add(sub = new GeometrySubShader(sb.toString())); break;
							default: //throw new Error("Invalid shader type declared");
								break;
						}
						if(sub != null)
							GL20.glAttachShader(ShaderProgramId, sub.ShaderId);

					}
					type = ntype;
					sb = new StringBuilder();
				}
				try { line = reader.readLine(); } catch(Exception ex) { ex.printStackTrace(); }
			} 
		} else {
			for(String file : files) {
				try {
					add(file.substring(file.length() - 4), IOUtils.readFileToString(IOUtils.ResourceDirectory + file, true));
				} catch(Exception ex) {
					ErrorManager.logWarning("Generating SubShader '" + file + "' failed: \n" + ex.getMessage(), ex);
				}
			}
		}
		ErrorManager.GLErrorCheck();

		GL20.glLinkProgram(ShaderProgramId);	
		ErrorManager.GLErrorCheck();
	}

	public ShaderProgram(String[] sources, int[] types) {
		if(sources.length != types.length)
			throw new Error("\'sources\' and \'types\' must be the same size");

		ShaderProgramId = GL20.glCreateProgram();
		SubShaders = new Vector<SubShader>();

		for(int i = 0; i < sources.length; i++) {
			SubShader sub = null;
			switch(types[i]) {
				case GL20.GL_VERTEX_SHADER: SubShaders.add(sub = new VertexSubShader(sources[i])); break;
				case GL20.GL_FRAGMENT_SHADER: SubShaders.add(sub = new FragmentSubShader(sources[i])); break;
				case GL32.GL_GEOMETRY_SHADER: SubShaders.add(sub = new GeometrySubShader(sources[i])); break;
				default: throw new Error("Invalid shader type declared");
			}
			GL20.glAttachShader(ShaderProgramId, sub.ShaderId);
		}

		GL20.glLinkProgram(ShaderProgramId);
	}

	private void add(String type, String contents) {
		SubShader add = null;
		type = type.toLowerCase();

		if(type.contains("frag"))
			add = new FragmentSubShader(contents);
		else if(type.contains("vert"))
			add = new VertexSubShader(contents);
		else if(type.contains("geom"))
			add = new GeometrySubShader(contents);
		else
			throw new Error("Invalid shader type: " + type);

		SubShaders.add(add);
		GL20.glAttachShader(ShaderProgramId, add.ShaderId);
	}

	/**
	 * Starts using this shader program to perform rendering.
	 * Note - This unbinds any other ShaderProgram that may be active.
	 */
	public void bind() {
		GL20.glUseProgram(ShaderProgramId);
	}
	public void unbind() {
		GL20.glUseProgram(0);
	}
	public static void unbindAll() {
		GL20.glUseProgram(0);
	}

	public void setUniformInts(String name, int... vals) {
		ErrorManager.GLErrorCheck();
		int location = GL20.glGetUniformLocation(ShaderProgramId, name);
		ErrorManager.GLErrorCheck();
		switch(vals.length) {
			case 0: break;
			case 1: GL20.glUniform1i(location, vals[0]); break;
			case 2: GL20.glUniform2i(location, vals[0], vals[1]); break;
			case 3: GL20.glUniform3i(location, vals[0], vals[1], vals[2]); break;
			case 4: GL20.glUniform4i(location, vals[0], vals[1], vals[2], vals[3]); break;
		}
		ErrorManager.GLErrorCheck();
	}
	public void setUniformFloats(String name, float... vals) {
		int location = GL20.glGetUniformLocation(ShaderProgramId, name);
		switch(vals.length) {
			case 0: break;
			case 1: GL20.glUniform1f(location, vals[0]); break;
			case 2: GL20.glUniform2f(location, vals[0], vals[1]); break;
			case 3: GL20.glUniform3f(location, vals[0], vals[1], vals[2]); break;
			case 4: GL20.glUniform4f(location, vals[0], vals[1], vals[2], vals[3]); break;
		}
	}
	public void setUniformVec4(String name, Rect rect) {
		setUniformFloats(name, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}
	public void setUniformVec4(String name, Color color) {
		setUniformFloats(name, color.glRed(), color.glGreen(), color.glBlue(), color.glAlpha());
	}
	public void setUniformVec4(String name, Point xy, Point zw) {
		setUniformFloats(name, xy.X, xy.Y, zw.X, zw.Y);
	}


	public void setAttribInts(String name, int... vals) {
		int location = GL20.glGetAttribLocation(ShaderProgramId, name);
		switch(vals.length) {
			case 0: break;
			case 1: GL20.glVertexAttrib1s(location, (short)vals[0]); break;
			case 2: GL20.glVertexAttrib2s(location, (short)vals[0], (short)vals[1]); break;
			case 3: GL20.glVertexAttrib3s(location, (short)vals[0], (short)vals[1], (short)vals[2]); break;
			case 4: GL20.glVertexAttrib4s(location, (short)vals[0], (short)vals[1], (short)vals[2], (short)vals[3]); break;
		}
		GL11.glGetError();
	}
	public void setAttribFloats(String name, float... vals) {
		int location = GL20.glGetAttribLocation(ShaderProgramId, name);
		switch(vals.length) {
			case 0: break;
			case 1: GL20.glVertexAttrib1f(location, vals[0]); break;
			case 2: GL20.glVertexAttrib2f(location, vals[0], vals[1]); break;
			case 3: GL20.glVertexAttrib3f(location, vals[0], vals[1], vals[2]); break;
			case 4: GL20.glVertexAttrib4f(location, vals[0], vals[1], vals[2], vals[3]); break;
		}
	}
	public void setAttribVec4(String name, Rect rect) {
		setAttribFloats(name, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}
	public void setAttribVec4(String name, Color color) {
		setAttribFloats(name, color.glRed(), color.glGreen(), color.glBlue(), color.glAlpha());
	}
	public void setAttribVec4(String name, Point xy, Point zw) {
		setAttribFloats(name, xy.X, xy.Y, zw.X, zw.Y);
	}

	public void bindTexture(String samplerName, int boundTextureIndex, Image img) {
		try {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + boundTextureIndex);
			img.bind(boundTextureIndex);
			GL20.glUniform1i(GL20.glGetUniformLocation(ShaderProgramId, samplerName), boundTextureIndex);
		} catch(Exception ex) {
			ErrorManager.logDebug("Unable to bind texture");
		}
	}

	public boolean dispose() {
		for(SubShader sub : SubShaders)
			sub.dispose();
		GL20.glDeleteProgram(ShaderProgramId);
		ShaderProgramId = 0;
		return true;
	}
	@Override
	public int getID() { return ShaderProgramId; }
	@Override
	public boolean isLoaded() {
		for(int i = 0; i < SubShaders.size(); i++)
			if(!SubShaders.get(i).isLoaded())
				return false;
		return ShaderProgramId != 0 && GL20.glIsProgram(ShaderProgramId); 
		// If it's been initialized and doesn't have ID 0, it has been loaded (unless it epic failed in which case an error would have been thrown)
	}
}
