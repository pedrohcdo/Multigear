package com.org.multigear.services;

/**
 * Service Thread
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ServiceThread extends Thread {
	
	// Final Private Variables
	final private com.org.multigear.services.ServicesManager mServicesManager;
	final private com.org.multigear.services.ServiceRunnable mServiceRunnable;
	final private com.org.multigear.services.ServiceControl mServiceControl;
	
	// Private Variables
	private boolean mClosed;
	
	/**
	 * Construtor
	 */
	protected ServiceThread(final ServicesThreadGroup servicesGroup, final ServiceRunnable serviceRunnable) {
		super(servicesGroup, "MGServiceThread");
		//setDaemon(true);
		mServicesManager = servicesGroup.getServicesManager();
		mServiceRunnable = serviceRunnable;
		mServiceControl = new ServiceControl(this, mServicesManager);
		mClosed = false;
	}
	
	/**
	 * Return Service Runnable
	 * 
	 * @return Services.ServiceRunnable
	 */
	final protected com.org.multigear.services.ServiceRunnable getServiceRunnable() {
		return mServiceRunnable;
	}
	
	/**
	 * Runner
	 */
	@Override
	public void run() {
		// Run Runnable
		mServiceRunnable.run(mServiceControl);
	}
	
	/*
	 * Fecha a Thread
	 */
	final protected void close() {
		mClosed = true;
		boolean flag = false;
		while (!flag) {
			try {
				if(!isInterrupted())
					interrupt();
				this.join();
				flag = true;
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Return true if is closed
	 * 
	 * @return
	 */
	protected boolean isEndService() {
		return (isInterrupted() || mClosed);
	}
}
