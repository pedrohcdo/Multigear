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
	private boolean mFixedSpace = false;
	private Ref2F mForces = new Ref2F(0, 0);
	private Ref2F mAccelerations = new Ref2F(0, 0);
	
	// Runn Variables
	private long mCreatedTime;
	private long mTimeNow;
	private float mTimeDelta;
	
	// Modifiers
	private float mOpacityModifier[] = { 1.0f, 1.0f };
	private float mScaleModifier[] = { 1.0f, 1.0f };
	
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
	final public void setScaleModifier(final float start, final float end) {
		mScaleModifier = new float[] { start, end };
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
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link multigear.general.utils.Ref2F} Position
	 */
	final public void setPosition(final multigear.general.utils.Ref2F position) {
		mPosition = position;
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
	final public float[] getScaleModifier() {
		return mScaleModifier.clone();
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
	 * Return Position
	 * 
	 * @return {@link multigear.general.utils.Ref2F} Position
	 */
	final public Ref2F getPosition() {
		return mPosition.clone();
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
	final protected float getScale() {
		return (mScaleModifier[0] - mScaleModifier[0] * mTimeDelta) + (mTimeDelta * mScaleModifier[1]);
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
		copy.mCreatedTime = mCreatedTime;
		copy.mFixedSpace = mFixedSpace;
		copy.mForces = mForces.clone();
		copy.mPosition = mPosition.clone();
		copy.mOpacityModifier = mOpacityModifier.clone();
		copy.mScaleModifier = mScaleModifier.clone();
		return copy;
	}
}
