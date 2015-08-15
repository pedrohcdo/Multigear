package com.org.multigear.communication.tcp.support;

/**
 * Support Message
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class SupportMessage {
	
	/** 
	 * Server started.
	 * 
	 * Cast to ServerSupport to get ServerSupport.
	 * <p>
	 * <b>Example:</b><br>
	 * (ServerSupport) message.Object
	 */
	final static public int SERVER_STARTED = 0x1;
	/** 
	 * Server Conection Failed.
	 */
	final static public int SERVER_CONNECTIONFAILED = 0x2;
	/** 
	 * Client connected to Server.<br>
	 * Cast to ConnectionInfo to get client info.
	 * <p>
	 * <b>Example:</b><br>
	 * (ConnectionInfo) message.Object
	 */
	final static public int SERVER_CLIENTCONNECTED = 0x3;
	/** 
	 * Client disconnected to Server.<br>
	 * Cast to ConnectionInfo to get client info.
	 * <p>
	 * <b>Example:</b><br>
	 * (ConnectionInfo) message.Object
	 */
	final static public int SERVER_CLIENTDISCONNECTED = 0x4;
	/** 
	 * Client Listed Servers.<br>
	 * Cast to ServersList to acess list.
	 * <p>
	 * <b>Example:</b><br>
	 * (ServersList) message.Object
	 */
	final static public int CLIENT_LISTEDSERVERS = 0x5;
	/**
	 * Cliente has been connected to Server.<br>
	 * Cast to ConnectionInfo to get server info.
	 * <p>
	 * <b>Example:</b><br>
	 * (ConnectionInfo) message.Object
	 */
	final static public int CLIENT_CONNECTEDTOSERVER = 0x6;
	/**
	 * Client connect to server failed.
	 */
	final static public int CLIENT_CONNECTFAILED = 0x7;
	/**
	 * Client disconnected
	 * 
	 */
	final static public int CLIENT_DISCONNECTED = 0x8;
	/**
	 * Client/Server prepared attributes.<br>
	 * Cast to ClientAttributes to get client/server attributes.
	 * <p>
	 * <b>Example:</b><br>
	 * (ParentAttributes) message.Object
	 * 
	 */
	final static public int PARENT_PREPAREDATTRIBUTES = 0x9;
	/**
	 * Client/Server prepared attributes.<br>
	 * Cast to ClientAttributes to get client/server attributes.
	 * <p>
	 * <b>Example:</b><br>
	 * (ParentAttributes) message.Object
	 */
	final static public int PARENT_CALIBRATEDATTRIBUTES = 0x10;
	
	
	/* Final Public Variables */
	final public int Message;
	final public Object Object;
	
	/*
	 * Construtor
	 */
	public SupportMessage(final int message, final Object object) {
		Message = message;
		Object = object;
	}
}
