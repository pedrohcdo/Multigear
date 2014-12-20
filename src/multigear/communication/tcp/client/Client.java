package multigear.communication.tcp.client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;

public class Client {
	
	// Final Private Variables
	final private Activity mActivity;
	final private int mPort;
	final private String mName;
	final private AtomicBoolean mLockListServers;
	final private AtomicBoolean mLockConnectToServer;
	
	// Private Variables
	private multigear.communication.tcp.client.Listener mListener;
	private multigear.communication.tcp.client.ConnectionThread mConnectionThread;
	private multigear.communication.tcp.client.ConnectionListThread mConnectionListThread;

	
	/*
	 * Conrtutor
	 */
	public Client(final multigear.mginterface.engine.Manager manager, final String name, final int port) {
		mActivity = manager.getEngine().getActivity();
		mName = name;
		mPort = port;
		mListener = null;
		mLockListServers = new AtomicBoolean(false);
		mLockConnectToServer = new AtomicBoolean(false);
		mConnectionThread = null;
		mConnectionListThread = null;
	}
	
	/*
	 * Lista os servidores
	 */
	final public void listServers(final int connectionAttempts) {
		if(mConnectionListThread != null) {
			if(!mConnectionListThread.isAlive() || mConnectionListThread.isInterrupted())
				mLockListServers.set(false);
		}
		if(!mLockListServers.getAndSet(true)) {
			mConnectionListThread = new multigear.communication.tcp.client.ConnectionListThread(this, connectionAttempts);
			mConnectionListThread.start();
		}
	}
	
	/*
	 * Conecta o cliente a um server
	 */
	final public void connect(final multigear.communication.tcp.client.ServersList.ServerInfo serverInfo, final int attempts) {
		if(mConnectionThread != null) {
			if(!mConnectionThread.isAlive() | mConnectionThread.isInterrupted())
				mLockConnectToServer.set(false);
		}
		if(!mLockConnectToServer.getAndSet(true)) {
			mConnectionThread = new multigear.communication.tcp.client.ConnectionThread(this, serverInfo, mPort, attempts);
			mConnectionThread.start();
		}
	}
	
	/*
	 * Libera o bloqueio de listamento
	 */
	final protected void releaseListThread() {
		mLockListServers.set(false);
	}
	
	/*
	 * Libera o bloqueio de conexão
	 */
	final protected void releaseConnectThread() {
		mLockConnectToServer.set(false);
	}
	
	/*
	 * Retorna a atividade
	 */
	final protected Activity getActivity() {
		return mActivity;
	}
	
	/*
	 * Retorna o nome
	 */
	final public String getName() {
		return mName;
	}
	
	/*
	 * Retorna a porta de conexão
	 */
	final protected int getConnectionPort() {
		return mPort;
	}
	
	/*
	 * Set Listener
	 */
	final public void setListener(final multigear.communication.tcp.client.Listener listener) {
		mListener = listener;
	}
	
	/*
	 * Listamento dos Servers
	 */
	final protected void onListed(final multigear.communication.tcp.client.ServersList serverList) {
		if (mListener != null)
			mListener.onListed(serverList);
	}
	
	/*
	 * Conectado
	 */
	final protected void onConnected(final Socket socket, final multigear.communication.tcp.client.ServersList.ServerInfo serverInfo, final BufferedReader in, final PrintWriter out) {
		final multigear.communication.tcp.base.BaseConnected connectedServer = new multigear.communication.tcp.base.BaseConnected(mActivity, serverInfo.Name, socket, in, out);
		if (mListener != null)
			mListener.onConnected(connectedServer);
	}
	
	/*
	 * Conectado
	 */
	final protected void onConnectedFailed() {
		if (mListener != null)
			mListener.onConnectionFailed();
	}
	
	/*
	 * Fecha o Client
	 */
	final public void close() {
		try {
			if(mConnectionListThread != null)
				mConnectionListThread.close();
			if(mConnectionThread != null)
				mConnectionThread.close();
		} catch(Exception e) {
		}
	}
}
