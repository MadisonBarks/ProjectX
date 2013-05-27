package com.focused.projectf.resources.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.Resource;
import com.focused.projectf.resources.shaders.FragmentSubShader;
import com.focused.projectf.resources.shaders.GeometrySubShader;
import com.focused.projectf.resources.shaders.ShaderProgram;
import com.focused.projectf.resources.shaders.SubShader;
import com.focused.projectf.resources.shaders.VertexSubShader;
import com.focused.projectf.utilities.IOUtils;

public class ShaderLoader extends ResourceLoader<ShaderProgram> {

	public static Object toGLLockObject;

	@Override
	public Object loadFromFile(String resource) {
		if(!Content.ShadersSupported)
			throw new Error("Shaders require OpenGL version 2.0 or greater to function.");
		fileData dat = new fileData();
		ArrayList<String> files = new ArrayList<String>();
		if(resource.endsWith(".glsl")) {
			BufferedReader reader = IOUtils.openBufferedReader(IOUtils.ResourceDirectory + resource);
			String line = "";
			try { line = reader.readLine().trim(); } catch(Exception ex) { }
			while(line != null) {
				if(line.startsWith("#")) continue;
				files.add(line);
				try { 
					line = reader.readLine();
					line = line.trim();
				} catch(Exception ex) { }
			}
			try { reader.close(); } catch (IOException e) { e.printStackTrace(); }
		} else {
			files.add(resource);
		}
		ArrayList<String> fileSrcs = new ArrayList<String>();
		for(String file : files) 
			fileSrcs.add(IOUtils.readFileToString(IOUtils.ResourceDirectory + file, true));

		dat.ComponentsSrc = fileSrcs;
		dat.ComponentsFiles = files;
		return dat;
	}

	@Override
	public void pushToOpenGL(String resource, Object fileData, Resource placeHolder) {
		fileData dat = (fileData)fileData;
		ShaderProgram shader = (ShaderProgram) placeHolder;

		shader.ShaderProgramId  = GL20.glCreateProgram();

		for(int i = 0; i < dat.ComponentsFiles.size(); i++) {
			String file = dat.ComponentsFiles.get(i);
			String src = dat.ComponentsSrc.get(i);
			SubShader sub;
			if(file.endsWith(".frag"))
				sub = new FragmentSubShader(src);
			else if(file.endsWith(".vert"))		
				sub = new VertexSubShader(src);
			else if(file.endsWith(".geom"))		
				sub = new GeometrySubShader(src);
			else								
				throw new Error("Invalid Shader program passed: \'" + file + "\'");
			shader.SubShaders.add(sub);
			GL20.glAttachShader(shader.ShaderProgramId, sub.ShaderId);
		}
		GL20.glLinkProgram(shader.ShaderProgramId);	
		ErrorManager.logInfo("Resource Loaded:\t\t" + resource);
	}

	@Override
	public boolean canLoadFile(String resource) {
		return resource.endsWith(".glsl") || resource.endsWith(".frag") || resource.endsWith(".vert");
	}

	@Override
	public ShaderProgram instancePlaceHolder(String file) {
		return new ShaderProgram();
	}

	protected static final String getLog(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	protected static class fileData {
		public List<String> ComponentsSrc;
		public List<String> ComponentsFiles;
	}
}
