package multigear.mginterface.scene.components.receivers;

import multigear.mginterface.graphics.opengl.drawer.Drawer;
import android.view.MotionEvent;

/**
 * Component
 * 
 * @author user
 *
 */
public interface Component {
	
	/**
	 * Depth used for organization of all drawable.
	 * @return Depth
	 */
	public int getZ();
	
	/**
	 * Identifier of a drawable.
	 * @return Integer used to identify a drawable.
	 */
	public int getId();
}
