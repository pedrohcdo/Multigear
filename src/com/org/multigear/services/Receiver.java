package com.org.multigear.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast Receiver Used to list Wifi connections.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class Receiver extends BroadcastReceiver {
	
	// Final Private Variables
	final private com.org.multigear.services.ServicesManager mConnectionManager;
	
	/**
	 * Constructor
	 * 
	 * @param connectionManager
	 */
	public Receiver(final com.org.multigear.services.ServicesManager connectionManager) {
		mConnectionManager = connectionManager;
	}
	
	/**
	 * Receiver
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// If as Results
		mConnectionManager.scanAccessPointsComplete();
	}
}
