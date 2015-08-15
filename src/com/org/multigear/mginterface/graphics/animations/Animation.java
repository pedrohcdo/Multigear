package com.org.multigear.mginterface.graphics.animations;

/**
 * 
 * Base para animação.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public interface Animation {
	
	/**
	 * Animation Callback
	 * @param animationSet AnimationSet
	 * @param delta Elapsed time of the animation, the value returned will be between 0.0 and 1.0
	 */
	abstract public void onAnimation(final AnimationSet animationSet, final float delta);
}
