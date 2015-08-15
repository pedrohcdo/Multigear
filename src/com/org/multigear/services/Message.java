package com.org.multigear.services;

/**
 * Message Used by Connection
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Message {
	
	// Consts
	/** Wifi Enabled. */
	final static public int WIFI_ENABLED = 1;
	/** Wifi Disabled. */
	final static public int WIFI_DISABLED = 2;
	/** Wifi Enable Error */
	final static public int WIFI_ENABLE_ERROR = 3;
	/** Wifi Disable Error */
	final static public int WIFI_DISABLE_ERROR = 4;
	/** Access Points Result. */
	final static public int SCAN_ACCESS_POINT_COMPLETED = 5;
	/** Scan Access Point error. */
	final static public int SCAN_ACCESS_POINT_ERROR = 6;
	/** Wifi Connected. */
	final static public int WIFI_CONNECTED = 7;
	/** Hotspot Correctly Enabled. */
	final static public int HOTSPOT_ENABLED = 8;
	/** Hotspot Disabled. */
	final static public int HOTSPOT_DISABLED = 9;
	/** Hotspot Enable Error */
	final static public int HOTSPOT_ENABLE_ERROR = 10;
	/** Hotspot Disable Error */
	final static public int HOTSPOT_DISABLE_ERROR = 11;
	/** Mobile Data Enabled. */
	final static public int MOBILEDATA_ENABLE = 12;
	/** Mobile Data Disabled. */
	final static public int MOBILEDATA_DISABLED = 13;
	/** Extra service result. */
	final static public int DISCOVERY_RESULT_AVAILABLE = 14;
	/** Services Restored */
	final static public int SERVICES_STATE_RESTORED = 15;
	
	// Final Private Variables
	final private int mMessageCode;
	
	
	/**
	 * Constructor
	 * 
	 * @param message
	 * @param object
	 */
	public Message(final int message) {
		mMessageCode = message;
	}
	
	/**
	 * Get Message
	 * 
	 * @return
	 */
	final public int getCode() {
		return mMessageCode;
	}
}
