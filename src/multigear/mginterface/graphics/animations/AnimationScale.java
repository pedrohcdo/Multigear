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
public class AnimationScale implements Animation {
	
	// Final Private Variables
	final private Vector2 mStart, mEnd;
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationScale(final Vector2 start, final Vector2 end) {
		mStart = start.clone();
		mEnd = end.clone();
	}
	
	/**
	 * Constructor
	 * 
	 * @param duration
	 */
	public AnimationScale(final float start, final float end) {
		mStart = new Vector2(start, start);
		mEnd = new Vector2(end, end);
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setScale(Vector2.sum(mStart, Vector2.scale( Vector2.sub(mEnd, mStart), delta)));
	}
}
