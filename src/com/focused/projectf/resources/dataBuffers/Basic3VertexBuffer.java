package com.focused.projectf.resources.dataBuffers;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Point3;

public class Basic3VertexBuffer extends GPUBuffer {

	public Basic3VertexBuffer(Point3[] points) {
		this(genBuffer(points), points.length * 3);
	}
	public Basic3VertexBuffer(List<Point3> points) {
		this(genBuffer(points), points.size() * 3);
	}

	public Basic3VertexBuffer(int points) {
		this(BufferUtils.createFloatBuffer(points), points * 3);
	}

	public Basic3VertexBuffer(FloatBuffer buffer, int bufferSize) {
		super(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferSize / 3);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, BufferId);
		ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
	}

	@Override
	public void draw(int mode) {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, BufferId);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
		
		GL11.glDrawArrays(mode, 0, Size);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}

	public void set(int index, float[] XYZs) {
		FloatBuffer data = FloatBuffer.wrap(XYZs);
		ARBVertexBufferObject.glBindBufferARB(Target, BufferId);
		ARBVertexBufferObject.glGetBufferSubDataARB(Target, index * (3 << 2), data);
		ARBVertexBufferObject.glBindBufferARB(Target, 0);
	}


	public void set(int index, float x, float y, float z) {
		FloatBuffer data = FloatBuffer.wrap(new float[] { x, y, z });
		ARBVertexBufferObject.glBindBufferARB(Target, BufferId);
		ARBVertexBufferObject.glGetBufferSubDataARB(Target, index * (3 << 2), data);
		ARBVertexBufferObject.glBindBufferARB(Target, 0);
	}



	private static FloatBuffer genBuffer(Point3[] points) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(points.length * 3);
		for(int i = 0; i < points.length; i++) {
			Point3 pt = points[i];
			buffer.put(pt.X);
			buffer.put(pt.Y);
			buffer.put(pt.Z);
		}
		return buffer;
	}
	private static FloatBuffer genBuffer(List<Point3> points) {
		ErrorManager.GLErrorCheck();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(points.size() * 3);
		ErrorManager.GLErrorCheck();
		for(int i = 0; i < points.size(); i++) {
			Point3 pt = points.get(i);
			buffer.put(pt.X);
			buffer.put(pt.Y);
			buffer.put(pt.Z);
		}
		ErrorManager.GLErrorCheck();
		return (FloatBuffer) buffer.flip();
	}
}