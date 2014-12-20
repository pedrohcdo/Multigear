package multigear.mginterface.graphics.animations;


/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationRightToCenter extends Animation {
	
	// Private Variables
	final float mWidth;
	
	/*
	 * Construtor
	 */
	public AnimationRightToCenter(int duration, final float width) {
		super(duration);
		mWidth = width;
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setPosition(mWidth * (1 - delta), 0);
	}
}
