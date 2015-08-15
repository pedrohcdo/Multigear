package com.org.multigear.mginterface.tools.touch;

import com.org.multigear.general.utils.Vector2;


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
