package multigear.mginterface.graphics.drawable.particles;

import multigear.general.utils.Vector2;

/**
 * Particle used to add in Particles Group
 * <p>
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class Particle {
	
	/**
	 * Custom Modifier
	 * 
	 * @author user
	 *
	 */
	public interface ParticleModifier {
		
		/**
		 * Get opacity
		 * @return
		 */
		public float getOpacity(final float delta);
		
		/**
		 * Get Scale
		 * @return
		 */
		public float getScale(final float delta);
		
		/**
		 * Get Angle
		 * @return
		 */
		public float getAngle(final float delta);
		
		/**
		 * Get Position
		 * 
		 * @return
		 */
		public Vector2 getPosition(final float delta);
	}
	
	// Constants
	final public static int UNLIMITED_TIME = -1;
	
	// Final Private Variables
	final private int mDuration;
	final private ParticleModifier mModifier;
	
	// Private Variables
	private Vector2 mPosition = new Vector2(0, 0);
	private boolean mFixedSpace = false;
	private Vector2 mForces = new Vector2(0, 0);
	private Vector2 mAccelerations = new Vector2(0, 0);
	private Vector2 mFinalPosition = new Vector2();
	
	// Runn Variables
	private long mCreatedTime;
	private long mTimeNow;
	private float mTimeDelta;
	
	// Modifiers
	private float mOpacityModifier[] = { 1.0f, 1.0f };
	private float mScaleModifier[] = { 1.0f, 1.0f };
	private float mAngleModifier[] = {0, 0};
	
	/**
	 * Constructor
	 */
	public Particle(final int durationInMillis) {
		mDuration = durationInMillis;
		mModifier = null;
	}
	
	/**
	 * Construct with a custom modifier
	 * @param durationInMillis
	 * @param modifier
	 */
	public Particle(final int durationInMillis, final ParticleModifier modifier) {
		mDuration = durationInMillis;
		mModifier = modifier;
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
	 * Set Angle Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public void setAngleModifier(final float start, final float end) {
		mAngleModifier = new float[] { start, end };
	}
	
	/**
	 * Set Forces
	 * <p>
	 * 
	 * @param forces
	 */
	final public void setForces(final Vector2 forces) {
		mForces = forces;
	}
	
	/**
	 * Set Accelerations
	 * <p>
	 * 
	 * @param accelerations
	 */
	final public void setAccelerations(final Vector2 accelerations) {
		mAccelerations = accelerations;
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link Vector2} Position
	 */
	final public void setPosition(final Vector2 position) {
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
	 * Get Angle Modifier
	 * 
	 * @param start
	 * @param end
	 */
	final public float[] getAngleModifier() {
		return mScaleModifier.clone();
	}
	
	/**
	 * Get Forces
	 * <p>
	 * 
	 * @param forces
	 */
	final public Vector2 getForces() {
		return mForces.clone();
	}
	
	/**
	 * Get Accelerations
	 * <p>
	 * 
	 * @param accelerations
	 */
	final public Vector2 getAccelerations() {
		return mAccelerations.clone();
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
	final protected Vector2 getFinalPosition() {
		if(mModifier != null)
			return mModifier.getPosition(mTimeDelta);
		return mFinalPosition;
	}
	
	/**
	 * Get Frame Opacity
	 * 
	 * @return
	 */
	final protected float getOpacity() {
		if(mModifier != null)
			return mModifier.getOpacity(mTimeDelta);
		return (mOpacityModifier[0] - mOpacityModifier[0] * mTimeDelta) + (mTimeDelta * mOpacityModifier[1]);
	}

	/**
	 * Get Frame Scale
	 * 
	 * @return
	 */
	final protected float getScale() {
		if(mModifier != null)
			return mModifier.getScale(mTimeDelta);
		return (mScaleModifier[0] - mScaleModifier[0] * mTimeDelta) + (mTimeDelta * mScaleModifier[1]);
	}
	
	/**
	 * Get Frame Angle
	 * 
	 * @return
	 */
	final protected float getAngle() {
		if(mModifier != null)
			return mModifier.getAngle(mTimeDelta);
		return (mAngleModifier[0] - mAngleModifier[0] * mTimeDelta) + (mTimeDelta * mAngleModifier[1]);
	}
	
	/**
	 * Update this Particle
	 */
	final protected void update() {
		if(mModifier == null) {
			//mAccelerations.sum(mForces);
			//mPosition.sum(mAccelerations);
			
			float time = (mTimeDelta * mDuration) / 17.0f;
			
			mFinalPosition = Vector2.sum(mPosition, Vector2.sum(Vector2.scale(mAccelerations, time), Vector2.scale(mForces, (time*time) / 2)));
			
		}
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
		final Particle copy = new Particle(mDuration, mModifier);
		copy.mAccelerations = mAccelerations.clone();
		copy.mCreatedTime = mCreatedTime;
		copy.mFixedSpace = mFixedSpace;
		copy.mForces = mForces.clone();
		copy.mPosition = mPosition.clone();
		copy.mOpacityModifier = mOpacityModifier.clone();
		copy.mScaleModifier = mScaleModifier.clone();
		copy.mAngleModifier = mAngleModifier.clone();
		copy.mFinalPosition = mPosition.clone();
		return copy;
	}
}
