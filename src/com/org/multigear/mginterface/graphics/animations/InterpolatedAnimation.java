package com.org.multigear.mginterface.graphics.animations;

import com.org.multigear.general.utils.Interpolation;

/**
 * Animation Interpolation
 * 
 * @author user
 *
 */
public class InterpolatedAnimation implements Animation {

	// Final Private Variables
	final private Animation mAnimation;
	final private Interpolation mInterpolation;
	
	/**
	 * COnstructor
	 * 
	 * @param animation
	 */
	public InterpolatedAnimation(final Animation animation, final Interpolation interpolation) {
		mAnimation = animation;
		mInterpolation = interpolation;
	}
	
	/**
	 * On Animation
	 */
	@Override
	public void onAnimation(AnimationSet animationSet, float delta) {
		mAnimation.onAnimation(animationSet, mInterpolation.onInterpolate(delta));
	}
}
