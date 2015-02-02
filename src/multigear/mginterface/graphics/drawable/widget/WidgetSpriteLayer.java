package multigear.mginterface.graphics.drawable.widget;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
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
	private Vector2 mScale = new Vector2(1, 1);
	private multigear.general.utils.Vector2 mPosition = new Vector2(0, 0);
	private float mOpacity = 1.0f;
	private multigear.general.utils.Vector2 mSize = new Vector2(32, 32);
	private multigear.general.utils.Vector2 mCenter = new Vector2(0, 0);
	private multigear.general.utils.Vector2 mScroll = new Vector2(0, 0);
	private float mAngle = 0;

	
	// For Draw
	private Texture mPreparedTexture;
	private float mPreparedOpacity;
	
	/**
	 * Constructor
	 */
	public WidgetSpriteLayer() {
		mAnimationStack = new AnimationStack();
	}
	
	/**
	 * Set Texture
	 * 
	 * @param texture
	 *            {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public void setTexture(final multigear.mginterface.graphics.opengl.texture.Texture texture) {
		mTexture = texture;
		mSize = texture.getSize().clone();
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
	final public void setScale(final Vector2 scale) {
		mScale = scale.clone();
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link multigear.general.utils.Vector2} Position
	 */
	final public void setPosition(final multigear.general.utils.Vector2 position) {
		mPosition = position.clone();
	}
	
	/**
	 * Set draw dest texture size. For this use, setRestectTextureSize(false).
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setSize(final multigear.general.utils.Vector2 size) {
		mSize = size.clone();
	}
	
	/**
	 * Set center .
	 * 
	 * @param center
	 *            {@link multigear.general.utils.Vector2} Center
	 */
	final public void setCenter(final multigear.general.utils.Vector2 center) {
		mCenter = center.clone();
	}
	
	/**
	 * Set Angle.
	 * 
	 * @param angle
	 *            {@link multigear.general.utils.Vector2} Angle
	 */
	final public void setAngle(final float angle) {
		mAngle = angle;
	}
	
	/**
	 * Set Scroll.
	 * 
	 * @param center
	 *            {@link multigear.general.utils.Vector2} Scroll
	 */
	final public void setScroll(final multigear.general.utils.Vector2 scroll) {
		mScroll = scroll.clone();
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
	final public Vector2 getScale() {
		return mScale.clone();
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link multigear.general.utils.Vector2} Position
	 */
	final public multigear.general.utils.Vector2 getPosition() {
		return mPosition.clone();
	}
	
	/**
	 * Return draw dest Texture size.
	 * 
	 * @return {@link multigear.general.utils.Vector2} Size
	 */
	final public multigear.general.utils.Vector2 getSize() {
		return mSize.clone();
	}
	
	/**
	 * Get center .
	 * 
	 * @return {@link multigear.general.utils.Vector2} Center
	 */
	final public multigear.general.utils.Vector2 getCenter() {
		return mCenter.clone();
	}
	
	/**
	 * Get Angle.
	 * 
	 * @return {@link multigear.general.utils.Vector2} Angle
	 */
	final public float getAngle() {
		return mAngle;
	}
	
	/**
	 * Get Scroll.
	 * 
	 * @return {@link multigear.general.utils.Vector2} Scroll
	 */
	final public multigear.general.utils.Vector2 getScroll() {
		return mScroll.clone();
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
		
		final Vector2 scale = Vector2.scale(mScale, animationSet.getScale());
		final float ox = mCenter.x * scale.x;
		final float oy = mCenter.y * scale.y;
		final float sx = mSize.x * scale.x;
		final float sy = mSize.y * scale.y;
		
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
		drawer.setTexture(mPreparedTexture);
		
		// Draw
		drawer.drawTexture(mSize);
		
		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();
		
		// Pop Matrix
		matrixRow.pop();
	}
}
