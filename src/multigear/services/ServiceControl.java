package multigear.services;

/**
 * Service Control
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ServiceControl {
	
	// Final Private Variables
	final multigear.services.ServiceThread mServiceThread;
	final private multigear.services.ServicesManager mServiceManager;
	
	/**
	 * Construtor
	 */
	public ServiceControl(final multigear.services.ServiceThread serviceThread, final multigear.services.ServicesManager serviceManager) {
		mServiceThread = serviceThread;
		mServiceManager = serviceManager;
	}
	
	/**
	 * Post Message
	 */
	final public void postMessage(final multigear.services.Message message) {
		mServiceManager.postMessage(message);
	}
	
	/**
	 * Return true if is closed
	 * 
	 * @return
	 */
	final public boolean isEndService() {
		return mServiceThread.isEndService();
	}
}
