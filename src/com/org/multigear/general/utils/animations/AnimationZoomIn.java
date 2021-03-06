package com.org.multigear.general.utils.animations;

import com.org.multigear.mginterface.graphics.animations.Animation;
import com.org.multigear.mginterface.graphics.animations.AnimationSet;

/**
 * 
 * Esta anima��o cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationZoomIn implements Animation {
	
	// Final Private Variables
	final private float mFactor;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationZoomIn(final float factor) {
		mFactor = factor;
	}
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationZoomIn() {
		mFactor = 0.12f;
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		final float f = 1 - (mFactor * delta);
		animationSet.setScale(f, f);
	}
}
