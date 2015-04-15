package multigear.mginterface.engine.servicessuport;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Support Thread Group
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class SupportThreadGroup extends ThreadGroup {
	
	// Constants
	final protected static int STATE_NONEXIST = 0;
	final protected static int STATE_PREPARING = 1;
	final protected static int STATE_RUNNING = 2;
	
	// Final Private Variables
	final private CopyOnWriteArrayList<SupportThread> mSupportServicePreparing;
	final private CopyOnWriteArrayList<SupportThread> mSupportServiceLaunched;
	final private SupportService mSupportService;
	
	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param name
	 */
	public SupportThreadGroup(final SupportService serviceSupport) {
		super(Thread.currentThread().getThreadGroup(), "MGServicesGroupSTG");
		mSupportServicePreparing = new CopyOnWriteArrayList<SupportThread>();
		mSupportServiceLaunched = new CopyOnWriteArrayList<SupportThread>();
		mSupportService = serviceSupport;
	}
	
	/**
	 * Get Support Service
	 * 
	 * @return
	 */
	final protected SupportService getSupportService() {
		return mSupportService;
	}
	
	/**
	 * Return Support Thread State.
	 * 
	 * @param serviceRunnable
	 *            Support Thread Command
	 * @return Support Thread Command
	 */
	final protected int getSupportThreadState(final int command) {
		// Use counter loop because this Support Thread Group use async
		// communication
		for (int i = 0; i < mSupportServicePreparing.size(); i++) {
			final SupportThread supportThread = mSupportServicePreparing.get(i);
			if (supportThread.getCommand() == command)
				return STATE_PREPARING;
		}
		for (int i = 0; i < mSupportServiceLaunched.size(); i++) {
			final SupportThread supportThread = mSupportServiceLaunched.get(i);
			// Does a service already completed
			if (supportThread.getCommand() == command && !supportThread.isEndWork()) {
				return STATE_RUNNING;
			}
		}
		return STATE_NONEXIST;
	}
	
	/**
	 * Prepare Support Thread to launch.
	 */
	final protected void prepareSupportThread(final int command) {
		// Safe Cache clear
		while (true) {
			boolean found = false;
			
			for (int i = 0; i < mSupportServiceLaunched.size(); i++) {
				final SupportThread supportThread = mSupportServiceLaunched.get(i);
				if (supportThread.isEndWork()) {
					mSupportServiceLaunched.remove(supportThread);
					found = true;
					break;
				}
			}
			if (!found)
				break;
		}
		
		// Launch all prepared Support Threads
		final SupportThread supportThread = new SupportThread(this, mSupportService, command);
		// Prepare Support Thread
		mSupportServicePreparing.add(supportThread);
	}
	
	/**
	 * Launch all Preparing Support Thread
	 */
	final protected void launchPreparedSupportThreads() {
		// Safe Prepare Threads and clear cache
		while (mSupportServicePreparing.size() > 0) {
			final SupportThread supportThread = mSupportServicePreparing.remove(0);
			killOrWaitSupportThread(supportThread.getCommand());
			supportThread.start();
			mSupportServiceLaunched.add(supportThread);
		}
	}
	
	/**
	 * Kill Support Thread.
	 * <p>
	 * Note: If the Support Thread is being prepared it also will be considered
	 * dead and the result will be true also.
	 * <p>
	 * 
	 * @param command
	 *            Support Command
	 * @return True if found
	 */
	final protected boolean killOrWaitSupportThread(final int command) {
		boolean found = false;
		// Safe search and clear
		for (int i = 0; i < mSupportServicePreparing.size(); i++) {
			final SupportThread supportThread = mSupportServicePreparing.get(i);
			if (supportThread.getCommand() == command) {
				mSupportServicePreparing.remove(supportThread);
				found = true;
			}
		}
		// Safe search and clear
		for (int i = 0; i < mSupportServiceLaunched.size(); i++) {
			final SupportThread supportThread = mSupportServiceLaunched.get(i);
			if (supportThread.getCommand() == command) {
				// If support thread working
				if (!supportThread.isEndWork())
					found = true;
				// Force close
				try {
					supportThread.join();
				} catch(Exception e) {};
				mSupportServiceLaunched.remove(supportThread);
			}
		}
		
		return found;
	}
	
	/**
	 * Close this Support Thread Group.
	 * <p>
	 * Note: This method force all Support Thread processes to close.
	 */
	final protected void close() {
		this.interrupt();
		mSupportServicePreparing.clear();
		// Safe clear
		while (mSupportServiceLaunched.size() > 0)  {
			mSupportServiceLaunched.remove(0).close();
		}
	}
}
