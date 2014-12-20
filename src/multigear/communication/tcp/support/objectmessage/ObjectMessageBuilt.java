package multigear.communication.tcp.support.objectmessage;

/**
 * Object Message Built
 * 
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ObjectMessageBuilt {
	
	// Final Private Variables
	final private String mMessage;
	
	/*
	 * Construtor
	 */
	protected ObjectMessageBuilt(final String message) {
		mMessage = message;
	}
	
	/*
	 * Retorna a mensagem
	 */
	final public String getMessage() {
		return mMessage;
	}
}
