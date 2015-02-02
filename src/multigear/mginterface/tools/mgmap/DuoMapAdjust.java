package multigear.mginterface.tools.mgmap;

/**
 * Device Adjust
 * 
 * @author user
 */
public enum DuoMapAdjust {
	
	/** It is the device with higher screen, the same shall be adjusted to support the lesser amount. */
	ADJUST_MAJOR,
	/** It is the device with less screen, it will not be adjusted. */
	ADJUST_MINOR,
	/** The two devices are equal in screen amount of term. None will be adjusted. */
	ADJUST_EQUAL,
	/** If not connected. Or an error has occurred on the connection. */
	NOT_SET;
}
