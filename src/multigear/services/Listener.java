package multigear.services;

/**
 * Listener
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface Listener {
	
	/** On Connection Message */
	public void onServiceMessage(final multigear.services.Message message);
}
