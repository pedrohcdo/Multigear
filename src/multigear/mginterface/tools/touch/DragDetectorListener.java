package multigear.mginterface.tools.touch;

import multigear.general.utils.Vector2;


/**
 * Drag Detector Listener
 * 
 * @author user
 *
 */
public interface DragDetectorListener {
	
	/**
	 * On Drag
	 * 
	 * @param scale
	 */
	public void onDrag(final Vector2 draged);
}
