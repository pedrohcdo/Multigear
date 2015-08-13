package multigear.mginterface.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import multigear.cache.CacheManager;
import multigear.general.utils.SafetyLock;
import multigear.general.utils.SafetyLock.Interception;
import multigear.mginterface.engine.servicessuport.SupportService;
import multigear.mginterface.graphics.opengl.font.FontManager;
import multigear.mginterface.scene.Scene;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
@SuppressLint("NewApi")
final public class Multigear {
	
	/**
	 * On Engine Destroy Interface
	 * @author user
	 *
	 */
	public interface OnDestroyListener {
		
		/** 
		 * On Destroy.<br>
		 * <b>Note:</b> This function called in UI Thread and not GL Thread.
		 */
		public void onDestroy();
	}
	
	/**
	 * On Engine Paused Interface.
	 * @author user
	 *
	 */
	public interface OnPauseListener {
		
		/** 
		 * On Pause.<br>
		 * <b>Note:</b> This function called in UI Thread and not GL Thread.
		 */
		public void onPause();
	}
	
	/**
	 * On Engine Resume Interface.
	 * @author user
	 *
	 */
	public interface OnResumeListener {
		
		/** 
		 * On Pause.<br>
		 * <b>Note:</b> This function called in UI Thread and not GL Thread.
		 */
		public void onResume();
	}
	
	// Private Variables
	final private Activity mActivity;
	final private multigear.mginterface.engine.Configuration mConfiguration;
	final private AtomicBoolean mSyncStarted;
	final private multigear.mginterface.engine.Surface mSurface;
	final private multigear.mginterface.engine.Manager mManager;
	final private multigear.mginterface.engine.eventsmanager.EventHandler mEventHandler;
	final private WifiLock mWifiLock;
	
	final private List<OnDestroyListener> mOnDestroyListeners = new ArrayList<OnDestroyListener>();
	final private List<OnPauseListener> mOnPauseListeners = new ArrayList<OnPauseListener>();
	final private List<OnResumeListener> mOnResumeListeners = new ArrayList<OnResumeListener>();
	final private List<Runnable> mRunOnEngineThread = new ArrayList<Runnable>();
	
	final Intent mServiceIntent;
	
	// Private Variables
	private AtomicBoolean mFinished = new AtomicBoolean(false);
	private long mCurrsentTime;
	private boolean mFirstBind;
	private SupportService mSupportService;
	private boolean mSupportServiceResumed;
	
	/**
	 * Service Connection
	 */
	final ServiceConnection mServiceConnection = new ServiceConnection() {
		
		/**
		 * Service Disconnect
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSupportService = null;
		}
		
		/**
		 * Service Connect
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSupportService = ((SupportService.SupportBinder) service).getService();
			Multigear.this.onServiceConnected(mFirstBind);
			mFirstBind = false;
		}
	};
	
	/*
	 * Construtor
	 */
	public Multigear(final Activity activity, final multigear.mginterface.engine.Configuration configuration) {
		mActivity = activity;
		mConfiguration = configuration;
		mConfiguration.onEngineCreated(activity);
		mSyncStarted = new AtomicBoolean(false);
		mSurface = new multigear.mginterface.engine.Surface(this);
		mManager = new multigear.mginterface.engine.Manager(this);
		mEventHandler = new multigear.mginterface.engine.eventsmanager.EventHandler(mManager);
		mFirstBind = true;
		

		
		// The startService () starts the service and ensures that 
		// the service keeps running even if all clients are disconnected. 
		// The bindService () used without startService () may result in the 
		// closure of the service after all unbindService ().
		if(getConfiguration().hasFunc(Configuration.FUNC_RESTORER_SERVICE)) {
			mSupportServiceResumed = false;
			mServiceIntent = new Intent(activity, SupportService.class);
			activity.startService(mServiceIntent);
		} else {
			mServiceIntent = null;
		}
		
		// Aquire WifiLock in Full High Perf
		WifiManager wifiManager = (WifiManager)mActivity.getSystemService(Context.WIFI_SERVICE);
		mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MGLOCKTAG");
		mWifiLock.setReferenceCounted(true);
		mWifiLock.acquire();
		
		
		Log.d("LogTest", "Is Held: " + mWifiLock.isHeld());
	}
	
	/**
	 * Add On Destroy Listener
	 * @param listener
	 */
	final public void addOnDestroyListener(final OnDestroyListener listener) {
		mOnDestroyListeners.add(listener);
	}
	
	/**
	 * Add On Pause Listener
	 * @param listener
	 */
	final public void addOnPauseListener(final OnPauseListener listener) {
		mOnPauseListeners.add(listener);
	}
	
	/**
	 * Add On Resume Listener
	 * @param listener
	 */
	final public void addOnResumeListener(final OnResumeListener listener) {
		mOnResumeListeners.add(listener);
	}
	
	/**
	 * Add On Destroy Listener
	 * @param listener
	 */
	final public void removeOnDestroyListener(final OnDestroyListener listener) {
		mOnDestroyListeners.remove(listener);
	}
	
	/**
	 * Add On Pause Listener
	 * @param listener
	 */
	final public void removeOnPauseListener(final OnPauseListener listener) {
		mOnPauseListeners.remove(listener);
	}
	
	/**
	 * Add On Resume Listener
	 * @param listener
	 */
	final public void removeOnResumeListener(final OnResumeListener listener) {
		mOnResumeListeners.remove(listener);
	}
	
	/**
	 * Run on Engine Thread
	 * 
	 * @param runnable
	 */
	final public void runOnEngineThread(final Runnable runnable) {
		mRunOnEngineThread.add(runnable);
	}
	
	/**
	 * Resume Engine
	 */
	final public void onResume() {
		// Destroyed
		if(mFinished.get() == true)
			return;
		
		
		// If use Restorer
		if(getConfiguration().hasFunc(Configuration.FUNC_RESTORER_SERVICE)) {
			mSupportServiceResumed = true;
			// Binding with Service
			getActivity().bindService(mServiceIntent, mServiceConnection, Service.BIND_AUTO_CREATE);
		}
		mManager.resume();
		mEventHandler.sendHandle();
		// Call listeners
		for(final OnResumeListener listener : mOnResumeListeners) 
			listener.onResume();
	}
	
	/**
	 * Pause Engine
	 */
	final public void onPause() {
		// Destroyed
		if(mFinished.get() == true)
			return;
		
		// If use Support Service
		if(getConfiguration().hasFunc(Configuration.FUNC_RESTORER_SERVICE)) {
			// Submit a notice to pause() before
			final SupportService supportService = getSupportService();
			// If the service is bound
			if (supportService != null) {
				final Object notification = (Object)getConfiguration().getObjectAttr(Configuration.ATTR_RESTORER_NOTIFICATION);
				if(notification instanceof Notification)
					supportService.enginePaused((Notification)notification);
				else
					supportService.enginePaused(null);
			}
			// Pause Service
			mSupportServiceResumed = false;
			// Unbind Service Connection
			if (mSupportService != null)
				getActivity().unbindService(mServiceConnection);
		}
		mManager.pause();
		mEventHandler.sendUnhandle();
		// Call listeners
		for(final OnPauseListener listener : mOnPauseListeners) 
			listener.onPause();
	}
	
	/**
	 * Update Engine
	 */
	final protected void update() {
		for(int i=0; i<mRunOnEngineThread.size(); i++) {
			mRunOnEngineThread.remove(0).run();
		}
		mManager.update();
	}
	
	/**
	 * Destroy Engine
	 */
	final public void onDestroy() {
		// Destroy one time
		if(mFinished.getAndSet(true) == false) {
			// If use Support Service
			if(getConfiguration().hasFunc(Configuration.FUNC_RESTORER_SERVICE)) {
				final SupportService supportService = getSupportService();
				if(supportService != null) {
					Log.d("LogTest", "CLosing by onDestroy().");
					supportService.engineDestroyed();
				}
			}
			if(mWifiLock.isHeld())
				mWifiLock.release();
			mSurface.destroy();
			mManager.destroy();
			for(final OnDestroyListener listener : mOnDestroyListeners) 
				listener.onDestroy();
		}
	}
	
	/**
	 * Service Connected Service bound to the channel, ready for communication.
	 */
	final private void onServiceConnected(final boolean firstOccurrence) {
		final SupportService supportService = getSupportService();
		// This case is not necessary, but just in case.
		if (supportService != null) {
			// Send to service this engine initialized
			if (firstOccurrence) {
				supportService.enginePrepare();
				mActivity.startService(mServiceIntent);
				supportService.engineInitialized();
			}
			// Send to service this engine resumed
			supportService.engineResumed();
		}
	}
	
	/**
	 * Return SupportService.
	 * <p>
	 * Note: It's likely return a null instance. In this case it was not
	 * possible to establish a connection to the service channel.
	 * <p>
	 * 
	 * @return SupportService Connection Channel
	 */
	final protected SupportService getSupportService() {
		// If not connected
		if (mSupportService == null) {
			if (!mSupportServiceResumed)
				return null;
			// Wait for connection
			final boolean intercepted = SafetyLock.lock(5000, new Interception() {
				
				@Override
				public boolean onIntercept() {
					return mSupportService != null;
				}
			});
			if(!intercepted)
				return null;
		}
		// Return Support Service
		return mSupportService;
	}
	
	/*
	 * Retorna a atividade
	 */
	final public Activity getActivity() {
		return mActivity;
	}
	
	/*
	 * Configura a ativade corretamente
	 */
	@SuppressLint("NewApi")
	final protected void setupActivity() {
		// Disable Rotaition
		//mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// Disable Title Bar
		// mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Hide Action Bar
		if (Build.VERSION.SDK_INT >= 11) {
			mActivity.getActionBar().hide();
		} else {
			((ActionBarActivity) mActivity).getSupportActionBar().hide();
		}
		// Set Full Screen
		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		// Set Flags for Full Screen & Immersive Mode
		if (Build.VERSION.SDK_INT >= 19) {
			View decorView = mActivity.getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}
	
	/**
	 * Get Main Room
	 * @return
	 */
	final public Scene getMainRoom() {
		return mManager.getMainRoom();
	}
	
	/*
	 * Retorna a configura��o da Engine
	 */
	final public multigear.mginterface.engine.Configuration getConfiguration() {
		return mConfiguration;
	}
	
	/**
	 * Return the AudioManager.
	 * 
	 * @return {@link multigear.audio.AudioManager}
	 */
	final public multigear.audio.AudioManager getAudioManager() {
		return mManager.getAudioManager();
	}
	
	/**
	 * Get ComManager
	 * 
	 * @return {@link multigear.communication.tcp.support.ComManager}
	 */
	final public multigear.communication.tcp.support.ComManager getComManager() {
		return mManager.getComManager();
	}
	
	/**
	 * Get Space Parser
	 * 
	 * @return {@link multigear.mginterface.scene.SpaceParser}
	 */
	final public multigear.mginterface.scene.SpaceParser getSpaceParser() {
		return mManager.getSpaceParser();
	}
	
	/**
	 * Get Density Parser
	 * 
	 * @return {@link multigear.mginterface.scene.DensityParser}
	 */
	final public multigear.mginterface.scene.DensityParser getDensityParser() {
		return mManager.getDensityParser();
	}
	
	/**
	 * Get Font Manager
	 * 
	 * @return {@link FontManager}
	 */
	final public FontManager getFontManager() {
		return mManager.getFontManager();
	}
	
	/**
	 * Get Services Manager
	 * 
	 * @return {@link multigear.services.ServicesManager}
	 */
	final public multigear.services.ServicesManager getServicesManager() {
		return mManager.getServicesManager();
	}
	
	/**
	 * Get Cache Manager
	 * 
	 * @return {@link CacheManager}
	 */
	final public CacheManager getCacheManager() {
		return mManager.getCacheManager();
	}
	
	/*
	 * Retorna o gerenciador
	 */
	final protected multigear.mginterface.engine.Manager getManager() {
		return mManager;
	}
	
	/*
	 * Retorna a Surface
	 */
	final protected multigear.mginterface.engine.Surface getSurface() {
		return mSurface;
	}
	
	/*
	 * Sincroniza a Engine para chamadas de fun��es paralelas
	 */
	final public MultigearSync sync() {
		// Wait for sync
		while (mSyncStarted.getAndSet(true)) {
		}
		// Sync Engine
		return new MultigearSync(this, mEventHandler);
	}
	
	/**
	 * Return Blank events
	 * @return
	 */
	final public MultigearBlank blank() {
		return new MultigearBlank(this, mEventHandler);
	}
	
	/*
	 * Retorna uma instancia Thread Safe da Engine
	 */
	final public multigear.mginterface.engine.MultigearSafe safe() {
		// Safe Engine
		return new multigear.mginterface.engine.MultigearSafe(this, mEventHandler);
	}
	
	/*
	 * Remove a sincroniza��o em andamento
	 */
	final protected void unsync() {
		mSyncStarted.set(false);
	}
	
	/**
	 * Return true if this Engine is Finished.
	 * 
	 * @return True if this Engine is Finished
	 */
	final public boolean isFinished() {
		return mFinished.get() == true;
	}
}
