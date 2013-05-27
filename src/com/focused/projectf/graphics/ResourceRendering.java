
package com.focused.projectf.graphics;

import org.lwjgl.opengl.GL11;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.Rect;
import com.focused.projectf.TileConstants;
import com.focused.projectf.entities.ResourceElement;
import com.focused.projectf.resources.Content;

public class ResourceRendering implements TileConstants {

	public static Image resourceImage;
	public static int tries = 0;

	public static final int TYPE_NONE		= 0;
	public static final int TYPE_TREE		= 1;
	public static final int TYPE_STONE		= 2;
	public static final int TYPE_GOLD		= 3;
	public static final int TYPE_RAD		= 4;
	public static final int TYPE_BUSH		= 5;

	public static void loadResources() {
		resourceImage = Content.getImage("mapElements/allResources.png");
		tries = 0;
	}

	public static void renderResources(Rect clip) {
		renderResourcesNorm(clip);
		ErrorManager.GLErrorCheck();
	}

	public static void renderResourcesNorm(Rect clip) {

		Map map = Map.get();

		int startX 	= (int) Map.toTile(clip.getTopLeft()).X;
		int startY 	= (int) Map.toTile(clip.getBottomLeft()).Y;
		int endX 	= (int) Map.toTile(clip.getBottomRight()).X;
		int endY 	= (int) Map.toTile(clip.getTopRight()).Y;

		Color.WHITE.bind();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		if(resourceImage == null) {
			loadResources();
			return;
		}
		resourceImage.bind();

		GL11.glPushAttrib(GL11.GL_DEPTH_BITS);

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.6f);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDepthMask(true);
		ErrorManager.GLErrorCheck();

		
		float invImgWidth = 1.0f / resourceImage.getWidth();
		float invImgHeight = 1.0f / resourceImage.getHeight();

		for(int tileX = startX; tileX < endX; tileX++) {
			GL11.glBegin(GL11.GL_QUADS);
			for(int tileY = startY; tileY < endY; tileY++) {

				ResourceElement resource = map.resources[tileX][tileY];

				if(resource != null && map.isTileDiscovered(tileX, tileY)) {
					float ty = -(tileY - tileX) * Map.tileHalfHeight;
					float tx = (tileX + tileY - map.getWidthInTiles()) * Map.tileHalfWidth + Map.tileHalfWidth;
					float halfWidth = 16, height = 0, beginX = 0, beginY = 0;

					switch(resource.DepositType) {

						case Gold:
						case Radium:
						case Stone:
							ty += 7;
							if(resource.getResourceAmount() > 400) {
								beginY = 192;
								height = 64;
							} else {
								beginY = 160;
								height = 32;
							}
							beginX = (resource.DepositType.ordinal() - 2) * 32;
							halfWidth = 16;
							break;

						case Wood:
							ty += 5;
							halfWidth = 32;
							height = 64;
							beginX = 64 * (resource.tick % 4);
							beginY = 64 * (resource.tick / 4);
							break;
						case None:
						case Food:
						default:
							break;
					}

					
					if(!map.isTileVissible(tileX, tileY))	Color.GRAY.bind();
					else									Color.WHITE.bind();
					
					float depth = Canvas.calcDepth(ty);
					float right 	= (beginX + halfWidth * 2) * invImgWidth;
					float left 		= beginX * invImgWidth;
					float top 		= beginY * invImgHeight;
					float bottom 	= (beginY + height) * invImgHeight;
					
					GL11.glTexCoord2f(right, top);
					GL11.glVertex3f(tx - halfWidth, ty - height, depth);
					GL11.glTexCoord2f(right, bottom);
					GL11.glVertex3f(tx - halfWidth, ty, depth);
					GL11.glTexCoord2f(left, bottom);
					GL11.glVertex3f(tx + halfWidth, ty, depth);
					GL11.glTexCoord2f(left, top);
					GL11.glVertex3f(tx + halfWidth, ty - height, depth);
				
				}
			}
			GL11.glEnd();
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPopAttrib();
	}
}
