package multigear.mginterface.engine.servicessuport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Used for Support Multigear Engine in background works.
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class SupportService extends Service {
	
	/**
	 * Support Binder
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public class SupportBinder extends Binder {
		
		/**
		 * Ger Support Service
		 * 
		 * @return
		 */
		final public SupportService getService() {
			return SupportService.this;
		}
	}
	
	// Final Private Variables
	final private SupportBinder mSupportBinder = new SupportBinder();
	
	// Private Variables
	private DedicatedServices mDedicatedServices;
	private SupportThreadGroup mSupportThreadGroup;
	private volatile boolean mEngineInitialized;
	private volatile boolean mEngineResumed;
	private boolean mNotificationShowed;
	private boolean mDestroyedBySystem;
	
	// Used for extra protection of support thread process used to kill app
	private boolean mAlive;
	private Object mAliveLock = new Object();
	
	/**
	 * Called on app created after really closed.
	 */
	@Override
	final public void onCreate() {
		mDedicatedServices = new DedicatedServices(this);
		mSupportThreadGroup = new SupportThreadGroup(this);
		mEngineInitialized = false;
		mEngineResumed = false;
		mNotificationShowed = false;
		mAlive = false;
	}
	
	/**
	 * On Bind from clients
	 * 
	 * Called in the first time and also when the onUnbind() returns false.
	 */
	@Override
	final public IBinder onBind(Intent intent) {
		return mSupportBinder;
	}
	
	/**
	 * On Rebind.
	 * 
	 * If the onUnbind() returns true, this will be called instead of onBind().
	 */
	@Override
	public void onRebind(Intent intent) {}
	
	/**
	 * On Unbind from clients
	 * 
	 * Called when the application disconnects this service. 
	 * As in cases that the application has been paused or else closed.
	 */
	@Override
	final public boolean onUnbind(Intent intent) {
		// When the application is to pause, a support thread will be performed
		// automatically already here, because it had been due to verify that the
		// application has been terminated.
		mSupportThreadGroup.launchPreparedSupportThreads();
		return true;
	}
	
	/**
	 * Show Notification and set this Service to Foreground.
	 */
	final private void showNotification(Notification notification) {
		
		// Lock
		//synchronized (mNotificationLock) {
			// If notification Showed
			if (mNotificationShowed)
				return;
			// Set notification showd
			mNotificationShowed = true;
			// Get Notification Manager
			final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// Build default notification
			if(notification == null) {
				Intent notificationIntent = new Intent(this, this.getClass());
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
				notification = new NotificationCompat
					.Builder(this)
					.setOngoing(true)
					//.setSmallIcon(com.createlier.pong.R.drawable.ic_launcher)
					.setContentTitle("Multigear")
					.setContentText("Start after game closed for restore all connections.")
					.setContentIntent(intent)
					.build();
			}
			// Notify
			notificationManager.notify(1450, notification);
			// Start Foreground
			startForeground(1450, notification);
		//}
	}
	
	/**
	 * Hide Notification and remove this Service of Foreground.
	 */
	final private void hideNotification() {
		//synchronized (mNotificationLock) {
			// Return if Notification not showed
			if (!mNotificationShowed)
				return;
			// Remove notification
			mNotificationShowed = false;
			stopForeground(true);
		//}
	}
	
	/**
	 * @param serviceClass
	 * @return
	 */
	final protected boolean isEngineRunning() {
		if(mDestroyedBySystem)
			return false;
		String[] activePackages;
		activePackages = getActivePackages();
			if(activePackages != null) {
			for (String activePackage : activePackages) {
				if(activePackage == null)
					continue;
				if (activePackage.equals("com.createlier.pongduo"))
					return true;
			}
		}
		activePackages = getActivePackagesCompat();
		if(activePackages != null) {
			for (String activePackage : activePackages) {
				if(activePackage == null)
					continue;
				if (activePackage.equals("com.createlier.pongduo"))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Get Active Packages Compat
	 * @return
	 */
	final private String[] getActivePackagesCompat() {
		ActivityManager activityService = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final List<ActivityManager.RunningTaskInfo> taskInfo = activityService.getRunningTasks(Integer.MAX_VALUE);
		final String[] activePackages = new String[taskInfo.size()];
		int i = 0;
		for(ActivityManager.RunningTaskInfo info : taskInfo) {
			if(info.numRunning > 0)
				activePackages[i++] = info.topActivity.getPackageName();
		}
		return activePackages;
	}
	
	/**
	 * Get Active Packages for KITKAT or Up
	 * @return
	 */
	String[] getActivePackages() {
		final Set<String> activePackages = new HashSet<String>();
		ActivityManager activityService = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final List<ActivityManager.RunningAppProcessInfo> processInfos = activityService.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
			if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				activePackages.addAll(Arrays.asList(processInfo.pkgList));
			}
		}
		return activePackages.toArray(new String[activePackages.size()]);
	}
	
	/**
	 * Prepare Engine
	 */
	final public void enginePrepare() {
		// For interrupt destroyer loop
		mEngineInitialized = false;
		// Kill all Support Thread
		mSupportThreadGroup.killOrWaitSupportThread(SupportThread.SUPPORT_DESROYER);
	}
	
	/**
	 * When Engine Destroyed
	 */
	final public void engineDestroyed() {
		mDestroyedBySystem = true;
	}
	
	/**
	 * Called when the engine was recently initialized.
	 */
	final public void engineInitialized() {
		// Set Engine Initialized
		mDedicatedServices.saveState();
		mEngineInitialized = true;
		mDestroyedBySystem = false;
	}
	
	/**
	 * Called when the engine was resumed.
	 */
	final public void engineResumed() {
		hideNotification();
		mEngineResumed = true;
		mSupportThreadGroup.killOrWaitSupportThread(SupportThread.SUPPORT_DESROYER);
	}
	
	/**
	 * Called when the engine was paused.
	 */
	final public void enginePaused(final Notification notification) {
		mEngineResumed = false;
		showNotification(notification);
		mSupportThreadGroup.prepareSupportThread(SupportThread.SUPPORT_DESROYER);
	}
	
	/**
	 * Called in internal Service after unbind.
	 */
	final public void engineDestroyedInternal(final SupportThread supportThread) {
		// Destroy if initialized
		if (mEngineInitialized) {
			
			// Engine destroyed
			mEngineInitialized = false;
			
			// Restore State
			mDedicatedServices.restoreState(supportThread);
			
			// If restored
			hideNotification();
			
			// If not started
			if(!supportThread.hasInterrupted()) {
				// Kill this service
				stopSelf();
				Log.d("LogTest", "S Stoped");
			}
			
		}
	}
	
	/**
	 * Return True if Engine was Initialized
	 * 
	 * @return True if Initialized
	 */
	final protected boolean isEngineInitialized() {
		return mEngineInitialized;
	}
	
	/**
	 * Return True if Engine was Resumed
	 * 
	 * @return True if Resumed
	 */
	final protected boolean isEngineResumed() {
		return mEngineResumed;
	}
}