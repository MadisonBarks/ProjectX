package com.focused.projectf.graphics;

import java.nio.FloatBuffer;
import java.util.Vector;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;
import com.focused.projectf.resources.dataBuffers.Basic3VertexBuffer;
import com.focused.projectf.screens.screens.GameplayScreen;
import com.focused.projectf.utilities.FMath;

/**
 * The base class for all drawing.
 */
public class Canvas {

	public static final float CIRCLE_RADIUS_RATIO 			= 0.6f;
	public static final int CIRCLE_BASE_POINT_COUNT 		= 10;

	public static final float PI 							= FMath.PI;
	public static TTFont Font12, Font15, Font18, Font12Bold, Font15Bold, Font18Bold;

	protected static float deltaFactor = FMath.TWO_PI;
	private static float zoomFactor = 1.0f;
	private static Point centeredAt = new Point(0,0);

	private static Basic3VertexBuffer CircleBuffer;

	public static void initialize() {
		final float delta = delta(101);
		int size = (int)(FMath.TWO_PI / delta + 2) * 3;
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		for(float theta = PI; theta > -PI; theta -= delta) {
			buffer.put(FMath.cos(theta));
			buffer.put(FMath.sin(theta));
			buffer.put(0.0f);
		}
		buffer.put(-1.0f);
		buffer.put(0.0f);
		buffer.put(0.0f);
		buffer = (FloatBuffer) buffer.rewind();
		CircleBuffer = new Basic3VertexBuffer(buffer, size);
		ErrorManager.GLErrorCheck();
		Font12 		= Content.getFont("Arial", 12, false, false);
		Font15		= Content.getFont("Arial", 15, false, false);
		Font18	 	= Content.getFont("Arial", 18, false, false);
		Font12Bold 	= Content.getFont("Arial", 12, true, false);
		Font15Bold	= Content.getFont("Arial", 15, true, false);
		Font18Bold 	= Content.getFont("Arial", 18, true, false);
		while(Content.isLoading() || !Font18Bold.isLoaded()) {
			Thread.yield();
			Content.bgLoaderTick();
		}
	}

	/** changes the zoom level. Takes effect on the next draw cycle */
	public static void setZoom(float zoom) { 
		zoom = FMath.clamp(zoom, 1, 0.33333333f);
		zoomFactor = zoom;
		deltaFactor = FMath.TWO_PI / zoom;
	}

	public static float getZoom() { return zoomFactor; }

	public static void setCenter(Point newCenter) {
		centeredAt = newCenter;
		clampCamera();
	}
	public static void moveCenter(Point offset) { 
		centeredAt.X += offset.X;
		centeredAt.Y += offset.Y;
		clampCamera();
	}
	/** don't let the camera move too far away from the gameplay area. */
	public static void clampCamera() {
		float xBounds = Math.abs(Map.get().getWidthInTiles() * Map.tileHalfWidth
				- Math.abs(centeredAt.Y * 2));
		float yBounds = Math.abs(Map.get().getHeightInTiles() * Map.tileHalfHeight
				- Math.abs(centeredAt.X * 0.5f));
		centeredAt.X = FMath.clamp(centeredAt.X, xBounds, -xBounds);
		centeredAt.Y = FMath.clamp(centeredAt.Y, yBounds, -yBounds);
	}

	public static Point getCenter() { return centeredAt; }

	public static void drawImage(Image image, Point position) {
		drawImage(image, position, -1);
	}
	public static void drawImage(Image image, Point position, float depth) {
		drawImage(image, position, Color.WHITE, depth);
	}
	public static void drawImage(Image image, float X, float Y, float depth) {
		drawImage(image, X, Y, Color.WHITE, depth);
	}
	public static void drawImage(Image image, Point position, Color tint, float depth) {
		drawImage(image, position.X, position.Y, tint, depth);
	}
	public static void drawImage(Image image, float X, float Y, Color tint, float depth) {
		tint.bind();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		image.bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		GL11.glBegin(GL11.GL_QUADS);
		; image.bindCoords(0);
		; GL11.glVertex3f(X, Y, depth);
		; image.bindCoords(1);
		; GL11.glVertex3f(X, Y + image.getHeight(), depth);
		; image.bindCoords(2);
		; GL11.glVertex3f(X + image.getWidth(), Y + image.getHeight(), depth);
		; image.bindCoords(3);
		; GL11.glVertex3f(X + image.getWidth(), Y, depth);
		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public static void drawSprite(Image image, Rect src, Rect rect, Color tint) {

		float x = src.getX() / image.getWidth();
		float y = src.getY() / image.getHeight();
		float w = (src.getX() + src.getWidth()) / image.getWidth();
		float h = (src.getX() + src.getHeight()) / image.getHeight();

		image.bind();
		tint.bind();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS); {
			image.bindCoords(0, x, y);
			GL11.glVertex2f(rect.getX(), rect.getY());
			image.bindCoords(1, x, h);
			GL11.glVertex2f(rect.getX(), rect.getY() + rect.getHeight());
			image.bindCoords(2, w, h);
			GL11.glVertex2f(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
			image.bindCoords(3, w, y);
			GL11.glVertex2f(rect.getX() + rect.getWidth(), rect.getY());
		} GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public static void drawImage(Image image, Rect rect, Color tint, float depth) {
		image.bind();
		tint.bind();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glBegin(GL11.GL_QUADS);		
		; image.bindCoords(0);
		; GL11.glVertex3f(rect.getX(), rect.getY(), depth);
		; image.bindCoords(1);
		; GL11.glVertex3f(rect.getX(), rect.getY() + rect.getHeight(), depth);
		; image.bindCoords(2);
		; GL11.glVertex3f(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), depth);
		; image.bindCoords(3);
		; GL11.glVertex3f(rect.getX() + rect.getWidth(), rect.getY(), depth);
		GL11.glEnd();
	}
	public static void drawImage(Image image, Rect rect, Color tint) {

		image.bind();
		tint.bind();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);

		image.bindCoords(0);
		GL11.glVertex2f(rect.getX(), rect.getY());
		image.bindCoords(1);
		GL11.glVertex2f(rect.getX(), rect.getY() + rect.getHeight());
		image.bindCoords(2);
		GL11.glVertex2f(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
		image.bindCoords(3);
		GL11.glVertex2f(rect.getX() + rect.getWidth(), rect.getY());

		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}


	public static float delta(float radius) {
		return deltaFactor / (CIRCLE_BASE_POINT_COUNT + CIRCLE_RADIUS_RATIO * radius);
	}

	public static void ellipse(int drawMode, float centerX, float centerY, float radiusX, float radiusY, float depth) {
		GL11.glPushMatrix();
		GL11.glTranslatef(centerX, centerY, depth);
		GL11.glScalef(radiusX, radiusY, 1);
		float width = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(width - 0.5f);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		CircleBuffer.draw(drawMode);
		GL11.glLineWidth(width);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		CircleBuffer.draw(drawMode);
		GL11.glPopMatrix();
	}

	public static void circle(int drawMode, float centerX, float centerY, float radius, float depth) {
		GL11.glPushMatrix();
		GL11.glTranslatef(centerX, centerY, depth);
		GL11.glScalef(radius, radius, 1);
		CircleBuffer.draw(drawMode);
		GL11.glPopMatrix();
	}

	public static void drawCircle(Point center, float radius, Color color, float strokeWidth) {
		drawCircle(center.X, center.Y, radius, color, strokeWidth);
	}
	public static void drawCircle(float x, float y, float radius, Color color, float strokeWidth) {				
		GL11.glLineWidth(strokeWidth / zoomFactor / zoomFactor);
		color.bind();
		circle(GL11.GL_LINE_STRIP, x, y, radius, 0);
	}

	public static void fillCircle(Point center, float radius, Color color) { 
		fillCircle(center.X, center.Y, radius, color); 
	}
	public static void fillCircle(Point center, float radius, Color color, float depth) { 
		fillCircle(center.X, center.Y, radius, color, depth); 
	}
	public static void fillCircle(float x, float y, float radius, Color color) { 	
		color.bind();
		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
		circle(GL11.GL_POLYGON, x, y, radius, 0);
	}
	public static void fillCircle(float x, float y, float radius, Color color, float depth) { 	
		color.bind();
		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
		circle(GL11.GL_POLYGON, x, y, radius, depth);
	}

	public static void drawEllipse(Point center, float radiusX, float radiusY, Color color, float strokeWidth) { 
		drawEllipse(center.X, center.Y, radiusX, radiusY, color, strokeWidth);
	}
	public static void drawEllipse(Point center, float radiusX, float radiusY, Color color, float strokeWidth, float depth) { 
		drawEllipse(center.X, center.Y, radiusX, radiusY, color, strokeWidth, depth);
	}
	public static void drawEllipse(float x, float y, float radiusX, float radiusY, Color color, float strokeWidth) { 
		color.bind();
		GL11.glLineWidth(strokeWidth * zoomFactor);
		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
		ellipse(GL11.GL_LINE_LOOP, x, y, radiusX, radiusY, -1);
	}
	public static void drawEllipse(float x, float y, float radiusX, float radiusY, Color color, float strokeWidth, float depth) { 
		color.bind();
		GL11.glLineWidth(strokeWidth * zoomFactor);
		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
		ellipse(GL11.GL_LINE_LOOP, x, y, radiusX, radiusY, depth);
		ErrorManager.GLErrorCheck();
	}

	public static void fillEllipse(Point center, float radiusX, float radiusY, Color color) { 
		fillEllipse(center.X, center.Y, radiusX, radiusY, color);
	}
	public static void fillEllipse(Point center, float radiusX, float radiusY, Color color, float depth) { 
		fillEllipse(center.X, center.Y, radiusX, radiusY, color, depth);
	}
	public static void fillEllipse(float x, float y, float radiusX, float radiusY, Color color) { 
		color.bind();
		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
		ellipse(GL11.GL_POLYGON, x, y, radiusX, radiusY, 0);
	}
	public static void fillEllipse(float x, float y, float radiusX, float radiusY, Color color, float depth) { 
		color.bind();
		GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
		ellipse(GL11.GL_POLYGON, x, y, radiusX, radiusY, depth);
	}

	public static void drawLine(Point p0, Point p1, float strokeWidth, Color color) { 
		GL11.glLineWidth(strokeWidth);
		color.bind();
		GL11.glBegin(GL11.GL_LINES);
		; p0.bind();
		; p1.bind();
		GL11.glEnd();
	}
	public static void drawLine(Point p0, Point p1, float strokeWidth, Color color, float depth) {
		GL11.glLineWidth(strokeWidth);
		color.bind();
		GL11.glBegin(GL11.GL_LINES);
		p0.bind3(depth);
		p1.bind3(depth);
		GL11.glEnd();
	}
	public static void drawLine(float x0, float y0, float x1, float y1, float strokeWidth, Color color) { 
		GL11.glLineWidth(strokeWidth);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		color.bind();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(x0, y0);
		GL11.glVertex2f(x1, y1);
		GL11.glEnd();
	}
	public static void drawLine(float x0, float y0, float x1, float y1, float strokeWidth, Color color, float depth) { 
		GL11.glLineWidth(strokeWidth);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		color.bind();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(x0, y0, depth);
		GL11.glVertex3f(x1, y1, depth);
		GL11.glEnd();
	}


	public static void fillRectangle(Rect rect, Color color) {
		fillRectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), color);
	}
	public static void fillRectangle(float x, float y, float width, float height, Color color) {

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
		color.bind();
		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x, y + height);
		GL11.glVertex2f(x + width, y + height);
		GL11.glVertex2f(x + width, y);

		GL11.glEnd(); 
	}
	public static void fillRectectangle(Rect rect, Color ltop, Color rtop, Color lbottom, Color rbottom) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);

		ltop.bind();
		GL11.glVertex2f(rect.getX(), rect.getY());
		lbottom.bind();
		GL11.glVertex2f(rect.getX(), rect.getY() + rect.getHeight());
		rbottom.bind();
		GL11.glVertex2f(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
		rtop.bind();
		GL11.glVertex2f(rect.getX() + rect.getWidth(), rect.getY());

		GL11.glEnd();		
	}



	public static void drawRectangle(Point p0, Point p1, float lineWidth, Color color) {
		drawRectangle(p0.X, p0.Y, p1.X, p1.Y, lineWidth, color);
	}
	public static void drawRectangle(Rect rect, float lineWidth, Color color) {
		drawRectangle(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom(), lineWidth, color);
	}
	public static void drawRectangle(float x1, float y1, float x2, float y2, float lineWidth, Color color) {
		GL11.glLineWidth(Math.max(1, lineWidth));
		color.bind();
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y1);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x1, y2);
		GL11.glVertex2f(x1, y1);
		GL11.glEnd();

		GL11.glPointSize(lineWidth);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y1);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x1, y2);
		GL11.glEnd();
	}

	private static FloatBuffer BeizerBuffer = BufferUtils.createFloatBuffer(12);

	public static void drawBeizerCurve(int mode, Point p0, Point c0, Point c1, Point p1) {
		drawBeizerCurve(mode, p0, c0, c1, p1, 0);
	}
	public static void drawBeizerCurve(int mode, Point p0, Point c0, Point c1, Point p1, float depth) {
		BeizerBuffer.clear();
		BeizerBuffer.put(new float[] { p0.X, p0.Y, depth, c0.X, c0.Y, depth, c1.X, c1.Y, depth, p1.X, p1.Y, depth, });
		BeizerBuffer.rewind();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glMap1f(GL11.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, BeizerBuffer);
		GL11.glEnable(GL11.GL_MAP1_VERTEX_3);

		GL11.glMapGrid1f(50, 0f, 1f);
		GL11.glEvalMesh1(mode, 0, 50);
	}

	private static Vector<Rect> clipStack = new Vector<Rect>();

	/**
	 * Sets the scissor clip. revert to the previously set scissor clip by using <code>Canvas.popClip()</code>
	 */
	public static void pushClip(Rect rect) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		clipStack.add(rect.clone());
		GL11.glScissor(rect.getXi(), Display.getHeight() - rect.getYi() - rect.getHeighti(), rect.getWidthi(), rect.getHeighti());
	}
	/**
	 * Revert to the previously set scissor clip. If no clip was set before the current one
	 * Scissor clipping is disabled until the next <code>Canvas.pushClip(Rect)</code> call
	 */
	public static void popClip() {
		switch(clipStack.size()) {
			case 0: return;
			case 1: 
				clipStack.remove(0); 
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				break;
			default:
				clipStack.remove(clipStack.size() - 1); 
				Rect rect = clipStack.lastElement();
				GL11.glScissor(rect.getXi(), rect.getYi(), rect.getWidthi(), rect.getHeighti());
				break;
		}
	}


	public static void enterGameplayCamera() {
		GL11.glPushMatrix();
		Point pos = centeredAt.minus(getWidth() / 2 / zoomFactor, (getHeight() - GameplayScreen.BOTTOM_BAR_HEIGHT) / 2 /  zoomFactor).timesEquals(-1);

		// clamps movement to one pixel increments so that moving over the map doesn't cause pixels to flash randomly
		pos.X = (float)((int)(pos.X * zoomFactor)) / zoomFactor;
		pos.Y = (float)((int)(pos.Y * zoomFactor)) / zoomFactor;

		GL11.glScalef(zoomFactor, zoomFactor, 1);
		GL11.glTranslatef(pos.X, pos.Y, 0);
	}
	public static void exitGameplayCamera() {
		GL11.glPopMatrix();
		GL11.glLoadIdentity();
	}

	public static int getWidth() { return Display.getWidth(); }
	public static int getHeight() { return Display.getHeight(); }

	public static void _beginLines(float lineWidth, Color color, boolean antiAlais) {
		if(antiAlais)		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		else				GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(lineWidth);
		GL11.glBegin(GL11.GL_LINES);
		color.bind();
	}
	public static void _line(float x0, float y0, float x1, float y1) {
		GL11.glVertex2f(x0, y0);
		GL11.glVertex2f(x1, y1);
	}
	public static void _line(float x0, float y0, float x1, float y1, Color color) {
		color.bind();
		GL11.glVertex2f(x0, y0);
		GL11.glVertex2f(x1, y1);
	}
	public static void _line(float x0, float y0, float x1, float y1, Color color0, Color color1) {
		color0.bind();
		GL11.glVertex2f(x0, y0);
		color1.bind();
		GL11.glVertex2f(x1, y1);
	}
	public static void _end() {
		GL11.glEnd();
	}


	/**
	 * Converts a point on the screen (in pixels) into a position in the game space.
	 */
	public static Point toGamePoint(float x, float y) {
		float cX = centeredAt.X - (getWidth() / 2f - x) / zoomFactor;
		float cY = centeredAt.Y - ((getHeight() - GameplayScreen.BOTTOM_BAR_HEIGHT) / 2 - y)/ zoomFactor;
		return new Point(cX, cY);
	}
	public static Point toGamePoint(Point pixel) {
		return toGamePoint(pixel.X, pixel.Y);
	}

	/**
	 * Converts a point in the game space into a pixel coordinate in the window.
	 */
	public static Point fromGamePoint(float x, float y) {
		float cX = -(centeredAt.X - getWidth() / 2f - x) * zoomFactor;
		float cY = -(centeredAt.Y - getHeight() / 2f - y) * zoomFactor;
		return new Point(cX, cY);
	}	
	public static Point fromGamePoint(Point pos) {
		return fromGamePoint(pos.X, pos.Y);
	}

	public static Rect getRect() {
		return new Rect(0, 0, getWidth(), getHeight());
	}


	/**
	 * returns the depth value of a y coordinate for sprite layering depth tests.
	 * the value returned will be between 0 and 1 if the y coordinate is in the 
	 * rendering space.
	 */
	public static float calcDepth(float gsY) {
		float top = centeredAt.Y + getHeight() / 2 / zoomFactor;
		float gsRatio = 1.0f / (Canvas.getHeight() / zoomFactor);
		return -(1 - Math.abs(gsRatio * (gsY - top)));
	}

	public static float getLeft() {
		return (centeredAt.X - (getWidth() / 5 * 1) / zoomFactor);
	}
	public static float getTop() {
		return (centeredAt.Y - (getHeight() / 5 * 1) / zoomFactor);
	}
}