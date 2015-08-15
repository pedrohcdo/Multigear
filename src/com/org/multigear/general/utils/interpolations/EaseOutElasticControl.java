package com.org.multigear.general.utils.interpolations;

import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Interpolation;

/**
 * Ease Out Limp Elastic
 * 
 * @author user
 *
 */
public class EaseOutElasticControl implements Interpolation {

	// Consts
	final private static double RAD45 = GeneralUtils.degreeToRad(65);
	final private static double RAD60 = GeneralUtils.degreeToRad(70);
	final private static double TAN45 = Math.tan(RAD45);
	final private static double TAN60 = Math.tan(RAD60);
	
	// Final Private Variables
	final private float mElasticity;
	
	
	/**
	 * Constructor with elasticity equal to .13f
	 */
	public EaseOutElasticControl() {
		mElasticity = 0.13f;
	}
	
	/**
	 * Constructor
	 * @param elasticity
	 */
	public EaseOutElasticControl(float elasticity) {
		mElasticity = elasticity;
	}
	
	/**
	 * Interpolate
	 * 
	 */
	@Override
	public float onInterpolate(float delta) {
		if(delta <= 0.4f) {
			float x = (delta / 0.4f) * 1.5f;
			if(x < 1f) {
				double f = Math.tan((RAD60 - RAD45) * x + RAD45) / TAN60 - TAN45 / TAN60;
				double d = 1 - TAN45 / TAN60;
				return (float) (f / d);
			} else {
				double y = ((x - 1.0f) / 0.5f) * Math.PI;
				return 1.0f + (float)Math.abs(Math.sin(y)) * mElasticity * 2;
			}
		} else if(delta <= 0.7f) {
			double x = ((delta - 0.4f) / 0.3f) * Math.PI;
			return 1.0f - (float)Math.abs(Math.sin(x)) * mElasticity;
		} else {
			double x = ((delta - 0.7f) / 0.3f) * Math.PI;
			return 1.0f + (float)Math.abs(Math.sin(x)) * mElasticity;
		}
	}
}
