package multigear.mginterface.tools.touch;

import multigear.general.utils.Vector2;


/**
 * Pull Detector Listener
 * 
 * @author user
 *
 */
public interface PullDetectorListener {
	
	/**
	 * On Pull
	 * 
	 * @param scale
	 */
	public void onPull(final Vector2 start, final Vector2 end);
}
