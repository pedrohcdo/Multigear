package com.org.multigear.mginterface.graphics.drawable.gui.widgets;

import com.org.multigear.audio.AudioManager;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.drawable.sprite.Sprite;
import com.org.multigear.mginterface.graphics.drawable.widget.Widget;


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
		public com.org.multigear.mginterface.graphics.animations.Animation Press;
		/** Animation started on release */
		public com.org.multigear.mginterface.graphics.animations.Animation Release;
	}
	
	// Constants
	final static public int SKIN_NORMAL = 0;
	final static public int SKIN_PRESSED = 1;
	
	// Final Private Variables
	final public Animation Animation = new Animation();
	
	// Final private Variables
	final private Sprite mLayer = new Sprite();
	final private AudioManager mAudioManager;
	
	
	private boolean mAudioPlayed = false;
	private int mRawId;
	
	/*
	 * Construtor
	 */
	public Button(final AudioManager manager, int rawId) {
		mAudioManager = manager;
		setStaticTouch(true);
		Animation.Press = null;
		Animation.Release = null;
		mAudioPlayed = false;
		mRawId = rawId;
		addComponent(mLayer);
	}
	
	/*
	 * Construtor
	 */
	public Button() {
		mAudioManager = null;
		setStaticTouch(true);
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
			final com.org.multigear.mginterface.graphics.opengl.texture.Texture texture = Skin.getTexture(SKIN_PRESSED);
			this.setSize(texture.getSize());
			mLayer.setTexture(texture);
		} else {
			final com.org.multigear.mginterface.graphics.opengl.texture.Texture texture = Skin.getTexture(SKIN_NORMAL);
			this.setSize(texture.getSize());
			mLayer.setTexture(texture);
		}
	}
	
	/**
	 * Press Animation
	 */
	final protected void pressAnimation() {
		if (Animation.Press != null) {
			getAnimationStack().clearAnimations();
			getAnimationStack().addAnimation(200, Animation.Press);
			getAnimationStack().start();
		}
	}
	
	/**
	 * Release Animation
	 */
	final protected void releaseAnimation() {
		if (Animation.Press != null) {
			getAnimationStack().clearAnimations();
			getAnimationStack().addAnimation(200, Animation.Release);
			getAnimationStack().start();
		}
	}
	
	/*
	 * Evento on press
	 */
	@Override
	protected void onPress() {
		pressAnimation();
		if(mAudioManager != null) {
			mAudioPlayed = true;
			mAudioManager.preConfig(0.7f, 0.7f, 1.1f);
			mAudioManager.playEffx(mRawId, 0);
		}
	}
	
	/**
	 * Check if audio really played
	 * @return
	 */
	final public boolean isAudioPlayed() {
		return mAudioPlayed;
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
