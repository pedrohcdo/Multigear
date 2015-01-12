package multigear.mginterface.scene.components.receivers;

import multigear.mginterface.scene.components.Component;
import android.view.MotionEvent;

/**
 * Touchable
 * 
 * @author user
 *
 */
public interface Touchable extends Component {

	/**
	 * Touch handler
	 * @return Depth
	 */
	public void touch(final MotionEvent motionEvent);
}
