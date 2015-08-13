package multigear.general.utils.interpolations;

import multigear.general.utils.Interpolation;


/**
 * Ease Out Bounce Interpolation
 * 
 * @author user
 *
 */
public class EaseOutBounce implements Interpolation {

	/**
	 * Interpolation
	 * 
	 * @param delta
	 * @return
	 */
	@Override
	public float onInterpolate(float delta) {
        if (delta < (1 / 2.75f)) {
            return 7.5625f*delta*delta;
        } else if(delta < (2/2.75)) {
        	delta -= 1.5f / 2.75f;
            return 7.5625f * delta * delta + .75f;
        } else if(delta < (2.5f/2.75f)) {
        	delta -= 2.25f / 2.75f;
            return 7.5625f * delta * delta + .9375f;
        } else {
        	delta -= 2.625f / 2.75f;
            return 7.5625f * delta * delta + .984375f;
        }
	}
}
