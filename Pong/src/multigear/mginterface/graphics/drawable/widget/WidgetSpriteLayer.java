package multigear.mginterface.graphics.drawable.widget;

import multigear.general.utils.Ref2F;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.MatrixRow;
import multigear.mginterface.graphics.opengl.texture.Texture;

/**
 * WidgetLayer
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class WidgetSpriteLayer extends WidgetLayer {
	
	// Private Variables
	private multigear.mginterface.graphics.opengl.texture.Texture mTexture;
	private multigear.mginterface.graphics.animations.AnimationStack mAnimationStack;
	private Ref2F mScale = new Ref2F(1, 1);
	private multigear.general.utils.Ref2F mPosition = multigear.general.utils.KernelUtils.ref2d(0, 0);
	private float mOpacity = 1.0f;
	private multigear.general.utils.Ref2F mSize = multigear.general.utils.KernelUtils.ref2d(32, 32);
	private multigear.general.utils.Ref2F mCenter = multigear.general.utils.KernelUtils.ref2d(0, 0);
	private multigear.general.utils.Ref2F mScroll = multigear.general.utils.KernelUtils.ref2d(0, 0);
	private multigear.general.utils.Vector2D mAngle = multigear.general.utils.KernelUtils.vec2d(0, -1);

	
	// For Draw
	private Texture mPreparedTexture;
	private float mPreparedOpacity;
	
	/**
	 * Constructor
	 */
	protected WidgetSpriteLayer(final multigear.mginterface.scene.Scene room) {
		mAnimationStack = new multigear.mginterface.graphics.animations.AnimationStack(room);
	}
	
	/**
	 * Set Texture
	 * 
	 * @param texture
	 *            {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public void setTexture(final multigear.mginterface.graphics.opengl.texture.Texture texture) {
		mTexture = texture;
		mSize = texture.getSize();
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
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final Ref2F scale) {
		mScale = scale;
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link multigear.general.utils.Ref2F} Position
	 */
	final public void setPosition(final multigear.general.utils.Ref2F position) {
		mPosition = position;
	}
	
	/**
	 * Set draw dest texture size. For this use, setRestectTextureSize(false).
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setSize(final multigear.general.utils.Ref2F size) {
		mSize = size;
	}
	
	/**
	 * Set center axis.
	 * 
	 * @param center
	 *            {@link multigear.general.utils.Ref2F} Center
	 */
	final public void setCenter(final multigear.general.utils.Ref2F center) {
		mCenter = center;
	}
	
	/**
	 * Set Angle.
	 * 
	 * @param angle
	 *            {@link multigear.general.utils.Vector2D} Angle
	 */
	final public void setAngle(final multigear.general.utils.Vector2D angle) {
		mAngle = angle;
	}
	
	/**
	 * Set Scroll.
	 * 
	 * @param center
	 *            {@link multigear.general.utils.Ref2F} Scroll
	 */
	final public void setScroll(final multigear.general.utils.Ref2F scroll) {
		mScroll = scroll;
	}
	
	
	
	/**
	 * Get Texture
	 * 
	 * @return texture
	 *         {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public multigear.mginterface.graphics.opengl.texture.Texture getTexture() {
		return mTexture;
	}
	
	/**
	 * Get Animation Stack
	 * 
	 * @return animationStack
	 *         {@link multigear.mginterface.graphics.animations.AnimationStack}
	 */
	final public multigear.mginterface.graphics.animations.AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Get Opacity
	 */
	final public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Get Scale
	 */
	final public Ref2F getScale() {
		return mScale;
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Position
	 */
	final public multigear.general.utils.Ref2F getPosition() {
		return mPosition;
	}
	
	/**
	 * Return draw dest Texture size.
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Size
	 */
	final public multigear.general.utils.Ref2F getSize() {
		return mSize;
	}
	
	/**
	 * Get center axis.
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Center
	 */
	final public multigear.general.utils.Ref2F getCenter() {
		return mCenter;
	}
	
	/**
	 * Get Angle.
	 * 
	 * @return {@link multigear.general.utils.Vector2D} Angle
	 */
	final public multigear.general.utils.Vector2D getAngle() {
		return mAngle;
	}
	
	/**
	 * Get Scroll.
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Scroll
	 */
	final public multigear.general.utils.Ref2F getScroll() {
		return mScroll;
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
		
		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Get final Opacity
		mPreparedOpacity = preOpacity * animationSet.getOpacity() * mOpacity;
		
		// Get Original texture
		mPreparedTexture = mTexture;
		
		// Animate texture
		final Texture animateTexture = animationSet.getTexture();
		if (animateTexture != null)
			mPreparedTexture = animateTexture;
		
		// Not Update
		if (mPreparedTexture == null || mPreparedOpacity <= 0)
			return false;
		
		// Get Infos
		final Ref2F scale = mScale.clone().mul(animationSet.getScale());
		final float ox = mCenter.XAxis * scale.XAxis;
		final float oy = mCenter.YAxis * scale.YAxis;
		final float sx = mSize.XAxis * scale.XAxis;
		final float sy = mSize.YAxis * scale.YAxis;
		
		// Get Matrix Row
		final MatrixRow matrixRow = drawer.getMatrixRow();
		
		// Push Matrix
		matrixRow.push();
		
		// Scale Matrix
		matrixRow.postScalef(sx, sy);
		
		// Translate and Rotate Matrix
		matrixRow.postTranslatef(-ox, -oy);
		matrixRow.postRotatef(mAngle.getDirection() + animationSet.getRotation());
		matrixRow.postTranslatef(ox, oy);
		
		// Translate Matrix
		final Ref2F translate = animationSet.getPosition();
		final float tX = (mPosition.XAxis - mScroll.XAxis - ox) + translate.XAxis;
		final float tY = (mPosition.YAxis - mScroll.YAxis - oy) + translate.YAxis;
		matrixRow.postTranslatef(tX, tY);

		return true;
		
	}
	
	/*
	 * Atualiza e Desenha
	 */
	protected void endDraw(final Drawer drawer) {
		// Draw
		drawer.drawTexture(mPreparedTexture, mSize, mPreparedOpacity);
		
		// Get Matrix Row
		final MatrixRow matrixRow = drawer.getMatrixRow();
		
		// Pop Matrix
		matrixRow.pop();
	}
}
