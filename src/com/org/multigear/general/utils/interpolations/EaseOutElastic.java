package com.org.multigear.general.utils.interpolations;

import com.org.multigear.general.utils.Interpolation;

/**
 * Ease Out Elastic Interpolation
 * @author user
 *
 */
public class EaseOutElastic implements Interpolation {

	/**
	 * Interpolate
	 */
	@Override
	public float onInterpolate(float delta) {
		double s = 0.3f / (2 * Math.PI) * Math.asin(1);
	    return (float)(Math.pow(2,-10 * delta) * Math.sin((delta-s) * (2*Math.PI)/0.3f) + 1);
	}
}
