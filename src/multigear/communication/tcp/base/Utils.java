package multigear.communication.tcp.base;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Communication Utils
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Utils {
	
	// Constants
	final static public int CODE_SERVER_GETNAME = -1;
	final static public int CODE_SERVER_WAITCONNECT = -2;
	final static public int CODE_SERVER_DISCONNECT = -3;
	
	final static public int CODE_CLIENT_SERVERNAME = -10;
	final static public int CODE_CLIENT_CONNECTED = -11;
	
	/** Used by ComSupport */
	final static public int CODE_INTERFACE_OBJECTMESSAGE = -20;
	
	// Final Private Variables
	final static private Pattern mPattern = Pattern.compile("\\[[ ]*code[ ]*\\:[ ]*((?:-|\\+)?\\d*)[ ]*\\|[ ]*msg[ ]*\\:[ ]*([^\\]]*)\\]");
	final static public String EXTRA_SOCKET_BUFF;
	final static public int SOCKET_SEND_BUFFER_SIZE = 7000;
	final static public int SOCKET_RECV_BUFFER_SIZE = 100000;
	
	// Set Final Extra Buff
	static {
		String buff = "";
		for(int i=0; i<30; i++)
			buff += "_";
		EXTRA_SOCKET_BUFF = buff;
	}
	
	/*
	 * Cria uma nova mensagem para ser enviada por Socket
	 */
	final static public String makeSocketMessage(final int code, final String message) {
		return "[code:" + code + "|msg:" + message + "]" + EXTRA_SOCKET_BUFF;
	}
	
	/*
	 * Cria uma nova mensagem vazia para ser enviada por Socket
	 */
	final static public String makeSocketMessage(final int code) {
		return makeSocketMessage(code, "empty");
	}
	
	/*
	 * Traduz o código de comunicação para objetos Message
	 */
	final static public Message translateSocketMessages(final String socketMessages) {
		final Matcher matcher = mPattern.matcher(socketMessages);
		Message lastMessage = null;
		Message firstMessage = null;
		while(matcher.find()) {
			final int code = Integer.parseInt(matcher.group(1));
			final String messageString = matcher.group(2);
			final multigear.communication.tcp.base.Message message = new multigear.communication.tcp.base.Message(code, messageString);
			if(lastMessage != null)
				lastMessage.setNext(message);
			if(firstMessage == null)
				firstMessage = message;
			lastMessage = message;
		}
		return firstMessage;
	}
	
	/*
	 * Traduz o código de comunicação para objetos Message
	 */
	final static public Message translateSocketMessages(final BufferedReader bufferedReader) {
		try {
			return translateSocketMessages(bufferedReader.readLine());
		} catch (Exception e) {
			return null;
		}
	}
}
