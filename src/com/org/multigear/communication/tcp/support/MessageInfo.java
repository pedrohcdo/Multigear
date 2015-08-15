package com.org.multigear.communication.tcp.support;

/**
 * Message Info
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class MessageInfo {
	
	// Final Private Variables
	final private com.org.multigear.communication.tcp.support.ConnectionInfo mConnectionInfo;
	final private com.org.multigear.communication.tcp.support.objectmessage.ObjectMessage mObjectMessage;
	
	/*
	 * Construtor
	 */
	protected MessageInfo(final com.org.multigear.communication.tcp.support.ConnectionInfo connectionInfo, final com.org.multigear.communication.tcp.support.objectmessage.ObjectMessage objectMessage) {
		mConnectionInfo = connectionInfo;
		mObjectMessage = objectMessage;
	}
	
	/*
	 * Return Connection Info
	 */
	final protected com.org.multigear.communication.tcp.support.ConnectionInfo getConnectionInfo() {
		return mConnectionInfo;
	}
	
	/*
	 * Return Object Message
	 */
	final protected com.org.multigear.communication.tcp.support.objectmessage.ObjectMessage getObjectMessage() {
		return mObjectMessage;
	}
}
