package multigear.mginterface.graphics.drawable.gui.widgets;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.drawable.sprite.Sprite;
import multigear.mginterface.graphics.drawable.widget.Widget;

/**
 * Button
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Button extends Widget {
	
	/**
	 * Animation effects.
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public class Animation {
		
		/** Animation started on press */
		public multigear.mginterface.graphics.animations.Animation Press;
		/** Animation started on release */
		public multigear.mginterface.graphics.animations.Animation Release;
	}
	
	// Constants
	final static public int SKIN_NORMAL = 0;
	final static public int SKIN_PRESSED = 1;
	
	// Final Private Variables
	final public Animation Animation = new Animation();
	
	// Final private Variables
	final private Sprite mLayer = new Sprite();
	
	/*
	 * Construtor
	 */
	public Button() {
		Animation.Press = null;
		Animation.Release = null;
		addComponent(mLayer);
	}
	
	/*
	 * Estado Alterado
	 */
	@Override
	protected void onRefresh() {
		if (hasState(STATE_PRESSED | STATE_IN)) {
			final multigear.mginterface.graphics.opengl.texture.Texture texture = Skin.getTexture(SKIN_PRESSED);
			this.setSize(texture.getSize());
			mLayer.setTexture(texture);
		} else {
			final multigear.mginterface.graphics.opengl.texture.Texture texture = Skin.getTexture(SKIN_NORMAL);
			this.setSize(texture.getSize());
			mLayer.setTexture(texture);
		}
	}
	
	/**
	 * Press Animation
	 */
	final protected void pressAnimation() {
		if (Animation.Press != null) {
			getAnimationStack().clear();
			getAnimationStack().addAnimation(Animation.Press);
			getAnimationStack().start();
		}
	}
	
	/**
	 * Release Animation
	 */
	final protected void releaseAnimation() {
		if (Animation.Press != null) {
			getAnimationStack().clear();
			getAnimationStack().addAnimation(Animation.Release);
			getAnimationStack().start();
		}
	}
	
	/*
	 * Evento on press
	 */
	@Override
	protected void onPress() {
		pressAnimation();
	}
	
	/*
	 * Evento on Release
	 */
	@Override
	protected void onRelease() {
		if (hasState(STATE_IN)) {
			releaseAnimation();
		}
	}
	
	/*
	 * Evento on Move
	 */
	@Override
	protected void onMove(Vector2 moved, boolean inOutSwitch) {
		if (inOutSwitch) {
			if (hasState(STATE_IN))
				pressAnimation();
			else
				releaseAnimation();
		}
	}
}
