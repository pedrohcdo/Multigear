package multigear.mginterface.graphics.animations;

import multigear.mginterface.graphics.animations.Animation;
import multigear.mginterface.graphics.animations.AnimationSet;

/**
 * Animation used to Swing this Dialog.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class AnimationSwing implements Animation {
	
	// Constants
	final static private double PI2 = Math.PI * 2;
	
	// Final Private Variables
	final private float mForce;
	
	/*
	 * Construtor
	 */
	public AnimationSwing(final float force) {
		mForce = force;
	}
	
	/**
	 * Animate
	 */
	@Override
	public void onAnimation(AnimationSet animationSet, float delta) {
		float factor = (float)PI2 * delta;
		float oscillation = (float)Math.sin(factor) * mForce;
		animationSet.setPosition(0, oscillation);
	}
}