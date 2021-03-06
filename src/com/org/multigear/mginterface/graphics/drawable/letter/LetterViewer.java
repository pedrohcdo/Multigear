package com.org.multigear.mginterface.graphics.drawable.letter;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.animations.AnimationSet;
import com.org.multigear.mginterface.graphics.animations.AnimationStack;
import com.org.multigear.mginterface.graphics.opengl.BlendFunc;
import com.org.multigear.mginterface.graphics.opengl.drawer.Drawer;
import com.org.multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import com.org.multigear.mginterface.graphics.opengl.font.FontMap;
import com.org.multigear.mginterface.graphics.opengl.font.Letter;
import com.org.multigear.mginterface.graphics.opengl.font.LetterDrawer;
import com.org.multigear.mginterface.graphics.opengl.font.LetterWriter;
import com.org.multigear.mginterface.scene.Component;
import com.org.multigear.mginterface.scene.components.receivers.Drawable;

import android.graphics.Rect;

public class LetterViewer implements Component, Drawable {

	// For Draw
	private float mPreparedOpacity;
	
	// Private Variables
	private AnimationStack mAnimationStack;
	private Vector2 mScale = new Vector2(1, 1);
	private Vector2 mPosition = new Vector2(0, 0);
	private Vector2 mCenter = new Vector2(0, 0);
	private Vector2 mScroll = new Vector2(0, 0);
	private float mOpacity = 1.0f;
	private float mAngle = 0;
	private boolean mTouchable = true;
	private boolean mFixedSpace = false;
	private boolean mMirror[] = { false, false };
	private Rect mViewport;
	private BlendFunc mBlendFuncs[] = new BlendFunc[] {BlendFunc.ONE, BlendFunc.ONE_MINUS_SRC_ALPHA, BlendFunc.ONE, BlendFunc.ZERO};
	private Letter mLetter = new Letter();
	private LetterWriter mLetterWriter;
	private int mZ = 0;
	private int mID = 0;
	
	/**
	 * Constructor
	 * @param scene
	 */
	public LetterViewer() {
		mAnimationStack = new AnimationStack();
	}
	
	/**
	 * Set FontMap
	 * 
	 * @param texture
	 *            {@link FontMap}
	 */
	final public void setFontMap(final FontMap fontMap) {
		mLetter.setFontMap(fontMap);
		if(mLetterWriter != null)
			mLetter.write(mLetterWriter);
	}
	
	/**
	 * Set Letter Writer
	 * 
	 * @param fontWriter
	 */
	final public void setLetterWriter(final LetterWriter letterWriter) {
		mLetterWriter = letterWriter;
		if(mLetter.getFontMap() != null && mLetterWriter != null)
			mLetter.write(mLetterWriter);
	}
	
	/**
	 * This function set new letter writer with <b>setLetterWriter(LetterWriter)</b> and write default text.
	 */
	final public void write(final String text) {
		setLetterWriter(new LetterWriter() {
			
			/**
			 * Draw
			 */
			@Override
			public void onDraw(FontMap fontMap, LetterDrawer letterDrawer) {
				letterDrawer.drawText(text);
			}
		});
	}
	
	/**
	 * Rewrite Letter
	 */
	final public void rewrite() {
		if(mLetter.getFontMap() != null && mLetterWriter != null)
			mLetter.write(mLetterWriter);
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
	 * Set Blend Func
	 * 
	 * @param blendFunc
	 */
	final public void setBlendFunc(final BlendFunc sFactor, final BlendFunc dFactor) {
		mBlendFuncs = new BlendFunc[] {sFactor, dFactor, BlendFunc.ONE, BlendFunc.ZERO};
	}
	
	/**
	 * Set Blend Func
	 * 
	 * @param blendFunc
	 */
	final public void setBlendFuncSeparate(final BlendFunc sFactor, final BlendFunc dFactor, final BlendFunc sAlphaFactor, final BlendFunc dAlphaFactor) {
		mBlendFuncs = new BlendFunc[] {sFactor, dFactor, sAlphaFactor, dAlphaFactor};
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
	 * Set Z depth
	 * @param z Depth
	 */
	final public void setZ(final int z) {
		mZ = z;
	}
	
	/**
	 * Set Id
	 * @param id Id
	 */
	final public void setId(final int id) {
		mID = id;
	}
		
	/**
	 * Get Letter Writer
	 * 
	 * @param fontWriter
	 */
	final public LetterWriter getLetterWriter() {
		return mLetterWriter;
	}
	
	/**
	 * Get Blend Func
	 * 
	 * @return Get Blend Func [sFactor, dFactor]
	 */
	final public BlendFunc[] getBlendFunc() {
		return mBlendFuncs.clone();
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
		final AnimationSet animationSet = mAnimationStack.animateFrame();
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
		return mLetter.getFontMap();
	}
	
	/**
	 * Get Z Depth
	 * @return Depth
	 */
	@Override
	final public int getZ() {
		return mZ;
	}
	
	/**
	 * Get Id
	 * @return Id
	 */
	@Override
	final public int getId() {
		return mID;
	}
	
	/**
	 * Set Matrix Transformations for this Layer
	 * <p>
	 * 
	 * @param matrixRow
	 *            MatrixRow
	 * @return True if need Draw
	 */
	final public void draw(final Drawer drawer) {
		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.animateFrame();
		
		// Get final Opacity
		mPreparedOpacity = animationSet.getOpacity() * mOpacity;
		
		// Not Update
		if (mPreparedOpacity <= 0)
			return;

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

		// Begin Drawer
		drawer.begin();
				
		// Draw
		drawer.setOpacity(mPreparedOpacity);
		drawer.snip(mViewport);
		drawer.setBlendFunc(mBlendFuncs[0], mBlendFuncs[1], mBlendFuncs[2], mBlendFuncs[3]);
		drawer.drawLetter(mLetter);
		
		// End Drawer
		drawer.end();
				
		// Pop Matrix
		matrixRow.pop();
	}
}
