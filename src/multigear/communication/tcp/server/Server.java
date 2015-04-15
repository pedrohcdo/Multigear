package multigear.communication.tcp.server;

import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import multigear.communication.tcp.base.Utils;
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
	private multigear.communication.tcp.server.Listener mListener;
	private multigear.communication.tcp.server.ValidateClientThread mValidateClientThread;
	
	/*
	 * Construtor
	 */
	public Server(final multigear.mginterface.engine.Manager manager, final String name, final int port) {
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
			if(Utils.SOCKET_RECV_BUFFER_SIZE > 0)
				mServerSocket.setReceiveBufferSize(Utils.SOCKET_RECV_BUFFER_SIZE);
		} catch(Exception e) {
			Log.d("LogTest", "Error To Set Buffer Size");
		}
		if(mListener != null)
			mListener.onStartServer();
		if(multigear.general.utils.KernelUtils.DEBUG)
			Log.d("LogTest", "Server: Server Started ..");
	}
	
	/*
	 * Espera por uma conexão
	 */
	final public void waitForClient() {
		if(mValidateClientThread != null) {
			if(!mValidateClientThread.isAlive() || mValidateClientThread.isInterrupted())
				mWaitForConnection.set(false);
		}
		if(!mWaitForConnection.getAndSet(true)) {
			mValidateClientThread = new multigear.communication.tcp.server.ValidateClientThread(this);
			mValidateClientThread.start();
		}
	}
	
	/*
	 * Client Conectado
	 */
	final public void onClientConnected(final multigear.communication.tcp.base.BaseConnected connectedClient) {
		if(mListener != null)
			mListener.onClientConnected(connectedClient);
	}
	
	/*
	 * Libera a trava de conexão
	 */
	final protected void releaseLock() {
		mWaitForConnection.set(false);
		mValidateClientThread = null;
	}
	
	/*
	 * Set Listener
	 */
	final public void setListener(final multigear.communication.tcp.server.Listener listener) {
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
	
	/*
	 * Finaliza o server e as Threads em operações.
	 */
	final public void close() {
		try {
			mServerSocket.close();
			if(mValidateClientThread != null)
				mValidateClientThread.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
