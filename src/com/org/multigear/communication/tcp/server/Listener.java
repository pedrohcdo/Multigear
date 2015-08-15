package com.org.multigear.communication.tcp.server;

/**
 * Server Listener
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface Listener {
	
	/* On Start Server */
	public void onStartServer();
	
	/* On Start Server Failed */
	public void onStartServerFailed();
	
	/* On Client Connected */
	public void onClientConnected(final com.org.multigear.communication.tcp.base.BaseConnected connectedClient);

}
