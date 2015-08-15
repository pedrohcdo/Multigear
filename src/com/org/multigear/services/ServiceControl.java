package com.org.multigear.services;

/**
 * Service Control
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ServiceControl {
	
	// Final Private Variables
	final com.org.multigear.services.ServiceThread mServiceThread;
	final private com.org.multigear.services.ServicesManager mServiceManager;
	
	/**
	 * Construtor
	 */
	public ServiceControl(final com.org.multigear.services.ServiceThread serviceThread, final com.org.multigear.services.ServicesManager serviceManager) {
		mServiceThread = serviceThread;
		mServiceManager = serviceManager;
	}
	
	/**
	 * Post Message
	 */
	final public void postMessage(final com.org.multigear.services.Message message) {
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
