package multigear.communication.tcp.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import multigear.general.utils.Vector2;

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
	final private List<multigear.communication.tcp.base.BaseConnected> mSupportList;
	final private List<multigear.communication.tcp.support.ServerSupport> mServerSupportList;
	final private List<multigear.communication.tcp.support.ClientSupport> mClientSupportList;
	final private multigear.mginterface.engine.Manager mManager;
	final private Vector<multigear.communication.tcp.support.SupportMessage> mSupportMessages;
	final private Vector<multigear.communication.tcp.support.MessageInfo> mMessagesInfo;
	
	// Private Variables
	private multigear.communication.tcp.support.listener.Listener mListener;
	private int mConnectionPort;
	private boolean mOpenedFlow;
	
	/*
	 * Construtor
	 */
	public ComManager(final multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mSupportList = new ArrayList<multigear.communication.tcp.base.BaseConnected>();
		mServerSupportList = new ArrayList<multigear.communication.tcp.support.ServerSupport>();
		mClientSupportList = new ArrayList<multigear.communication.tcp.support.ClientSupport>();
		mSupportMessages = new Vector<multigear.communication.tcp.support.SupportMessage>();
		mMessagesInfo = new Vector<multigear.communication.tcp.support.MessageInfo>();
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
	final public void setListener(final multigear.communication.tcp.support.listener.Listener listener) {
		mListener = listener;
	}
	
	/**
	 * Open Dataflow
	 */
	final public void openDataflow() {
		mOpenedFlow = true;
	}
	
	/**
	 * Close Dataflow
	 */
	final public void closeDataflow() {
		mOpenedFlow = false;
	}
	
	/*
	 * Adciona um suporte
	 */
	final public void addSupport(final multigear.communication.tcp.base.BaseConnected baseConnected) {
		mSupportList.add(baseConnected);
	}
	
	/*
	 * Retorna quantidade de conex�es
	 */
	final public int getConnectionsSize() {
		return mSupportList.size();
	}
	
	/*
	 * Atualiza o suporte
	 */
	final public void update() {
		for (final multigear.communication.tcp.base.BaseConnected connection : mSupportList) {
			updateConnection(connection);
		}
		// Safe Messages Read
		if(mListener != null) {
			while(mSupportMessages.size() > 0 ) {
				mListener.onComMessage(mSupportMessages.remove(0));
			}
			while(mMessagesInfo.size() > 0 && mOpenedFlow) {
				multigear.communication.tcp.support.MessageInfo messageInfo = mMessagesInfo.remove(0);
				mListener.onMessage(messageInfo.getConnectionInfo(), messageInfo.getObjectMessage());
			}
		}
		
	}
	
	/*
	 * Atualiza uma conex�o
	 */
	final private void updateConnection(final multigear.communication.tcp.base.BaseConnected connection) {
		
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
	
	/*
	 * Envia um objeto para todos
	 */
	final public void sendForAll(final multigear.communication.tcp.support.objectmessage.ObjectMessageBuilt object) {
		for (final multigear.communication.tcp.base.BaseConnected connection : mSupportList) {
			connection.sendMessage(multigear.communication.tcp.base.Utils.CODE_INTERFACE_OBJECTMESSAGE, object.getMessage());
		}
	}
	
	/*
	 * Cria um Servidor
	 */
	final public multigear.communication.tcp.support.ServerSupport createServer(final String name) {
		final multigear.communication.tcp.support.ServerSupport serverSupport = new multigear.communication.tcp.support.ServerSupport(this, mManager, name, mConnectionPort);
		mServerSupportList.add(serverSupport);
		return serverSupport;
	}
	
	/*
	 * Cria um Client
	 */
	final public multigear.communication.tcp.support.ClientSupport createClient(final String name) {
		final multigear.communication.tcp.support.ClientSupport clientSupport = new multigear.communication.tcp.support.ClientSupport(this, mManager, name, mConnectionPort);
		mClientSupportList.add(clientSupport);
		return clientSupport;
	}
	
	/*
	 * Recebe mensagem dos suportes
	 */
	final protected void recvMessageForSupport(final multigear.communication.tcp.support.SupportMessage supportMessage) {
		//mListener.onComSupportMessage(supportMessage);
		mSupportMessages.add(supportMessage);
	}
	
	/**
	 * Call this method to prepare Clients Attributes. 
	 * Prepared attributes will be informed after the finish.
	 */
	final public void prepareParentsAttributes() {
		sendForAll(multigear.communication.tcp.support.objectmessage.ObjectMessage.create(OBJECT_CODE_PREPAREPARENTATTRIBUTES).build());
	}
	
	/**
	 * Call this method to inform all client that attributes were calibrated or modified.
	 * The calibrated or modified attributes will be informed to all Clients.
	 */
	final public void sendCalibratedAttributes() {
		final float sendDpi = mManager.getMainRoom().getDPI();
		final int sendWidth = (int) mManager.getMainRoom().getScreenSize().x;
		final int sendHeight = (int) mManager.getMainRoom().getScreenSize().y;
		sendForAll(multigear.communication.tcp.support.objectmessage.ObjectMessage.create(OBJECT_CODE_PARENTCALIBRATEDATTRIBUTES).add(sendDpi).add(sendWidth).add(sendHeight).build());
	}
	
	/*
	 * Recebe uma mensagem do suporte
	 */
	final protected void recvSupportMessage(final multigear.communication.tcp.base.BaseConnected connection, final multigear.communication.tcp.base.Message message) {
		// Translate Object Message
		if (message.getCode() == multigear.communication.tcp.base.Utils.CODE_INTERFACE_OBJECTMESSAGE) {
			final multigear.communication.tcp.support.objectmessage.ObjectMessage objectMessage = multigear.communication.tcp.support.objectmessage.ObjectMessage.read(message.getMessage());
			final int code = objectMessage.getCode();
			// If system message
			if (code < 0) {
				if (code == OBJECT_CODE_PREPAREPARENTATTRIBUTES) {
					final float sendDpi = mManager.getMainRoom().getDPI();
					final int sendWidth = (int) mManager.getMainRoom().getScreenSize().x;
					final int sendHeight = (int) mManager.getMainRoom().getScreenSize().y;
					sendForAll(multigear.communication.tcp.support.objectmessage.ObjectMessage.create(OBJECT_CODE_PARENTATTRIBUTESPREPARED).add(sendDpi).add(sendWidth).add(sendHeight).build());
				} else if (code == OBJECT_CODE_PARENTATTRIBUTESPREPARED) {
					final float dpi = (Float) objectMessage.getValue(0);
					final int width = (Integer) objectMessage.getValue(1);
					final int height = (Integer) objectMessage.getValue(2);
					final Vector2 size = new Vector2(width, height);
					final multigear.communication.tcp.support.ParentAttributes clientAttributes = new multigear.communication.tcp.support.ParentAttributes(dpi, size, mManager.getMainRoom().getScreenSize());
					final multigear.communication.tcp.support.SupportMessage recvMessage = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.PARENT_PREPAREDATTRIBUTES, clientAttributes);
					recvMessageForSupport(recvMessage);
				} else if (code == OBJECT_CODE_PARENTCALIBRATEDATTRIBUTES) {
					final float dpi = (Float) objectMessage.getValue(0);
					final int width = (Integer) objectMessage.getValue(1);
					final int height = (Integer) objectMessage.getValue(2);
					final Vector2 size = new Vector2(width, height);
					final multigear.communication.tcp.support.ParentAttributes clientAttributes = new multigear.communication.tcp.support.ParentAttributes(dpi, size, mManager.getMainRoom().getScreenSize());
					final multigear.communication.tcp.support.SupportMessage recvMessage = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.PARENT_CALIBRATEDATTRIBUTES, clientAttributes);
					recvMessageForSupport(recvMessage);
				}
				return;
			} else {
				// Add Message Info to Safe Stack
				final multigear.communication.tcp.support.ConnectionInfo connectionInfo = new multigear.communication.tcp.support.ConnectionInfo(connection.getName(), connection.getAddress());
				mMessagesInfo.add(new multigear.communication.tcp.support.MessageInfo(connectionInfo, objectMessage));
			}
			return;
		}
	}
	
	/**
	 * This method called by Engine System.
	 */
	final public void finish() {
		// Finish Servers
		for (multigear.communication.tcp.support.ServerSupport serverSupport : mServerSupportList) {
			serverSupport.finish();
		}
		// Finish Clients
		for (multigear.communication.tcp.support.ClientSupport clientSupport : mClientSupportList) {
			clientSupport.finish();
		}
		// Finish Supports
		for (final multigear.communication.tcp.base.BaseConnected connected : mSupportList) {
			connected.close();
		}
	}
}