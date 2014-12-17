package multigear.mginterface.graphics.drawable.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import multigear.general.utils.Ref2F;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.drawable.SimpleDrawable;
import multigear.mginterface.graphics.opengl.drawer.MatrixRow;
import android.opengl.GLES20;
import android.view.MotionEvent;

/**
 * 
 * Used to create a floating and flexible texture. Support the positions of and
 * also their mapping vertices thereof.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class BaseWidget extends SimpleDrawable {
	
	/**
	 * Widget Skin
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public class Skin {
		
		/**
		 * Skin Value
		 */
		final private class SkinValue {
			// Final Private Variables
			final private int ID;
			final private multigear.mginterface.graphics.opengl.texture.Texture Texture;
			
			/*
			 * Construtor
			 */
			public SkinValue(final int id, final multigear.mginterface.graphics.opengl.texture.Texture texture) {
				ID = id;
				Texture = texture;
			}
		}
		
		// Final Private Variables
		final private List<SkinValue> mSkinValues;
		
		/*
		 * Construtor
		 */
		private Skin() {
			mSkinValues = new ArrayList<SkinValue>();
		}
		
		/**
		 * Set Skin Textue
		 * 
		 * @param id
		 *            Skin Id
		 * @param textue
		 *            Used
		 *            {@link multigear.mginterface.graphics.opengl.texture.Texture}
		 */
		final public void setTexture(final int id, final multigear.mginterface.graphics.opengl.texture.Texture texture) {
			mSkinValues.add(new SkinValue(id, texture));
			onRefresh();
		}
		
		/**
		 * Get Skin Texture ID
		 * 
		 * @param id
		 *            Skin Id
		 * @return Reference skin
		 *         {@link multigear.mginterface.graphics.opengl.texture.Texture}
		 */
		final public multigear.mginterface.graphics.opengl.texture.Texture getTexture(final int id) {
			for (final SkinValue skinValue : mSkinValues)
				if (skinValue.ID == id)
					return skinValue.Texture;
			return null;
		}
	}
	
	/**
	 * Comparador utilisado para ordenamento de sobreposi��o para todos Layers
	 * para fins de Desenho.
	 */
	final private Comparator<multigear.mginterface.graphics.drawable.widget.WidgetLayer> mLayersComparatorDraw = new Comparator<multigear.mginterface.graphics.drawable.widget.WidgetLayer>() {
		
		/*
		 * Comparador
		 */
		@Override
		public int compare(multigear.mginterface.graphics.drawable.widget.WidgetLayer lhs, multigear.mginterface.graphics.drawable.widget.WidgetLayer rhs) {
			return lhs.getZ() - rhs.getZ();
		}
	};
	
	// Final Public Variables
	final public Skin Skin = new Skin();
	
	// Constants
	final static public int STATE_PRESSED = 0x1;
	final static public int STATE_IN = 0x2;
	
	// Final Private Variables
	final private List<multigear.mginterface.graphics.drawable.widget.WidgetLayer> mLayers;
	
	// Private Variables
	private int mState;
	private boolean mTouchHandledImpl;
	private multigear.general.utils.Ref2F mTouchLastPositionImpl;
	private multigear.mginterface.graphics.animations.AnimationStack mAnimationStack;
	
	/**
	 * Constructor
	 */
	public BaseWidget(final multigear.mginterface.scene.Scene room) {
		super(room);
		mState = 0;
		mTouchHandledImpl = false;
		mLayers = new ArrayList<multigear.mginterface.graphics.drawable.widget.WidgetLayer>();
		mAnimationStack = new AnimationStack(room);
	}
	
	/**
	 * Get Animation Stack
	 * 
	 * @return animationStack
	 *         {@link multigear.mginterface.graphics.animations.AnimationStack}
	 */
	final public AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Add a new Sprite Layer. Similar to Sprite.
	 */
	final protected WidgetSpriteLayer addSpriteLayer() {
		WidgetSpriteLayer layer = new WidgetSpriteLayer(getAttachedRoom());
		mLayers.add(layer);
		return layer;
	}
	
	/**
	 * Add a new Trxt Layer. Similar to Sprite.
	 */
	final protected WidgetTextLayer addTextLayer() {
		WidgetTextLayer layer = new WidgetTextLayer(getAttachedRoom());
		mLayers.add(layer);
		return layer;
	}
	
	/**
	 * Remove the Layer of this Object
	 * 
	 * @param layer
	 */
	final protected void removeLayer(multigear.mginterface.graphics.drawable.widget.WidgetLayer layer) {
		mLayers.remove(layer);
	}
	
	/**
	 * Add State
	 * 
	 * @param state
	 *            State
	 */
	final protected void addState(final int state) {
		if (hasState(state))
			return;
		mState |= state;
		onRefresh();
	}
	
	/**
	 * Remove State
	 * 
	 * @param state
	 *            State
	 */
	final protected void removeState(final int state) {
		if (!hasState(state))
			return;
		mState ^= (mState & state);
		onRefresh();
	}
	
	/**
	 * Clear State
	 */
	final protected void clearState() {
		mState = 0;
		onRefresh();
	}
	
	/**
	 * Check if has State.
	 * 
	 * @param state
	 * @return True if has State
	 * 
	 *         trocar .Var por .setVar(..)
	 * 
	 * 
	 */
	final protected boolean hasState(final int state) {
		if (!mTouchable) {
			final int lastState = mState;
			mState ^= (mState & (STATE_PRESSED | STATE_IN));
			if (mState != lastState) {
				onRefresh();
			}
		}
		return ((mState & state) == state);
	}
	
	/**
	 * Return State
	 * 
	 * @return State
	 */
	final protected int getState() {
		if (!mTouchable)
			removeState(STATE_PRESSED | STATE_IN);
		return mState;
	}
	
	/**
	 * Not Impl
	 */
	@Override
	protected AnimationStack getImplAnimationStack() {
		return null;
	}
	
	/**
	 * Update Widget
	 */
	@Override
	final public void updateAndDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer, final float preOpacity) {
		
		// Prepare Animations
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Scissor switch
		boolean disableScissor = false;
		
		// Set Scisor
		if (mViewport != null) {
			final int screenHeight = (int) getAttachedRoom().getScreenSize().YAxis;
			final int top = screenHeight - mViewport.bottom;
			final int bottom = screenHeight - mViewport.top - top;
			GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
			GLES20.glScissor(mViewport.left, top, mViewport.right, bottom);
			disableScissor = true;
		}
		
		// Prepare Transformations
		// Top Level
		final float ox = mCenter.XAxis * mScale.XAxis;
		final float oy = mCenter.YAxis * mScale.YAxis;
		final float sx = mSize.XAxis * mScale.XAxis;
		final float sy = mSize.YAxis * mScale.YAxis;
		
		// Animation Level
		final Ref2F ascale = mScale.clone().mul(animationSet.getScale());
		final float aox = mCenter.XAxis * ascale.XAxis;
		final float aoy = mCenter.YAxis * ascale.YAxis;
		final float arotation = mAngle + animationSet.getRotation();
		final Ref2F atranslate = animationSet.getPosition();
		
		// Get Matrix Row
		final MatrixRow matrixRow = drawer.getMatrixRow();
		
		// Push Matrix
		matrixRow.push();
		
		// Order Layers
		Collections.sort(mLayers, mLayersComparatorDraw);
		
		// Opacity
		final float opacity = preOpacity * animationSet.getOpacity() * getOpacity();
		
		// Draw
		for (final multigear.mginterface.graphics.drawable.widget.WidgetLayer layer : mLayers) {
			// Begin Layer Draw
			if (layer.beginDraw(opacity, drawer)) {
				
				// Scale
				matrixRow.postScalef(ascale.XAxis, ascale.YAxis);
				
				// Top Transformations
				matrixRow.postTranslatef(-aox, -aoy);
				matrixRow.postRotatef(arotation);
				matrixRow.postTranslatef(aox, aoy);
				
				// Bottom Transformations
				final float tX = (mPosition.XAxis - mScroll.XAxis - aox) + atranslate.XAxis;
				final float tY = (mPosition.YAxis - mScroll.YAxis - aoy) + atranslate.YAxis;
				matrixRow.postTranslatef(tX, tY);
				
				// End Layer Draw
				layer.endDraw(drawer);
			}
		}
		
		// Disable Scissor
		if (disableScissor)
			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		
		// Scale Widget
		matrixRow.postScalef(sx, sy);
		
		// Pre Rotate
		matrixRow.postTranslatef(-ox, -oy);
		matrixRow.postRotatef(mAngle);
		matrixRow.postTranslatef(ox, oy);
		
		// Translate Matrix
		final float tX = (float) (mPosition.XAxis - mScroll.XAxis - ox);
		final float tY = (float) (mPosition.YAxis - mScroll.YAxis - oy);
		matrixRow.postTranslatef(tX, tY);
		
		// Get Transformed Vertices
		final float transformMatrix[] = new float[16];
		matrixRow.copyValues(transformMatrix);
		
		// Prepare Vertices Position
		refreshVerticesPosition(transformMatrix);
		
		// Pop Matrix
		matrixRow.pop();
		
		//
		onUpdate();
	}
	
	/**
	 * Get Touch Event.
	 * 
	 * @param motionEvent
	 *            MotionEvent used for touch.
	 * @return Return true if handled.
	 */
	final public void touch(final MotionEvent motionEvent) {
		if (!mTouchable) {
			mTouchHandledImpl = false;
			return;
		}
		final multigear.mginterface.graphics.drawable.BaseListener listener = getListener();
		multigear.general.utils.Ref2F point = null;
		switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				addState(STATE_PRESSED);
				point = multigear.general.utils.KernelUtils.ref2d(motionEvent.getX(), motionEvent.getY());
				if (pointOver(point)) {
					mTouchLastPositionImpl = point;
					addState(STATE_IN);
					onPress();
					onTouch(motionEvent);
					mTouchHandledImpl = true;
					if (listener != null && listener instanceof multigear.mginterface.graphics.drawable.SimpleListener)
						((multigear.mginterface.graphics.drawable.SimpleListener) listener).onPress(this);
					if (listener != null && listener instanceof multigear.mginterface.graphics.drawable.TouchListener)
						((multigear.mginterface.graphics.drawable.TouchListener) listener).onTouch(this, motionEvent);
					
					break;
				}
			case MotionEvent.ACTION_CANCEL:
				if (mTouchHandledImpl) {
					removeState(STATE_PRESSED);
					mTouchHandledImpl = false;
					onRelease();
					onTouch(motionEvent);
					if (listener != null && listener instanceof multigear.mginterface.graphics.drawable.TouchListener)
						((multigear.mginterface.graphics.drawable.TouchListener) listener).onTouch(this, motionEvent);
					
					removeState(STATE_IN);
				}
				break;
			case MotionEvent.ACTION_UP:
				
				if (mTouchHandledImpl) {
					removeState(STATE_PRESSED);
					onRelease();
					mTouchHandledImpl = false;
					point = multigear.general.utils.KernelUtils.ref2d(motionEvent.getX(), motionEvent.getY());
					onTouch(motionEvent);
					if (listener != null) {
						if (listener instanceof multigear.mginterface.graphics.drawable.SimpleListener)
							((multigear.mginterface.graphics.drawable.SimpleListener) listener).onRelease(this);
						if (pointOver(point) && listener instanceof multigear.mginterface.graphics.drawable.ClickListener)
							((multigear.mginterface.graphics.drawable.ClickListener) listener).onClick(this);
						if (listener instanceof multigear.mginterface.graphics.drawable.TouchListener)
							((multigear.mginterface.graphics.drawable.TouchListener) listener).onTouch(this, motionEvent);
					}
					removeState(STATE_IN);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mTouchHandledImpl) {
					point = multigear.general.utils.KernelUtils.ref2d(motionEvent.getX(), motionEvent.getY());
					final float diffX = point.XAxis - mTouchLastPositionImpl.XAxis;
					final float diffY = point.YAxis - mTouchLastPositionImpl.YAxis;
					final float scaleFactor = getBaseScaleFacor();
					final multigear.general.utils.Ref2F moved = multigear.general.utils.KernelUtils.ref2d(diffX / scaleFactor, diffY / scaleFactor);
					boolean switchFlag = false;
					if (pointOver(point)) {
						switchFlag = !hasState(STATE_IN);
						addState(STATE_IN);
					} else {
						switchFlag = hasState(STATE_IN);
						removeState(STATE_IN);
					}
					onTouch(motionEvent);
					onMove(moved, switchFlag);
					mTouchLastPositionImpl = point;
					if (listener != null && listener instanceof multigear.mginterface.graphics.drawable.SimpleListener)
						((multigear.mginterface.graphics.drawable.SimpleListener) listener).onMove(this, moved);
					if (listener != null && listener instanceof multigear.mginterface.graphics.drawable.TouchListener)
						((multigear.mginterface.graphics.drawable.TouchListener) listener).onTouch(this, motionEvent);
				}
				break;
		}
		
	}
	
	/** On Refresh */
	protected void onRefresh() {
	}
	
	/** On Press Event */
	protected void onPress() {
	}
	
	/** On Release Event */
	protected void onRelease() {
	}
	
	protected void onTouch(final MotionEvent motionEvent) {
	}
	
	/** On Move Event */
	protected void onMove(final multigear.general.utils.Ref2F moved, boolean inOutSwitch) {
	}
	
	/** On Update Event */
	protected void onUpdate() {
	}
}
