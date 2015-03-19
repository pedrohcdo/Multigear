package multigear.mginterface.scene.components.receivers;

import android.view.MotionEvent;

/**
 * Touchable
 * 
 * @author user
 *
 */
public interface Touchable {

	/**
	 * Touch handler
	 * @return Depth
	 */
	public boolean touch(final MotionEvent motionEvent);
}
