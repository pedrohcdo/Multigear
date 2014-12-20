package multigear.mginterface.graphics.animations;

import java.util.ArrayList;
import java.util.List;

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
	 * Null Animation
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final private class NullAnimation extends Animation {
		
		/**
		 * Constructor
		 * 
		 * @param duration
		 */
		public NullAnimation() {
			super(-1);
		}
		
		/** Unused */
		@Override
		final public void onAnimation(AnimationSet animationSet, float delta) {
		}
	}
	
	// Final Private Variables
	final private List<multigear.mginterface.graphics.animations.Animation> mAnimationStack;
	final private List<AnimationStack.Handler> mStateProcedures;
	final private multigear.mginterface.graphics.animations.Animation mNullAnimation;
	final private multigear.mginterface.scene.Scene mRoom;
	final private Object mLock;
	
	// Private VAriables
	private int mAnimationIndex;
	private boolean mRepeat;
	private boolean mStarted;
	private boolean mRunning;
	// private int mEndAnimationsIndex;
	private AnimationListener mListener;
	
	/*
	 * Construtor
	 */
	public AnimationStack(final multigear.mginterface.scene.Scene room) {
		mRoom = room;
		mLock = new Object();
		mNullAnimation = new NullAnimation();
		mAnimationStack = new ArrayList<Animation>();
		mStateProcedures = new ArrayList<AnimationStack.Handler>();
		mAnimationIndex = 0;
		mStarted = false;
		mRunning = false;
		// mEndAnimationsIndex = -1;
		
		mListener = null;
	}
	
	/*
	 * Altera o gerenciador de eventos
	 */
	final public void setListener(final AnimationListener listener) {
		synchronized (mLock) {
			mListener = listener;
		}
	}
	
	/*
	 * Inicia as animações
	 */
	public void start() {
		
		// Set default Index
		mAnimationIndex = -1;
		
		// Clear States
		mStateProcedures.clear();
		// mEndAnimationsIndex = -1;
		
		// Start Animations
		mStarted = true;
		mRunning = true;
		
		// No Need to add in Proc
		if (mListener != null)
			mListener.onAnimationsStart();
		
		// Set First Animation
		startAnimation(0);
	}
	
	/*
	 * Para as animações
	 */
	public void stop() {
		mStarted = false;
		mRunning = false;
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
		return mRunning && mStarted;
	}
	
	/*
	 * Repetição da animação
	 */
	public void setRepeat(final boolean repeat) {
		mRepeat = repeat;
	}
	
	/*
	 * Retorna true caso a animação seja repetida
	 */
	public boolean getRepeat() {
		return mRepeat;
	}
	
	/*
	 * Adicinona uma animação
	 */
	public void addAnimation(Animation base) {
		mAnimationStack.add(base);
		// Se já estiver iniciada e estiver no fim da pilha sera executado esta
		if (mStarted && !mRunning)
			startAnimation(mAnimationStack.size() - 1);
		mRunning = true;
	}
	
	/**
	 * Add New Procedure
	 * 
	 * @param code
	 * @param animation
	 */
	final private void addProc(final int code, final multigear.mginterface.graphics.animations.Animation animation) {
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
	
	/*
	 * Inicia a animação
	 */
	final private void startAnimation(final int id) {
		if (id < mAnimationStack.size()) {
			mAnimationIndex = id;
			final multigear.mginterface.graphics.animations.Animation animation = mAnimationStack.get(mAnimationIndex);
			animation.set(mRoom.getThisTime());
			if (mListener != null)
				addProc(AnimationStack.Handler.ANIMATION_START, animation);
		}
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
	 * Prepare animation.
	 * 
	 * @return
	 */
	final public multigear.mginterface.graphics.animations.Animation prepareAnimation() {
		// Return Null Animation if not Started
		if (!mStarted)
			return mNullAnimation;
		
		// Prepare Procedures
		prepareProcedures();
		
		// Return Null Animation if not Started
		if (!mStarted)
			return mNullAnimation;
		
		// Update Animation
		Animation animation;
		if (mAnimationIndex >= 0) {
			animation = mAnimationStack.get(mAnimationIndex);
			animation.update(mRoom.getThisTime());
		} else {
			animation = mNullAnimation;
			if (mAnimationStack.size() == 0)
				return animation;
		}
		
		// If Animation End
		// Caso não estiver mais em funcionamento, não sera revisto a ultima
		// animação
		if (animation.isFinish() && mRunning) {
			
			// Add to Stack States
			addProc(AnimationStack.Handler.ANIMATION_END, animation);
			
			// Calculate next animation
			int nextAnimationIndex = mAnimationIndex + 1;
			boolean running = true;
			
			// If end animations
			if (nextAnimationIndex >= mAnimationStack.size()) {
				if (mRepeat)
					nextAnimationIndex = 0;
				else {
					mRunning = false;
					running = false;
				}
			}
			
			// If Running
			if (running)
				startAnimation(nextAnimationIndex);
			else
				addProc(AnimationStack.Handler.ANIMATIONS_END);
			
		}
		
		// Return Prepared Animation
		return animation;
	}
	
	/**
	 * Return AnimationSet of this Frame
	 * @return
	 */
	final public AnimationSet animateFrame() {
		return prepareAnimation().animate();
	}
	
	/*
	 * final public void waitForNextUpdate() { if(isRunning() &&
	 * !animationsEnd()) { AnimationBase animation =
	 * mAnimationStack.get(mAnimationIndex); animation.waitForNextUpdate(); } }
	 */
	
	/*
	 * Limpa todas animações
	 */
	final public void clear() {
		// Clear Animations and States
		mAnimationStack.clear();
		mStateProcedures.clear();
		// Reset
		mAnimationIndex = 0;
		mStarted = false;
		mRunning = false;
		mRepeat = false;
	}
}
