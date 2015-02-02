package multigear.mginterface.tools.touch;

import multigear.general.utils.Vector2;


/**
 * Focus Detector Listener
 * 
 * @author user
 *
 */
public interface FocusDetectorListener {
	
	/**
	 * On Focus
	 * 
	 * @param scale
	 */
	public void onFocus(final Vector2 focused);
}
