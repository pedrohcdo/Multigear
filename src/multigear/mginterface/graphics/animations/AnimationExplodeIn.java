package multigear.mginterface.graphics.animations;





/**
 * 
 * Esta animação cria um efeito de explosão.
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
public class AnimationExplodeIn extends Animation {
	
	/*
	 * Construtor
	 */
	public AnimationExplodeIn(int duration) {
		super(duration);
	}
	
	/**
	 * Animate
	 */
	@Override
	final public void onAnimation(AnimationSet animationSet, float delta) {
		final float factor = (float)Math.PI * delta;
		final float cos = (1.0f - (float)Math.abs(Math.cos(factor))) / 8.0f + 1.0f;
		animationSet.setScale(cos, cos);
		animationSet.setOpacity(1.0f - delta);
	}
}
