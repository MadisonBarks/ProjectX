package com.focused.projectf.resources.dataBuffers;

import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;

import com.focused.projectf.resources.Resource;

/**
 * The basics structure for storing vector information on the GPU. 
 * Anything with lots of vertices (such as circles, curves, or 3D models) 
 * should be drawn using an extension of this
 */
public abstract class GPUBuffer implements Resource {
	
	public final int BufferId;
	public final int Target;
	protected int Size;
	
	protected int Elements;
	
	protected GPUBuffer(int target, int size) {
		if(!GLContext.getCapabilities().GL_ARB_vertex_buffer_object)
			throw new Error("Vertex Buffers not supported by the graphics card");
		BufferId = ARBVertexBufferObject.glGenBuffersARB();
		Target = target;
		Size = size;
	}
	
	public boolean dispose() {
		ARBVertexBufferObject.glDeleteBuffersARB(BufferId);
		return true;
	}
	
	public int getID() {
		return BufferId;
	}
	
	public abstract void draw(int mode);

	@Override
	public boolean isLoaded() {
		return false;
	}
}

