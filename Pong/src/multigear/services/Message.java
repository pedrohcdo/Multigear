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
	/**  Access Points Result. */
	final static public int SCAN_ACCESS_POINT_COMPLETED = 3;
	/** Wifi Connected. */
	final static public int WIFI_CONNECTED = 4;
	/** Hotspot Correctly Enabled. */
	final static public int HOTSPOT_ENABLED = 5;
	/** Hotspot Disabled. */
	final static public int HOTSPOT_DISABLED = 6;
	/** Hotspot Enable Error. */
	final static public int HOTSPOT_ENABLE_ERROR = 7;
	/** Services Data Restored. */
	final static public int DATA_SERVICES_RESTORED = 8;
	/** State Restored. */
	final static public int STATE_RESTORED = 9;
	/** Mobile Data Enabled. */
	final static public int MOBILEDATA_ENABLE = 10;
	/** Mobile Data Disabled. */
	final static public int MOBILEDATA_DISABLED = 11;
	
	
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
