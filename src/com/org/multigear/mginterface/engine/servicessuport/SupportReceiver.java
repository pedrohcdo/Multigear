package com.org.multigear.mginterface.engine.servicessuport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Support Receiver
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class SupportReceiver extends BroadcastReceiver {

	/**
	 * Receiver, fully work
	 */	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Start a new Service
		context.startService(new Intent(context, SupportService.class));
	}
}
