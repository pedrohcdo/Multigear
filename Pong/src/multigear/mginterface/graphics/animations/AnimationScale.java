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
public class AnimationScale extends Animation {
	
	// Final Private Variables
	final private Ref2F mStart, mEnd;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationScale(final int duration, final Ref2F start, final Ref2F end) {
		super(duration);
		mStart = start.clone();
		mEnd = end.clone();
	}
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationScale(final int duration, final float start, final float end) {
		super(duration);
		mStart = new Ref2F(start, start);
		mEnd = new Ref2F(end, end);
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setScale(mStart.clone().add(mEnd.clone().sub(mStart).mul(delta)));
	}
}
