package multigear.mginterface.scene.components.receivers;

import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.scene.components.Component;

/**
 * Drawable
 * 
 * @author user
 *
 */
public interface Drawable extends Component {
	
	/**
	 * Draw handler
	 * @return Depth
	 */
	public void draw(final Drawer drawer);
}
