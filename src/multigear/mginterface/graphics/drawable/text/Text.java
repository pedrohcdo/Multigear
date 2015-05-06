package multigear.mginterface.graphics.drawable.text;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.opengl.drawer.BlendFunc;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.font.FontDrawer;
import multigear.mginterface.graphics.opengl.font.FontMap;
import multigear.mginterface.graphics.opengl.font.FontWriter;
import multigear.mginterface.scene.Component;
import multigear.mginterface.scene.components.receivers.Drawable;
import android.graphics.Rect;

/**
 * Text Sprite
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Text implements Drawable, Component {
	
	// Final Private Variables
	final private float mFinalTransformation[] = new float[] { 0, 0, 0, 0, 0, 0, 0, 0, 1 };
	final private AnimationStack mAnimationStack;
	
	// Private Variables
	private FontMap mFontMap;
	private String mText = "";
	private Vector2 mScale = new Vector2(1, 1);
	private Vector2 mPosition = new Vector2(0, 0);
	private Vector2 mCenter = new Vector2(0, 0);
	private Vector2 mScroll = new Vector2(0, 0);
	private float mAngle = 0;
	private boolean mMirror[] = { false, false };
	private Rect mViewport;
	private int mId, mZ;
	private float mOpacity = 1;
	private BlendFunc mBlendFunc = BlendFunc.ONE_MINUS_SRC_ALPHA;
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
	 */
	public Text() {
		mAnimationStack = new AnimationStack();
	}

	/**
	 * Set Depth
	 * @param z Depth
	 */
	public void setZ(int z) {
		mZ = z;
	}

	/**
	 * Set Identifier
	 * @param id Identifier
	 */
	public void setId(int id) {
		mId = id;
	}
	
	/**
	 * Set Opacity
	 * @param opacity Opacity
	 */
	public void setOpacity(float opacity) {
		mOpacity = Math.max(Math.min(opacity, 1.0f), 0.0f);
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
	 * Set center .
	 * 
	 * @param center
	 *            {@link Vector2} Center
	 */
	final public void setCenter(final Vector2 center) {
		mCenter = center;
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
		mScroll = scroll;
	}
	
	/**
	 * Get Depth
	 */
	@Override
	public int getZ() {
		return mZ;
	}

	/**
	 * Get Identifier
	 */
	@Override
	public int getId() {
		return mId;
	}
	
	/**
	 * Get Opacity
	 * @return Opacity
	 */
	public float getOpacity() {
		return mOpacity;
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
	 * @param blendFunc
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
	 * Update Sprite. obs(If it is created in a ROM, it will update
	 * altomatically on onUpdate() event.)
	 */
	@Override
	final public void draw(final Drawer drawer) {
		
		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Get final Opacity
		final float opacity = animationSet.getOpacity() * mOpacity;
		
		// Not Update
		if (mFontMap == null || opacity <= 0)
			return;
		
		// Get Infos
		final Vector2 scale = Vector2.scale(mScale, animationSet.getScale());
		final Vector2 translate = animationSet.getPosition();
		final float rotate = mAngle + animationSet.getRotation();

		// Calc values
		final float ox = mCenter.x * scale.x;
		final float oy = mCenter.y * scale.y;
		float sx = scale.x;
		float sy = scale.y;
		float tX = mPosition.x + translate.x;
		float tY = mPosition.y + translate.y;
		float six = ox;
		float siy = oy;
		float mx = 1;
		float my = 1;
		float mtx = 0;
		float mty = 0;
		
		if (mMirror[0]) {
			mx = -1;
			mtx = 1;
		}
		
		if (mMirror[1]) {
			my = -1;
			mty = 1;
		}

		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();

		// Push Matrix
		matrixRow.push();

		
		// Translate and Rotate Matrix with correction
		float rad = (float) GeneralUtils.degreeToRad(rotate);
		float c = (float) Math.cos(rad);
		float s = (float) Math.sin(rad);
		mFinalTransformation[0] = c * sx * mx;
		mFinalTransformation[1] = -s * sy * my;
		mFinalTransformation[2] = c * (sx * mtx - six) + -s * (sy * mty - siy) + tX;
		mFinalTransformation[3] = s * sx * mx;
		mFinalTransformation[4] = c * sy * my;
		mFinalTransformation[5] = s * (sx * mtx - six) + c * (sy * mty - siy) + tY;
		matrixRow.preConcatf(mFinalTransformation);
		
		
		// Draw Text
		drawer.begin();
		drawer.setBlendFunc(mBlendFunc);
		drawer.setOpacity(opacity);
		drawer.snip(mViewport);
		drawer.drawText(mFontMap, mText, mFontWriter);
		drawer.end();
		
		// Pop transformations
		matrixRow.pop();
	}
}
