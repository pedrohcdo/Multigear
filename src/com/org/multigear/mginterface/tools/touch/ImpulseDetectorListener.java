package com.org.multigear.mginterface.tools.touch;

import com.org.multigear.general.utils.Vector2;


/**
 * Impulse Detector Listener
 * 
 * @author user
 *
 */
public interface ImpulseDetectorListener {
	
	/**
	 * On Impulse
	 * 
	 * @param scale
	 */
	public void onImpulse(final Vector2 impulsed);
}
