package multigear.communication.tcp.support;

import java.util.Iterator;

import android.util.Log;
import multigear.communication.tcp.base.BaseConnected;
import multigear.communication.tcp.base.Message;
import multigear.communication.tcp.base.Utils;
import multigear.communication.tcp.client.Client;
import multigear.communication.tcp.client.ServersList;
import multigear.mginterface.engine.Manager;
import multigear.mginterface.engine.eventsmanager.GlobalClock;


/**
 * Client Support
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class ClientSupport {
	
	/**
	 * Blocked Listener
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final private class BlockedListener implements multigear.communication.tcp.client.Listener {
		/*
		 * Servers Listed
		 */
		@Override
		public void onListed(ServersList serverList) {
			final multigear.communication.tcp.support.SupportMessage message = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.CLIENT_LISTEDSERVERS, serverList);
			mComSupport.recvMessageForSupport(message);
		}

		/*
		 * Connected to Server
		 */
		@Override
		public void onConnected(BaseConnected serverCon) {
			// Set connection
			mConnection = serverCon;
			
			// Set connected
			mSendKeepAliveTime = GlobalClock.currentTimeMillis();
			mRecvKeepAliveTime = GlobalClock.currentTimeMillis();
			mConnecting = false;
			mConnected = true;
			
			// Send support message
			final ConnectionInfo connectionInfo = new ConnectionInfo(serverCon.getName(), serverCon.getAddress());
			final SupportMessage message = new SupportMessage(multigear.communication.tcp.support.SupportMessage.CLIENT_CONNECTEDTOSERVER, connectionInfo);
			mComSupport.recvMessageForSupport(message);
		}

		/*
		 * Client Connect to Server failed.
		 */
		@Override
		public void onConnectionFailed() {
			// Not connected
			mConnecting = false;
			// Send support message
			final SupportMessage message = new SupportMessage(multigear.communication.tcp.support.SupportMessage.CLIENT_CONNECTFAILED, null);
			mComSupport.recvMessageForSupport(message);
		}
	}
	
	// Options
	final public static int OPTION_CLOSE_ON_PAUSE = 1;
	
	// Final Private Variables
	final private Client mClient;
	final private ComManager mComSupport;
	final private Manager mManager;
	final private Message[] mMessagesOutput = new Message[10];
	
	// Private Variables
	private volatile boolean mConnecting, mConnected, mDisconnected;
	private boolean mFinish;
	private volatile BaseConnected mConnection;
	
	private long mSendKeepAliveTime = 0;
	private long mRecvKeepAliveTime = 0;
	private long mKeepAliveTime = -1;
	private int mOptions = 0;
	private Object mCloseSync = new Object();
	
	/*
	 * Construtor
	 */
	public ClientSupport(final multigear.communication.tcp.support.ComManager comSupport, final multigear.mginterface.engine.Manager manager, final String name, final int port) {
		mComSupport = comSupport;
		mClient = new multigear.communication.tcp.client.Client(manager, name, port);
		mClient.setListener(new BlockedListener());
		mManager = manager;
		mConnecting = false;
		mFinish = false;
	}

	/*
	 * Lista os servidores
	 */
	final public void listServers(final int range) {
		if(mConnecting)
			multigear.general.utils.KernelUtils.error(mManager.getEngine().getActivity(), "ClientSupport: An error ocurred while calling 'connect'. The client is already trying to connect.", 0x11);
		mClient.listServers(range);
	}
	
	/*
	 * Conecta a um Host
	 */
	final public void connect(final multigear.communication.tcp.client.ServersList.ServerInfo serverInfo, final int attempts) {
		if(mConnected)
			throw new RuntimeException("Already connected.");
		if(mConnecting)
			throw new RuntimeException("Already connecting.");
		mConnecting = true;
		mClient.connect(serverInfo, attempts);
	}
	
	/**
	 * Set Keep Alive Timeout
	 * 
	 * @param time
	 */
	final public void setKeepAliveTimeout(final long time) {
		mKeepAliveTime = time;
	}
	
	/**
	 * Get Keep Alive Timeout
	 * 
	 * @param time
	 */
	final public long getKeepAliveTimeout() {
		return mKeepAliveTime;
	}
	
	/**
	 * Enable Options
	 */
	final public void enableOption(final int options) {
		mOptions |= options;
	}
	
	/**
	 * Disable Options
	 * @param options
	 */
	final public void disableOption(final int options) {
		mOptions = (mOptions & ~options);
	}
	
	/**
	 * Return true if has Option
	 * 
	 * @param option
	 * @return
	 */
	final public boolean hasOption(final int option) {
		return (mOptions & option) == option;
	}
	
	/**
	 * Update Connections
	 */
	final protected void update() {
		// Security lock
		synchronized(mCloseSync) {
			// Return if not have stabilized connection
			if(!mConnected || mDisconnected || mConnection == null)
				return;
			
			// Send Keep Alive message
			if((GlobalClock.currentTimeMillis() - mSendKeepAliveTime) > 200) {
				mSendKeepAliveTime = GlobalClock.currentTimeMillis();
				mConnection.sendMessage(Utils.CODE_INTERFACE_KEEPALIVE);
			}
			
			// Read Messages
			int count = mConnection.readMessage(mMessagesOutput);
			// Delivery all messages
			for(int i=0; i<count; i++) {
				Message message = mMessagesOutput[i];
				switch(message.getCode()) {
				// Keep alive message
				case Utils.CODE_INTERFACE_KEEPALIVE:
					mRecvKeepAliveTime = GlobalClock.currentTimeMillis();
					break;
				// Closed by Server
				case Utils.CODE_INTERFACE_CLOSED:
				case Utils.CODE_INTERFACE_PAUSED_AND_CLOSED:
					closedByServer();
					return;
				// Pass to manager
				default:
					mComSupport.recvSupportMessage(mConnection, message);
				}
			}
			
			// If use keep alive
			if(mKeepAliveTime >= 0) {
				long diff = (GlobalClock.currentTimeMillis() - mRecvKeepAliveTime);
				// if timeout (disconnect)
				if(diff >= mKeepAliveTime)
					closedByTimeout();
			}
		}
	}
	
	/**
	 * Send for connection
	 * 
	 * @param code
	 * @param message
	 */
	final protected void send(final int code, final String message) {
		if(mConnected && mConnection != null)
			mConnection.sendMessage(code, message);
	}
	
	/**
	 * Send for connection
	 * @param info
	 */
	final protected void send(final ConnectionInfo info, int code, String message) {
		// If connected
		if(mConnected && mConnection != null) {
			// Check if is same connection
			if(mConnection.getName().equals(info.Name) && mConnection.getAddress().equals(info.Address))
				mConnection.sendMessage(code, message);
		}
	}
	
	/**
	 * Pause connection
	 */
	final protected void pause() {
		if(mConnection != null) {
			// Close if need
			if(hasOption(OPTION_CLOSE_ON_PAUSE)) {
				closedByPause();
			} else {
				mConnection.pause();
			}
		}
	}
	
	/**
	 * Resume connection
	 */
	final protected void resume() {
		if(mConnection != null)
			mConnection.resume();
	}
	
	/**
	 * Return true if is connecting
	 * @return
	 */
	final public boolean isConnecting() {
		return mConnecting;
	}
	
	/**
	 * Return true if is connected
	 * @return
	 */
	final public boolean isConnected() {
		return mConnected;
	}
	
	/**
	 * Return true if was disconnected
	 * @return
	 */
	final public boolean isDisconnected() {
		return mDisconnected;
	}
	
	/**
	 * Return true if is closed
	 * @return
	 */
	final public boolean isClosed() {
		return mFinish;
	}
	
	/**
	 * Closed by server
	 */
	final private void closedByServer() {
		// Close Connection
		if(mConnection != null) {
			// Pause and close connection
			mConnection.pause();
			mConnection.close();
			// Send Message to Support
			final ConnectionInfo connectionInfo = new ConnectionInfo(mConnection.getName(), mConnection.getAddress());
			final SupportMessage message = new SupportMessage(SupportMessage.CLIENT_DISCONNECTED, connectionInfo);
			mComSupport.recvMessageForSupport(message);
		}
		// Close Client
		mClient.close();
		// Set values
		mConnection = null;
		mFinish = true;
	}
	
	/**
	 * Closed by pause
	 */
	final private void closedByPause() {
		// Security Lock
		synchronized(mCloseSync) {
			// Send client closed
			if(mConnection != null) {
				// Send closed and pause, close this connection
				mConnection.sendMessage(Utils.CODE_INTERFACE_PAUSED_AND_CLOSED);
				mConnection.pause();
				mConnection.forceToSendAll();
				mConnection.close();
				// Send Message to Support
				final ConnectionInfo connectionInfo = new ConnectionInfo(mConnection.getName(), mConnection.getAddress());
				final SupportMessage message = new SupportMessage(SupportMessage.CLIENT_DISCONNECTED, connectionInfo);
				mComSupport.recvMessageForSupport(message);
			}
			// Close Client
			mClient.close();
			// Set values
			mConnection = null;
			mFinish = true;
		}
	}
	
	/**
	 * Close by Timeout
	 */
	final public void closedByTimeout() {
		// Send client closed
		if(mConnection != null) {
			// Send closed and pause, close this connection
			mConnection.sendMessage(Utils.CODE_INTERFACE_CLOSED);
			mConnection.pause();
			mConnection.forceToSendAll();
			mConnection.close();
			// Send Message to Support
			final ConnectionInfo connectionInfo = new ConnectionInfo(mConnection.getName(), mConnection.getAddress());
			final SupportMessage message = new SupportMessage(SupportMessage.CLIENT_DISCONNECTED, connectionInfo);
			mComSupport.recvMessageForSupport(message);
		}
		// Close Client
		mClient.close();
		// Set values
		mConnection = null;
		mFinish = true;
	}
	
	/**
	 * Close this ClientSupport
	 */
	final synchronized public void close() {
		if(mFinish)
			throw new RuntimeException("This ClientSupport has already been closed.");
		// Send client closed
		if(mConnection != null) {
			// Send closed and pause, close this connection
			mConnection.sendMessage(Utils.CODE_INTERFACE_CLOSED);
			mConnection.pause();
			mConnection.forceToSendAll();
			mConnection.close();
		}
		// Close Client
		mClient.close();
		// Set values
		mConnection = null;
		mFinish = true;
	}
}
