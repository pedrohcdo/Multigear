package multigear.mginterface.graphics.animations;


/**
 * Animation used to Swing this Dialog.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class AnimationSwingOpacity extends Animation {
	
	// Constants
	final static private double PI2 = Math.PI * 2;
	
	/*
	 * Construtor
	 */
	public AnimationSwingOpacity(int duration) {
		super(duration);
	}
	
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