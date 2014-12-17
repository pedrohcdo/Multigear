package multigear.mginterface.graphics.drawable.particles;

import multigear.general.utils.Ref2F;
import multigear.mginterface.graphics.opengl.texture.Texture;

/**
 * Particle used to add in Particles Group
 * <p>
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Particle {
	
	// Final Private Variables
	final private int mDuration;
	
	// Private Variables
	private Ref2F mPosition = new Ref2F(0, 0);
	private Ref2F mSize = new Ref2F(32, 32);
	private Texture mTexture;
	private Ref2F mCenter = multigear.general.utils.KernelUtils.ref2d(0, 0);
	private boolean mFixedSpace = false;
	private Ref2F mForces = new Ref2F(0, 0);
	private Ref2F mAccelerations = new Ref2F(0, 0);
	
	// Runn Variables
	private long mCreatedTime;
	private long mTimeNow;
	private float mTimeDelta;
	
	// Modifiers
	private float mOpacityModifier[] = { 1.0f, 1.0f };
	private float mRotationModifier[] = { 0.0f, 0.0f };
	private Ref2F mScaleModifier[] = { new Ref2F(1.0f, 1.0f), new Ref2F(1.0f, 1.0f) };
	
	/**
	 * Constructor
	 */
	public Particle(final int durationInMillis) {
		mDuration = durationInMillis;
	}
	
	/**
	 * Particle Created
	 */
	final protected void onCreated(final long time) {
		mCreatedTime = time;
	}
	
	/**
	 * Return true if Time Out
	 * 
	 * @param time
	 * @return
	 */
	final protected boolean isTimeOut() {
		return (mTimeNow - mCreatedTime) > mDuration;
	}
	
	/**
	 * Set Opacity Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public void setOpacityModifier(final float start, final float end) {
		mOpacityModifier = new float[] { start, end };
	}
	
	/**
	 * Set Scale Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public void setScaleModifier(final Ref2F start, final Ref2F end) {
		mScaleModifier = new Ref2F[] { start, end };
	}
	
	/**
	 * Set Scale Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public void setScaleModifier(final float start, final float end) {
		mScaleModifier = new Ref2F[] { new Ref2F(start, start), new Ref2F(end, end) };
	}
	
	/**
	 * Set Rotation Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public void setRotationModifier(final float start, final float end) {
		mRotationModifier = new float[] { start, end };
	}
	
	/**
	 * Set Forces
	 * <p>
	 * 
	 * @param forces
	 */
	final public void setForces(final Ref2F forces) {
		mForces = forces;
	}
	
	/**
	 * Set Accelerations
	 * <p>
	 * 
	 * @param accelerations
	 */
	final public void setAccelerations(final Ref2F accelerations) {
		mAccelerations = accelerations;
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
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link multigear.general.utils.Ref2F} Position
	 */
	final public void setPosition(final multigear.general.utils.Ref2F position) {
		mPosition = position;
	}
	
	/**
	 * Set draw dest texture size.
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
	 * Set Fixed Space.
	 * 
	 * @param fixed
	 *            Boolean Fixed
	 */
	final public void setFixedSpace(final boolean fixed) {
		mFixedSpace = fixed;
	}
	
	/**
	 * Get Opacity Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public float[] getOpacityModifier() {
		return mOpacityModifier.clone();
	}
	
	/**
	 * Get Scale Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public Ref2F[] getScaleModifier() {
		return mScaleModifier.clone();
	}
	
	/**
	 * Get Rotation Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public float[] getRotationModifier() {
		return mRotationModifier.clone();
	}
	
	/**
	 * Get Forces
	 * <p>
	 * 
	 * @param forces
	 */
	final public Ref2F getForces() {
		return mForces.clone();
	}
	
	/**
	 * Get Accelerations
	 * <p>
	 * 
	 * @param accelerations
	 */
	final public Ref2F getAccelerations() {
		return mAccelerations.clone();
	}
	
	/**
	 * Get Texture
	 * 
	 * @return texture
	 *         {@link multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public Texture getTexture() {
		return mTexture;
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Position
	 */
	final public Ref2F getPosition() {
		return mPosition.clone();
	}
	
	/**
	 * Return draw dest Texture size.
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Size
	 */
	final public Ref2F getSize() {
		return mSize.clone();
	}
	
	/**
	 * Get center axis.
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Center
	 */
	final public Ref2F getCenter() {
		return mCenter.clone();
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
	 * Get Frame Opacity
	 * 
	 * @return
	 */
	final protected float getOpacity() {
		return (mOpacityModifier[0] - mOpacityModifier[0] * mTimeDelta) + (mTimeDelta * mOpacityModifier[1]);
	}

	/**
	 * Get Frame Scale
	 * 
	 * @return
	 */
	final protected Ref2F getScale() {
		final float scaleX = (mScaleModifier[0].XAxis - mScaleModifier[0].XAxis * mTimeDelta) + (mTimeDelta * mScaleModifier[1].XAxis);
		final float scaleY = (mScaleModifier[0].YAxis - mScaleModifier[0].YAxis * mTimeDelta) + (mTimeDelta * mScaleModifier[1].YAxis);
		return new Ref2F(scaleX, scaleY);
	}
	
	/**
	 * Get Frame Rotation
	 * 
	 * @return
	 */
	final protected float getRotation() {
		return (mRotationModifier[0] - mRotationModifier[0] * mTimeDelta) + (mTimeDelta * mRotationModifier[1]);
	}
	
	/**
	 * Update this Particle
	 */
	final protected void update() {
		mAccelerations.add(mForces);
		mPosition.add(mAccelerations);
	}
	
	/**
	 * Set Time
	 * @param time
	 */
	final protected void timeNow(final long time) {
		mTimeNow = time;
		mTimeDelta = (mTimeNow - mCreatedTime) * 1.0f / mDuration;
	}
	
	
	/**
	 * Prepare this particle to insert
	 * 
	 * @return
	 */
	protected Particle prepareToInsert() {
		final Particle copy = new Particle(mDuration);
		copy.mAccelerations = mAccelerations.clone();
		copy.mCenter = mCenter.clone();
		copy.mCreatedTime = mCreatedTime;
		copy.mFixedSpace = mFixedSpace;
		copy.mForces = mForces.clone();
		copy.mPosition = mPosition.clone();
		copy.mSize = mSize.clone();
		copy.mTexture = mTexture;
		copy.mOpacityModifier = mOpacityModifier.clone();
		copy.mScaleModifier = mScaleModifier.clone();
		copy.mRotationModifier = mRotationModifier.clone();
		return copy;
	}
}
