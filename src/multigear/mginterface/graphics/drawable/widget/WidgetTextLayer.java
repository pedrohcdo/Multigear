package multigear.mginterface.graphics.drawable.widget;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.font.FontDrawer;
import multigear.mginterface.graphics.opengl.font.FontMap;
import multigear.mginterface.graphics.opengl.font.FontWriter;
import android.graphics.Rect;

/**
 * WidgetLayer
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class WidgetTextLayer extends WidgetLayer {
	

	// For Draw
	private float mPreparedOpacity;
	
	// Private Variables
	private AnimationStack mAnimationStack;
	private FontMap mFontMap;
	private String mText = "";
	private Vector2 mScale = new Vector2(1, 1);
	private Vector2 mPosition = new Vector2(0, 0);
	private Vector2 mCenter = new Vector2(0, 0);
	private Vector2 mScroll = new Vector2(0, 0);
	private float mOpacity = 1.0f;
	private float mAngle = 0;
	private boolean mTouchable = true;
	private boolean mFixedSpace = false;
	private boolean mInverted[] = { false, false };
	private Rect mViewport;
	private FontWriter mFontWriter = new FontWriter() {
		
		// Final private Variable
		final private Vector2 mDefaultDrawPos = new Vector2(0, 0);
		
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
	 * @param scene
	 */
	public WidgetTextLayer() {
		mAnimationStack = new AnimationStack();
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
	 * Set center axis.
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
	 * Set Opacity
	 * 
	 * @param Int
	 *            Opacity {0-255}
	 */
	final public void setOpacity(final float opacity) {
		mOpacity = Math.max(Math.min(opacity, 1.0f), 0.0f);
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
	 * Get center axis.
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
	 * Get Opacity
	 */
	final public float getOpacity() {
		return mOpacity;
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
	
	/**
	 * Set Matrix Transformations for this Layer
	 * <p>
	 * 
	 * @param matrixRow
	 *            MatrixRow
	 * @return True if need Draw
	 */
	final protected boolean beginDraw(final float preOpacity, final Drawer drawer) {
		//
		if(mText.length() == 0)
			return false;
		
		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Get final Opacity
		mPreparedOpacity = preOpacity * animationSet.getOpacity() * mOpacity;
		
		// Not Update
		if (mFontMap == null || mPreparedOpacity <= 0)
			return false;

		// Get Infos
		final Vector2 scale = Vector2.scale(mScale, animationSet.getScale());
		final float ox = mCenter.x * scale.x;
		final float oy = mCenter.y * scale.y;
		final float sx = scale.x;
		final float sy = scale.y;
		
		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();
		
		// Push Matrix
		matrixRow.push();
		
		// Scale Matrix
		matrixRow.postScalef(sx, sy);
		
		// Translate and Rotate Matrix
		matrixRow.postTranslatef(-ox, -oy);
		matrixRow.postRotatef(mAngle + animationSet.getRotation());
		matrixRow.postTranslatef(ox, oy);
		
		// Translate Matrix
		final Vector2 translate = animationSet.getPosition();
		final float tX = (mPosition.x - mScroll.x - ox) + translate.x;
		final float tY = (mPosition.y - mScroll.y - oy) + translate.y;
		matrixRow.postTranslatef(tX, tY);

		return true;
		
	}
	
	/*
	 * Atualiza e Desenha
	 */
	protected void endDraw(final Drawer drawer) {
		// Prepare Drawer
		drawer.setOpacity(mPreparedOpacity);
		
		// Draw
		drawer.drawText(mFontMap, mText, mFontWriter);
		
		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();
		
		// Pop Matrix
		matrixRow.pop();
	}
}
