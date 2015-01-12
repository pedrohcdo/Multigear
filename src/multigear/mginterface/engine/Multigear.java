package multigear.mginterface.engine;

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
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
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
	
	// Private Variables
	final private Activity mActivity;
	final private multigear.mginterface.engine.Configuration mConfiguration;
	final private AtomicBoolean mSyncStarted;
	final private multigear.mginterface.engine.Surface mSurface;
	final private multigear.mginterface.engine.Manager mManager;
	final private multigear.mginterface.engine.eventsmanager.EventHandler mEventHandler;
	
	final Intent mServiceIntent;
	
	// Private Variables
	private boolean mFinished;
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
		mFinished = false;
		mFirstBind = true;
		
		mSupportServiceResumed = false;
		mServiceIntent = new Intent(activity, SupportService.class);
		
		// The startService () starts the service and ensures that 
		// the service keeps running even if all clients are disconnected. 
		// The bindService () used without startService () may result in the 
		// closure of the service after all unbindService ().
		activity.startService(mServiceIntent);
	}
	
	/**
	 * Resume Engine
	 */
	final protected void resume() {
		mSupportServiceResumed = true;
		// Binding with Service
		getActivity().bindService(mServiceIntent, mServiceConnection, 0);
	}
	
	/**
	 * Pause Engine
	 */
	final protected void pause() {
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
	
	/**
	 * Update Engine
	 */
	final protected void update() {
		mManager.update();
	}
	
	/**
	 * Destroy Engine
	 */
	final public void destroy() {
		mFinished = true;
		mSurface.destroy();
	}
	
	/**
	 * Service Connected Service bound to the channel, ready for communication.
	 */
	final private void onServiceConnected(final boolean firstOccurrence) {
		final SupportService supportService = getSupportService();
		// This case is not necessary, but just in case.
		if (supportService != null) {
			// Send to service this engine initialized
			if (firstOccurrence)
				supportService.engineInitialized();
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
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
	 * Retorna a configuração da Engine
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
	 * Sincroniza a Engine para chamadas de funções paralelas
	 */
	final public MultigearSync sync() {
		// Wait for sync
		while (mSyncStarted.getAndSet(true)) {
		}
		// Sync Engine
		return new MultigearSync(this, mEventHandler);
	}
	
	/*
	 * Retorna uma instancia Thread Safe da Engine
	 */
	final public multigear.mginterface.engine.MultigearSafe safe() {
		// Safe Engine
		return new multigear.mginterface.engine.MultigearSafe(this, mEventHandler);
	}
	
	/*
	 * Remove a sincronização em andamento
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
		return mFinished;
	}
}
