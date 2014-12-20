package multigear.mginterface.graphics.animations;





/**
 * 
 * Esta anima��o cria um efeito de explos�o.
 * 
 * @author PedroH, RaphaelB
 *
 * Property SpringBall.
 */
public class AnimationExplodeOut extends Animation {
	
	
	/*
	 * Construtor
	 */
	public AnimationExplodeOut(int duration) {
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
		animationSet.setOpacity(delta);
	}
}
