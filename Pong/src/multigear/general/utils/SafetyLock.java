package multigear.general.utils;

/**
 * Safety Lock
 * <p>
 * Used to block a process safely.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class SafetyLock {
	
	/**
	 * Interception used to intercept a launched lock.
	 * <p>
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	public interface Interception {
		
		/** On Intercept callback */
		public boolean onIntercept();
	}
	
	/**
	 * Locks a process at a given time in seconds.
	 * To break the lock, return true on Interception callback.
	 * <p>
	 * @param delayInMillis Time in Seconds.
	 * @return Return true if interrupt
	 */
	final public static boolean lock(final long delayInMillis, final Interception interception) {
		final long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) < delayInMillis) {
			// If intercepted or Interrupted
			if(interception.onIntercept() || Thread.currentThread().isInterrupted())
				return true;
		}
		// Not interrupt
		return false;
	}
	
	/**
	 * Locks a process.
	 * To break the lock, return true on Interception callback.
	 * <p>
	 * @param delayInMillis Time in Seconds.
	 */
	final public static void lock(final Interception interception) {
		while(true) {
			// If intercepted or Interrupted
			if(interception.onIntercept() || Thread.currentThread().isInterrupted())
				break;
		}
	}
}
