package multigear.communication.tcp.client;


/**
 * Client Listener
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface Listener {
	
	/* On Server Listed */
	public void onListed(final multigear.communication.tcp.client.ServersList serverList);
	
	/* On Server Connected */
	public void onConnected(final multigear.communication.tcp.base.BaseConnected serverCon);
	
	/* On Server Connected Failed */
	public void onConnectionFailed();
}
