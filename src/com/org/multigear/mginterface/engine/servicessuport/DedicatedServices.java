package com.org.multigear.mginterface.engine.servicessuport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.org.multigear.general.utils.SafetyLock;
import com.org.multigear.general.utils.SafetyLock.Interception;

/**
 * Connection Manager
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class DedicatedServices {
	
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
	
	// Final Private Variables
	final private WifiManager mWifiManager;
	final private Context mContext;
	
	// Private Variables
	private Database mDatabase;
	
	/**
	 * Construtor
	 */
	public DedicatedServices(final Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	/**
	 * Saves the current state of the service manager. This is valid for the
	 * following services: Wi-fi, Hotspot, 3G.
	 */
	final public void saveState() {
		// Sync with this instance
		synchronized (DedicatedServices.this) {
			mDatabase = new Database();
			mDatabase.WifiEnabled = isWifiEnabled();
			mDatabase.HotspotEnabled = isHotspotEnabled();
			mDatabase.MobileDataEnabled = isMobileDataEnabled();
			mDatabase.ConfiguredNetworks = getConfiguredNetworksSafety();
			mDatabase.HotspotConfiguration = getHotspotConfiguration();
		}
	}
	
	/**
	 * Compare Networks
	 * @param netA
	 * @param netB
	 * @return
	 */
	final private boolean compareNetworks(final WifiConfiguration netA, final WifiConfiguration netB) {
		final String ssidA = netA.SSID;
		final String ssidB = netB.SSID;
		if(ssidA != null && ssidB != null) {
			if(ssidA.equals(ssidB))
				return true;
		}
		final String bssidA = netA.BSSID;
		final String bssidB = netB.BSSID;
		if(bssidA != null && bssidB != null) {
			if(bssidA.equals(bssidB))
				return true;
		}
		return false;
	}
	
	/**
	 * Restores the last saved state of the service manager. This is valid for
	 * the following services: Wi-fi, Hotspot, 3G.
	 */
	final public void restoreState(final SupportThread supportThread) {
		// Database used to restore
		Database restoreDataBase;
		// Sync with this instance
		synchronized (DedicatedServices.this) {
			restoreDataBase = mDatabase;
		}
		// Null Database
		if(restoreDataBase == null)
			return;
		// Wait for Established
		establishConnections();
		// Prevents interrupt
		if (supportThread.hasInterrupted())
			return;
		setHotspotConfiguration(restoreDataBase.HotspotConfiguration, supportThread);
		// Wait for Established
		establishConnections();
		// Restore Wifi State
		if (restoreDataBase.WifiEnabled)
			enableWifi(supportThread);
		else
			disableWifi(supportThread);
		// Wait for Established
		establishConnections();
		// Prevents interrupt
		if (supportThread.hasInterrupted())
			return;
		// Restore Configured Networks
		boolean refresh = false;
		boolean reassociate = false;
		for (final WifiConfiguration configuredNetwork : getConfiguredNetworksSafety()) {
			boolean found = false;
			// find
			for(final WifiConfiguration savedNetwork : restoreDataBase.ConfiguredNetworks) {
				if(compareNetworks(configuredNetwork, savedNetwork)) {
					found = true;
					if(savedNetwork.status != configuredNetwork.status) {
						reassociate = true;
						if (savedNetwork.status == android.net.wifi.WifiConfiguration.Status.CURRENT || savedNetwork.status == android.net.wifi.WifiConfiguration.Status.ENABLED) {
							mWifiManager.enableNetwork(configuredNetwork.networkId, false);
						} else {
							mWifiManager.disableNetwork(configuredNetwork.networkId);
						}
					}
					break;
				}
			}
			// Remove
			if(!found) {
				reassociate = true; // testing ..
				//mWifiManager.disableNetwork(configuredNetwork.networkId);
				mWifiManager.removeNetwork(configuredNetwork.networkId);
				
				refresh = true;
			}
			// Prevents interrupt
			if (supportThread.hasInterrupted())
				return;
		}
		
		// If Refresh
		if(refresh)
			mWifiManager.saveConfiguration();
		if(reassociate || refresh) {
			//mWifiManager.reconnect();
			mWifiManager.reassociate();
			return;
		}
		// Prevents interrupt
		if (supportThread.hasInterrupted())
			return;
		// Restore Hotspot State
		if (restoreDataBase.HotspotEnabled && !restoreDataBase.WifiEnabled)
			enableHotspot(restoreDataBase.HotspotConfiguration, supportThread);
		else {
			disableHotspot(supportThread);
		}
		// Prevents interrupt
		if (supportThread.hasInterrupted())
			return;
		// Restore Mobile Data State
		if (restoreDataBase.MobileDataEnabled)
			enableMobileData(supportThread);
		else
			disableMobileData(supportThread);
		
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
	final public void enableWifi(final SupportThread supportThread) {
		// Prevents interrupt
		if (supportThread.hasInterrupted())
			return;
		// Establish Connections
		establishConnections();
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Immediate Disable Hotspot
		disableHotspot(supportThread);
		// If interrupt
		if (supportThread.hasInterrupted() || isWifiEnabled())
			return;
		// Enable Wifi
		mWifiManager.setWifiEnabled(true);
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
	final public void disableWifi(final SupportThread supportThread) {
		// Establish Connections
		establishConnections();
		// If interrupt
		if (supportThread.hasInterrupted() || !isWifiEnabled())
			return;
		// Disable Wifi
		mWifiManager.setWifiEnabled(false);
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
	 * Get all Enabled Netoworks List
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
	 * Wait for established connections
	 * 
	 * @return
	 */
	final public void establishConnections() {
		// Catch Erros
		try {
			// Get Method
			final Method getWifiApState = mWifiManager.getClass().getMethod("getWifiApState");
			// Wait for State
			SafetyLock.lock(6000, new Interception() {
				
				/**
				 * Interpect
				 */
				@Override
				public boolean onIntercept() {
					try {
						// Get Hotspot State
						int state = (Integer) getWifiApState.invoke(mWifiManager);
						// Reduce state const for Android 4
						if (state > 10)
							state = state - 10;
						// Get Enum State
						final HotspotState hotspotState = HotspotState.values()[state];
						final int wifiState = mWifiManager.getWifiState();
						// If established, intercept lock
						if (hotspotState != HotspotState.STATE_ENABLING && hotspotState != HotspotState.STATE_DISABLING && wifiState != WifiManager.WIFI_STATE_ENABLING && wifiState != WifiManager.WIFI_STATE_DISABLING)
							return true;
					} catch (Exception e) {
					}
					// Wait for stabilization
					return false;
				}
			});
		} catch (Exception e) {
		}
	}
	
	/**
	 * Enable Hotspot
	 * 
	 * @param hotspotConfiguration
	 *            Hotspot Configuration
	 */
	final public void enableHotspot(final WifiConfiguration hotspotConfiguration, final SupportThread supportThread) {
		// Wait For this Wifi States
		establishConnections();
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Immediate Disable Wi-fi
		disableWifi(supportThread);
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// If Hotspot Enabled
		disableHotspot(supportThread);
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Wait For this Wifi States
		establishConnections();
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Check exception
		try {
			// Get Enabler Method
			@SuppressWarnings("rawtypes")
			final Class[] argsTypes = new Class[2];
			argsTypes[0] = WifiConfiguration.class;
			argsTypes[1] = Boolean.TYPE;
			final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
			// Enable and get Result
			setWifiApEnabled.invoke(mWifiManager, hotspotConfiguration, true);
		} catch (Exception e) {
		}
		
	}
	
	/**
	 * Disable Hotspot
	 */
	final public void disableHotspot(final SupportThread supportThread) {
		// Establish Connection
		establishConnections();
		// If interrupt
		if (supportThread.hasInterrupted() || !isHotspotEnabled())
			return;
		// Check exception
		try {
			// Get Enabler Method
			@SuppressWarnings("rawtypes")
			final Class[] argsTypes = new Class[2];
			argsTypes[0] = WifiConfiguration.class;
			argsTypes[1] = Boolean.TYPE;
			final Method setWifiApEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled", argsTypes);
			// Enable and get Result
			setWifiApEnabled.invoke(mWifiManager, null, false);
		} catch (Exception e) {
		}
		
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
	final public void setHotspotConfiguration(final WifiConfiguration hotspotConfiguration, final SupportThread supportThread) {
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Catch Errors
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
	final private void setMobileDataAndroidVersion2_2(final boolean enabled, final SupportThread supportThread) {
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Get Telephony Manager
		final TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		// Wait for Mobile Data end Services
		if (!enabled) {
			// Wait for connecting
			SafetyLock.lock(6000, new Interception() {
				
				/**
				 * Intercept
				 */
				@Override
				public boolean onIntercept() {
					return telephonyManager.getDataState() != TelephonyManager.DATA_CONNECTING;
				}
			});
			// If interrupt
			if (supportThread.hasInterrupted() || telephonyManager.getDataState() == TelephonyManager.DATA_DISCONNECTED)
				return;
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
				// If Disable
			} else {
				final Method disableDataConnectivity = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
				disableDataConnectivity.setAccessible(true);
				disableDataConnectivity.invoke(ITelephonyStub);
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * Set Mobilie Data for Android 2.3+
	 * 
	 * @param enabled
	 */
	final private void setMobileDataAndroidVersion2_3OrMore(boolean enabled, final SupportThread supportThread) {
		// Get Telephony Manager
		final TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		// Wait for Mobile Data end Services
		if (!enabled) {
			// Wait for connecting
			SafetyLock.lock(6000, new Interception() {
				
				/**
				 * Intercept
				 */
				@Override
				public boolean onIntercept() {
					return telephonyManager.getDataState() != TelephonyManager.DATA_CONNECTING;
				}
			});
			// If interrupt
			if (supportThread.hasInterrupted())
				return;
		} else {
			if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTING || telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)
				return;
		}
		
		final ConnectivityManager conman = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
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
			if (enabled == (Boolean) getMobileDataEnabledMethod.invoke(iConnectivityManager))
				return;
			// Set state
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Set Mobilie Data for Android 2.2+
	 * 
	 * @param enabled
	 */
	final private void setMobileData(boolean enabled, final SupportThread supportThread) {
		// Froyo Version
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO)
			setMobileDataAndroidVersion2_2(enabled, supportThread);
		// Set for others versions (2.3+)
		else
			setMobileDataAndroidVersion2_3OrMore(enabled, supportThread);
	}
	
	/**
	 * Return is Mobile State Enabled
	 * 
	 * @return True if Enabled
	 */
	final public boolean isMobileDataEnabled() {
		// Get Telephony Manager
		final ConnectivityManager conman = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	 */
	final public void enableMobileData(final SupportThread supportThread) {
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Enable Mobile Data
		setMobileData(true, supportThread);
	}
	
	/**
	 * Disable Mobile Data
	 */
	final public void disableMobileData(final SupportThread supportThread) {
		// If interrupt
		if (supportThread.hasInterrupted())
			return;
		// Enable Mobile Data
		setMobileData(false, supportThread);
	}
}
