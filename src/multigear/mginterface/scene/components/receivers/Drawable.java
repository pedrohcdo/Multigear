package multigear.mginterface.scene.components.receivers;

import multigear.mginterface.graphics.opengl.drawer.Drawer;

/**
 * Drawable
 * 
 * @author user
 *
 */
public interface Drawable {
	
	/**
	 * Draw handler
	 * @return Depth
	 */
	public void draw(final Drawer drawer);
}
