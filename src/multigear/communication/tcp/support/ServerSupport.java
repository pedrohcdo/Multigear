package multigear.communication.tcp.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import multigear.communication.tcp.base.BaseConnected;
import multigear.communication.tcp.base.Message;
import multigear.communication.tcp.base.Utils;
import multigear.communication.tcp.server.Server;
import multigear.mginterface.engine.Manager;
import multigear.mginterface.engine.eventsmanager.GlobalClock;



/**
 * Server Support
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ServerSupport {
	
	/**
	 * Client Adapter
	 * 
	 * @author user
	 *
	 */
	final private class ClientAdapter {
		
		// Vars
		private BaseConnected connection = null;
		private long sendKeepAliveTime = 0;
		private long recvKeepAliveTime = 0;
	}
	
	/**
	 * Blocked Listener
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final private class BlockedListener implements multigear.communication.tcp.server.Listener {

		/*
		 * Start Server
		 */
		@Override
		public void onStartServer() {
			// Started
			mStarted = true;
			// Send Support message
			final SupportMessage message = new SupportMessage(SupportMessage.SERVER_STARTED, ServerSupport.this);
			mComSupport.recvMessageForSupport(message);
		}

		/*
		 * Server Failed
		 */
		@Override
		public void onStartServerFailed() {
			// Send Support message
			final SupportMessage message = new SupportMessage(SupportMessage.SERVER_CONNECTIONFAILED, null);
			mComSupport.recvMessageForSupport(message);
		}

		/*
		 * Client Connected
		 */
		@Override
		public void onClientConnected(final BaseConnected connectedClient) {
			
			// Add client adapter
			final ClientAdapter clientAdapter = new ClientAdapter();
			clientAdapter.recvKeepAliveTime = GlobalClock.currentTimeMillis();
			clientAdapter.sendKeepAliveTime = GlobalClock.currentTimeMillis();
			clientAdapter.connection = connectedClient;
			synchronized(mConnectionsAdapters) {
				mConnectionsAdapters.add(clientAdapter);
			}
			
			// Send support message
			final ConnectionInfo connectionInfo = new ConnectionInfo(connectedClient.getName(), connectedClient.getAddress());
			final SupportMessage message = new SupportMessage(SupportMessage.SERVER_CLIENTCONNECTED, connectionInfo);
			mComSupport.recvMessageForSupport(message);
		}
	}
	
	// Options
	final public static int OPTION_CLOSE_ON_PAUSE = 1;
	
	// Final Private Variables
	final private Server mServer;
	final private ComManager mComSupport;
	final private Manager mManager;
	
	final private Message[] mMessagesOutput = new Message[10];
	final private List<ClientAdapter> mConnectionsAdapters = new ArrayList<ClientAdapter>();
	final private Object mSecurityLock = new Object();
	
	
	// Private Variables
	private boolean mStarted;
	private boolean mStarting;
	private boolean mFinish;
	private long mKeepAliveTime = -1;
	private int mOptions = 0;
	
	/*
	 * Construtor
	 */
	public ServerSupport(final multigear.communication.tcp.support.ComManager comSupport, final multigear.mginterface.engine.Manager manager, final String name, final int port) {
		mComSupport = comSupport;
		mServer = new Server(manager, name, port);
		mServer.setListener(new BlockedListener());
		mStarted = false;
		mStarting = false;
		mFinish = false;
		mManager = manager;
	}
	
	/*
	 * Start Server
	 */
	final public void start() {
		if(mStarting | mStarted)
			multigear.general.utils.KernelUtils.error(mManager.getEngine().getActivity(), "ServerSupport: An error ocurred while calling 'start'. The Server is Starting or has been started..", 0x10);
		mStarting = true;
		mServer.start();
	}
	
	/*
	 * Espera por uma conexão
	 */
	final public void startWaitForClient() {
		if(!mStarted)
			multigear.general.utils.KernelUtils.error(mManager.getEngine().getActivity(), "ServerSupport: An error ocurred while calling 'waitForClient'. The Server was not correctly started.", 0x9);
		mServer.startWaitForClient();
	}
	
	/**
	 * Stop wait for client
	 */
	final public void stopWaitForClient() {
		if(!mStarted)
			multigear.general.utils.KernelUtils.error(mManager.getEngine().getActivity(), "ServerSupport: An error ocurred while calling 'waitForClient'. The Server was not correctly started.", 0x9);
		mServer.stopWaitForClient();
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
		// Security Lock
		synchronized(mSecurityLock) {
			// Connection Lock
			synchronized(mConnectionsAdapters) {
				// Update Connection
				final Iterator<ClientAdapter> itr = mConnectionsAdapters.iterator();
				//
				while(itr.hasNext()) {
					ClientAdapter adapter = itr.next();
					// Send Keep Alive message
					if((GlobalClock.currentTimeMillis() - adapter.sendKeepAliveTime) > 200) {
						adapter.sendKeepAliveTime = GlobalClock.currentTimeMillis();
						adapter.connection.sendMessage(Utils.CODE_INTERFACE_KEEPALIVE);
					}
					// Read Messages
					int count = adapter.connection.readMessage(mMessagesOutput);
					// Delivery all messages
					for(int i=0; i<count; i++) {
						Message message = mMessagesOutput[i];
						switch(message.getCode()) {
						// Keep alive message
						case Utils.CODE_INTERFACE_KEEPALIVE:
							adapter.recvKeepAliveTime = GlobalClock.currentTimeMillis();
							break;
						// If closed in client side
						case Utils.CODE_INTERFACE_CLOSED:
						case Utils.CODE_INTERFACE_PAUSED_AND_CLOSED:
							itr.remove();
							closedByClient(adapter.connection);
							return;
						// Pass to manager
						default:
							mComSupport.recvSupportMessage(adapter.connection, message);
						}
					}
					// If use keep alive
					if(mKeepAliveTime >= 0) {
						long diff = (GlobalClock.currentTimeMillis() - adapter.recvKeepAliveTime);
						// if timeout (disconnect)
						if(diff >= mKeepAliveTime) {
							itr.remove();
							closeByTimeout(adapter.connection);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Return connections count
	 * @return
	 */
	final public int getConnectionCount() {
		return mConnectionsAdapters.size();
	}
	
	/**
	 * Get Connection Info
	 * @param index
	 * @return
	 */
	final public ConnectionInfo getConnectionInfo(final int index) {
		if(index < 0 || index >= mConnectionsAdapters.size())
			throw new IndexOutOfBoundsException();
		final ClientAdapter adapter = mConnectionsAdapters.get(index);
		return new ConnectionInfo(adapter.connection.getName(), adapter.connection.getAddress());
	}
	
	/**
	 * Return true if found and close the specific connection, or false in otherwise
	 */
	final public boolean closeConnection(final ConnectionInfo connectionInfo) {
		boolean found = false;
		Iterator<ClientAdapter> itr = mConnectionsAdapters.iterator();
		// Search
		while(itr.hasNext()) {
			//
			final ClientAdapter adapter = itr.next();
			// Check if is same connection
			if(adapter.connection.getName().equals(connectionInfo.Name) && adapter.connection.getAddress().equals(connectionInfo.Address)) {
				// Close connection
				closeSilent(adapter.connection);
				// found and remove connection
				found = true;
				itr.remove();
			}
		}
		return found;
	}
	
	/**
	 * Send for all connections
	 * 
	 * @param code
	 * @param message
	 */
	final protected void sendForAll(final int code, final String message) {
		for(final ClientAdapter adapter : mConnectionsAdapters) {
			adapter.connection.sendMessage(code, message);
		}
	}
	
	/**
	 * Send for connection
	 * @param info
	 */
	final protected void sendToConnection(final ConnectionInfo info, int code, String message) {
		// Search
		for(final ClientAdapter adapter : mConnectionsAdapters) {
			// Check if is same connection
			if(adapter.connection.getName().equals(info.Name) && adapter.connection.getAddress().equals(info.Address))
				adapter.connection.sendMessage(code, message);
		}
	}
	
	/**
	 * Pause Support
	 */
	final protected void pause() {
		boolean closeOnPause = hasOption(OPTION_CLOSE_ON_PAUSE);
		// Security Lock
		synchronized(mSecurityLock) {
			Iterator<ClientAdapter> itr = mConnectionsAdapters.iterator();
			// Pause or close all connections
			while(itr.hasNext()) {
				ClientAdapter adapter = itr.next();
				// Close all connections if has close on pause option
				if(closeOnPause) {
					// Remove this client
					itr.remove();
					// Close
					closedByPause(adapter.connection);
				} else {
					adapter.connection.pause();
				}
			}
			mServer.pause();
		}
	}
	
	/**
	 * Resume Support
	 */
	final protected void resume() {
		mServer.resume();
		for(final ClientAdapter adapter : mConnectionsAdapters) {
			adapter.connection.resume();
		}
	}
	
	/**
	 * Return true if is closed
	 * @return
	 */
	final public boolean isClosed() {
		return mFinish;
	}
	
	/**
	 * Close by Client
	 */
	final synchronized protected void closedByClient(final BaseConnected connection) {
		// Pause Threads
		connection.pause();
		connection.close();
		// Send disconnected to support
		final ConnectionInfo connectionInfo = new ConnectionInfo(connection.getName(), connection.getAddress());
		final SupportMessage message = new SupportMessage(SupportMessage.SERVER_CLIENTDISCONNECTED, connectionInfo);
		mComSupport.recvMessageForSupport(message);
	}
	
	/**
	 * Close by Pause
	 */
	final synchronized protected void closedByPause(final BaseConnected connection) {
		// Send closed and pause, close this connection
		connection.sendMessage(Utils.CODE_INTERFACE_PAUSED_AND_CLOSED);
		connection.pause();
		connection.forceToSendAll();
		connection.close();
		// Send disconnected to support
		final ConnectionInfo connectionInfo = new ConnectionInfo(connection.getName(), connection.getAddress());
		final SupportMessage message = new SupportMessage(SupportMessage.SERVER_CLIENTDISCONNECTED, connectionInfo);
		mComSupport.recvMessageForSupport(message);
	}
	
	/**
	 * Closed by Timeout
	 * @param connection
	 */
	final public void closeByTimeout(final BaseConnected connection) {
		// Send closed and pause, close this connection
		connection.sendMessage(Utils.CODE_INTERFACE_CLOSED);
		connection.pause();
		connection.forceToSendAll();
		connection.close();
		// Send disconnected to support
		final ConnectionInfo connectionInfo = new ConnectionInfo(connection.getName(), connection.getAddress());
		final SupportMessage message = new SupportMessage(SupportMessage.SERVER_CLIENTDISCONNECTED, connectionInfo);
		mComSupport.recvMessageForSupport(message);
	}
	
	/**
	 * Closed by Timeout
	 * @param connection
	 */
	final public void closeSilent(final BaseConnected connection) {
		// Send closed and pause, close this connection
		connection.sendMessage(Utils.CODE_INTERFACE_CLOSED);
		connection.pause();
		connection.forceToSendAll();
		connection.close();
	}
	
	/**
	 * Close this ServerSupport
	 */
	final synchronized protected void close() {
		if(mFinish)
			throw new RuntimeException("This ServerSupport has already been closed.");
		
		// Stop Wait for prevent
		mServer.stopWaitForClient();
		
		// Send closed to clients
		for(final ClientAdapter adapter : mConnectionsAdapters)
			closeSilent(adapter.connection);
		
		// Close 
		mServer.close();
		mFinish = true;
	}

}
