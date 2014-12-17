package multigear.communication.tcp.support;

/**
 * Message Info
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class MessageInfo {
	
	// Final Private Variables
	final private multigear.communication.tcp.support.ConnectionInfo mConnectionInfo;
	final private multigear.communication.tcp.support.objectmessage.ObjectMessage mObjectMessage;
	
	/*
	 * Construtor
	 */
	protected MessageInfo(final multigear.communication.tcp.support.ConnectionInfo connectionInfo, final multigear.communication.tcp.support.objectmessage.ObjectMessage objectMessage) {
		mConnectionInfo = connectionInfo;
		mObjectMessage = objectMessage;
	}
	
	/*
	 * Return Connection Info
	 */
	final protected multigear.communication.tcp.support.ConnectionInfo getConnectionInfo() {
		return mConnectionInfo;
	}
	
	/*
	 * Return Object Message
	 */
	final protected multigear.communication.tcp.support.objectmessage.ObjectMessage getObjectMessage() {
		return mObjectMessage;
	}
}
