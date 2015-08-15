package com.org.multigear.mginterface.tools.mgmap;

/**
 * Control
 * 
 * @author user
 *
 */
public interface MultigearGameListener {
	
	/**
	 * On Connected
	 * @return Return true if successfully connected, return false if connection rejected or failed in this case get error code in {@link DuoMap.getErrorCode()}
	 */
	public void onConnect(boolean result);
}
