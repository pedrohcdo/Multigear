package com.org.multigear.general.utils.animations;

import com.org.multigear.mginterface.graphics.animations.Animation;
import com.org.multigear.mginterface.graphics.animations.AnimationSet;


/**
 * Animation used to Swing this Dialog.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class AnimationSwingOpacity implements Animation {
	
	// Constants
	final static private double PI2 = Math.PI * 2;
	
	/**
	 * Animate
	 */
	@Override
	public void onAnimation(AnimationSet animationSet, float delta) {
		float factor = (float)PI2 * delta;
		float oscillation = (float)Math.sin(factor) * 1;
		animationSet.setOpacity(oscillation);
	}
}