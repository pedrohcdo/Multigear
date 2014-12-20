package multigear.communication.tcp.base;

/**
 * Translated Message
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Message {
	
	// Final Private Variables
	final private int mCode;
	final private String mMessage;
	
	// Private Variables
	private Message mNext;
	
	protected Message(final int code, final String message) {
		mCode = code;
		mMessage = message;
	}
	
	/*
	 * Retorna o código da mensagem
	 */
	final public int getCode() {
		return mCode;
	}
	
	/*
	 * Retorna o corpo da mensagem
	 */
	final public String getMessage() {
		return mMessage;
	}
	
	/*
	 * Altera a proxima mensagem
	 */
	final protected void setNext(final Message next) {
		mNext = next;
	}
	
	/*
	 * Retorna a proxima mensagem
	 */
	final public Message next() {
		return mNext;
	}
	
	/*
	 * Retorna true caso haja uma proxima mensagem
	 */
	final protected boolean hasNext() {
		return (mNext != null);
	}
}
