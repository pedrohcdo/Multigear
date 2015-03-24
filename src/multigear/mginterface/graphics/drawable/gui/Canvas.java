package multigear.mginterface.graphics.drawable.gui;

import java.nio.FloatBuffer;

import multigear.general.utils.Color;
import multigear.general.utils.Vector2;
import multigear.general.utils.buffers.GlobalFloatBuffer;
import multigear.mginterface.graphics.drawable.polygon.Polygon;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.font.Letter;
import multigear.mginterface.graphics.opengl.texture.Texture;

/**
 * 
 * @author user
 *
 */
public class Canvas {
	
	// Final private variables
	final private Drawer mDrawer;
	final private WorldMatrix mWorldMatrix;
	
	/**
	 * Constructor
	 * 
	 * @param drawer
	 */
	public Canvas(final Drawer drawer) {
		mDrawer = drawer;
		mWorldMatrix = mDrawer.getWorldMatrix();
	}
	
	/**
	 * Draw Texture
	 * 
	 * @param texture
	 * @param position
	 */
	final public void drawTexture(final Texture texture, final Vector2 position) {
		mWorldMatrix.push();
		mWorldMatrix.preTranslatef(position.x, position.y);
		mDrawer.begin();
		mDrawer.setTexture(texture);
		mDrawer.drawRectangle(texture.getSize());
		mDrawer.end();
		mWorldMatrix.pop();
	}
	
	/**
	 * Draw Texture
	 * 
	 * @param texture
	 * @param position
	 */
	final public void drawTexture(final Texture texture, final Vector2 position, final Vector2 size) {
		mWorldMatrix.push();
		mWorldMatrix.preTranslatef(position.x, position.y);
		mDrawer.begin();
		mDrawer.setTexture(texture);
		mDrawer.drawRectangle(size);
		mDrawer.end();
		mWorldMatrix.pop();
	}
	
	/**
	 * Draw Letter
	 * @param letter
	 * @param position
	 */
	final public void drawLetter(final Letter letter, final Vector2 position) {
		mWorldMatrix.push();
		mWorldMatrix.preTranslatef(position.x, position.y);
		mDrawer.begin();
		mDrawer.drawLetter(letter);
		mDrawer.end();
		mWorldMatrix.pop();
	}
	
	/**
	 * Draw Rect
	 * @param color
	 * @param position
	 * @param size
	 */
	final public void drawRect(final Color color, final Vector2 position, final Vector2 size) {
		mWorldMatrix.push();
		mWorldMatrix.preTranslatef(position.x, position.y);
		mDrawer.begin();
		mDrawer.setColor(color);
		mDrawer.drawRectangle(size);
		mDrawer.end();
		mWorldMatrix.pop();
	}
	
	/**
	 * Draw Rect
	 * @param color
	 * @param position
	 * @param size
	 */
	final public void drawRect(final Color color, final Vector2 position, final Vector2 size, final float opacity) {
		mWorldMatrix.push();
		mWorldMatrix.preTranslatef(position.x, position.y);
		mDrawer.begin();
		mDrawer.setOpacity(opacity);
		mDrawer.setColor(color);
		mDrawer.drawRectangle(size);
		mDrawer.end();
		mWorldMatrix.pop();
	}
	
	/**
	 * Draw Rect
	 * @param color
	 * @param position
	 * @param size
	 */
	final public void drawRoundedRect(final Color color, final Vector2 position, final Vector2 size, final float radius) {
		mWorldMatrix.push();
		mWorldMatrix.preTranslatef(position.x, position.y);
		
		
		mDrawer.begin();
		mDrawer.setColor(color);
		
		// Detail
		final double detail = Math.PI / 180.0f;
		
		// Obtain buffer
		int verticesCount = (int)Math.ceil((Math.PI*2) / detail);
		FloatBuffer buffer = GlobalFloatBuffer.obtain(verticesCount + 1);
		verticesCount = 0;
		
		// Put vertices
		for(double i=0; i<Math.PI/2; i+=detail) {
			final float x = (float)(Math.cos(i) * radius) + (size.x - radius);
			final float y = (float)(((Math.sin(i) * -1) + 1) * radius);
			buffer.put(x);
			buffer.put(y);
			verticesCount++;
		}
		for(double i=Math.PI/2; i<Math.PI; i+=detail) {
			final float x = (float)((Math.cos(i) + 1) * radius);
			final float y = (float)(((Math.sin(i) * -1) + 1) * radius);
			buffer.put(x);
			buffer.put(y);
			verticesCount++;
		}
		for(double i=Math.PI; i<Math.PI*1.5; i+=detail) {
			final float x = (float)((Math.cos(i) + 1) * radius);
			final float y = (float)(Math.sin(i) * radius * -1) + (size.y - radius);
			buffer.put(x);
			buffer.put(y);
			verticesCount++;
		}
		for(double i=Math.PI*1.5; i<Math.PI*2; i+=detail) {
			final float x = (float)(Math.cos(i) * radius) + (size.x - radius);
			final float y = (float)(Math.sin(i) * radius * -1) + (size.y - radius);
			buffer.put(x);
			buffer.put(y);
			verticesCount++;
		}
		
		// Set
		buffer.position(0);
		mDrawer.setElementVertex(buffer);
		mDrawer.setColor(color);
		
		// Draw
		mDrawer.drawPolygon(verticesCount);
		mDrawer.end();
		mWorldMatrix.pop();
		
		// Release
		GlobalFloatBuffer.release(buffer);
	}
}
