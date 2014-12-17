package multigear.services;

/**
 * Service Thread
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ServiceThread extends Thread {
	
	// Final Private Variables
	final private multigear.services.ServicesManager mServicesManager;
	final private multigear.services.ServiceRunnable mServiceRunnable;
	final private multigear.services.ServiceControl mServiceControl;
	
	// Private Variables
	private boolean mClosed;
	
	/**
	 * Construtor
	 */
	protected ServiceThread(final multigear.services.ServicesThreadGroup servicesGroup, final multigear.services.ServiceRunnable serviceRunnable) {
		super(servicesGroup, "MGServiceThread");
		mServicesManager = servicesGroup.getServicesManager();
		mServiceRunnable = serviceRunnable;
		mServiceControl = new multigear.services.ServiceControl(this, mServicesManager);
		mClosed = false;
	}
	
	/**
	 * Return Service Runnable
	 * 
	 * @return Services.ServiceRunnable
	 */
	final protected multigear.services.ServiceRunnable getServiceRunnable() {
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
		return (Thread.currentThread().isInterrupted() || mClosed);
	}
}
