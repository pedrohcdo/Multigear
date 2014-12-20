package multigear.mginterface.engine.servicessuport;

import multigear.general.utils.SafetyLock;
import multigear.general.utils.SafetyLock.Interception;
import android.util.Log;

/**
 * Support Thread
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class SupportThread extends Thread {
	
	// Constants
	final protected static int SUPPORT_DESROYER = 1;
	
	// Final Private Variables
	final private int mCommand;
	final private SupportService mSupportService;
	
	// Private Variables
	private boolean mEndWork, mInterrupted;
	
	/**
	 * Constructor
	 * 
	 * @param command
	 *            Support Command
	 */
	public SupportThread(final SupportThreadGroup supportThreadGroup, final SupportService supportService, final int command) {
		super(supportThreadGroup, "MGThreadGroupST");
		//setPriority(MAX_PRIORITY);
		mSupportService = supportService;
		mCommand = command;
		mEndWork = false;
		mInterrupted = false;
	}
	
	/**
	 * Return Command
	 * <p>
	 * 
	 * @return Command
	 */
	final protected int getCommand() {
		return mCommand;
	}
	
	/**
	 * Return true if end work
	 * <p>
	 * 
	 * @return true if end work
	 */
	final protected boolean isEndWork() {
		return mEndWork;
	}
	
	/**
	 * Post End Work
	 */
	final private void postEndWork() {
		mEndWork = true;
	}
	
	/**
	 * Interrupt
	 */
	final private void postInterrupt() {
		mInterrupted = true;
		this.interrupt();
	}
	
	/**
	 * Has interrupted
	 * 
	 * @return
	 */
	final protected boolean hasInterrupted() {
		return mInterrupted || this.isInterrupted();
	}
	
	/**
	 * Runner
	 */
	@Override
	public void run() {
		switch (mCommand) {
			case SUPPORT_DESROYER:
				runSupportDestroyer();
				break;
		}
	}
	
	/**
	 * Support Thread for force destroy Engine.
	 */
	final public void runSupportDestroyer() {
		// Lock Process
		SafetyLock.lock(new Interception() {
			
			@Override
			public boolean onIntercept() {
				boolean intercept = true;
				// If engine has been initialized
				if (mSupportService.isEngineInitialized()) {
					// If engine as paused
					if (!mSupportService.isEngineResumed()) {
						// Wait if Engine is running
						if (mSupportService.isEngineRunning() && !isInterrupted())
							intercept = false;
						// Destroy Engine if is not running
						else
							mSupportService.engineDestroyedInternal(SupportThread.this);
					}
				}
				return intercept;
			}
		});
		// End Work
		postEndWork();
	}
	
	/**
	 * Stop Support Thread
	 */
	final public void close() {
		//this.interrupt();
		this.postInterrupt();
		//
		try {
			boolean waitInterrupt = true;
			while (waitInterrupt) {
				this.join();
				waitInterrupt = false;
			}
		} catch (Exception e) {
			Log.d("LogTest", "Interrupt Error");
		}
	}
}
