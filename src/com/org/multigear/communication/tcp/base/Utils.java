package com.org.multigear.communication.tcp.base;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Communication Utils
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
@SuppressLint("DefaultLocale") 
final public class Utils {
	
	// Constants
	final static public int CODE_SERVER_GETNAME = -1;
	final static public int CODE_SERVER_WAITCONNECT = -2;
	final static public int CODE_SERVER_DISCONNECT = -3;
	
	final static public int CODE_CLIENT_SERVERNAME = -10;
	final static public int CODE_CLIENT_CONNECTED = -11;
	
	/** Used by ComSupport */
	final static public int CODE_INTERFACE_OBJECTMESSAGE = -20;
	final static public int CODE_INTERFACE_KEEPALIVE = -60;
	final static public int CODE_INTERFACE_PAUSED_AND_CLOSED =  -61;
	final static public int CODE_INTERFACE_CLOSED =  -62;
	
	// Final Private Variables
	final static public int SOCKET_SEND_BUFFER_SIZE = 7000;
	final static public int SOCKET_RECV_BUFFER_SIZE = 100000;
	

	/*
	 * Cria uma nova mensagem para ser enviada por Socket
	 */
	final static public String makeSocketMessage(final int code, final String message) {
		final String codeString = String.format(Locale.US, "%12d", code);
		
		return codeString + ":" + message;
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
		
		if(socketMessages.length() < 13)
			return null;
		
		final int code = Integer.parseInt(socketMessages.substring(0, 12).trim());
		String messageString = "";
		
		if(socketMessages.length() > 13)
			messageString = socketMessages.substring(13, socketMessages.length());

		
		return new com.org.multigear.communication.tcp.base.Message(code, messageString);
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
