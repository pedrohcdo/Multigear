package com.org.multigear.communication.tcp.server;

import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import com.org.multigear.communication.tcp.base.BaseConnected;
import com.org.multigear.communication.tcp.base.Utils;

import android.app.Activity;
import android.util.Log;

/**
 * 
 * Server
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class Server {
	
	// Final Private Variables
	final private Activity mActivity;
	final String mName;
	final int mPort;
	final AtomicBoolean mWaitForConnection;
	
	// Private Variables
	private ServerSocket mServerSocket;
	private com.org.multigear.communication.tcp.server.Listener mListener;
	private com.org.multigear.communication.tcp.server.ValidateClientThread mValidateClientThread;
	
	
	private boolean mWaitForClientTestFlag = false;
	private boolean mWaitForClientFlag = false;
	private AtomicBoolean mThreadSafeSync = new AtomicBoolean(false);
	
	/*
	 * Construtor
	 */
	public Server(final com.org.multigear.mginterface.engine.Manager manager, final String name, final int port) {
		mActivity = manager.getEngine().getActivity();;
		mName = name;
		mPort = port;
		mListener = null;
		mWaitForConnection = new AtomicBoolean(false);
		mValidateClientThread = null;
	}
	
	/*
	 * Retorna a atividade
	 */
	final protected Activity getActivity() {
		return mActivity;
	}
	
	/**
	 * Inicia o servidor
	 */
	final public void start() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(mPort);
		} catch (Exception e) {
			if(mListener != null)
				mListener.onStartServerFailed();
			return;
		}
		mServerSocket = serverSocket;
		
		try {
			mServerSocket.setSoTimeout(1000);
			if(Utils.SOCKET_RECV_BUFFER_SIZE > 0)
				mServerSocket.setReceiveBufferSize(Utils.SOCKET_RECV_BUFFER_SIZE);
			
		} catch(Exception e) {
			Log.d("LogTest", "Error To Set Buffer Size");
		}
		if(mListener != null)
			mListener.onStartServer();
		if(com.org.multigear.general.utils.KernelUtils.DEBUG)
			Log.d("LogTest", "Server: Server Started ..");
	}
	
	/*
	 * Espera por uma conexão
	 */
	final public void startWaitForClient() {
		if(mValidateClientThread != null) {
			if(!mValidateClientThread.isAlive() || mValidateClientThread.isInterrupted())
				mWaitForConnection.set(false);
		}
		if(!mWaitForConnection.getAndSet(true)) {
			mValidateClientThread = new com.org.multigear.communication.tcp.server.ValidateClientThread(this);
			mValidateClientThread.start();
		}
	}
	
	/*
	 * Espera por uma conexão
	 */
	final public void stopWaitForClient() {
		if(mValidateClientThread != null) {
			mValidateClientThread.close();
		}
		mWaitForConnection.set(false);
	}
	
	/**
	 * On Client Connected (100% Thread Safe)
	 * @param connectedClient
	 */
	final protected void onClientConnected(final BaseConnected connectedClient) {
		// Acquire lock
		while(mThreadSafeSync.getAndSet(true) != false);
		
		// Send connection
		if(connectedClient != null) {
			// Set flat to false (Client connected, no need wait after resume())
			mWaitForClientTestFlag = false;
			// Send message to listener
			if(mListener != null)
				mListener.onClientConnected(connectedClient);
		}
		
		// Release wait Lock
		mValidateClientThread = null;
		mWaitForConnection.set(false);
		
		// Release safe Lock
		mThreadSafeSync.set(false);
	}
	
	/*
	 * Set Listener
	 */
	final public void setListener(final com.org.multigear.communication.tcp.server.Listener listener) {
		mListener = listener;
	}
	
	/*
	 * Return Server Socket
	 */
	final protected ServerSocket getServerSocket() {
		return mServerSocket;
	}
	
	/*
	 * Retorna o nome do server
	 */
	final public String getServerName() {
		return mName;
	}
	
	/*
	 * Error no Servidor
	 */
	final public void error() {
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mActivity.finish();
			}
		});
	}

	/**
	 * Resume Server services
	 */
	final public void resume() {
		// If wait for connection
		if(mWaitForClientFlag) {
			mWaitForClientFlag = false;
			startWaitForClient();
		}
	}
	
	/**
	 * Pause Server Services (100% Thread Safe)
	 */
	final public void pause() {
		// Acquire Lock
		while(mThreadSafeSync.getAndSet(true) != false);
		// If waiting for client
		if(mValidateClientThread != null) {
			mWaitForClientTestFlag = true;
			// Release lock
			mThreadSafeSync.set(false);
			//
			stopWaitForClient();
			// Set final flag
			mWaitForClientFlag = mWaitForClientTestFlag;
		}
		// Release locks (security)
		mThreadSafeSync.set(false);
		mWaitForConnection.set(false);
		mValidateClientThread = null;
	}
	
	/**
	 * Close Server
	 */
	final public void close() {
		try {
			if(mValidateClientThread != null)
				mValidateClientThread.close();
			mServerSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
