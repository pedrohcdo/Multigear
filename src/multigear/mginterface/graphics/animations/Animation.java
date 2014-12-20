package multigear.mginterface.graphics.animations;

/**
 * 
 * Base para animação.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public abstract class Animation {
	
	// Variables
	private int mDuration;
	public long mStartedTime;
	public long mThisTime;
	// private boolean mStarted;
	private boolean mWaitForNextUpdate;
	private long mTimePassedAfterWait;
	
	// private int usesId;
	
	/*
	 * Construtor
	 */
	public Animation(final int duration) {
		mDuration = duration;
		mStartedTime = 0;
		mThisTime = 0;
		mWaitForNextUpdate = false;
		// usesId = -1;
	}
	
	/*
	 * Inicia animação
	 */
	final void set(final long time) {
		mStartedTime = time;
		mThisTime = time;
	}
	
	/*
	 * Altera duração
	 */
	public void setDuration(final int duration) {
		mDuration = duration;
	}
	
	/*
	 * Retorna true caso a animação terminar
	 */
	final public boolean isFinish() {
		if (mWaitForNextUpdate)
			return false;
		final long diff = mThisTime - mStartedTime;
		return (diff > mDuration);
	}
	
	/*
	 * Atualizando animação
	 */
	final void update(final long time) {
		if (mWaitForNextUpdate) {
			mStartedTime = time - mTimePassedAfterWait;
			mWaitForNextUpdate = false;
		}
		mThisTime = time;
	}
	
	/*
	 * Retorna o andamento da animação por um valor desejado
	 */
	final private float getElapsedTime() {
		final float diff = Math.min(mThisTime - mStartedTime, mDuration);
		return diff / mDuration;
	}
	
	/*
	 * Espera pela proxima atualização
	 */
	final public void waitForNextUpdate() {
		mWaitForNextUpdate = true;
		mTimePassedAfterWait = mThisTime - mStartedTime;
	}
	
	/**
	 * Request for call onAnimation().
	 * <br>
	 * This method performs the animation at the
	 * current frame and returns an instance of the result animation.
	 */
	final public AnimationSet animate() {
		final AnimationSet animationSet = new AnimationSet();
		onAnimation(animationSet, getElapsedTime());
		return animationSet;
	}
	
	/**
	 * Request for call onAnimation().
	 * <br>
	 * This method performs the animation in 
	 * current frame and applies the results in the current ANimationSet.
	 * @param current
	 * @return
	 */
	final public void animate(final AnimationSet current, final float delta) {
		onAnimation(current, delta);
	}
	
	/**
	 * Animation Callback
	 * @param animationSet AnimationSet
	 * @param delta Elapsed time of the animation, the value returned will be between 0.0 and 1.0
	 */
	abstract public void onAnimation(final AnimationSet animationSet, final float delta);
}
