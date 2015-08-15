package com.org.multigear.services;

/**
 * Listener
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface Listener {
	
	/** On Connection Message */
	public void onServiceMessage(final com.org.multigear.services.Message message);
}
