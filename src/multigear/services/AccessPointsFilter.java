package multigear.services;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

/**
 * Access Points Filter
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface AccessPointsFilter {
	
	/** Return true if Access Point is Valid */
	public boolean isValidScanResult(final ScanResult scanResult);
	
	/**
	 * Configurate Wifi Access Point.
	 * 
	 * Note: If not necessary, or the configuration is already validated, 
	 * return the same pre configuration or the same instance.
	 *  
	 * @param preConfiguration
	 * @return
	 */
	public WifiConfiguration onWifiConfigure(final ScanResult scanResult, final WifiConfiguration preConfiguration);
	
	/** Return true if Servers valid */
	public boolean isValidServers(final multigear.communication.tcp.client.ServersList serversList);
}
