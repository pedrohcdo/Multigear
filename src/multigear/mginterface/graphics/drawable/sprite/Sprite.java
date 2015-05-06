package multigear.mginterface.graphics.drawable.sprite;

import multigear.general.utils.Color;
import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.opengl.drawer.BlendFunc;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Component;
import multigear.mginterface.scene.components.receivers.Drawable;
import android.graphics.Rect;

/**
 * 
 * Used to create a floating and flexible texture. Support the positions of and
 * also their mapping vertices thereof.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Sprite implements Drawable, Component {

	// Final Private Variables
	final private float mFinalTransformation[] = new float[] { 0, 0, 0, 0, 0, 0, 0, 0, 1 };
	final private AnimationStack mAnimationStack;
	
	// Private Variables
	private Rect mViewport;
	private Texture mTexture;
	
	// Public Variables
	private Vector2 mScale = new Vector2(1, 1);
	private Vector2 mPosition = new Vector2(0, 0);
	private Vector2 mSize = new Vector2(32, 32);
	private Vector2 mCenter = new Vector2(0, 0);
	private float mAngle = 0;
	private float mOpacity = 1;
	private boolean mMirror[] = { false, false };
	private int mZ = 0;
	private int mId = 0;
	private Color mColor = Color.WHITE;
	private BlendFunc mBlendFunc = BlendFunc.ONE_MINUS_SRC_ALPHA;
	
	/**
	 * Constructor
	 */
	public Sprite() {
		mTexture = null;
		mAnimationStack = new AnimationStack();
		mViewport = null;
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
	final public void setViewport(final int left, final int top,final int width, final int height) {
		mViewport = new Rect(left, top, width, height);
	}

	/**
	 * Retorna a pilha de animações
	 * 
	 * @return
	 */
	final public AnimationStack getAnimationStack() {
		return mAnimationStack;
	}

	/**
	 * Set Texture
	 * 
	 * @param texture
	 *            {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public void setTexture(final Texture texture) {
		mTexture = texture;
		this.setSize(mTexture.getSize());
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
	 * Set draw dest texture size.
	 * 
	 * @param size
	 *            Draw texture dest Size
	 */
	final public void setSize(final Vector2 size) {
		mSize = size.clone();
	}

	/**
	 * Set depth
	 * 
	 * @param z Depth
	 */
	public void setZ(final int z) {
		mZ = z;
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
	 * Set identifier
	 * 
	 * @param id Identifier
	 */
	public void setId(int id) {
		mId = id;
	}

	/**
	 * Set drawable opacity
	 * 
	 * @param opacity
	 *            [in] Opacity
	 */
	final public void setOpacity(final float opacity) {
		mOpacity = Math.max(Math.min(opacity, 1.0f), 0.0f);
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
	 * Set Color
	 * 
	 * @param color Color
	 */
	final public void setColor(final Color color) {
		mColor = color;
	}
	
	/**
	 * Get Texture
	 */
	final public Texture getTexture() {
		return mTexture;
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
		final AnimationSet animationSet = getAnimationStack().prepareAnimation().animate();
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
	 * Get depth
	 * 
	 * @return Return Depth
	 */
	public int getZ() {
		return mZ;
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
	 * Get identifier
	 * 
	 * @return Return Indentifier
	 */
	public int getId() {
		return mId;
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
	 * Get Color
	 * 
	 * @return Color
	 */
	final public Color getColor() {
		return mColor;
	}
	
	/*
	 * Prepara para desenho. Utiliza AnimationStack.
	 */
	@Override
	public void draw(final Drawer drawer) {

		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Get final Opacity
		final float opacity = animationSet.getOpacity() * getOpacity();
		
		// Get Original texture
		Texture usedTexture = mTexture;
		
		// Animate texture
		final Texture animateTexture = animationSet.getTexture();
		if (animateTexture != null)
			usedTexture = animateTexture;
		
		// Not Update
		if (usedTexture == null || opacity <= 0)
			return;
		
		// Get Infos
		final Vector2 scale = Vector2.scale(mScale, animationSet.getScale());
		final Vector2 translate = animationSet.getPosition();
		final float rotate = mAngle + animationSet.getRotation();

		// Calc values
		final float ox = mCenter.x * scale.x;
		final float oy = mCenter.y * scale.y;
		float sx = mSize.x * scale.x;
		float sy = mSize.y * scale.y;
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
		
		// Set Texture
		drawer.begin();
		drawer.setTexture(usedTexture);
		drawer.setColor(mColor);
		drawer.setOpacity(opacity);
		drawer.setBlendFunc(mBlendFunc);
		drawer.snip(mViewport);
		drawer.drawTexture(mSize);
		drawer.end();
		
		// pop
		matrixRow.pop();
	}
}
