package multigear.mginterface.graphics.animations;

import multigear.general.utils.Ref2F;

/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationPosition extends Animation {
	
	// Final Private Variables
	final private Ref2F mStart, mEnd;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationPosition(final int duration, final Ref2F start, final Ref2F end) {
		super(duration);
		mStart = start.clone();
		mEnd = end.clone();
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setPosition(mStart.clone().add(mEnd.clone().sub(mStart).mul(delta)));
	}
}
