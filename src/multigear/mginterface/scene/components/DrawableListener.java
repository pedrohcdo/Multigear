package multigear.mginterface.scene.components;

import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.scene.Scene;

/**
 * Base of a drawable.
 * 
 * @author user
 *
 */
public interface DrawableListener {
	
	/**
	 * Draws the drawable object.
	 */
	public void onDraw(final Scene scene, final Drawer drawer);
}
