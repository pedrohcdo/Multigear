package multigear.services;

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
	/**  Access Points Result. */
	final static public int SCAN_ACCESS_POINT_COMPLETED = 5;
	/** Wifi Connected. */
	final static public int WIFI_CONNECTED = 6;
	/** Hotspot Correctly Enabled. */
	final static public int HOTSPOT_ENABLED = 7;
	/** Hotspot Disabled. */
	final static public int HOTSPOT_DISABLED = 8;
	/** Hotspot Enable Error */
	final static public int HOTSPOT_ENABLE_ERROR = 9;
	/** Hotspot Disable Error */
	final static public int HOTSPOT_DISABLE_ERROR = 10;
	/** Services Data Restored. */
	final static public int DATA_SERVICES_RESTORED = 11;
	/** State Restored. */
	final static public int STATE_RESTORED = 12;
	/** Mobile Data Enabled. */
	final static public int MOBILEDATA_ENABLE = 13;
	/** Mobile Data Disabled. */
	final static public int MOBILEDATA_DISABLED = 14;
	
	
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
