package multigear.mginterface.graphics.animations;



/**
 * 
 * Esta animação cria um efeito de se espremendo.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationCenterToLeft extends Animation {
	
	// Private Variables
	final float mWidth;
	
	/*
	 * Construtor
	 */
	public AnimationCenterToLeft(int duration, final float width) {
		super(duration);
		mWidth = width;
	}

	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		animationSet.setPosition(mWidth * -delta, 0);
	}
}
