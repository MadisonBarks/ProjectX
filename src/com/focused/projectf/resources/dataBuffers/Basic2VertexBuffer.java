package com.focused.projectf.resources.dataBuffers;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.Point;

public class Basic2VertexBuffer extends GPUBuffer {

	public Basic2VertexBuffer(Point[] points) {
		this(genBuffer(points), points.length * 2);
	}
	public Basic2VertexBuffer(List<Point> points) {
		this(genBuffer(points), points.size() * 2);
	}

	public Basic2VertexBuffer(int points) {
		this(BufferUtils.createFloatBuffer(points), points * 2);
	}

	public Basic2VertexBuffer(FloatBuffer buffer, int bufferSize) {
		super(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferSize / 2);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, BufferId);
		ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
	}

	@Override
	public void draw(int mode) {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, BufferId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0L);
		
		GL11.glDrawArrays(mode, 0, Size);
		
		ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}

	private static FloatBuffer genBuffer(Point[] points) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(points.length * 2);
		for(int i = 0; i < points.length; i++) {
			Point pt = points[i];
			buffer.put(pt.X);
			buffer.put(pt.Y);
		}
		return buffer;
	}
	private static FloatBuffer genBuffer(List<Point> points) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(points.size() * 2);
		for(int i = 0; i < points.size(); i++) {
			Point pt = points.get(i);
			buffer.put(pt.X);
			buffer.put(pt.Y);
		}
		return (FloatBuffer) buffer.flip();
	}
}