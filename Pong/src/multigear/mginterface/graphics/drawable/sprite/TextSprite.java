package multigear.mginterface.graphics.drawable.sprite;

import multigear.general.utils.KernelUtils;
import multigear.general.utils.Ref2F;
import multigear.general.utils.Vector2D;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.drawable.BaseDrawable;
import multigear.mginterface.graphics.opengl.drawer.MatrixRow;
import multigear.mginterface.graphics.opengl.font.FontDrawer;
import multigear.mginterface.graphics.opengl.font.FontMap;
import multigear.mginterface.graphics.opengl.font.FontWriter;
import multigear.mginterface.scene.Scene;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.opengl.GLES20;

/**
 * Text Sprite
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class TextSprite extends BaseDrawable {
	
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
	
	// Private Variables
	private AnimationStack mAnimationStack;
	private FontMap mFontMap;
	private String mText = "";
	private Ref2F mScale = new Ref2F(1, 1);
	private Ref2F mPosition = KernelUtils.ref2d(0, 0);
	private Ref2F mCenter = KernelUtils.ref2d(0, 0);
	private Ref2F mScroll = KernelUtils.ref2d(0, 0);
	private float mAngle = 0;
	private boolean mTouchable = true;
	private boolean mFixedSpace = false;
	private boolean mInverted[] = { false, false };
	private Rect mViewport;
	private FontWriter mFontWriter = new FontWriter() {
		
		// Final private Variable
		final private Ref2F mDefaultDrawPos = new Ref2F(0, 0);
		
		/**
		 * Default Draw
		 */
		@Override
		public void onDraw(FontDrawer fontDrawer, String text) {
			fontDrawer.drawText(text, mDefaultDrawPos);
		}
	};
	
	/**
	 * Constructor
	 */
	public TextSprite(final Scene scene) {
		super(scene);
		mAnimationStack = new AnimationStack(scene);
		mVertices = new Ref2F[4];
	}
	
	/**
	 * Set Text
	 * @param text
	 */
	final public void setText(final String text) {
		mText = text;
	}
	
	/**
	 * Set FontMap
	 * 
	 * @param texture
	 *            {@link FontMap}
	 */
	final public void setFontMap(final FontMap fontMap) {
		mFontMap = fontMap;
	}
	
	/**
	 * Set Font Writer
	 * 
	 * @param fontWriter
	 */
	final public void setFontWriter(final FontWriter fontWriter) {
		mFontWriter = fontWriter;
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
	 * Set center axis.
	 * 
	 * @param center
	 *            {@link Ref2F} Center
	 */
	final public void setCenter(final Ref2F center) {
		mCenter = center;
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
		mScroll = scroll;
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
	 * Get Font Writer
	 * 
	 * @param fontWriter
	 */
	final public FontWriter getFontWriter() {
		return mFontWriter;
	}
	
	/**
	 * Get Text
	 * @param text
	 */
	final public String getText() {
		return mText;
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
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		Ref2F position = mPosition.clone();
		position.add(animationSet.getPosition());
		return position;
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
	
	/**
	 * Get Animation Stack
	 * 
	 * @return animationStack {@link AnimationStack}
	 */
	final public AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Get FontMap
	 * 
	 * @param texture
	 *            {@link FontMap}
	 */
	final public FontMap getFontMap() {
		return mFontMap;
	}
	
	/*
	 * Retorna o fator de escala
	 */
	final protected float getInverseBaseScaleFacor() {
		if (!getAttachedRoom().hasFunc(multigear.mginterface.scene.Scene.FUNC_VIRTUAL_DPI))
			return 1f;
		return getAttachedRoom().getSpaceParser().getInverseScaleFactor();
	}
	
	/**
	 * Update Sprite. obs(If it is created in a ROM, it will update
	 * altomatically on onUpdate() event.)
	 */
	@SuppressLint("WrongCall") @Override
	final public void updateAndDraw(final multigear.mginterface.graphics.opengl.drawer.Drawer drawer, final float preOpacity) {
		
		//
		if(mText.length() == 0)
			return;
		
		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Get final Opacity
		final float opacity = preOpacity * animationSet.getOpacity() * getOpacity();
		
		// Not Update
		if (mFontMap == null || opacity <= 0)
			return;
		
		// Get Infos
		final Ref2F scale = mScale.clone().mul(animationSet.getScale());
		final float ox = mCenter.XAxis * scale.XAxis;
		final float oy = mCenter.YAxis * scale.YAxis;
		float sx = scale.XAxis;
		float sy = scale.YAxis;
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
		
		// Draw Text
		drawer.drawText(mFontMap, mFontWriter, mText, opacity);
		
		// Disable Scissor
		if (disableScissor)
			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		
		// Pop Matrix
		matrixRow.pop();
		
		onUpdate();
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
	
	
	/** Update your Objects */
	public void onUpdate() {
	};
	
}
