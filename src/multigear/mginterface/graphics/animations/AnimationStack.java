package multigear.mginterface.graphics.animations;

import java.util.ArrayList;
import java.util.List;

import multigear.mginterface.engine.eventsmanager.GlobalClock;

/**
 * 
 * Base para animação de uma Textura.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property SpringBall.
 */
public class AnimationStack {
	
	/**
	 * Handler Used for Control State
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final private class Handler {
		
		// Consts
		final private static int ANIMATION_START = 1;
		final private static int ANIMATION_END = 2;
		final private static int ANIMATIONS_END = 3;
		
		// Final Private Variables
		final private int mCode;
		final private multigear.mginterface.graphics.animations.Animation mAnimation;
		
		/**
		 * Constructor
		 * 
		 * @param code
		 */
		private Handler(final int code) {
			mCode = code;
			mAnimation = null;
		}
		
		/**
		 * Constructor
		 * 
		 * @param code
		 */
		private Handler(final int code, final multigear.mginterface.graphics.animations.Animation animation) {
			mCode = code;
			mAnimation = animation;
		}
		
		/**
		 * Call Proc
		 */
		final private void call() {
			synchronized (mLock) {
				if (mListener != null) {
					switch (mCode) {
						case ANIMATION_START:
							mListener.onAnimationStart(mAnimation);
							break;
						case ANIMATION_END:
							mListener.onAnimationEnd(mAnimation);
							break;
						case ANIMATIONS_END:
							mListener.onAnimationsEnd();
					}
				}
			}
		}
	}
	
	/**
	 * Animation Control
	 * 
	 * @author user
	 *
	 */
	final private class AnimationControl {
		
		int duration;
		Animation animation;
	}
	
	/**
	 * Animation State
	 * 
	 * @author user
	 *
	 */
	final private class AnimationState {
		
		int state;
		float delta;
		int index;
		Animation animation;
	}
	
	// Conts
	final private static int ANIMATION_STATE_RUNNING = 0;
	final private static int ANIMATION_STATE_EMPTY = 1;
	final private static int ANIMATION_STATE_END = 2;
	
	// Final Private Variables
	final private List<AnimationControl> mAnimationStack;
	final private List<AnimationStack.Handler> mStateProcedures;
	final private Object mLock;
	
	// Private VAriables
	private AnimationListener mListener;
	private int mIndex;
	private int mLoops;
	private long mStartedTime, mTotalTime;
	private boolean mStarted = false;
	private boolean mRunning = false;
	
	/*
	 * Construtor
	 */
	public AnimationStack() {
		
		mLock = new Object();
		
		mAnimationStack = new ArrayList<AnimationControl>();
		mStateProcedures = new ArrayList<AnimationStack.Handler>();
		
		
		mListener = null;
	}
	
	/**
	 * Set Listener
	 * @param listener
	 */
	final public void setListener(final AnimationListener listener) {
		synchronized (mLock) {
			mListener = listener;
		}
	}
	
	/**
	 * Add New Procedure
	 * 
	 * @param code
	 * @param animation
	 */
	final private void addProc(final int code, final Animation animation) {
		mStateProcedures.add(new AnimationStack.Handler(code, animation));
	}
	
	/**
	 * Add New Procedure
	 * 
	 * @param code
	 * @param animation
	 */
	final private void addProc(final int code) {
		mStateProcedures.add(new AnimationStack.Handler(code));
	}
	
	/**
	 * Prepare Procedures
	 */
	final private void prepareProcedures() {
		for (final AnimationStack.Handler proc : mStateProcedures)
			proc.call();
		mStateProcedures.clear();
	}
	
	/**
	 * Start Animations
	 * @param loop
	 */
	public void start() {
		start(1);
	}
	
	/**
	 * Start Animations with loop<br>
	 * <b>Note:</b> Use -1 to infinity loops
	 * @param loop
	 */
	public void start(int loop) {
		// If illegal
		if(loop <= 0 && loop != -1)
			throw new IllegalArgumentException("The number of loops can not be less than 0.");
		
		// Normalize
		if(loop == -1)
			loop = 0;
		
		// If empty return and force to stop
		// In same cases have animation request to retry
		if(mAnimationStack.size() == 0) {
			mStarted = false;
			mRunning = false;
			return;
		}
		
		// Uses
		mStateProcedures.clear();
		mIndex = -1;
		mLoops = loop;
		mStarted = true;
		mRunning = true;
		mStartedTime = GlobalClock.currentTimeMillis();
		mTotalTime = 0;
		for(int i=0; i<mAnimationStack.size(); i++) {
			final AnimationControl control = mAnimationStack.get(i);
			mTotalTime += control.duration;
		}
		
		// No Need to add in Proc
		if (mListener != null)
			mListener.onAnimationsStart();
	}
	
	/*
	 * Para as animações
	 */
	public void stop() {
		mStarted = false;
	}
	
	/**
	 * Return true if Started Animations.
	 */
	public boolean isStarted() {
		return mStarted;
	}
	
	/**
	 * Return true if Animations Running.
	 */
	public boolean isRunning() {
		return mStarted && mRunning;
	}
	
	/**
	 * Adds an animation, if the stack of animations is operating the same is restarted from the beginning .
	 * @param duration
	 * @param animation
	 */
	public void addAnimation(final int duration, final Animation animation) {
		// Control
		final AnimationControl control = new AnimationControl();
		control.duration = duration;
		control.animation = animation;
		//
		mAnimationStack.add(control);
		// Se já estiver iniciada e estiver no fim da pilha sera executado esta
		if(isStarted())
			start(mLoops);
	}
	
	/**
	 * Remove the instance of animation, if the stack of animations is operating the same is restarted from the beginning .
	 * @param animation
	 */
	public void removeAnimation(final Animation animation) {
		AnimationControl control = null;
		for(int i=0; i<mAnimationStack.size(); i++) {
			control = mAnimationStack.get(i);
			if(control.animation == animation)
				break;
		}
		if(control != null)
			mAnimationStack.remove(control);
		// Se já estiver iniciada e estiver no fim da pilha sera executado esta
		if(isStarted())
			start(mLoops);
	}
	
	/**
	 * Get Animation in Frame
	 * @return
	 */
	final private AnimationState getAnimationFrameState() {
		//
		final AnimationState state = new AnimationState();
		// If empty, return empty state
		if(mAnimationStack.size() == 0) {
			state.animation = new Animation() {
				
				@Override
				public void onAnimation(AnimationSet animationSet, float delta) {
				}
			};
			state.state = ANIMATION_STATE_EMPTY;
			state.delta = 1.0f;
			state.index = -1;
			return state;
		}
		//
		final long time = GlobalClock.currentTimeMillis() - mStartedTime;
		final int loops = (int)(time / mTotalTime);
		// if exceed and not infinity loops
		if(mLoops > 0 && loops >= mLoops) {
			state.animation = mAnimationStack.get(mAnimationStack.size()-1).animation;
			state.state = ANIMATION_STATE_END;
			state.delta = 1.0f;
			state.index = mAnimationStack.size() - 1;
		// If counted
		} else {
			final long normalized = time % mTotalTime;
			AnimationControl current = null;
			int timeline = 0;
			int currentTimeline = 0;
			int currentIndex = 0;
			for(int i=0; i<mAnimationStack.size(); i++) {
				final AnimationControl control = mAnimationStack.get(i);
				if(normalized >= timeline) {
					current = control;
					currentTimeline = timeline;
					currentIndex = i;
				} else 
					break;
				timeline += control.duration;
			}
			float delta = ((normalized - currentTimeline) * 1.0f) / (current.duration * 1.0f);
			state.animation = current.animation;
			state.state = ANIMATION_STATE_RUNNING;
			state.delta = delta;
			state.index = currentIndex;
		}
		return state;
	}
	
	/**
	 * Prepare animation.
	 * 
	 * @return
	 */
	final public AnimationSet animateFrame() {
		// New Set
		AnimationSet set = new AnimationSet();
		
		// Prepare Procedures
		prepareProcedures();
		
		// Return Null Animation if not Started
		if (!isStarted())
			return set;
		
		// Get Animation Frame State
		final AnimationState state = getAnimationFrameState();
		
		// Update handlers
		if (mStarted && mRunning) {
			// Control Passed and Started animations
			int currentIndex = state.index;
			// If retry (fast align)
			if(currentIndex < mIndex)
				currentIndex += mAnimationStack.size();
			for(int i=mIndex; i<currentIndex; i++) {
				if(i > -1)
					addProc(AnimationStack.Handler.ANIMATION_END, mAnimationStack.get(i % mAnimationStack.size()).animation);
				addProc(AnimationStack.Handler.ANIMATION_START, mAnimationStack.get((i+1) % mAnimationStack.size()).animation);
			}
			mIndex = state.index;
			// If End Animations
			if(state.state == ANIMATION_STATE_END) {
				addProc(AnimationStack.Handler.ANIMATION_END, state.animation);
				addProc(AnimationStack.Handler.ANIMATIONS_END);
				mRunning = false;
			}
		}
		
		// Animate Set
		state.animation.onAnimation(set, state.delta);
		
		// Return current set
		return set;
	}
	
	/**
	 * Clear Animations and stop animation process
	 */
	final public void clearAnimations() {
		// Clear Animations and States
		mAnimationStack.clear();
		mStateProcedures.clear();
		// Reset
		mStarted = false;
		mRunning = false;
	}
}
