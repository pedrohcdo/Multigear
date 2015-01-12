package multigear.mginterface.scene.components.receivers;

import multigear.mginterface.scene.components.Component;
import android.view.MotionEvent;

/**
 * Updatable
 * 
 * @author user
 *
 */
public interface Updatable extends Component {

	/**
	 * Touch handler
	 * @return Depth
	 */
	public void update();
}
