package multigear.mginterface.graphics.animations;

import multigear.general.utils.Vector2;

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
	final private Vector2 mStart, mEnd;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationPosition(final int duration, final Vector2 start, final Vector2 end) {
		super(duration);
		mStart = start.clone();
		mEnd = end.clone();
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setPosition(Vector2.sum(mStart, Vector2.scale( Vector2.sub(mEnd, mStart), delta)));
	}
}
