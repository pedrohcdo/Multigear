package multigear.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Services Group
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ServicesThreadGroup extends ThreadGroup {

	// Final Private Variables
	final private multigear.services.ServicesManager mServicesManager;
	final private List<multigear.services.ServiceThread> mServices;
	
	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param name
	 */
	public ServicesThreadGroup(multigear.services.ServicesManager servicesManager) {
		super(Thread.currentThread().getThreadGroup(), "MGServicesGroup");
		mServicesManager = servicesManager;
		mServices = new ArrayList<ServiceThread>();
	}
	
	/**
	 * Get Services Manager
	 * 
	 * @return Services.ServicesManager
	 */
	final protected multigear.services.ServicesManager getServicesManager() {
		return mServicesManager;
	}
	
	/**
	 * Return True if has Service Thread from Runnable
	 * 
	 * @param serviceRunnable
	 * @return
	 */
	final protected boolean hasServiceThread(final multigear.services.ServiceRunnable serviceRunnable) {
		synchronized (mServices) {
			for(final multigear.services.ServiceThread serviceThread : mServices) {
				if(serviceThread.getServiceRunnable() == serviceRunnable) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Add Service Thread
	 */
	final protected void addServiceThread(final multigear.services.ServiceRunnable serviceRunnable) {
		if(hasServiceThread(serviceRunnable))
			throw new multigear.services.ServiceException("This process is already running for another service. This same instance can not be used in two services.");
		synchronized (mServices) {
			final multigear.services.ServiceThread serviceThread = new multigear.services.ServiceThread(this, serviceRunnable);
			mServices.add(serviceThread);
			serviceThread.start();
		}
	}
	
	/**
	 * Remove Service Thread
	 */
	final protected void removeServiceThread(final multigear.services.ServiceRunnable serviceRunnable) {
		synchronized (mServices) {
			Iterator<multigear.services.ServiceThread> itr = mServices.iterator();
			while(itr.hasNext()) {
				final multigear.services.ServiceThread serviceThread = itr.next();
				if(serviceThread.getServiceRunnable() == serviceRunnable) {
					// Close Thread
					serviceThread.interrupt();
					serviceThread.close();
					// Remove from Stack
					itr.remove();
					// Service Removed
					return;
				}
			}
		}
		throw new multigear.services.ServiceException("Unable to remove this service. This process was not found in any service.");
	}
	
	/**
	 * Wait for Service End
	 */
	final protected void waitForService(final multigear.services.ServiceRunnable serviceRunnable) {
		multigear.services.ServiceThread serviceThread = null;
		// Lock
		synchronized (mServices) {
			Iterator<multigear.services.ServiceThread> itr = mServices.iterator();
			while(itr.hasNext()) {
				final multigear.services.ServiceThread findServiceThread = itr.next();
				if(findServiceThread.getServiceRunnable() == serviceRunnable) {
					serviceThread = findServiceThread;
					break;
				}
			}
		}
		//
		if(serviceThread == null)
			throw new multigear.services.ServiceException("Unable to wait for service. This process was not found in any service.");
		boolean flag = false;
		while (!flag) {
			try {
				serviceThread.join();
				flag = true;
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Close Group
	 */
	final protected void close() {
		this.interrupt();
		synchronized (mServices) {
			for(final multigear.services.ServiceThread serviceThread : mServices)
				serviceThread.close();
		}
	}
}
