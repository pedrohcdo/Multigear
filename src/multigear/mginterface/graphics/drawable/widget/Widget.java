package multigear.mginterface.graphics.drawable.widget;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.opengl.drawer.BlendFunc;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.scene.Component;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.components.receivers.Drawable;
import multigear.mginterface.scene.components.receivers.Touchable;
import multigear.mginterface.scene.listeners.BaseListener;
import multigear.mginterface.scene.listeners.ClickListener;
import multigear.mginterface.scene.listeners.SimpleListener;
import multigear.mginterface.scene.listeners.TouchListener;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Parcel;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
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
public class Widget implements Drawable, Touchable, Component {
	
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
	 * Comparador utilisado para ordenamento de sobreposição para todos Layers
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
	
	/**
	 * Pointer
	 * 
	 * @author user
	 *
	 */
	final private class Pointer {
		
		int id;
		Vector2 lastPosition;
		Vector2 framePosition;
	}

	/**
	 * Drawing Layer
	 * 
	 * @author user
	 *
	 */
	public enum DrawingLayer {
		
		/* Conts */
		LAYER_BOTTOM,
		LAYER_TOP;
	}
	
	// Final Private Variables
	final private List<Pointer> mPointers = new ArrayList<Pointer>();
	final private Vector2[] mVertices;
	final private float mResultMatrixA[] = new float[2];
	final private float mResultMatrixB[] = new float[2];
	final private float mResultMatrixC[] = new float[2];
	final private float mResultMatrixD[] = new float[2];
	final private float mBaseVerticeA[] = new float[] { 0, 0 };
	final private float mBaseVerticeB[] = new float[] { 1, 0 };
	final private float mBaseVerticeC[] = new float[] { 1, 1 };
	final private float mBaseVerticeD[] = new float[] { 0, 1 };
	
	// Private Variables
	private BaseListener mListener;
	protected Rect mViewport;
		
	// Public Variables
	protected Vector2 mScale = new Vector2(1, 1);
	protected Vector2 mPosition = new Vector2(0, 0);
	protected Vector2 mSize = new Vector2(32, 32);
	protected Vector2 mCenter = new Vector2(0, 0);
	protected Vector2 mScroll = new Vector2(0, 0);
	protected float mAngle = 0;
	protected float mOpacity = 1;
	protected boolean mTouchable = true;
	protected boolean mFixedSpace = false;
	protected boolean mMirror[] = { false, false };
	protected int mZ, mId;
	protected BlendFunc mBlendFunc = BlendFunc.ONE_MINUS_SRC_ALPHA;
	
	
	// Final Public Variables
	final public Skin Skin = new Skin();
	
	// Constants
	final static public int STATE_PRESSED = 0x1;
	final static public int STATE_IN = 0x2;
	
	// Final Private Variables
	final private List<multigear.mginterface.graphics.drawable.widget.WidgetLayer> mLayers;
	
	// Private Variables
	private int mState;
	private AnimationStack mAnimationStack;
	
	/**
	 * Constructor
	 */
	public Widget() {
		mState = 0;
		mLayers = new ArrayList<WidgetLayer>();
		mAnimationStack = new AnimationStack();
		
		mListener = null;
		mVertices = new Vector2[4];
		mViewport = null;
		mFixedSpace = false;
		
		setVerticesPosition();
	}

	/**
	 * Set Viewport
	 * 
	 * @param left
	 *            Left
	 * @param top
	 *            Top
	 * @param width
	 *            Width
	 * @param height
	 *            Height
	 */
	final public void setViewport(final int left, final int top, final int width, final int height) {
		mViewport = new Rect(left, top, width, height);
	}
	
	/**
	 * Set a listener. Listener used for send Touch Events.
	 * 
	 * @param listener
	 *            Used Listener.
	 */
	final public void setListener(final multigear.mginterface.scene.listeners.BaseListener listener) {
		mListener = listener;
	}
	
	/**
	 * Invert in Vertical
	 * 
	 * @param inverted
	 */
	final public void setMirrorInverted(final boolean mirrorX, final boolean mirrorY) {
		mMirror[0] = mirrorX;
		mMirror[1] = mirrorY;
	}
	
	/**
	 * Set Blend Func
	 * 
	 * @param blendFunc
	 */
	final public void setBlendFunc(final BlendFunc blendFunc) {
		mBlendFunc = blendFunc;
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final Vector2 scale) {
		mScale = scale.clone();
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final float scaleX, final float scaleY) {
		mScale = new Vector2(scaleX, scaleY);
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final float scale) {
		mScale = new Vector2(scale, scale);
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link Vector2} Position
	 */
	final public void setPosition(final Vector2 position) {
		mPosition = position.clone();
	}
	
	/**
	 * Set draw dest texture size.
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setSize(final Vector2 size) {
		mSize = size.clone();
	}
	
	/**
	 * Set center .
	 * 
	 * @param center
	 *            {@link Vector2} Center
	 */
	final public void setCenter(final Vector2 center) {
		mCenter = center.clone();
	}
	
	/**
	 * Set Angle.
	 * 
	 * @param angle
	 *            {@link Vector2} Angle
	 */
	final public void setAngle(final float angle) {
		mAngle = angle;
	}
	
	/**
	 * Set Scroll.
	 * 
	 * @param center
	 *            {@link Vector2} Scroll
	 */
	final public void setScroll(final Vector2 scroll) {
		mScroll = scroll.clone();
	}
	
	/**
	 * Set Touchable.
	 * 
	 * @param touchable
	 *            Boolean Touchable
	 */
	final public void setTouchable(final boolean touchable) {
		mTouchable = touchable;
		if(!mTouchable) {
			mPointers.clear();
		}
	}
	
	/**
	 * Set Fixed Space.
	 * 
	 * @param fixed
	 *            Boolean Fixed
	 */
	final public void setFixedSpace(final boolean fixed) {
		mFixedSpace = fixed;
	}
	
	/**
	 * Set Opacity
	 * @param opacity Opacity
	 */
	public void setOpacity(final float opacity) {
		mOpacity = Math.max(Math.min(opacity, 1.0f), 0.0f);
	}
	
	/**
	 * Set Z
	 * 
	 * @param z
	 */
	public void setZ(final int z) {
		mZ = z;
	}

	/**
	 * Set Id
	 * 
	 * @param id
	 */
	public void setId(int id) {
		mId = id;
	}
	
	/**
	 * Get Listener
	 * @return
	 */
	final protected BaseListener getListener() {
		return mListener;
	}
	
	/**
	 * Invert in Vertical
	 * 
	 * @param inverted
	 */
	final public boolean[] getMirror() {
		return mMirror.clone();
	}
	
	/**
	 * Get Viewport
	 */
	final public Rect getViewport() {
		return mViewport;
	}
	
	/**
	 * Get Blend Func
	 * 
	 * @return Get Blend Func
	 */
	final public BlendFunc getBlendFunc() {
		return mBlendFunc;
	}
	
	/**
	 * Get Scale
	 */
	final public Vector2 getScale() {
		return mScale.clone();
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link Vector2} Position
	 */
	final public Vector2 getPosition() {
		return mPosition.clone();
	}
	
	/**
	 * Return Real Position
	 * <p>
	 * Get Position with animations modify.
	 * 
	 * @return {@link Vector2} Position
	 */
	final public Vector2 getRealPosition() {
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		Vector2 position = mPosition.clone();
		position.sum(animationSet.getPosition());
		return position;
	}
	
	/**
	 * Return draw dest Texture size.
	 * 
	 * @return {@link Vector2} Size
	 */
	final public Vector2 getSize() {
		return mSize.clone();
	}
	
	/**
	 * Get drawable opacity
	 * 
	 * @return Return drawable opacity
	 */
	final public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Get center .
	 * 
	 * @return {@link Vector2} Center
	 */
	final public Vector2 getCenter() {
		return mCenter.clone();
	}
	
	/**
	 * Get Angle.
	 * 
	 * @return {@link Vector2} Angle
	 */
	final public float getAngle() {
		return mAngle;
	}
	
	/**
	 * Get Scroll.
	 * 
	 * @return {@link Vector2} Scroll
	 */
	final public Vector2 getScroll() {
		return mScroll.clone();
	}
	
	/**
	 * Get Touchable.
	 * 
	 * @return Boolean Touchable
	 */
	final public boolean getTouchable() {
		return mTouchable;
	}
	
	/**
	 * Get Fixed Space.
	 * 
	 * @param fixed
	 *            Boolean Fixed
	 */
	final public boolean getFixedSpace() {
		return mFixedSpace;
	}
	
	/**
	 * Get Id
	 */
	@Override
	public int getId() {
		return mId;
	}
	
	/**
	 * Get Z
	 */
	@Override
	public int getZ() {
		// TODO Auto-generated method stub
		return mZ;
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
	
	/*
	 * Retorna o fator de escala
	 */
	final protected float getBaseScaleFacor(Scene scene) {
		if (!scene.hasFunc(multigear.mginterface.scene.Scene.FUNC_VIRTUAL_DPI))
			return 1f;
		return scene.getSpaceParser().getScaleFactor();
	}
	
	/*
	 * Retorna o fator de escala
	 */
	final protected float getInverseBaseScaleFacor(Scene scene) {
		if (!scene.hasFunc(multigear.mginterface.scene.Scene.FUNC_VIRTUAL_DPI))
			return 1f;
		return scene.getSpaceParser().getInverseScaleFactor();
	}
	
	
	
	/**
	 * Return Sprite pressed state.
	 * 
	 * @return Return true if Sprite pressed state.
	 */
	final public boolean isPressed() {
		return mTouchable && hasState(Widget.STATE_PRESSED);
	}
	
	/**
	 * Add a new Sprite Layer. Similar to Sprite.
	 */
	final public void addLayer(final WidgetLayer layer) {
		if(mLayers.contains(layer))
			throw new RuntimeException("This layer has already been added.");
		mLayers.add(layer);
	}
	
	/**
	 * Remove the Layer of this Object
	 * 
	 * @param layer
	 */
	final public void removeLayer(multigear.mginterface.graphics.drawable.widget.WidgetLayer layer) {
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
		final int lastState = mState;
		mState |= state;
		if(lastState != mState)
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
		if(mState == 0)
			return;
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
	 * Update Widget
	 */
	@SuppressLint("WrongCall") 
	@Override
	final public void draw(final Drawer drawer) {
		//
		onUpdate();
		
		// Prepare Animations
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Prepare Transformations
		// Top Level
		final float ox = mCenter.x * mScale.x;
		final float oy = mCenter.y * mScale.y;
		final float sx = mSize.x * mScale.x;
		final float sy = mSize.y * mScale.y;
		
		// Animation Level
		final Vector2 ascale = Vector2.scale(mScale, animationSet.getScale());
		final float aox = mCenter.x * ascale.x;
		final float aoy = mCenter.y * ascale.y;
		final float arotation = mAngle + animationSet.getRotation();
		final Vector2 atranslate = animationSet.getPosition();
		
		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();
		
		// Push Matrix
		matrixRow.push();
		
		// Order Layers
		Collections.sort(mLayers, mLayersComparatorDraw);
		
		// Opacity
		final float opacity = animationSet.getOpacity() * getOpacity();
		
		// Enable Viewport
		drawer.begin();
		drawer.snip(mViewport);
		drawer.setBlendFunc(mBlendFunc);
		
		// Transformations for onDraw()
		Matrix postTransformations = new Matrix();
		
		// Scale
		postTransformations.postScale(ascale.x, ascale.y);
					
		// Top Transformations
		postTransformations.postTranslate(-aox, -aoy);
		postTransformations.postRotate(arotation);
		postTransformations.postTranslate(aox, aoy);
		
		// Bottom Transformations
		postTransformations.postTranslate((mPosition.x - mScroll.x - aox) + atranslate.x, (mPosition.y - mScroll.y - aoy) + atranslate.y);
		
		// Enable pre transformations
		matrixRow.setPostTransformations(postTransformations);
		matrixRow.enablePostTransformationsMatrix();
		
		// Draw bottom layer
		onDraw(drawer, DrawingLayer.LAYER_BOTTOM);
		
		// Draw
		for (final WidgetLayer layer : mLayers)
			layer.draw(opacity, drawer);
		
		// Draw top layer
		onDraw(drawer, DrawingLayer.LAYER_TOP);
		
		// Disable pre transformations
		matrixRow.setPostTransformations(null);
		matrixRow.disablePostTransformationsMatrix();
		
		// End Drawer
		drawer.end();
		
		// Scale Widget
		matrixRow.postScalef(sx, sy);
						
		// Pre Rotate
		matrixRow.postTranslatef(-ox, -oy);
		matrixRow.postRotatef(mAngle);
		matrixRow.postTranslatef(ox, oy);
						
		// Translate Matrix
		final float tX = mPosition.x - mScroll.x - ox;
		final float tY = mPosition.y - mScroll.y - oy;
		matrixRow.postTranslatef(tX, tY);
		
		
		// Get Transformed Vertices
		matrixRow.swap();
		final float transformMatrix[] = new float[16];
		matrixRow.copyValues(transformMatrix);
						
		// Swap transformations
		matrixRow.swap();
						
		// Prepare Vertices Position
		refreshVerticesPosition(matrixRow);
						
		// Pop Matrix
		matrixRow.pop();
				
		
	}
	
	/*
	 * Seta a posição dos vertices para posição original
	 */
	final protected void setVerticesPosition() {
		mVertices[0] = new Vector2(0, 0);
		mVertices[1] = new Vector2(1, 0);
		mVertices[2] = new Vector2(1, 1);
		mVertices[3] = new Vector2(0, 1);
	}
	
	/*
	 * Refresh Vertices Position
	 */
	final protected void refreshVerticesPosition(final WorldMatrix matrixRow) {
		// Swap transformations and project points
		matrixRow.project(mBaseVerticeA, mResultMatrixA);
		matrixRow.project(mBaseVerticeB, mResultMatrixB);
		matrixRow.project(mBaseVerticeC, mResultMatrixC);
		matrixRow.project(mBaseVerticeD, mResultMatrixD);
		// Set Vertices
		mVertices[0] = new Vector2(mResultMatrixA[0], mResultMatrixA[1]);
		mVertices[1] = new Vector2(mResultMatrixB[0], mResultMatrixB[1]);
		mVertices[2] = new Vector2(mResultMatrixC[0], mResultMatrixC[1]);
		mVertices[3] = new Vector2(mResultMatrixD[0], mResultMatrixD[1]);
	}
	
	/**
	 * Check if point is over Sprite.
	 * 
	 * @param point
	 *            Point used for check.
	 * @return Return true if point over Sprite.
	 */
	final public boolean pointOver(final Vector2 point) {
		// Get Edges
		final float left = mVertices[0].x;
		final float top = mVertices[0].y;
		final float right = mVertices[2].x;
		final float bottom = mVertices[2].y;
		// Return result
		return (point.x >= left && point.x < right && point.y >= top && point.y < bottom);
	}
	
	/**
	 * Returns the four vertices of the edge of the sprite.
	 * 
	 * @return Pack of four vertices.
	 */
	final public Vector2[] getDesignedVerticesPosition() {
		return mVertices;
	}
	
	/**
	 * Add pointer
	 * 
	 * @param id
	 * @param touch
	 */
	final private void addPointer(final MotionEvent touch) {
		final int index = MotionEventCompat.getActionIndex(touch);
		final int id = MotionEventCompat.getPointerId(touch, index);
		final Vector2 position = new Vector2(MotionEventCompat.getX(touch, index), MotionEventCompat.getY(touch, index));
		removePointer(id);
		final Pointer pointer = new Pointer();
		pointer.id = id;
		pointer.lastPosition = position;
		pointer.framePosition = position;
		mPointers.add(pointer);
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private boolean removePointer(final MotionEvent touch) {
		final int index = MotionEventCompat.getActionIndex(touch);
		final int id = MotionEventCompat.getPointerId(touch, index);
		return removePointer(id);
	}
	
	/**
	 * Remove Pointer
	 * @param id
	 */
	final private boolean removePointer(final int id) {
		final Iterator<Pointer> iterator = mPointers.iterator();
		boolean found = false;
		while(iterator.hasNext()) {
			Pointer pointer = iterator.next();
			if(pointer.id == id) {
				iterator.remove();
				found = true;
			}
		}
		return found;
	}
	
	/**
	 * Move Pointers
	 * @param touch
	 */
	final private void movePointers(final MotionEvent touch) {
		for(int index=0; index<MotionEventCompat.getPointerCount(touch); index++) {
			final Vector2 position = new Vector2(MotionEventCompat.getX(touch, index), MotionEventCompat.getY(touch, index));
			final int id = MotionEventCompat.getPointerId(touch, index);
			for(final Pointer pointer : mPointers) {
				if(pointer.id == id) {
					pointer.lastPosition = pointer.framePosition;
					pointer.framePosition = position;
					break;
				}
			}
		}
	}
	
	/**
	 * Update Move
	 */
	final private void updateMove() {
		if(mPointers.size() > 0) {
			boolean switchFlag = false;
			final BaseListener listener = getListener();
			final Pointer major = mPointers.get(mPointers.size()-1);
			final Vector2 moved = Vector2.sub(major.framePosition, major.lastPosition);
			if (listener != null && listener instanceof SimpleListener)
				((SimpleListener) listener).onMove(this, moved);
			if (pointOver(major.framePosition)) {
				switchFlag = !hasState(STATE_IN);
				addState(STATE_IN);
			} else {
				switchFlag = hasState(STATE_IN);
				removeState(STATE_IN);
			}
			onMove(moved, switchFlag);
		} 
		for(final Pointer pointer : mPointers)
			pointer.lastPosition = pointer.framePosition;
	}
	
	/**
	 * Get Touch Event.
	 * 
	 * @param motionEvent
	 *            MotionEvent used for touch.
	 * @return Return true if handled.
	 */
	final public void touch(MotionEvent motionEvent) {
		if (!mTouchable) {
			return;
		}
		
		// Vars
		Vector2 point = null;
		int index = 0;
		final BaseListener listener = getListener();
		
		// Motion
		switch(MotionEventCompat.getActionMasked(motionEvent)) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			// Point info
			index = MotionEventCompat.getActionIndex(motionEvent);
			point = new Vector2(motionEvent.getX(index), motionEvent.getY(index));
			// accept if event in
			if (pointOver(point)) {
				addState(STATE_PRESSED);
				addState(STATE_IN);
				addPointer(motionEvent);
				//
				if(mPointers.size() == 1)
					onPress();
				onTouch(motionEvent);
				// Pass event if foucused
				if(listener != null) {
					if (mPointers.size() == 1 && listener instanceof SimpleListener)
						((SimpleListener) listener).onPress(this);
					if (listener instanceof TouchListener)
						((TouchListener) listener).onTouch(this, motionEvent);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			// Send touch event if this pointer consumed for this widget
			boolean consumed = removePointer(motionEvent);
			// If no pointers
			if(mPointers.size() == 0 && consumed) {
				// Remove pressed state but not used
				removeState(STATE_PRESSED);
				onRelease();
				// Point info
				index = MotionEventCompat.getActionIndex(motionEvent);
				point = new Vector2(motionEvent.getX(index), motionEvent.getY(index));
				if (listener != null) {
					if (listener instanceof SimpleListener)
						((SimpleListener) listener).onRelease(this);
					if (pointOver(point) && listener instanceof ClickListener) {
						((ClickListener) listener).onClick(this);
						onClick();
					}
				}
				// Remove State In
				// This state probably used in events
				removeState(STATE_IN);
			} else if(consumed) {
				// Refresh Pointer
				final Pointer major = mPointers.get(mPointers.size()-1);
				if(pointOver(major.framePosition))
					addState(STATE_IN);
				else
					removeState(STATE_IN);
			}
			// Pass Event
			if(consumed) {
				// If consumed
				onTouch(motionEvent);
				if (consumed && listener != null && listener instanceof TouchListener)
					((TouchListener) listener).onTouch(this, motionEvent);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			mPointers.clear();
			removeState(STATE_PRESSED);
			removeState(STATE_IN);
			
			onTouch(motionEvent);
			if (listener != null && listener instanceof TouchListener)
				((TouchListener) listener).onTouch(this, motionEvent);
			break;
		case MotionEvent.ACTION_MOVE:
			movePointers(motionEvent);
			updateMove();
			
			onTouch(motionEvent);
			if (listener != null && listener instanceof TouchListener)
				((TouchListener) listener).onTouch(this, motionEvent);
			break;
		default:
			onTouch(motionEvent);
			if (listener != null && listener instanceof TouchListener)
				((TouchListener) listener).onTouch(this, motionEvent);
		}
	}
	
	/**
	 * Dispose This Drawable.
	 * 
	 * This method removes all of the same updates, then this drawable be
	 * dead/frozen from the time this method is called.
	 */
	public void dispose() {}
	
	/** On Update Event */
	protected void onDraw(final Drawer drawer, final DrawingLayer drawingLayer) {}
	protected void onUpdate() {}
	
	/** On Refresh */
	protected void onRefresh() {}
	
	/** Reform */
	protected void onPress() {}
	protected void onRelease() {}
	protected void onMove(final Vector2 moved, final boolean inOutSwitch) {};
	protected void onTouch(MotionEvent motionEvent) {};
	protected void onClick() {};
	
}
