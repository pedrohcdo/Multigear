package multigear.mginterface.graphics.drawable;

import multigear.general.utils.KernelUtils;
import multigear.general.utils.Ref2F;
import multigear.general.utils.Vector2D;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.MatrixRow;
import multigear.mginterface.graphics.opengl.texture.Texture;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.view.MotionEvent;

public abstract class SimpleDrawable extends BaseDrawable {
	
	// Final Private Variables
	final private Ref2F[] mVertices;
	final private float mResultMatrixA[] = new float[4];
	final private float mResultMatrixB[] = new float[4];
	final private float mResultMatrixC[] = new float[4];
	final private float mResultMatrixD[] = new float[4];
	final private float mBaseVerticeA[] = new float[] { 0, 0, 0, 1 };
	final private float mBaseVerticeB[] = new float[] { 1, 0, 0, 1 };
	final private float mBaseVerticeC[] = new float[] { 1, 1, 0, 1 };
	final private float mBaseVerticeD[] = new float[] { 0, 1, 0, 1 };
	final private multigear.mginterface.graphics.animations.AnimationStack mDefaultAnimationStack;
	
	// Private Variables
	private multigear.mginterface.graphics.drawable.BaseListener mListener;
	private boolean mTouchHandled;
	private Ref2F mHandledPosition;
	private multigear.mginterface.graphics.opengl.texture.Loader mTextureLoader;
	protected Rect mViewport;
	
	
	// Gambi
	Drawer mDrawer;
	
	// Public Variables
	protected Ref2F mScale = new Ref2F(1, 1);
	protected Ref2F mPosition = KernelUtils.ref2d(0, 0);
	protected Ref2F mSize = KernelUtils.ref2d(32, 32);
	protected Ref2F mCenter = KernelUtils.ref2d(0, 0);
	protected Ref2F mScroll = KernelUtils.ref2d(0, 0);
	protected float mAngle = 0;
	protected boolean mTouchable = true;
	protected boolean mFixedSpace = false;
	protected boolean mInverted[] = { false, false };
	
	/**
	 * Constructor
	 */
	public SimpleDrawable(final multigear.mginterface.scene.Scene room) {
		super(room);
		mListener = null;
		mTouchHandled = false;
		mHandledPosition = KernelUtils.ref2d(0, 0);
		mVertices = new Ref2F[4];
		mDefaultAnimationStack = new multigear.mginterface.graphics.animations.AnimationStack(room);
		mViewport = null;
		mTextureLoader = room.getTextureLoader();
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
	 * Return Texture Loader
	 */
	final protected multigear.mginterface.graphics.opengl.texture.Loader getTextureLoader() {
		return mTextureLoader;
	}
	
	/**
	 * Retorna a pilha de animações
	 * 
	 * @return
	 */
	protected multigear.mginterface.graphics.animations.AnimationStack getImplAnimationStack() {
		return mDefaultAnimationStack;
	}
	
	/**
	 * Set a listener. Listener used for send Touch Events.
	 * 
	 * @param listener
	 *            Used Listener.
	 */
	final public void setListener(final multigear.mginterface.graphics.drawable.BaseListener listener) {
		mListener = listener;
	}
	
	/*
	 * Retorna o listener
	 */
	final protected multigear.mginterface.graphics.drawable.BaseListener getListener() {
		return mListener;
	}
	
	/**
	 * Invert in Vertical
	 * 
	 * @param inverted
	 */
	final public void setVerticalInverted(boolean inverted) {
		mInverted[0] = true;
	}
	
	/**
	 * Invert in Horizontal
	 * 
	 * @param inverted
	 */
	final public void setHorizontalInverted(boolean inverted) {
		mInverted[1] = true;
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final Ref2F scale) {
		mScale = scale.clone();
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final float scaleX, final float scaleY) {
		mScale = new Ref2F(scaleX, scaleY);
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final float scale) {
		mScale = new Ref2F(scale, scale);
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link Ref2F} Position
	 */
	final public void setPosition(final Ref2F position) {
		mPosition = position.clone();
	}
	
	/**
	 * Set draw dest texture size.
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setSize(final Ref2F size) {
		mSize = size.clone();
	}
	
	/**
	 * Set center axis.
	 * 
	 * @param center
	 *            {@link Ref2F} Center
	 */
	final public void setCenter(final Ref2F center) {
		mCenter = center.clone();
	}
	
	/**
	 * Set Angle.
	 * 
	 * @param angle
	 *            {@link Vector2D} Angle
	 */
	final public void setAngle(final float angle) {
		mAngle = angle;
	}
	
	/**
	 * Set Scroll.
	 * 
	 * @param center
	 *            {@link Ref2F} Scroll
	 */
	final public void setScroll(final Ref2F scroll) {
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
	 * Invert in Vertical
	 * 
	 * @param inverted
	 */
	final public boolean getVerticalInverted() {
		return mInverted[0];
	}
	
	/**
	 * Invert in Horizontal
	 * 
	 * @param inverted
	 */
	final public boolean getHorizontalInverted() {
		return mInverted[1];
	}
	
	/**
	 * Get Viewport
	 */
	final public Rect getViewport() {
		return mViewport;
	}
	
	/**
	 * Get Scale
	 */
	final public Ref2F getScale() {
		return mScale.clone();
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link Ref2F} Position
	 */
	final public Ref2F getPosition() {
		return mPosition.clone();
	}
	
	/**
	 * Return Real Position
	 * <p>
	 * Get Position with animations modify.
	 * 
	 * @return {@link Ref2F} Position
	 */
	final public Ref2F getRealPosition() {
		final AnimationSet animationSet = getImplAnimationStack().prepareAnimation().animate();
		Ref2F position = mPosition.clone();
		position.add(animationSet.getPosition());
		return position;
	}
	
	/**
	 * Return draw dest Texture size.
	 * 
	 * @return {@link Ref2F} Size
	 */
	final public Ref2F getSize() {
		return mSize.clone();
	}
	
	/**
	 * Get center axis.
	 * 
	 * @return {@link Ref2F} Center
	 */
	final public Ref2F getCenter() {
		return mCenter.clone();
	}
	
	/**
	 * Get Angle.
	 * 
	 * @return {@link Vector2D} Angle
	 */
	final public float getAngle() {
		return mAngle;
	}
	
	/**
	 * Get Scroll.
	 * 
	 * @return {@link Ref2F} Scroll
	 */
	final public Ref2F getScroll() {
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
	
	/** Update */
	public void updateAndDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer, final float preOpacity) {
	}
	
	/*
	 * Retorna o fator de escala
	 */
	final protected float getBaseScaleFacor() {
		if (!getAttachedRoom().hasFunc(multigear.mginterface.scene.Scene.FUNC_VIRTUAL_DPI))
			return 1f;
		return getAttachedRoom().getSpaceParser().getScaleFactor();
	}
	
	/*
	 * Retorna o fator de escala
	 */
	final protected float getInverseBaseScaleFacor() {
		if (!getAttachedRoom().hasFunc(multigear.mginterface.scene.Scene.FUNC_VIRTUAL_DPI))
			return 1f;
		return getAttachedRoom().getSpaceParser().getInverseScaleFactor();
	}
	
	/*
	 * Prepara para desenho. Utiliza AnimationStack.
	 */
	protected void updateAndDraw(final Texture staticTexture, final Drawer drawer, final float preOpacity) {
		
		mDrawer = drawer;
		
		// Prepare Animation
		final AnimationSet animationSet = getImplAnimationStack().prepareAnimation().animate();
		
		// Get final Opacity
		final float opacity = preOpacity * animationSet.getOpacity() * getOpacity();
		
		// Get Original texture
		Texture usedTexture = staticTexture;
		
		// Animate texture
		final multigear.mginterface.graphics.opengl.texture.Texture animateTexture = animationSet.getTexture();
		if (animateTexture != null)
			usedTexture = animateTexture;
		
		// Not Update
		if (usedTexture == null || opacity <= 0)
			return;
		
		// Get Infos
		final Ref2F scale = mScale.clone().mul(animationSet.getScale());
		final float ox = mCenter.XAxis * scale.XAxis;
		final float oy = mCenter.YAxis * scale.YAxis;
		float sx = mSize.XAxis * scale.XAxis;
		float sy = mSize.YAxis * scale.YAxis;
		float six = 0;
		float siy = 0;
		if (mInverted[0]) {
			siy = sy;
			sy *= -1;
		}
		if (mInverted[1]) {
			six = sx;
			sx *= -1;
		}
		
		// Get Matrix Row
		final MatrixRow matrixRow = drawer.getMatrixRow();
		
		// Push Matrix
		matrixRow.push();
		
		// Scale Matrix
		matrixRow.postScalef(sx, sy);
		
		// Correct reflect
		matrixRow.postTranslatef(six, siy);
		
		// Translate and Rotate Matrix
		matrixRow.postTranslatef(-ox, -oy);
		matrixRow.postRotatef(mAngle + animationSet.getRotation());
		matrixRow.postTranslatef(ox, oy);
		
		// Translate Matrix
		final Ref2F translate = animationSet.getPosition();
		final float tX = (mPosition.XAxis - mScroll.XAxis - ox) + translate.XAxis;
		final float tY = (mPosition.YAxis - mScroll.YAxis - oy) + translate.YAxis;
		matrixRow.postTranslatef(tX, tY);
		
		// Invert Scale Factor
		if (mFixedSpace) {
			final float scaleFactor = getInverseBaseScaleFacor();
			// Scale Space
			matrixRow.postScalef(scaleFactor, scaleFactor);
		}
		
		// Get Transformation Matrix
		final float transformMatrix[] = new float[16];
		matrixRow.copyValues(transformMatrix);
		
		// Prepare Vertices Position
		refreshVerticesPosition(transformMatrix);
		
		// Disable Scissor
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
		
		// Draw
		drawer.drawTexture(usedTexture, mSize, opacity);
		
		// Disable Scissor
		if (disableScissor)
			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		
		// Pop Matrix
		matrixRow.pop();
	}
	
	final public void refreshPosition() {
		// Prepare Animation
		final AnimationSet animationSet = getImplAnimationStack().prepareAnimation().animate();
		
		
		// Get Infos
		final Ref2F scale = mScale.clone().mul(animationSet.getScale());
		final float ox = mCenter.XAxis * scale.XAxis;
		final float oy = mCenter.YAxis * scale.YAxis;
		float sx = mSize.XAxis * scale.XAxis;
		float sy = mSize.YAxis * scale.YAxis;
		float six = 0;
		float siy = 0;
		if (mInverted[0]) {
			siy = sy;
			sy *= -1;
		}
		if (mInverted[1]) {
			six = sx;
			sx *= -1;
		}
		
		// Get Matrix Row
		final MatrixRow matrixRow = mDrawer.getMatrixRow();
		
		// Push Matrix
		matrixRow.push();
		
		// Scale Matrix
		matrixRow.postScalef(sx, sy);
		
		// Correct reflect
		matrixRow.postTranslatef(six, siy);
		
		// Translate and Rotate Matrix
		matrixRow.postTranslatef(-ox, -oy);
		matrixRow.postRotatef(mAngle + animationSet.getRotation());
		matrixRow.postTranslatef(ox, oy);
		
		// Translate Matrix
		final Ref2F translate = animationSet.getPosition();
		final float tX = (mPosition.XAxis - mScroll.XAxis - ox) + translate.XAxis;
		final float tY = (mPosition.YAxis - mScroll.YAxis - oy) + translate.YAxis;
		matrixRow.postTranslatef(tX, tY);
		
		// Invert Scale Factor
		if (mFixedSpace) {
			final float scaleFactor = getInverseBaseScaleFacor();
			// Scale Space
			matrixRow.postScalef(scaleFactor, scaleFactor);
		}
		
		// Get Transformation Matrix
		final float transformMatrix[] = new float[16];
		matrixRow.copyValues(transformMatrix);
		
		// Prepare Vertices Position
		refreshVerticesPosition(transformMatrix);
		
		// Pop Matrix
		matrixRow.pop();
	}
	
	
	/*
	 * Seta a posição dos vertices para posição original
	 */
	final protected void setVerticesPosition() {
		mVertices[0] = KernelUtils.ref2d(0, 0);
		mVertices[1] = KernelUtils.ref2d(1, 0);
		mVertices[2] = KernelUtils.ref2d(1, 1);
		mVertices[3] = KernelUtils.ref2d(0, 1);
	}
	
	/*
	 * Refresh Vertices Position
	 */
	final protected void refreshVerticesPosition(final float[] transformMatrix) {
		// Transform Vertices
		android.opengl.Matrix.multiplyMV(mResultMatrixA, 0, transformMatrix, 0, mBaseVerticeA, 0);
		android.opengl.Matrix.multiplyMV(mResultMatrixB, 0, transformMatrix, 0, mBaseVerticeB, 0);
		android.opengl.Matrix.multiplyMV(mResultMatrixC, 0, transformMatrix, 0, mBaseVerticeC, 0);
		android.opengl.Matrix.multiplyMV(mResultMatrixD, 0, transformMatrix, 0, mBaseVerticeD, 0);
		// Set Vertices
		mVertices[0] = KernelUtils.ref2d(mResultMatrixA[0], mResultMatrixA[1]);
		mVertices[1] = KernelUtils.ref2d(mResultMatrixB[0], mResultMatrixB[1]);
		mVertices[2] = KernelUtils.ref2d(mResultMatrixC[0], mResultMatrixC[1]);
		mVertices[3] = KernelUtils.ref2d(mResultMatrixD[0], mResultMatrixD[1]);
	}
	
	/**
	 * Check if point is over Sprite.
	 * 
	 * @param point
	 *            Point used for check.
	 * @return Return true if point over Sprite.
	 */
	final public boolean pointOver(final Ref2F point) {
		// Get Edges
		final float left = mVertices[0].XAxis;
		final float top = mVertices[0].YAxis;
		final float right = mVertices[2].XAxis;
		final float bottom = mVertices[2].YAxis;
		// Return result
		return (point.XAxis >= left && point.XAxis < right && point.YAxis >= top && point.YAxis < bottom);
	}
	
	/**
	 * Returns the four vertices of the edge of the sprite.
	 * 
	 * @return Pack of four vertices.
	 */
	final public Ref2F[] getDesignedVerticesPosition() {
		return mVertices;
	}
	
	/**
	 * Get Touch Event.
	 * 
	 * @param motionEvent
	 *            MotionEvent used for touch.
	 * @return Return true if handled.
	 */
	public void touch(final MotionEvent motionEvent) {
		if (!mTouchable) {
			mTouchHandled = false;
			return;
		}
		if (mListener != null) {
			Ref2F point = null;
			switch (motionEvent.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					point = KernelUtils.ref2d(motionEvent.getX(), motionEvent.getY());
					if (pointOver(point)) {
						mTouchHandled = true;
						if (mListener instanceof multigear.mginterface.graphics.drawable.SimpleListener)
							((multigear.mginterface.graphics.drawable.SimpleListener) mListener).onPress(this);
						mHandledPosition = point;
						if (mListener instanceof multigear.mginterface.graphics.drawable.TouchListener)
							((multigear.mginterface.graphics.drawable.TouchListener) mListener).onTouch(this, motionEvent);
						return;
					}
					break;
				case MotionEvent.ACTION_CANCEL:
					if (mTouchHandled) {
						mTouchHandled = false;
						if (mListener instanceof multigear.mginterface.graphics.drawable.TouchListener)
							((multigear.mginterface.graphics.drawable.TouchListener) mListener).onTouch(this, motionEvent);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mTouchHandled) {
						if (mListener instanceof multigear.mginterface.graphics.drawable.SimpleListener)
							((multigear.mginterface.graphics.drawable.SimpleListener) mListener).onRelease(this);
						mTouchHandled = false;
						point = KernelUtils.ref2d(motionEvent.getX(), motionEvent.getY());
						if (pointOver(point) && mListener instanceof multigear.mginterface.graphics.drawable.ClickListener)
							((multigear.mginterface.graphics.drawable.ClickListener) mListener).onClick(this);
						if (mListener instanceof multigear.mginterface.graphics.drawable.TouchListener)
							((multigear.mginterface.graphics.drawable.TouchListener) mListener).onTouch(this, motionEvent);
						return;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					point = KernelUtils.ref2d(motionEvent.getX(), motionEvent.getY());
					final float diffX = point.XAxis - mHandledPosition.XAxis;
					final float diffY = point.YAxis - mHandledPosition.YAxis;
					final float scaleFactor = getBaseScaleFacor();
					final Ref2F moved = KernelUtils.ref2d(diffX / scaleFactor, diffY / scaleFactor);
					if (mTouchHandled) {
						if (mListener instanceof multigear.mginterface.graphics.drawable.SimpleListener)
							((multigear.mginterface.graphics.drawable.SimpleListener) mListener).onMove(this, moved);
						mHandledPosition = point;
						if (mListener instanceof multigear.mginterface.graphics.drawable.TouchListener)
							((multigear.mginterface.graphics.drawable.TouchListener) mListener).onTouch(this, motionEvent);
						return;
					}
					break;
			}
		}
	}
	
	/**
	 * Return Sprite pressed state.
	 * 
	 * @return Return true if Sprite pressed state.
	 */
	final public boolean isPressed() {
		return mTouchHandled && mTouchable;
	}
	
	/**
	 * Dispose This Drawable.
	 * 
	 * This method removes all of the same updates, then this drawable be
	 * dead/frozen from the time this method is called.
	 */
	public void dispose() {
		getAttachedRoom().disposeDrawable(this);
	}
}
