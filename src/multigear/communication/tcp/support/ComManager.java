package multigear.communication.tcp.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import multigear.communication.tcp.base.BaseConnected;
import multigear.communication.tcp.base.Message;
import multigear.communication.tcp.base.Utils;
import multigear.communication.tcp.support.listener.Listener;
import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilt;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.Manager;

/**
 * Connection Support
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ComManager {
	
	// Constants
	final static private int OBJECT_CODE_PREPAREPARENTATTRIBUTES = -1;
	final static private int OBJECT_CODE_PARENTATTRIBUTESPREPARED = -2;
	final static public int OBJECT_CODE_PARENTCALIBRATEDATTRIBUTES = -3;
	
	// Final Private Variables
	final private List<BaseConnected> mSupportList;
	final private List<ServerSupport> mServerSupportList;
	final private List<ClientSupport> mClientSupportList;
	final private Manager mManager;
	final private Vector<SupportMessage> mSupportMessages;
	final private Vector<MessageInfo> mMessagesInfo;
	
	// Private Variables
	private multigear.communication.tcp.support.listener.Listener mListener;
	private int mConnectionPort;
	private boolean mOpenedFlow;
	
	/*
	 * Construtor
	 */
	public ComManager(final Manager manager) {
		mManager = manager;
		mSupportList = new ArrayList<BaseConnected>();
		mServerSupportList = new ArrayList<ServerSupport>();
		mClientSupportList = new ArrayList<ClientSupport>();
		mSupportMessages = new Vector<SupportMessage>();
		mMessagesInfo = new Vector<MessageInfo>();
		mListener = null;
		mConnectionPort = 4545;
		mOpenedFlow = false;
	}
	
	/**
	 * Set connection port. Default is 4545.
	 * 
	 * @param port
	 *            Port
	 */
	final public void setConnectionPort(final int port) {
		mConnectionPort = port;
	}
	
	/*
	 * Altera o listener
	 */
	final public void setListener(final Listener listener) {
		mListener = listener;
	}
	
	/**
	 * Opens the flow of communication between clients. 
	 * When a stream is open all communication will be made 
	 * with a control interface, so there is no loss of data 
	 * is recommended to close the flow when not in use.
	 */
	final public void openDataflow() {
		mOpenedFlow = true;
	}
	
	/**
	 * Closes the flow of communication between clients.
	 * When a stream is closed, it will not be treated and had 
	 * been in waiting, so preventing data loss.
	 */
	final public void closeDataflow() {
		mOpenedFlow = false;
	}
	
	/*
	 * Adciona um suporte
	 */
	final public void addSupport(final BaseConnected baseConnected) {
		mSupportList.add(baseConnected);
	}
	
	/*
	 * Retorna quantidade de conexões
	 */
	final public int getConnectionsSize() {
		return mSupportList.size();
	}
	
	/*
	 * Atualiza o suporte
	 */
	final public void update() {
		for (final BaseConnected connection : mSupportList) {
			updateConnection(connection);
		}
		// Safe Messages Read
		if(mListener != null) {
			while(mSupportMessages.size() > 0 ) {
				mListener.onComMessage(mSupportMessages.remove(0));
			}
			while(mMessagesInfo.size() > 0 && mOpenedFlow) {
				MessageInfo messageInfo = mMessagesInfo.remove(0);
				mListener.onMessage(messageInfo.getConnectionInfo(), messageInfo.getObjectMessage());
			}
		}
		
	}
	
	/**
	 * Read Messages.
	 * Nota: Esta lendo 10 mensagens por frame.
	 * 
	 * @param connection
	 */
	final private void updateConnection(final BaseConnected connection) {
		
		multigear.communication.tcp.base.Message message;
		//long time = System.currentTimeMillis();
		long messages = 0;
		while ((message = connection.readMessage()) != null) {
			messages++;
			recvSupportMessage(connection, message);
			message = message.next();
			if (messages >= 10)
				break;
		}
		
		//Log.d("LogTest2", "Messages Read: " + messages);
	}
	
	/**
	 * Send message for all clients
	 * @param object
	 */
	final public void sendForAll(final ObjectMessageBuilt object) {
		for (final BaseConnected connection : mSupportList) {
			connection.sendMessage(Utils.CODE_INTERFACE_OBJECTMESSAGE, object.getMessage());
		}
	}
	
	/**
	 * Cria um Server
	 * @param name
	 * @return
	 */
	final public ServerSupport createServer(final String name) {
		final ServerSupport serverSupport = new ServerSupport(this, mManager, name, mConnectionPort);
		mServerSupportList.add(serverSupport);
		return serverSupport;
	}
	
	/**
	 * Cria um Cliente
	 * 
	 * @param name
	 * @return
	 */
	final public ClientSupport createClient(final String name) {
		final ClientSupport clientSupport = new ClientSupport(this, mManager, name, mConnectionPort);
		mClientSupportList.add(clientSupport);
		return clientSupport;
	}
	
	/*
	 * Recebe mensagem dos suportes
	 */
	final protected void recvMessageForSupport(final SupportMessage supportMessage) {
		//mListener.onComSupportMessage(supportMessage);
		mSupportMessages.add(supportMessage);
	}
	
	/**
	 * Call this method to prepare Clients Attributes. 
	 * Prepared attributes will be informed after the finish.
	 */
	final public void prepareParentsAttributes() {
		sendForAll(ObjectMessage.create(OBJECT_CODE_PREPAREPARENTATTRIBUTES).build());
	}
	
	/**
	 * Call this method to inform all client that attributes were calibrated or modified.
	 * The calibrated or modified attributes will be informed to all Clients.
	 */
	final public void sendCalibratedAttributes() {
		final float sendDpi = mManager.getMainRoom().getDPI();
		final int sendWidth = (int) mManager.getMainRoom().getScreenSize().x;
		final int sendHeight = (int) mManager.getMainRoom().getScreenSize().y;
		sendForAll(ObjectMessage.create(OBJECT_CODE_PARENTCALIBRATEDATTRIBUTES).add(sendDpi).add(sendWidth).add(sendHeight).build());
	}
	
	/*
	 * Recebe uma mensagem do suporte
	 */
	final protected void recvSupportMessage(final BaseConnected connection, final Message message) {
		// Translate Object Message
		if (message.getCode() == Utils.CODE_INTERFACE_OBJECTMESSAGE) {
			final ObjectMessage objectMessage = ObjectMessage.read(message.getMessage());
			final int code = objectMessage.getCode();
			// If system message
			if (code < 0) {
				if (code == OBJECT_CODE_PREPAREPARENTATTRIBUTES) {
					final float sendDpi = mManager.getMainRoom().getDPI();
					final int sendWidth = (int) mManager.getMainRoom().getScreenSize().x;
					final int sendHeight = (int) mManager.getMainRoom().getScreenSize().y;
					sendForAll(ObjectMessage.create(OBJECT_CODE_PARENTATTRIBUTESPREPARED).add(sendDpi).add(sendWidth).add(sendHeight).build());
				} else if (code == OBJECT_CODE_PARENTATTRIBUTESPREPARED) {
					final float dpi = (Float) objectMessage.getValue(0);
					final int width = (Integer) objectMessage.getValue(1);
					final int height = (Integer) objectMessage.getValue(2);
					final Vector2 size = new Vector2(width, height);
					final ParentAttributes clientAttributes = new ParentAttributes(dpi, size);
					final SupportMessage recvMessage = new SupportMessage(multigear.communication.tcp.support.SupportMessage.PARENT_PREPAREDATTRIBUTES, clientAttributes);
					recvMessageForSupport(recvMessage);
				} else if (code == OBJECT_CODE_PARENTCALIBRATEDATTRIBUTES) {
					final float dpi = (Float) objectMessage.getValue(0);
					final int width = (Integer) objectMessage.getValue(1);
					final int height = (Integer) objectMessage.getValue(2);
					final Vector2 size = new Vector2(width, height);
					final ParentAttributes clientAttributes = new ParentAttributes(dpi, size);
					final SupportMessage recvMessage = new SupportMessage(SupportMessage.PARENT_CALIBRATEDATTRIBUTES, clientAttributes);
					recvMessageForSupport(recvMessage);
				}
				return;
			} else {
				// Add Message Info to Safe Stack
				final ConnectionInfo connectionInfo = new ConnectionInfo(connection.getName(), connection.getAddress());
				mMessagesInfo.add(new MessageInfo(connectionInfo, objectMessage));
			}
			return;
		}
	}
	
	/**
	 * This method called by Engine System.
	 */
	final public void finish() {
		// Finish Servers
		for (ServerSupport serverSupport : mServerSupportList) {
			serverSupport.finish();
		}
		// Finish Clients
		for (ClientSupport clientSupport : mClientSupportList) {
			clientSupport.finish();
		}
		// Finish Supports
		for (final BaseConnected connected : mSupportList) {
			connected.close();
		}
	}
}
