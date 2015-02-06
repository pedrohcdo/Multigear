package multigear.mginterface.tools.touch;

import multigear.general.utils.Vector2;


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
