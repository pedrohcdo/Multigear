package multigear.services;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import multigear.communication.tcp.client.ServersList;
import multigear.communication.tcp.support.ComManager;
import multigear.communication.tcp.support.ConnectionInfo;
import multigear.communication.tcp.support.SupportMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.general.utils.SafetyLock;
import multigear.general.utils.SafetyLock.Interception;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Connection Manager
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ServicesManager implements multigear.communication.tcp.support.listener.Listener {
	
	/**
	 * Access Point Security
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	public enum Security {
		
		WEP,
		PSK,
		EAP,
		OPENED;
	}
	
	/**
	 * Hotspot State
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	public enum HotspotState {
		
		STATE_DISABLING,
		STATE_DISABLED,
		STATE_ENABLING,
		STATE_ENABLED,
		STATE_FAILED;
	}
	
	/**
	 * Wifi Priority Comparator
	 */
	final Comparator<WifiConfiguration> mWifiPriorityComparator = new Comparator<WifiConfiguration>() {
		
		/**
		 * Compare
		 * 
		 * @param lhs
		 * @param rhs
		 * @return
		 */
		@Override
		public int compare(WifiConfiguration lhs, WifiConfiguration rhs) {
			return lhs.priority - rhs.priority;
		}
	};
	
	/**
	 * Used for save and restore the state of ServicesManager.
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public class Database {
		
		// Private Variables
		private boolean WifiEnabled;
		private boolean HotspotEnabled;
		private boolean MobileDataEnabled;
		private List<WifiConfiguration> ConfiguredNetworks;
		private WifiConfiguration HotspotConfiguration;
	}
	
	/**
	 * Delay Service
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final private class DelayService {
		
		// Final Private Variables
		final private multigear.services.ServiceRunnable mServiceRunnable;
		final private long mScheduleTime;
		
		/**
		 * Constructor
		 */
		private DelayService(final ServiceRunnable serviceRunnable, final long scheduleTime) {
			mServiceRunnable = serviceRunnable;
			mScheduleTime = scheduleTime;
		}
	}
	
	// Final Private Variables
	final private multigear.mginterface.engine.Multigear mEngine;
	final private multigear.services.Receiver mReceiver;
	final private List<multigear.services.Message> mMessages;
	final private WifiManager mWifiManager;
	final private Object mHotspotLock;
	final private Object mScanLock;
	final private multigear.services.ServicesThreadGroup mServicesGroup;
	final private multigear.communication.tcp.support.ComManager mComManager;
	final private List<DelayService> mDelayServices;
	final private Database mDatabase;
	
	// Private Variables
	private multigear.services.Listener mListener;
	private boolean mScanStarted;
	private Object mScanFilterLock;
	private boolean mImmediateAction;
	private List<ScanResult> mScanWifiAccessPointsList;
	private multigear.services.AccessPointsFilter mScanFilter;
	private multigear.communication.tcp.client.ServersList mServersList;
	
	/**
	 * Construtor
	 */
	public ServicesManager(final multigear.mginterface.engine.Manager manager) {
		mEngine = manager.getEngine();
		mReceiver = new multigear.services.Receiver(this);
		mMessages = new ArrayList<multigear.services.Message>();
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		//intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		//intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mEngine.getActivity().registerReceiver(mReceiver, intentFilter);
		mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		mListener = null;
		mServicesGroup = new multigear.services.ServicesThreadGroup(this);
		mHotspotLock = new Object();
		mScanLock = new Object();
		mImmediateAction = false;
		mComManager = new ComManager(manager);
		mComManager.setListener(this);
		mDelayServices = new ArrayList<ServicesManager.DelayService>();
		mDatabase = new Database();
		mServersList = null;
		mScanFilter = null;
		mScanFilterLock = null;
	}
	
	/**
	 * Get Activity
	 * 
	 * @return Current Activity
	 */
	final private Activity getActivity() {
		return mEngine.getActivity();
	}
	
	/**
	 * Set connection port. Default is 4545.
	 * 
	 * @param port
	 *            Port
	 */
	final public void setConnectionPort(final int port) {
		mComManager.setConnectionPort(port);
	}
	
	/**
	 * Get WifiManager
	 * 
	 * @return WifiManager
	 */
	final protected WifiManager getWifiManager() {
		return mWifiManager;
	}
	
	/**
	 * Set Listener
	 * 
	 * @param listener
	 *            {@link multigear.services.Listener}
	 */
	final public void setListener(final multigear.services.Listener listener) {
		synchronized (mMessages) {
			mListener = listener;
		}
	}
	
	/**
	 * Set Immediate Action.
	 * 
	 * Note: Immediate action can be time consuming, so the current process will
	 * block until the action has been completed.
	 * 
	 * Default is False.
	 * 
	 * @param trigger
	 *            true/false
	 */
	final public void setImmediateAction(final boolean trigger) {
		mImmediateAction = trigger;
	}
	
	/**
	 * Return true if immediate
	 * 
	 * @return
	 */
	final public boolean isImmediateAction() {
		return mImmediateAction;
	}
	
	/**
	 * Update Connection Manager.
	 * 
	 * This method is automatically called by Engine.
	 */
	final public void update() {
		// Update Communication Manager
		mComManager.update();
		// Update Services Message
		synchronized (mMessages) {
			if (mListener != null) {
				for (final multigear.services.Message message : mMessages)
					mListener.onServiceMessage(message);
			}
			mMessages.clear();
		}
		// Update Delay Services
		// if the service is waiting
		synchronized (mDelayServices) {
			final Iterator<DelayService> itr = mDelayServices.iterator();
			while (itr.hasNext()) {
				final DelayService delayService = itr.next();
				if (GlobalClock.currentTimeMillis() > delayService.mScheduleTime) {
					addService(delayService.mServiceRunnable);
					itr.remove();
				}
			}
		}
	}
	
	/**
	 * Add a Message
	 * 
	 * @param message
	 * @param object
	 */
	final protected void postMessage(final int message) {
		synchronized (mMessages) {
			mMessages.add(new multigear.services.Message(message));
		}
	}
	
	/**
	 * Add a Message
	 * 
	 * @param message
	 * @param object
	 */
	final protected void postMessage(final multigear.services.Message message) {
		synchronized (mMessages) {
			mMessages.add(message);
		}
	}
	
	/**
	 * Add Service Thread
	 */
	final public void addService(final multigear.services.ServiceRunnable serviceRunnable) {
		mServicesGroup.addServiceThread(serviceRunnable);
	}
	
	/**
	 * Add Service Thread with Delay
	 */
	final public void addService(final multigear.services.ServiceRunnable serviceRunnable, final long delay) {
		mDelayServices.add(new DelayService(serviceRunnable, GlobalClock.currentTimeMillis() + delay));
	}
	
	/**
	 * Remove Service Thread
	 */
	final public void removeService(final multigear.services.ServiceRunnable serviceRunnable) {
		synchronized (mDelayServices) {
			for (final DelayService delayService : mDelayServices)
				if (delayService.mServiceRunnable == serviceRunnable)
					mDelayServices.remove(delayService);
		}
		mServicesGroup.removeServiceThread(serviceRunnable);
	}
	
	/**
	 * Saves the current state of the service manager. This is valid for the
	 * following services: Wi-fi, Hotspot, 3G.
	 */
	final public Database saveState() {
		mDatabase.WifiEnabled = isWifiEnabled();
		mDatabase.HotspotEnabled = isHotspotEnabled();
		mDatabase.MobileDataEnabled = isMobileDataEnabled();
		mDatabase.ConfiguredNetworks = getConfiguredNetworksSafety();
		mDatabase.HotspotConfiguration = getHotspotConfiguration();
		return mDatabase;
	}
	
	/**
	 * Saves the current state of the service manager. This is valid for the
	 * following services: Wi-fi, Hotspot, 3G.
	 * <p>
	 * 
	 * @param predictedState
	 *            Predicted Database State
	 */
	final public void savePredictedState(final Database predictedState) {
		mDatabase.WifiEnabled = predictedState.WifiEnabled;
		mDatabase.HotspotEnabled = predictedState.HotspotEnabled;
		mDatabase.MobileDataEnabled = predictedState.MobileDataEnabled;
		mDatabase.ConfiguredNetworks = predictedState.ConfiguredNetworks;
		mDatabase.HotspotConfiguration = predictedState.HotspotConfiguration;
	}
	
	/**
	 * Restores the last saved state of the service manager. This is valid for
	 * the following services: Wi-fi, Hotspot, 3G.
	 */
	final private multigear.services.ServiceRunnable restoreState(final boolean immediateAction) {
		if (immediateAction) {
			// Restore Wifi State
			if (mDatabase.WifiEnabled)
				enableWifi(true);
			else
				disableWifi(true);
			// Restore Configured Networks
			final List<Integer> foundNetworkList = new ArrayList<Integer>();
			for (final WifiConfiguration savedNetworkConfiguration : mDatabase.ConfiguredNetworks) {
				final WifiConfiguration findConfiguredNetwork = getConfiguredNetworkBSSID(savedNetworkConfiguration.BSSID, savedNetworkConfiguration.SSID);
				if (findConfiguredNetwork != null)
					foundNetworkList.add(findConfiguredNetwork.networkId);
			}
			boolean refresh = false;
			for (final WifiConfiguration configuredNetwork : getConfiguredNetworksSafety()) {
				if (!foundNetworkList.contains(configuredNetwork.networkId)) {
					mWifiManager.removeNetwork(configuredNetwork.networkId);
					refresh = true;
				}
			}
			if (refresh)
				mWifiManager.saveConfiguration();
			// Restore Hotspot State
			if (mDatabase.HotspotEnabled)
				enableHotspot(mDatabase.HotspotConfiguration, true);
			else {
				disableHotspot(true);
				setHotspotConfiguration(mDatabase.HotspotConfiguration);
			}
			// Restore Mobile Data State
			if (mDatabase.MobileDataEnabled)
				enableMobileData(true);
			else
				disableMobileData(true);
			// No services launched
			return null;
		} else {
			// Service Runnable
			multigear.services.ServiceRunnable service = new ServiceRunnable() {
				
				/**
				 * Runner
				 */
				@Override
				public void run(ServiceControl serviceControl) {
					// Restore Wifi State
					if (mDatabase.WifiEnabled)
						enableWifi(true);
					else
						disableWifi(true);
					// Restore Configured Networks
					final List<Integer> foundNetworkList = new ArrayList<Integer>();
					for (final WifiConfiguration savedNetworkConfiguration : mDatabase.ConfiguredNetworks) {
						final WifiConfiguration findConfiguredNetwork = getConfiguredNetworkBSSID(savedNetworkConfiguration.BSSID, savedNetworkConfiguration.SSID);
						if (findConfiguredNetwork != null)
							foundNetworkList.add(findConfiguredNetwork.networkId);
					}
					boolean refresh = false;
					for (final WifiConfiguration configuredNetwork : getConfiguredNetworksSafety()) {
						if (!foundNetworkList.contains(configuredNetwork.networkId)) {
							mWifiManager.removeNetwork(configuredNetwork.networkId);
							refresh = true;
						}
					}
					if (refresh)
						mWifiManager.saveConfiguration();
					// Restore Hotspot State
					if (mDatabase.HotspotEnabled)
						enableHotspot(mDatabase.HotspotConfiguration, true);
					else {
						disableHotspot(true);
						setHotspotConfiguration(mDatabase.HotspotConfiguration);
					}
					// Restore Mobile Data State
					if (mDatabase.MobileDataEnabled)
						enableMobileData(true);
					else
						disableMobileData(true);
					Log.d("LogTest", "Restored");
					// Post Restored Message
					postMessage(Message.STATE_RESTORED);
				}
			};
			// Add service
			addService(service);
			// Return Service
			return service;
		}
	}
	
	/**
	 * Restores the last saved state of the service manager. This is valid for
	 * the following services: Wi-fi, Hotspot, 3G.
	 */
	final public multigear.services.ServiceRunnable restoreState() {
		return restoreState(mImmediateAction);
	}
	
	/**
	 * Wait for Service process end.
	 * <p>
	 * Note: If the process is not an end, the current process had been stuck in
	 * this statement.
	 * <p>
	 * 
	 * @param serviceRunnable
	 */
	final public void waitForService(final multigear.services.ServiceRunnable serviceRunnable) {
		// if the service is waiting
		synchronized (mDelayServices) {
			for (final DelayService delayService : mDelayServices)
				if (delayService.mServiceRunnable == serviceRunnable) {
					while (delayService.mScheduleTime > GlobalClock.currentTimeMillis() && !Thread.currentThread().isInterrupted()) {
					}
					addService(delayService.mServiceRunnable);
					mDelayServices.remove(delayService);
					break;
				}
		}
		// Wait for end service
		mServicesGroup.waitForService(serviceRunnable);
	}
	
	/**
	 * Enable Wifi
	 * 
	 * Note: Need Permissions "CHANGE_WIFI_STATE"
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable enableWifi(final boolean immediate) {
		// If Immediate Action
		if (immediate) {
			// Disable Mobile Data Immediate
			disableMobileData(true);
			// Immediate Disable Hotspot
			disableHotspot(true);
			// Wait For Wifi States
			while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
				;
			// If Wifi Enabled ignore this process
			if (isWifiEnabled())
				return null;
			// Enable Wifi
			mWifiManager.setWifiEnabled(true);
			// Wait for enable complete
			while (!mWifiManager.isWifiEnabled()) {
			}
			// Wait For Wifi States
			while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
				;
			// No service launched
			return null;
		} else {
			// Service Runnable
			final multigear.services.ServiceRunnable serviceRunnable = new ServiceRunnable() {
				
				/** Runner */
				@Override
				public void run(multigear.services.ServiceControl serviceControl) {
					// Disable Mobile Data Immediate
					disableMobileData(true);
					// Immediate Disable Hotspot
					disableHotspot(true);
					// Wait For Wifi States
					while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
						;
					// If Wifi Enabled ignore this process
					if (isWifiEnabled()) {
						postMessage(multigear.services.Message.WIFI_ENABLED);
						return;
					}
					// Enable Wifi
					mWifiManager.setWifiEnabled(true);
					while (!mWifiManager.isWifiEnabled()) {
					}
					// Wait For Wifi States
					while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
						;
					// Enabled
					postMessage(multigear.services.Message.WIFI_ENABLED);
				}
			};
			// Add Service
			addService(serviceRunnable);
			// Return Service
			return serviceRunnable;
		}
	}
	
	/**
	 * Enable Wifi
	 * 
	 * Note: Need Permissions "CHANGE_WIFI_STATE"
	 */
	final public multigear.services.ServiceRunnable enableWifi() {
		return enableWifi(mImmediateAction);
	}
	
	/**
	 * Disable Wifi
	 * 
	 * Note: Need Permissions "CHANGE_WIFI_STATE"
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable disableWifi(final boolean immediate) {
		// If Immediate Action
		if (immediate) {
			// Wait For Wifi States
			while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
				;
			// If Wifi Enabled ignore this process
			if (!isWifiEnabled())
				return null;
			// Disable Wifi
			mWifiManager.setWifiEnabled(false);
			// Wait for disable complete
			while (mWifiManager.isWifiEnabled()) {
			}
			// Wait for end state
			while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
			}
			;
			// No Service Launched
			return null;
		} else {
			// Service Runnable
			final multigear.services.ServiceRunnable serviceRunnable = new ServiceRunnable() {
				
				/** Runner */
				@Override
				public void run(multigear.services.ServiceControl serviceControl) {
					// Wait For Wifi States
					while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
						;
					// If Wifi Enabled ignore this process
					if (!isWifiEnabled()) {
						postMessage(multigear.services.Message.WIFI_DISABLED);
						return;
					}
					// Disable Wifi
					mWifiManager.setWifiEnabled(false);
					// Wait for disable complete
					while (mWifiManager.isWifiEnabled()) {
					}
					// Wait end state
					while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
					}
					;
					// Post Message
					postMessage(multigear.services.Message.WIFI_DISABLED);
				}
			};
			// Add Service
			addService(serviceRunnable);
			// Return launched service
			return serviceRunnable;
		}
	}
	
	/**
	 * Disable Wifi
	 * 
	 * Note: Need Permissions "CHANGE_WIFI_STATE"
	 */
	final public multigear.services.ServiceRunnable disableWifi() {
		return disableWifi(mImmediateAction);
	}
	
	/**
	 * Get Scan Result Security. If is opened return empty list.
	 * 
	 * @param scanResult
	 * @return
	 */
	final public List<Security> getScanResultSecurity(ScanResult scanResult) {
		final List<Security> securityList = new ArrayList<ServicesManager.Security>();
		if (scanResult.capabilities.contains("WEP"))
			securityList.add(Security.WEP);
		if (scanResult.capabilities.contains("PSK"))
			securityList.add(Security.PSK);
		if (scanResult.capabilities.contains("EAP"))
			securityList.add(Security.EAP);
		return securityList;
	}
	
	/**
	 * Add Newtowork
	 * 
	 * @param wifiConfiguration
	 * @return Network ID
	 */
	final public int addNetwork(final WifiConfiguration wifiConfiguration) {
		final WifiConfiguration configuredNetwork = getConfiguredNetworkBSSID(wifiConfiguration.BSSID, wifiConfiguration.SSID);
		if (configuredNetwork != null)
			mWifiManager.removeNetwork(configuredNetwork.networkId);
		return mWifiManager.addNetwork(wifiConfiguration);
	}
	
	/**
	 * Add or Update Network
	 * 
	 * @param wifiConfiguration
	 * @return
	 */
	final public int addOrUpdateNetwork(final WifiConfiguration wifiConfiguration) {
		int netId;
		final WifiConfiguration configuredNetwork = getConfiguredNetworkBSSID(wifiConfiguration.BSSID, wifiConfiguration.SSID);
		// If not found configured network
		if (configuredNetwork == null)
			netId = addNetwork(wifiConfiguration);
		else {
			wifiConfiguration.networkId = configuredNetwork.networkId;
			netId = mWifiManager.updateNetwork(wifiConfiguration);
			mWifiManager.saveConfiguration();
		}
		return netId;
	}
	
	/**
	 * Remove Network
	 * 
	 * @param netId
	 *            Network ID
	 * @return
	 */
	final public boolean removeNetork(final int netId) {
		return mWifiManager.removeNetwork(netId);
	}
	
	/**
	 * Return Configured list of Networks.
	 * 
	 * @return
	 */
	final List<WifiConfiguration> getConfiguredNetworks() {
		return mWifiManager.getConfiguredNetworks();
	}
	
	/**
	 * Create WPA Wifi Configuration
	 * 
	 * @param ssid
	 * @param password
	 * @return
	 */
	final public WifiConfiguration createWPAConfiguration(final String ssid, final String bssid, final String password) {
		WifiConfiguration wifiConfiguration = new WifiConfiguration();
		wifiConfiguration.SSID = "\"" + ssid + "\"";
		wifiConfiguration.BSSID = bssid;
		wifiConfiguration.preSharedKey = "\"" + password + "\"";
		wifiConfiguration.status = Status.DISABLED;
		wifiConfiguration.priority = 40;
		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wifiConfiguration.allowedProtocols.set(WifiConfiguration.GroupCipher.WEP104);
		wifiConfiguration.allowedProtocols.set(WifiConfiguration.GroupCipher.WEP40);
		return wifiConfiguration;
	}
	
	/**
	 * Add Newtowork
	 * 
	 * @param wifiCOnfiguration
	 * @return Network ID
	 */
	final public int addWPANetwork(final String ssid, final String bssid, final String password) {
		return addNetwork(createWPAConfiguration(ssid, bssid, password));
	}
	
	/**
	 * Wait For Wifi Connection
	 * 
	 * @return
	 */
	final private boolean waitForWifiConnetionComplete(final int networkId, final multigear.services.ServiceControl serviceControl, boolean extraSleep) {
		// Set Last Time
		long lastTime = System.currentTimeMillis();
		boolean completed = false;
		// Wait 10 sec
		while ((System.currentTimeMillis() - lastTime) < 10000) {
			// If end service
			if (serviceControl != null) {
				if (serviceControl.isEndService())
					break;
			}
			// Get Connection Info
			final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			final SupplicantState state = wifiInfo.getSupplicantState();
			// Check if Connection Correctly
			if (networkId == wifiInfo.getNetworkId() && state == SupplicantState.COMPLETED) {
				completed = true;
				break;
			}
		}
		// If Error
		if (!completed)
			return false;
		// If not need sleep
		if (!extraSleep)
			return true;
		// Set Last Time
		lastTime = System.currentTimeMillis();
		
		// Wait unlock
		realleyConnect: while ((System.currentTimeMillis() - lastTime) < 120000) {
			
			// Wait for really connection
			ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			for (NetworkInfo netInfo : cm.getAllNetworkInfo()) {
				if (netInfo.isConnected()) {
					if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						break realleyConnect;
					}
				}
			}
			
			// If end service
			if (serviceControl != null) {
				if (serviceControl.isEndService())
					break;
			}
		}
		Log.d("LogTest", "Tempo decorrido na conexão: " + (System.currentTimeMillis() - lastTime));
		// Time out
		return true;
	}
	
	/**
	 * Return Configured Network WifiConfiguration.
	 * 
	 * @param netId
	 * @return
	 */
	final public WifiConfiguration getConfiguredNetwork(final int netId) {
		final List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		if (configuredNetworks == null)
			return null;
		for (final WifiConfiguration wifiConfiguration : configuredNetworks)
			if (wifiConfiguration.networkId == netId)
				return wifiConfiguration;
		return null;
	}
	
	/**
	 * Return Configured Network WifiConfiguration.
	 * 
	 * @param netId
	 * @return
	 */
	final public WifiConfiguration getConfiguredNetwork(final String ssid) {
		final List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		if (configuredNetworks == null)
			return null;
		for (final WifiConfiguration wifiConfiguration : configuredNetworks) {
			if (wifiConfiguration.SSID.equals('"' + ssid + '"') || ssid.equals('"' + wifiConfiguration.SSID + '"') || wifiConfiguration.SSID.equals(ssid))
				return wifiConfiguration;
		}
		return null;
	}
	
	/**
	 * Return Configured Network WifiConfiguration with BSSID.
	 * 
	 * @param netId
	 * @return
	 */
	final private WifiConfiguration getConfiguredNetworkBSSID(final String bssid, final String default_ssid) {
		final List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		if (configuredNetworks == null)
			return null;
		for (final WifiConfiguration wifiConfiguration : configuredNetworks) {
			if (wifiConfiguration.BSSID == null)
				continue;
			if (wifiConfiguration.BSSID.equals(bssid))
				return wifiConfiguration;
		}
		return getConfiguredNetwork(default_ssid);
	}
	
	/**
	 * Get all Configured Netowrks List
	 * 
	 * @return
	 */
	final private List<WifiConfiguration> getConfiguredNetworksSafety() {
		final List<WifiConfiguration> wifiConfiguredList = new ArrayList<WifiConfiguration>();
		final List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		if (configuredNetworks != null) {
			for (final WifiConfiguration wifiConfigured : configuredNetworks)
				wifiConfiguredList.add(wifiConfigured);
		}
		return wifiConfiguredList;
	}
	
	/**
	 * Return Top Priority of all configured Wifi.
	 * 
	 * @return
	 */
	final private int getTopWifiPriority() {
		int topPriority = 0;
		final List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		if (configuredNetworks != null) {
			for (final WifiConfiguration wifiConfiguration : configuredNetworks)
				topPriority = Math.max(wifiConfiguration.priority, topPriority);
		}
		return topPriority;
	}
	
	/**
	 * Save Networks Priority to a List
	 * 
	 * @return
	 */
	final private List<WifiConfiguration> saveNetoworksPriority() {
		final List<WifiConfiguration> networksList = new ArrayList<WifiConfiguration>();
		final List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
		if (configuredNetworks != null) {
			for (final WifiConfiguration wifiConfiguration : configuredNetworks) {
				final WifiConfiguration saveWifiConfiguration = new WifiConfiguration();
				saveWifiConfiguration.SSID = wifiConfiguration.SSID;
				saveWifiConfiguration.priority = wifiConfiguration.priority;
				networksList.add(saveWifiConfiguration);
			}
		}
		return networksList;
	}
	
	/**
	 * Restore Networks priority by List
	 */
	final private void restoreNetworksPriority(final List<WifiConfiguration> networksPriority) {
		for (final WifiConfiguration wifiConfiguration : networksPriority) {
			final WifiConfiguration wifiConfigurationInstance = getConfiguredNetwork(wifiConfiguration.SSID);
			if (wifiConfigurationInstance != null) {
				wifiConfigurationInstance.priority = wifiConfiguration.priority;
				mWifiManager.updateNetwork(wifiConfigurationInstance);
			}
		}
		mWifiManager.saveConfiguration();
		mWifiManager.reassociate();
	}
	
	/**
	 * Connect to Configured Network
	 * 
	 * @param networkId
	 *            Configured Network Id
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable connectTo(final int networkId, final boolean immediate) {
		// If invalid Network
		if (networkId == -1)
			throw new multigear.services.ServiceException("Could not connect to the network. Id provided is invalid.");
		// If immediate action
		if (immediate) {
			// Update All Configured Wid Priority
			if (getTopWifiPriority() >= Integer.MAX_VALUE) {
				final List<WifiConfiguration> wifiConfigurationList = getConfiguredNetworksSafety();
				Collections.sort(wifiConfigurationList, mWifiPriorityComparator);
				int priorityControl = 0;
				for (final WifiConfiguration wifiConfiguration : wifiConfigurationList) {
					wifiConfiguration.priority = priorityControl++;
					mWifiManager.updateNetwork(wifiConfiguration);
				}
			}
			// Disconnect for work
			mWifiManager.disconnect();
			// Set Network to top prioriry
			final WifiConfiguration wifiConfiguration = getConfiguredNetwork(networkId);
			wifiConfiguration.priority = getTopWifiPriority();
			final int reconfiguredNetId = mWifiManager.updateNetwork(wifiConfiguration);
			// Connect to network
			mWifiManager.enableNetwork(reconfiguredNetId, true);
			// Reconnect
			mWifiManager.reconnect();
			// Wait for Connection Completed
			if (!waitForWifiConnetionComplete(networkId, null, true)) {
				Log.d("LogTest", "Connect to " + wifiConfiguration.SSID + " Failed!");
			}
			// No Service Launched
			return null;
		} else {
			// Create Service Runnable
			multigear.services.ServiceRunnable serviceRunnable = new ServiceRunnable() {
				
				/**
				 * Runner
				 */
				@Override
				public void run(ServiceControl serviceControl) {
					// Update All Configured Wid Priority
					if (getTopWifiPriority() >= Integer.MAX_VALUE) {
						final List<WifiConfiguration> wifiConfigurationList = getConfiguredNetworksSafety();
						Collections.sort(wifiConfigurationList, mWifiPriorityComparator);
						int priorityControl = 0;
						for (final WifiConfiguration wifiConfiguration : wifiConfigurationList) {
							wifiConfiguration.priority = priorityControl++;
							mWifiManager.updateNetwork(wifiConfiguration);
						}
					}
					// Set Network to top prioriry
					final WifiConfiguration wifiConfiguration = getConfiguredNetwork(networkId);
					wifiConfiguration.priority = getTopWifiPriority() + 1;
					final int reconfiguredNetId = mWifiManager.updateNetwork(wifiConfiguration);
					// Disconnect for work
					mWifiManager.disconnect();
					// Connect to network
					mWifiManager.enableNetwork(reconfiguredNetId, false);
					// Reassociate Connections
					mWifiManager.reconnect();
					// Wait for Connection Completed
					if (!waitForWifiConnetionComplete(networkId, serviceControl, true)) {
						Log.d("LogTest", "Connect to " + wifiConfiguration.SSID + " Failed!");
					}
					// Send Message
					postMessage(multigear.services.Message.WIFI_CONNECTED);
				}
			};
			// Add Service
			addService(serviceRunnable);
			// Return Launched Service
			return serviceRunnable;
		}
	}
	
	/**
	 * Connect to Configured Network
	 * 
	 * @param networkId
	 *            Configured Network Id
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final public void connectTo(final int networkId) {
		connectTo(networkId, mImmediateAction);
	}
	
	/**
	 * Get Wifi State
	 * 
	 * Note: Need Permissions "ACCESS_WIFI_STATE"
	 * 
	 * @return True if Wifi enabled/ False if Wifi disabled
	 */
	final public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}
	
	/**
	 * Scan Access Points. The results
	 * 
	 * Note: Need Permissions "ACCESS_WIFI_STATE"
	 */
	final public void scanAccessPoints() {
		// Wait For this Wifi States
		SafetyLock.lock(3000, new Interception() {
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return !(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING);
			}
		});
		
		// Lock
		synchronized (mScanLock) {
			// if (!mScanStarted && mScanFilterLock == null) {
			mScanStarted = true;
			mScanFilter = null;
			mWifiManager.startScan();
			// } else
			// throw new
			// multigear.services.ServiceException("Unable to start a new search. For a search already in progress.");
		}
	}
	
	/**
	 * Scan Access Points. This process control Wifi.
	 * 
	 * Note: Need Permissions "ACCESS_WIFI_STATE"
	 * 
	 * @return Wifi State
	 *         {@link multigear.mginterface.engine.servicessuport.DedicatedServices.WIFI_ON}
	 *         or
	 *         {@link multigear.mginterface.engine.servicessuport.DedicatedServices.WIFI_OFF}
	 */
	final public void scanAccessPoints(final multigear.services.AccessPointsFilter filter) {
		// Wait For this Wifi States
		SafetyLock.lock(3000, new Interception() {
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return !(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING);
			}
		});
		
		// Lock
		synchronized (mScanLock) {
			if (!mScanStarted && mScanFilterLock == null) {
				mScanStarted = true;
				mScanFilter = filter;
				mScanFilterLock = new Object();
			}
			mWifiManager.startScan();
		}
	}
	
	/**
	 * @SuppressLint("NewApi") final public void canscellAllToast() {
	 *                         NotificationManager notificationmanager =
	 *                         (NotificationManager)
	 *                         getActivity().getSystemService
	 *                         (Activity.NOTIFICATION_SERVICE);
	 *                         notificationmanager.cancelAll(); try { Method
	 *                         NotificationManagerGetService =
	 *                         NotificationManager.class.getMethod("getService",
	 *                         new Class[]{}); Object INotificationManager =
	 *                         NotificationManagerGetService
	 *                         .invoke(notificationmanager); for (final Method
	 *                         method :
	 *                         INotificationManager.getClass().getMethods()) {
	 *                         //Log.d("LogTest", "Met: " + method.getName());
	 *                         if (method.getName().equals(
	 *                         "setNotificationsEnabledForPackage")) {
	 * 
	 *                         Log.d("LogTest", "Initiating: " +
	 *                         method.getParameterTypes().length);
	 * 
	 *                         method.invoke(INotificationManager,
	 *                         "com.android.settings", 0, false);
	 * 
	 * 
	 * 
	 *                         } }
	 * 
	 *                         // Log.d("LogTest", "OK"); } catch (Exception e)
	 *                         { Log.d("LogTest", "Reflect ERR: " +
	 *                         e.toString()); } }
	 * 
	 * 
	 *                         final public void cancelToast__() { WindowManager
	 *                         windowManager = (WindowManager)
	 *                         getActivity().getSystemService
	 *                         (Context.WINDOW_SERVICE);
	 * 
	 *                         try { Field GetWindowManagerGlobal =
	 *                         windowManager
	 *                         .getClass().getDeclaredField("mGlobal");
	 *                         GetWindowManagerGlobal.setAccessible(true);
	 *                         Object WindowManagerGlobal =
	 *                         GetWindowManagerGlobal.get(windowManager);
	 * 
	 *                         // /Field GetContentParent = //
	 *                         ParentWindow.getClass
	 *                         ().getDeclaredField("mContentParent"); //
	 *                         GetContentParent.setAccessible(true); //
	 *                         ViewGroup ContentParent = (ViewGroup) //
	 *                         GetContentParent.get(ParentWindow);
	 * 
	 *                         Field GetWindowManagerGlobalViews =
	 *                         WindowManagerGlobal
	 *                         .getClass().getDeclaredField("mViews");
	 *                         GetWindowManagerGlobalViews.setAccessible(true);
	 * 
	 *                         Field GetWindowSession =
	 *                         WindowManagerGlobal.getClass
	 *                         ().getDeclaredField("sWindowSession");
	 *                         GetWindowSession.setAccessible(true); Object
	 *                         WindowSession =
	 *                         GetWindowSession.get(WindowManagerGlobal);
	 * 
	 *                         Field GetRemote =
	 *                         WindowSession.getClass().getDeclaredField
	 *                         ("mRemote"); GetRemote.setAccessible(true);
	 *                         Object Remote = GetRemote.get(WindowSession);
	 * 
	 *                         Field GetSelf =
	 *                         Remote.getClass().getDeclaredField("mSelf");
	 *                         GetSelf.setAccessible(true); Object SelfR =
	 *                         GetSelf.get(Remote);
	 * 
	 *                         // Log.d("LogTest", "Meths"); for (Method m :
	 *                         Remote.getClass().getDeclaredMethods()) { //
	 *                         Log.d("LogTest", "  -> Method: " + m.getName());
	 *                         }
	 * 
	 *                         Method RemoteTransactMethod =
	 *                         Remote.getClass().getDeclaredMethod("transact",
	 *                         Integer.TYPE, android.os.Parcel.class,
	 *                         android.os.Parcel.class, Integer.TYPE);
	 *                         RemoteTransactMethod.setAccessible(true);
	 * 
	 *                         Field IBinder_FIRST_CALL_TRANSACTION =
	 *                         IBinder.class
	 *                         .getDeclaredField("FIRST_CALL_TRANSACTION");
	 *                         IBinder_FIRST_CALL_TRANSACTION
	 *                         .setAccessible(true); final int
	 *                         FIRST_CALL_TRANSACTION =
	 *                         IBinder_FIRST_CALL_TRANSACTION.getInt(null);
	 * 
	 *                         android.os.Parcel _data =
	 *                         android.os.Parcel.obtain(); android.os.Parcel
	 *                         _reply = android.os.Parcel.obtain();
	 * 
	 *                         _data.writeInterfaceToken(
	 *                         "android.app.INotificationManager");
	 *                         _data.writeString
	 *                         (getActivity().getPackageName());
	 * 
	 *                         RemoteTransactMethod.invoke(Remote,
	 *                         FIRST_CALL_TRANSACTION + 2, _data, _reply, 0);
	 * 
	 *                         _data.recycle(); _reply.recycle();
	 * 
	 *                         View[] WindowManagerGlobalViews = (View[])
	 *                         GetWindowManagerGlobalViews
	 *                         .get(WindowManagerGlobal);
	 * 
	 *                         Resources res = Resources.getSystem(); int id =
	 *                         res.getIdentifier("message", "id", "android");
	 * 
	 *                         for (final View view : WindowManagerGlobalViews)
	 *                         { View textView = view.findViewById(id); if
	 *                         (textView != null) { String value = ((TextView)
	 *                         textView).getText().toString();
	 *                         view.setVisibility(View.INVISIBLE); } }
	 * 
	 *                         Log.d("LogTest", "Views: " +
	 *                         WindowManagerGlobalViews.length);
	 * 
	 *                         ; } catch (Exception e) { Log.d("LogTest",
	 *                         "Reflection Error"); } } /
	 * 
	 *                         /** Filter Scan Result
	 * 
	 * @param scanResult
	 * @return
	 */
	final private List<ScanResult> filterScanResult(final List<ScanResult> scanResultList, final multigear.services.ServiceControl serviceControl) {
		// Create Filtered Scan Result List
		final List<ScanResult> filteredScanResultList = new ArrayList<ScanResult>();
		
		// Disable
		disableWifi(true);
		// Immediate Enable Wifi
		enableWifi(true);
		
		// Disconnect for Work
		mWifiManager.disconnect();
		
		// Save Networks Priority
		final List<WifiConfiguration> savedNetworksPriority = saveNetoworksPriority();
		final List<Integer> removeNetworks = new ArrayList<Integer>();
		
		// Check for all Scan Result
		for (final ScanResult scanResult : scanResultList) {
			// Filter ScanResult
			if (mScanFilter.isValidScanResult(scanResult)) {
				
				// If hidden SSID
				if (scanResult.SSID == null) {
					Log.d("LogTest", "Have a network with hidden SSID!");
					continue;
				}
				
				// Add Network
				int netId = -1;
				boolean netExist = false;
				
				// Get Configured Network by BSSID or default by SSID
				WifiConfiguration preWifiConfiguration = getConfiguredNetworkBSSID(scanResult.BSSID, scanResult.SSID);
				if (preWifiConfiguration != null)
					netExist = true;
				
				// If not has configuration
				if (preWifiConfiguration == null) {
					preWifiConfiguration = new WifiConfiguration();
					preWifiConfiguration.SSID = '"' + scanResult.SSID + '"';
					preWifiConfiguration.BSSID = scanResult.BSSID;
					// If is opened
					if (getScanResultSecurity(scanResult).size() == 0) {
						preWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
						// preWifiConfiguration.hiddenSSID = true;
					}
				}
				
				// Save Network Id
				final int lastNetworkId = preWifiConfiguration.networkId;
				
				// Get Wifi Configuration
				WifiConfiguration wifiConfiguration = mScanFilter.onWifiConfigure(scanResult, preWifiConfiguration);
				
				//
				if (wifiConfiguration == null)
					wifiConfiguration = preWifiConfiguration;
				
				// Configure NetWork
				if (!netExist) {
					netId = mWifiManager.addNetwork(wifiConfiguration);
					removeNetworks.add(netId);
				} else {
					
					// Recovery Network Id
					wifiConfiguration.networkId = lastNetworkId;
					// Update Network
					netId = mWifiManager.updateNetwork(wifiConfiguration);
					// If has Error remove and add new configuration
					if (netId == -1) {
						mWifiManager.removeNetwork(getConfiguredNetwork(wifiConfiguration.SSID).networkId);
						netId = mWifiManager.addNetwork(wifiConfiguration);
					}
				}
				
				// If valid Configuration
				if (netId != -1) {
					
					// Connect to network
					connectTo(netId, true);
					
					// Wait and check if connected
					if (waitForWifiConnetionComplete(netId, serviceControl, false)) {
						final long time = System.currentTimeMillis();
						// Clear Servers List
						mServersList = null;
						// List Servers in range ....<5..0..5>...
						mComManager.createClient("TestClient").listServers(10);
						// Wait for a new list of Servers
						while (mServersList == null) {
						}
						//
						if (mScanFilter.isValidServers(mServersList)) {
							filteredScanResultList.add(scanResult);
						}
						Log.d("LogTest", "Time to Filter Con" + (System.currentTimeMillis() - time));
					} else {
						Log.d("LogTest", "Filter Warning - Network Connection Failed");
					}
				} else {
					Log.d("LogTest", "Filter Error - Network add error");
				}
				
			}
		}
		
		// Remove unused networks
		for (final Integer netId : removeNetworks)
			mWifiManager.removeNetwork(netId);
		
		// Restore all priority
		restoreNetworksPriority(savedNetworksPriority);
		
		/*
		 * // If need Save Configuration if
		 * (!restoreNetworksPriority(savedNetworksPriority) && reconfigured)
		 * mWifiManager.saveConfiguration();
		 * 
		 * // Reassociate Wifi Access Points Connection
		 * mWifiManager.reassociate();
		 */
		
		// Return Filtered Scan Result List
		return filteredScanResultList;
	}
	
	/** No Used */
	@Override
	public void onMessage(ConnectionInfo connectionInfo, ObjectMessage objectMessage) {
	}
	
	/**
	 * Communication Message
	 */
	@Override
	public void onComMessage(SupportMessage message) {
		// Get Message
		switch (message.Message) {
		// Listed Servers
			case SupportMessage.CLIENT_LISTEDSERVERS:
				mServersList = (ServersList) message.Object;
				break;
		}
	}
	
	/**
	 * Scan Access Points Complete
	 */
	final protected void scanAccessPointsComplete() {
		// Lock
		synchronized (mScanLock) {
			if (mScanStarted) {
				// If Filtered
				if (mScanFilter != null) {
					
					/** Runnable */
					final multigear.services.ServiceRunnable serviceRunnable = new multigear.services.ServiceRunnable() {
						
						/**
						 * Runner
						 */
						@Override
						public void run(ServiceControl serviceControl) {
							mScanWifiAccessPointsList = filterScanResult(mWifiManager.getScanResults(), serviceControl);
							// Unlock Scan Filter
							mScanFilterLock = null;
							// Post MEssage
							postMessage(multigear.services.Message.SCAN_ACCESS_POINT_COMPLETED);
						}
					};
					// Add Service
					addService(serviceRunnable);
				} else {
					mScanWifiAccessPointsList = mWifiManager.getScanResults();
					postMessage(multigear.services.Message.SCAN_ACCESS_POINT_COMPLETED);
				}
				mScanStarted = false;
			}
		}
	}
	
	/**
	 * Return Scan Result List
	 * <p>
	 * If a search has been initiated and it is not ready the process will be
	 * retained until the end of it.
	 * 
	 * @return
	 */
	final public List<ScanResult> getScanResult() {
		while (mScanStarted || mScanFilterLock != null) {
		}
		;
		if (mScanWifiAccessPointsList == null)
			throw new multigear.services.ServiceException("Was not possible to capture the scan list. Because no research has been done previously.");
		return mScanWifiAccessPointsList;
	}
	
	/**
	 * Wait for Hotspot Connect or Disconnect
	 * 
	 * @return
	 */
	final public void waitForHotspotSatate() {
		// Catch Erros
		try {
			// Get Method
			final Method getWifiApState = mWifiManager.getClass().getMethod("getWifiApState");
			// Get Hotspot State
			int state = (Integer) getWifiApState.invoke(mWifiManager);
			// Reduce state const for Android 4
			if (state > 10)
				state = state - 10;
			// Get Enum State
			// final HotspotState enumState = HotspotState.values()[state];
			// Wait for State
			SafetyLock.lock(5000, new Interception() {
				
				@Override
				public boolean onIntercept() {
					try {
						int state = (Integer) getWifiApState.invoke(mWifiManager);
						Log.d("LogTest", "State: " + state);
					} catch (Exception e) {
						
					}
					return false;
				}
			});
		} catch (Exception e) {
			Log.d("LogTest", "ERR");
		}
	}
	
	/**
	 * Enable Hotspot
	 * <p>
	 * Note: There is immediate. When properly enabled one Message will be sent
	 * to the Listener.
	 * <p>
	 * 
	 * @param hotspotConfiguration
	 *            Hotspot Configuration
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable enableHotspot(final WifiConfiguration hotspotConfiguration, final boolean immediate) {
		
		// Lock
		synchronized (mHotspotLock) {
			// If Immediate Action
			if (immediate) {
				// Immediate Disable Wi-fi
				disableWifi(true);
				
				// Wait For this Wifi States
				while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
				}
				;
				
				// If Hotspot Enabled
				if (isHotspotEnabled())
					disableHotspot(true);
				
				// Check exception
				try {
					// Get Enabler Method
					@SuppressWarnings("rawtypes")
					final Class[] argsTypes = new Class[2];
					argsTypes[0] = WifiConfiguration.class;
					argsTypes[1] = Boolean.TYPE;
					final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
					// Get Check Method
					final Method isWifiApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled", new Class[0]);
					// Enable and get Result
					boolean result = (Boolean) setWifiApEnabled.invoke(mWifiManager, hotspotConfiguration, true);
					// If Ok
					if (result) {
						// Wait for Hotspot started
						while (!(Boolean) isWifiApEnabled.invoke(mWifiManager)) {
						}
					}
				} catch (Exception e) {
				}
				// No Service Launched
				return null;
			} else {
				/** Runnable */
				final multigear.services.ServiceRunnable serviceRunnable = new multigear.services.ServiceRunnable() {
					
					/**
					 * Runner
					 */
					@Override
					public void run(final multigear.services.ServiceControl serviceControl) {
						// Immediate Disable Wi-fi
						disableWifi(true);
						
						// Wait For this Wifi States
						while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
						}
						;
						
						// If Hotspot Enabled
						if (isHotspotEnabled())
							disableHotspot(true);
						
						// Get Wifi Manager
						boolean enabled = false;
						
						// Check exception
						try {
							// Get Enabler Method
							@SuppressWarnings("rawtypes")
							final Class[] argsTypes = new Class[2];
							argsTypes[0] = WifiConfiguration.class;
							argsTypes[1] = Boolean.TYPE;
							final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
							// Get Check Method
							final Method isWifiApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled", new Class[0]);
							// Enable and get Result
							boolean result = (Boolean) setWifiApEnabled.invoke(mWifiManager, hotspotConfiguration, true);
							// If Ok
							if (result) {
								// Wait for Hotspot started
								while (!(Boolean) isWifiApEnabled.invoke(mWifiManager)) {
									// If was interrupted
									if (serviceControl.isEndService())
										return;
								}
								// Set Enabled
								enabled = true;
							}
						} catch (Exception e) {
						}
						// Lock
						if (enabled)
							postMessage(multigear.services.Message.HOTSPOT_ENABLED);
						else
							postMessage(multigear.services.Message.HOTSPOT_ENABLE_ERROR);
					}
				};
				// Add Service
				addService(serviceRunnable);
				// Return Launched Service
				return serviceRunnable;
			}
		}
	}
	
	/**
	 * Enable Hotspot
	 * <p>
	 * Note: There is immediate. When properly enabled one Message will be sent
	 * to the Listener.
	 * <p>
	 * 
	 * @param hotspotConfiguration
	 *            Hotspot COnfiguration
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final public multigear.services.ServiceRunnable enableHotspot(final WifiConfiguration hotspotConfiguration) {
		return enableHotspot(hotspotConfiguration, mImmediateAction);
	}
	
	/**
	 * Enable Hotspot with Password
	 * <p>
	 * Note: There is immediate. When properly enabled one Message will be sent
	 * to the Listener.
	 * <p>
	 * 
	 * @param name
	 *            Hotspot Name
	 * @param password
	 *            Hotspot WPA Password
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable enableHotspot(final String name, final String password, final boolean immediate) {
		// Lock
		synchronized (mHotspotLock) {
			// Immediate Action
			if (immediate) {
				// Immediate Disable Wi-fi
				disableWifi(true);
				
				// Wait For this Wifi States
				while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
				}
				;
				
				// If Hotspot Enabled
				if (isHotspotEnabled())
					disableHotspot(true);
				
				// Create Configuration
				final WifiConfiguration hotspotConfiguration = new WifiConfiguration();
				hotspotConfiguration.SSID = name;
				hotspotConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				hotspotConfiguration.preSharedKey = password;
				hotspotConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				hotspotConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				hotspotConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
				hotspotConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
				hotspotConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
				hotspotConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
				hotspotConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				
				// Check exception
				try {
					// Get Enabler Method
					@SuppressWarnings("rawtypes")
					final Class[] argsTypes = new Class[2];
					argsTypes[0] = WifiConfiguration.class;
					argsTypes[1] = Boolean.TYPE;
					final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
					// Get Check Method
					final Method isWifiApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled", new Class[0]);
					// Enable and get Result
					boolean result = (Boolean) setWifiApEnabled.invoke(mWifiManager, hotspotConfiguration, true);
					// If Ok
					if (result) {
						// Wait for Hotspot started
						while (!(Boolean) isWifiApEnabled.invoke(mWifiManager)) {
						}
					}
				} catch (Exception e) {
				}
				// No Service Launched
				return null;
			} else {
				/** Runnable */
				final multigear.services.ServiceRunnable serviceRunnable = new multigear.services.ServiceRunnable() {
					
					/**
					 * Runner
					 */
					@Override
					public void run(final multigear.services.ServiceControl serviceControl) {
						
						// Immediate Disable Wi-fi
						disableWifi(true);
						
						// Wait For this Wifi States
						while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
						}
						;
						
						// If Hotspot Enabled
						if (isHotspotEnabled())
							disableHotspot(true);
						
						// Create Configuration
						final WifiConfiguration hotspotConfiguration = new WifiConfiguration();
						hotspotConfiguration.SSID = name;
						hotspotConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
						hotspotConfiguration.preSharedKey = password;
						hotspotConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
						hotspotConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
						hotspotConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
						hotspotConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
						hotspotConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
						hotspotConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						hotspotConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
						
						// Get Wifi Manager
						boolean enabled = false;
						
						// Check exception
						try {
							// Get Enabler Method
							@SuppressWarnings("rawtypes")
							final Class[] argsTypes = new Class[2];
							argsTypes[0] = WifiConfiguration.class;
							argsTypes[1] = Boolean.TYPE;
							final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
							// Get Check Method
							final Method isWifiApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled", new Class[0]);
							// Enable and get Result
							boolean result = (Boolean) setWifiApEnabled.invoke(mWifiManager, hotspotConfiguration, true);
							// If Ok
							if (result) {
								// Wait for Hotspot started
								while (!(Boolean) isWifiApEnabled.invoke(mWifiManager)) {
									// If was interrupted
									if (serviceControl.isEndService())
										return;
								}
								// Set Enabled
								enabled = true;
							}
						} catch (Exception e) {
						}
						// Lock
						if (enabled)
							postMessage(multigear.services.Message.HOTSPOT_ENABLED);
						else
							postMessage(multigear.services.Message.HOTSPOT_ENABLE_ERROR);
					}
				};
				// Add Service
				addService(serviceRunnable);
				// Return Launched Service
				return serviceRunnable;
			}
		}
	}
	
	/**
	 * Enable Hotspot with Password
	 * <p>
	 * Note: There is immediate. When properly enabled one Message will be sent
	 * to the Listener.
	 * <p>
	 * 
	 * @param name
	 *            Hotspot Name
	 * @param password
	 *            Hotspot WPA Password
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final public multigear.services.ServiceRunnable enableHotspot(final String name, final String password) {
		return enableHotspot(name, password, mImmediateAction);
	}
	
	/**
	 * Disable Hotspot
	 * 
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable disableHotspot(final boolean immediate) {
		// Lock
		synchronized (mHotspotLock) {
			// Immediate Action
			if (immediate) {
				// Return if hotspot enabled
				if (!isHotspotEnabled())
					return null;
				// Check exception
				try {
					// Get Enabler Method
					@SuppressWarnings("rawtypes")
					final Class[] argsTypes = new Class[2];
					argsTypes[0] = WifiConfiguration.class;
					argsTypes[1] = Boolean.TYPE;
					final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
					// Get Check Method
					final Method isWifiApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled", new Class[0]);
					// Enable and get Result
					boolean result = (Boolean) setWifiApEnabled.invoke(mWifiManager, null, false);
					// If Ok
					if (result) {
						// Wait for Hotspot started
						while ((Boolean) isWifiApEnabled.invoke(mWifiManager)) {
						}
					}
				} catch (Exception e) {
				}
				// No Service Launched
				return null;
			} else {
				/** Runnable */
				final multigear.services.ServiceRunnable serviceRunnable = new multigear.services.ServiceRunnable() {
					
					/**
					 * Runner
					 */
					@Override
					public void run(final multigear.services.ServiceControl serviceControl) {
						// If the hotspot is already enabled
						if (!isHotspotEnabled()) {
							postMessage(multigear.services.Message.HOTSPOT_DISABLED);
							return;
						}
						// Check exception
						try {
							// Get Enabler Method
							@SuppressWarnings("rawtypes")
							final Class[] argsTypes = new Class[2];
							argsTypes[0] = WifiConfiguration.class;
							argsTypes[1] = Boolean.TYPE;
							final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
							// Get Check Method
							final Method isWifiApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled", new Class[0]);
							// Enable and get Result
							boolean result = (Boolean) setWifiApEnabled.invoke(mWifiManager, null, false);
							// If Ok
							if (result) {
								// Wait for Hotspot started
								while ((Boolean) isWifiApEnabled.invoke(mWifiManager)) {
									// If was interrupted
									if (serviceControl.isEndService())
										return;
								}
							}
						} catch (Exception e) {
						}
						// Disabled
						postMessage(multigear.services.Message.HOTSPOT_DISABLED);
					}
				};
				// Add Service
				addService(serviceRunnable);
				// Return Launched Service
				return serviceRunnable;
			}
		}
	}
	
	/**
	 * Disable Hotspot
	 * 
	 * @return If immediate mode is active will be not returned any service and
	 *         the result will be null, because the service will be executed
	 *         immediately. Otherwise will be returned to instantiate the
	 *         service launched..
	 */
	final public void disableHotspot() {
		disableHotspot(mImmediateAction);
	}
	
	/**
	 * Return Hotspot State
	 * 
	 * @return True if Hotspot Enabled/ False if Hotspot Disabled
	 */
	final public boolean isHotspotEnabled() {
		try {
			// Get Check Method
			final Method isWifiApEnabled = mWifiManager.getClass().getMethod("isWifiApEnabled", new Class[0]);
			// Enable and get Result
			return (Boolean) isWifiApEnabled.invoke(mWifiManager);
		} catch (Exception e) {
		}
		// Return false
		return false;
	}
	
	/**
	 * Return Hotspot Configuration
	 * 
	 * @return WifiConfiguration
	 */
	final public WifiConfiguration getHotspotConfiguration() {
		try {
			// Get Wifi Access Point Configuration Method
			final Method getWifiApConfiguration = mWifiManager.getClass().getMethod("getWifiApConfiguration", new Class[0]);
			// Get Hotspot Configuration
			return (WifiConfiguration) getWifiApConfiguration.invoke(mWifiManager);
		} catch (Exception e) {
		}
		// Return null Configuration
		return new WifiConfiguration();
	}
	
	/**
	 * Set Hotspot Configuration
	 * 
	 * @param hotspotConfiguration
	 *            Hotspot Configuration
	 */
	final public void setHotspotConfiguration(final WifiConfiguration hotspotConfiguration) {
		try {
			// Get Wifi Access Point Configuration Method
			final Method setWifiApConfiguration = mWifiManager.getClass().getMethod("setWifiApConfiguration", new Class[] { WifiConfiguration.class });
			// Set Hotspot Configuration
			setWifiApConfiguration.invoke(mWifiManager, hotspotConfiguration);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Set Mobilie Data for Android version 2.2
	 * 
	 * @param enabled
	 */
	final private void setMobileDataAndroidVersion2_2(final boolean enabled, final ServiceControl serviceControl) {
		// Get Telephony Manager
		TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		
		// Wait for Mobile Data end Services
		if (!enabled) {
			while (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTING) {
			}
			
			// Already disconnected
			if (telephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED) {
				return;
			}
		} else {
			if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTING || telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)
				return;
		}
		// Catch Errors
		try {
			// Reflect Methods
			Class<?> telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
			Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
			getITelephonyMethod.setAccessible(true);
			Object ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
			Class<?> ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
			
			// If Enable
			if (enabled) {
				final Method enableDataConnectivity = ITelephonyClass.getDeclaredMethod("enableDataConnectivity");
				enableDataConnectivity.setAccessible(true);
				enableDataConnectivity.invoke(ITelephonyStub);
				// Wait for really connect
				while (telephonyManager.getDataState() != TelephonyManager.DATA_CONNECTED) {
					if (serviceControl != null && serviceControl.isEndService())
						break;
					else if (Thread.currentThread().isInterrupted())
						break;
				}
				// If Disable
			} else {
				final Method disableDataConnectivity = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
				disableDataConnectivity.setAccessible(true);
				disableDataConnectivity.invoke(ITelephonyStub);
				// Wait for really disconect
				while (telephonyManager.getDataState() != TelephonyManager.DATA_DISCONNECTED) {
					if (serviceControl != null && serviceControl.isEndService())
						break;
					else if (Thread.currentThread().isInterrupted())
						break;
				}
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * Set Mobilie Data for Android 2.3+
	 * 
	 * @param enabled
	 */
	final private void setMobileDataAndroidVersion2_3OrMore(boolean enabled, final ServiceControl serviceControl) {
		// If ok
		if (isMobileDataEnabled() == enabled)
			return;
		// Get Telephony Manager
		final TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		// Wait for Mobile Data end Services
		if (!enabled) {
			// while (telephonyManager.getDataState() ==
			// TelephonyManager.DATA_CONNECTING) {
			// }
		} else {
			if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTING || telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)
				return;
		}
		final ConnectivityManager conman = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			final Class<?> conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			final Method getMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");
			setMobileDataEnabledMethod.setAccessible(true);
			getMobileDataEnabledMethod.setAccessible(true);
			// If state equal command
			if (enabled == (Boolean) getMobileDataEnabledMethod.invoke(iConnectivityManager)) {
				return;
			}
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);

			while (true) {
				
				if (enabled)
					SafetyLock.lock(5000, new Interception() {
						
						@Override
						public boolean onIntercept() {
							if (serviceControl != null && serviceControl.isEndService())
								return true;
							return !(telephonyManager.getDataState() != TelephonyManager.DATA_CONNECTED);
						}
					});
				
				else
					SafetyLock.lock(5000, new Interception() {
						
						@Override
						public boolean onIntercept() {
							
							if (serviceControl != null && serviceControl.isEndService())
								return true;
							
							return !(telephonyManager.getDataState() != TelephonyManager.DATA_DISCONNECTED);
						}
					});
				
				break;
				
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * Set Mobilie Data for Android 2.2+
	 * 
	 * @param enabled
	 */
	final private void setMobileData(boolean enabled, ServiceControl serviceControl) {
		// Froyo Version
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO)
			setMobileDataAndroidVersion2_2(enabled, serviceControl);
		// Set for others versions (2.3+)
		else
			setMobileDataAndroidVersion2_3OrMore(enabled, serviceControl);
	}
	
	/**
	 * Return is Mobile State Enabled
	 * 
	 * @return True if Enabled
	 */
	final public boolean isMobileDataEnabled() {
		// Get Telephony Manager
		final ConnectivityManager conman = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			final Class<?> conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method getMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");
			getMobileDataEnabledMethod.setAccessible(true);
			return (Boolean) getMobileDataEnabledMethod.invoke(iConnectivityManager);
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * Enable Mobile Data
	 * 
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable enableMobileData(final boolean immediate) {
		// Lock
		synchronized (mHotspotLock) {
			// Immediate Action
			if (immediate) {
				// Disable Wifi Immediate
				disableWifi(true);
				// Enable Mobile Data
				setMobileData(true, null);
				// No Service Launched
				return null;
			} else {
				/** Runnable */
				final multigear.services.ServiceRunnable serviceRunnable = new multigear.services.ServiceRunnable() {
					
					/**
					 * Runner
					 */
					@Override
					public void run(final multigear.services.ServiceControl serviceControl) {
						// Disable Wifi Immediate
						disableWifi(true);
						// Enable Mobile Data
						setMobileData(true, serviceControl);
						// Disabled
						postMessage(Message.MOBILEDATA_ENABLE);
					}
				};
				// Add Service
				addService(serviceRunnable);
				// Return Launched Service
				return serviceRunnable;
			}
		}
	}
	
	/**
	 * Enable Mobile Data
	 * 
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final public multigear.services.ServiceRunnable enableMobileData() {
		return enableMobileData(mImmediateAction);
	}
	
	/**
	 * Disable Mobile Data
	 * 
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final private multigear.services.ServiceRunnable disableMobileData(final boolean immediate) {
		// Lock
		synchronized (mHotspotLock) {
			// Immediate Action
			if (immediate) {
				// Enable Mobile Data
				setMobileData(false, null);
				// No Service Launched
				return null;
			} else {
				/** Runnable */
				final multigear.services.ServiceRunnable serviceRunnable = new multigear.services.ServiceRunnable() {
					
					/**
					 * Runner
					 */
					@Override
					public void run(final multigear.services.ServiceControl serviceControl) {
						// Enable Mobile Data
						setMobileData(false, serviceControl);
						// Disabled
						postMessage(Message.MOBILEDATA_DISABLED);
					}
				};
				// Add Service
				addService(serviceRunnable);
				// Return Launched Service
				return serviceRunnable;
			}
		}
	}
	
	/**
	 * Disable Mobile Data
	 * 
	 * @param immediate
	 *            Immediate Action
	 * 
	 * @return If the service manager is in immediate mode the returned service
	 *         will be null because not released will be no service. Otherwise
	 *         will be returned to instantiate the service launched.
	 */
	final public multigear.services.ServiceRunnable disableMobileData() {
		return disableMobileData(mImmediateAction);
	}
	
	/**
	 * Finish
	 */
	final public void finish() {
		// Finish Communication Manager
		mComManager.finish();
		// Lock
		synchronized (mHotspotLock) {
			// Interrupt Services Group
			mServicesGroup.close();
		}
		// Unregister Receiver
		mEngine.getActivity().unregisterReceiver(mReceiver);
	}
}
