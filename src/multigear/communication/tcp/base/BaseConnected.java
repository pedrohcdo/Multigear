package multigear.communication.tcp.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.util.Log;

/**
 * Base Connected
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class BaseConnected {
	
	// Constants
	final static private int CODE_ERROR_READMESSAGE_BLOCKED = 0x8;
	
	// Final Private Variables
	final private Socket mSocket;
	final private BufferedReader mIn;
	final private PrintWriter mOut;
	final private Activity mActivity;
	final private String mName;
	final private String mAddress;
	
	// Private Variables
	private boolean mReadBlocked;
	
	/*
	 * Construtor
	 */
	public BaseConnected(final Activity activity, final String name, final Socket socket, final BufferedReader in, final PrintWriter out) {
		mActivity = activity;
		mName = name;
		mAddress = socket.getInetAddress().getHostAddress();
		mSocket = socket;
		mIn = in;
		mOut = out;
	}
	
	/*
	 * Retorna o nome
	 */
	final public String getName() {
		return mName;
	}
	
	/*
	 * Retorna o endereço
	 */
	final public String getAddress() {
		return mAddress;
	}
	
	/**
	 * Block 'readMessage'. This method called by Engine.
	 */
	final public void blockRead() {
		mReadBlocked = true;
	}
	
	/*
	 * Envia uma mensagem
	 */
	final public void sendMessage(final int code, final String message) {
		if (message.matches(".*(?:\\[|\\]).*"))
			Log.d("LogTest", "Client: Warning, do not use any of these characters: []|");
		final String messageSocket = multigear.communication.tcp.base.Utils.makeSocketMessage(code, message);
		mOut.println(messageSocket);
		mOut.flush();
	}
	
	/*
	 * Envia uma mensagem vazia
	 */
	final public void sendMessage(final int code) {
		final String messageSocket = multigear.communication.tcp.base.Utils.makeSocketMessage(code);
		mOut.println(messageSocket);
		mOut.flush();
	}
	
	/**
	 * Send a generic message.
	 * 
	 * <blockquote> <b>**WARN**</b> </p> Caution, do not use this method because
	 * the engine uses is a communication system models. The message sent here
	 * may be confused by the engine. </blockquote>
	 * 
	 * @param message
	 *            Generic Message
	 */
	final public void sendGenericMessage(final String message) {
		mOut.println(message);
		mOut.flush();
	}
	
	/*
	 * Le todas as mensagens em espera
	 */
	final public multigear.communication.tcp.base.Message readMessage() {
		if (mReadBlocked)
			multigear.general.utils.KernelUtils.error(mActivity, "BaseConnected: An error occurred while calling the 'ReadMessage' function. The same was blocked, probably by a top object, such as ConSupport.", CODE_ERROR_READMESSAGE_BLOCKED);
		String stream = null;
		try {
			if (mIn.ready()) {
				stream = mIn.readLine();
			}
		} catch (IOException e) {
			Log.d("LogTest", "Client: Error on read message. IOException message: " + e.getMessage() + ".");
		}
		if (stream == null)
			return null;
		return multigear.communication.tcp.base.Utils.translateSocketMessages(stream);
	}
	
	/*
	 * Retorna o stream Generico
	 */
	final public String readGeneric() {
		if (mReadBlocked)
			multigear.general.utils.KernelUtils.error(mActivity, "BaseConnected: An error occurred while calling the 'ReadMessage' function. The same was blocked, probably by a top object, such as ConSupport.", CODE_ERROR_READMESSAGE_BLOCKED);
		String stream = null;
		try {
			if (mIn.ready()) {
				stream = mIn.readLine();
			}
		} catch (IOException e) {
			Log.d("LogTest", "Client: Error on read message. IOException message: " + e.getMessage() + ".");
		}
		return stream;
	}
	
	/*
	 * Fecha a conexão
	 */
	final public void close() {
		try {
			mIn.close();
			mOut.close();
			mSocket.close();
		} catch (IOException e) {
			Log.d("LogTest", "Client: Error to close server connection.");
		}
	}
}
