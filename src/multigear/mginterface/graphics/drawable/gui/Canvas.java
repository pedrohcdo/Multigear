package multigear.mginterface.graphics.drawable.gui;

import multigear.general.utils.Color;
import multigear.general.utils.Vector2;
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
		mWorldMatrix.postTranslatef(position.x, position.y);
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
		mWorldMatrix.postTranslatef(position.x, position.y);
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
		mWorldMatrix.postTranslatef(position.x, position.y);
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
		mWorldMatrix.postTranslatef(position.x, position.y);
		mDrawer.begin();
		mDrawer.setColor(color);
		mDrawer.drawRectangle(size);
		mDrawer.end();
		mWorldMatrix.pop();
	}
}
