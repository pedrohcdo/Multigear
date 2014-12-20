package multigear.communication.tcp.support.listener;



/**
 * Connection CLient Listener
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface Listener {
	
	/** Receive Object Message */
	public void onMessage(final multigear.communication.tcp.support.ConnectionInfo connectionInfo, final multigear.communication.tcp.support.objectmessage.ObjectMessage objectMessage);
	
	/** Com Support Message */
	public void onComMessage(final multigear.communication.tcp.support.SupportMessage message);
}
