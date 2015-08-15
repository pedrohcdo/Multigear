package com.org.multigear.cache;

import java.util.HashMap;

/**
 * Cache Manager
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class CacheManager {
	
	// Private Variables
	final private HashMap<String, CacheComponent> mCache = new HashMap<String, CacheComponent>();
	
	/**
	 * Add component
	 * @param tag Tag to interact with this component
	 * @param component Component intance
	 */
	public void putComponent(String tag, CacheComponent component) {
		mCache.put(tag, component);
	}
	
	/**
	 * Remove component
	 * @param tag Tag to interact with this component
	 */
	public void removeComponent(String tag) {
		mCache.remove(tag);
	}
	
	/**
	 * Get component
	 * @param tag Tag to interact with this component
	 */
	public CacheComponent getComponent(String tag) {
		return mCache.get(tag);
	}
}
