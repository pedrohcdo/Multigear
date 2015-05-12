package multigear.mginterface.graphics.drawable.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Line2;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.opengl.BlendFunc;
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
import android.graphics.RectF;
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
			for (int i=0; i<mSkinValues.size(); i++) {
				final SkinValue skinValue = mSkinValues.get(i);
				if (skinValue.ID == id)
					return skinValue.Texture;
			}
			return null;
		}
	}
	
	/**
	 * Comparador utilisado para ordenamento de sobreposição para todos Layers
	 * para fins de Desenho.
	 */
	final private Comparator<Component> mLayersComparatorDraw = new Comparator<Component>() {
		
		/*
		 * Comparador
		 */
		@Override
		public int compare(Component lhs, Component rhs) {
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
	final private float mFinalTransformation[] = new float[] {0, 0, 0, 0, 0, 0, 0, 0, 1};
	
	// Private Variables
	private BaseListener mListener;
	protected Rect mViewport;
		
	// Public Variables
	protected Vector2 mScale = new Vector2(1, 1);
	protected Vector2 mPosition = new Vector2(0, 0);
	protected Vector2 mSize = new Vector2(32, 32);
	protected Vector2 mCenter = new Vector2(0, 0);
	protected float mAngle = 0;
	protected float mOpacity = 1;
	protected boolean mTouchable = true;
	protected boolean mFixedSpace = false;
	protected boolean mMirror[] = { false, false };
	protected int mZ, mId;
	protected BlendFunc mBlendFunc = BlendFunc.ONE_MINUS_SRC_ALPHA;
	private boolean mStaticTouch = false;
	
	// Final Public Variables
	final public Skin Skin = new Skin();
	
	// Constants
	final static public int STATE_PRESSED = 0x1;
	final static public int STATE_IN = 0x2;
	
	// Final Private Variables
	final private List<Component> mComponents;
	
	// Private Variables
	private int mState;
	private AnimationStack mAnimationStack;
	
	/**
	 * Constructor
	 */
	public Widget() {
		mState = 0;
		mComponents = new ArrayList<Component>();
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
	final public void setMirror(final boolean mirrorX, final boolean mirrorY) {
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
	public void setSize(final Vector2 size) {
		mSize = size.clone();
	}
	
	/**
	 * Set center .
	 * 
	 * @param center
	 *            {@link Vector2} Center
	 */
	public void setCenter(final Vector2 center) {
		mCenter = center.clone();
	}
	
	/**
	 * Set Angle.
	 * 
	 * @param angle
	 *            {@link Vector2} Angle
	 */
	public void setAngle(final float angle) {
		mAngle = angle;
	}

	
	/**
	 * Set Touchable.
	 * 
	 * @param touchable
	 *            Boolean Touchable
	 */
	public void setTouchable(final boolean touchable) {
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
	public void setFixedSpace(final boolean fixed) {
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
	 * 
	 * @param staticTouch
	 */
	public void setStaticTouch(final boolean staticTouch) {
		mStaticTouch = staticTouch;
	}
	
	/**
	 * Get Listener
	 * @return
	 */
	protected BaseListener getListener() {
		return mListener;
	}
	
	/**
	 * Invert in Vertical
	 * 
	 * @param inverted
	 */
	public boolean[] getMirror() {
		return mMirror.clone();
	}
	
	/**
	 * Get Viewport
	 */
	public Rect getViewport() {
		return mViewport;
	}
	
	/**
	 * Get Blend Func
	 * 
	 * @return Get Blend Func
	 */
	public BlendFunc getBlendFunc() {
		return mBlendFunc;
	}
	
	/**
	 * Get Scale
	 */
	public Vector2 getScale() {
		return mScale.clone();
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link Vector2} Position
	 */
	public Vector2 getPosition() {
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
	public Vector2 getSize() {
		return mSize.clone();
	}
	
	/**
	 * Get drawable opacity
	 * 
	 * @return Return drawable opacity
	 */
	public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Get center .
	 * 
	 * @return {@link Vector2} Center
	 */
	public Vector2 getCenter() {
		return mCenter.clone();
	}
	
	/**
	 * Get Angle.
	 * 
	 * @return {@link Vector2} Angle
	 */
	public float getAngle() {
		return mAngle;
	}
	
	/**
	 * Get Touchable.
	 * 
	 * @return Boolean Touchable
	 */
	public boolean getTouchable() {
		return mTouchable;
	}
	
	/**
	 * Get Fixed Space.
	 * 
	 * @param fixed
	 *            Boolean Fixed
	 */
	public boolean getFixedSpace() {
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
	 * Get state of Static Touch
	 * @return True/False
	 */
	public boolean getStaticTouch() {
		return mStaticTouch;
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
	final public void addComponent(final Component component) {
		if(mComponents.contains(component))
			throw new RuntimeException("This component has already been added.");
		mComponents.add(component);
	}
	
	/**
	 * Remove the Layer of this Object
	 * 
	 * @param component
	 */
	final public void removeComponent(final Component component) {
		mComponents.remove(component);
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
		
		// Prepare Animations
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Opacity
		final float opacity = animationSet.getOpacity() * getOpacity();
		
		// Invisible
		if(opacity <= 0)
			return;
				
		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();
		
		// Put new Transformation
		matrixRow.push();
		
		// If static touch
		if(mStaticTouch) {
			// Static Transform and copy
			matrixRow.push();
				staticTransform(matrixRow);
				matrixRow.swap();
				refreshVerticesPosition(matrixRow);
			matrixRow.pop();
			// Normal Transform
			normalTransform(matrixRow, animationSet);
		} else {
			// Normal transform and copy
			normalTransform(matrixRow, animationSet);
			matrixRow.swap();
			refreshVerticesPosition(matrixRow);
		}

		
				
		//
		onUpdate();
		
		
		
		// Order Layers
		Collections.sort(mComponents, mLayersComparatorDraw);
		
		
		// Enable Viewport
		drawer.begin();
		drawer.snip(mViewport);
		drawer.setBlendFunc(mBlendFunc);
		drawer.setOpacity(opacity);
		
		// Draw bottom component
		onDraw(drawer, DrawingLayer.LAYER_BOTTOM);
		
		// Draw
		for (int i=0; i<mComponents.size(); i++) {
			final Component component = mComponents.get(i);
			if(component instanceof Drawable)
				((Drawable)component).draw(drawer);
		}
		
		// Draw top component
		onDraw(drawer, DrawingLayer.LAYER_TOP);
		
		// End Drawer
		drawer.end();
		
		// Release transformation
		matrixRow.pop();
	}
	
	/**
	 * Normal Transformation
	 * @param drawer
	 */
	final protected void normalTransform(final WorldMatrix matrixRow, final AnimationSet animationSet ) {
		



		
		
		// Translate and Rotate Matrix with correction
		
		
		// Animation Level
		final Vector2 scale = Vector2.scale(mScale, animationSet.getScale());
		final float rotation = mAngle + animationSet.getRotation();
		final Vector2 translate = Vector2.sum(mPosition, animationSet.getPosition());
		
		// Variables
		float six = mCenter.x * scale.x;
		float siy = mCenter.y * scale.y;
		float mx = 1;
		float my = 1;
		float mtx = 0;
		float mty = 0;
		
		if (mMirror[0]) {
			mx = -1;
			mtx = mSize.x;
		}
		
		if (mMirror[1]) {
			my = -1;
			mty = mSize.y;
		}
		
		// Final Transformation
		// pre = M * other
		// M Transform "other" and not "other" transform M because "other" 
		// Calculate without M informations

		float rad = (float) GeneralUtils.degreeToRad(rotation);
		float c = (float) Math.cos(rad);
		float s = (float) Math.sin(rad);
		mFinalTransformation[0] = c * scale.x * mx;
		mFinalTransformation[1] = -s * scale.y * my;
		mFinalTransformation[2] = c * (scale.x * mtx - six) + -s * (scale.y * mty - siy) + translate.x;
		mFinalTransformation[3] = s * scale.x * mx;
		mFinalTransformation[4] = c * scale.y * my;
		mFinalTransformation[5] = s * (scale.x * mtx - six) + c * (scale.y * mty - siy) + translate.y;
		
		matrixRow.preConcatf(mFinalTransformation);
	}
	
	/**
	 * Static Transformation
	 * @param matrixRow
	 */
	final private void staticTransform(final WorldMatrix matrixRow) {
		// Prepare Transformations
		final float ox = mCenter.x * mScale.x;
		final float oy = mCenter.y * mScale.y;
		final float sx = mScale.x;
		final float sy = mScale.y;
				
		// Final Transformation
		double rad = GeneralUtils.degreeToRad(mAngle);
		float c = (float) Math.cos(-rad);
		float s = (float) Math.sin(-rad);
		mFinalTransformation[0] = c * sx;
		mFinalTransformation[1] = -s * sy;
		mFinalTransformation[2] = c * -ox + -s * -oy + mPosition.x;
		mFinalTransformation[3] = s * sx;
		mFinalTransformation[4] = c * sy;
		mFinalTransformation[5] = s * -ox + c * -oy + mPosition.y;
		
		// Transform
		matrixRow.preConcatf(mFinalTransformation);
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
		// Set Size
		mBaseVerticeB[0] = mSize.x;
		mBaseVerticeC[0] = mSize.x;
		mBaseVerticeC[1] = mSize.y;
		mBaseVerticeD[1] = mSize.y;
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
		// Get distances
		Line2 left = new Line2(mVertices[0], mVertices[3]);
		Line2 top = new Line2(mVertices[1], mVertices[0]);
		Line2 right = new Line2(mVertices[2], mVertices[1]);
		Line2 bottom = new Line2(mVertices[3], mVertices[2]);
		float distance_left = left.distanceToPoint(point);
		float distance_top = top.distanceToPoint(point);
		float distance_right = right.distanceToPoint(point);
		float distance_bottom = bottom.distanceToPoint(point);
		// Return result
		return !(distance_left < 0 || distance_top < 0 || distance_right < 0 || distance_bottom < 0);
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
	 * Returns the four vertices of the edge of the sprite.
	 * 
	 * @return Pack of four vertices.
	 */
	final public RectF getDesignedRect() {
		return new RectF(mVertices[0].x, mVertices[0].y, mVertices[2].x, mVertices[2].y);
	}
	
	/**
	 * Transform position into widget
	 * @param position
	 * @return
	 */
	final public Vector2 transformPosition(final Vector2 position) {
		
		Line2 left = new Line2(mVertices[0], mVertices[3]);
		Line2 top = new Line2(mVertices[1], mVertices[0]);
		Line2 right = new Line2(mVertices[2], mVertices[1]);
		Line2 bottom = new Line2(mVertices[3], mVertices[2]);
		
		float distance_left = left.distanceToPoint(position);
		float distance_top = top.distanceToPoint(position);
		float distance_right = right.distanceToPoint(position);
		float distance_bottom = bottom.distanceToPoint(position);
		
		
		float x = (distance_left / (distance_left + distance_right)) * getSize().x;
		float y = (distance_top / (distance_top + distance_bottom)) * getSize().y;
		
		return new Vector2(x, y);
	}
	
	/**
	 * Transform position into widget
	 * @param position
	 * @return
	 */
	final public Vector2 transformVector(Vector2 position) {
		position = new Vector2(position.x+mVertices[0].x, position.y+mVertices[0].y);
		Line2 left = new Line2(mVertices[0], mVertices[3]);
		Line2 top = new Line2(mVertices[1], mVertices[0]);
		Line2 right = new Line2(mVertices[2], mVertices[1]);
		Line2 bottom = new Line2(mVertices[3], mVertices[2]);
		float distance_left = left.distanceToPoint(position);
		float distance_top = top.distanceToPoint(position);
		float distance_right = right.distanceToPoint(position);
		float distance_bottom = bottom.distanceToPoint(position);
		float x = (distance_left / (distance_left + distance_right)) * getSize().x;
		float y = (distance_top / (distance_top + distance_bottom)) * getSize().y;
		return new Vector2(x, y);
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
	final public boolean touch(MotionEvent motionEvent) {
		if (!mTouchable) {
			return false;
		}
		
		// Order Components
		Collections.sort(mComponents, mLayersComparatorDraw);
		
		// 
		for(int i=0; i<mComponents.size(); i++) {
			final Component component = mComponents.get(i);
			if(component instanceof Touchable) {
				if(((Touchable)component).touch(motionEvent))
					return true;
			}
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
				// Consumed
				return true;
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
				// Consumed
				return true;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			mPointers.clear();
			
			removeState(STATE_PRESSED);
			
			onRelease();
			
			
			removeState(STATE_IN);
			
			onTouch(motionEvent);
			if (listener != null && listener instanceof TouchListener)
				((TouchListener) listener).onTouch(this, motionEvent);
			// for all consume
			return false;
		case MotionEvent.ACTION_MOVE:
			movePointers(motionEvent);
			updateMove();
			
			onTouch(motionEvent);
			if (listener != null && listener instanceof TouchListener)
				((TouchListener) listener).onTouch(this, motionEvent);
			
			// for all consume
			return false;
			
		default:
			onTouch(motionEvent);
			if (listener != null && listener instanceof TouchListener)
				((TouchListener) listener).onTouch(this, motionEvent);
		}
		
		// for all consume
		return false;
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
