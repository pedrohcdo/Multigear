package multigear.communication.tcp.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

/**
 * Connection Thread
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ConnectionThread extends Thread {
	
	// Final Private Variables
	final private multigear.communication.tcp.client.Client mClient;
	final private multigear.communication.tcp.client.ServersList.ServerInfo mServerInfo;
	
	final private int mPort;
	final private int mAttempts;
	
	// Private Variables
	private boolean mClosed;
	
	/*
	 * Construtor
	 */
	public ConnectionThread(final multigear.communication.tcp.client.Client client, final multigear.communication.tcp.client.ServersList.ServerInfo serverInfo, final int port, final int attempts) {
		setName("Client Connection");
		mClient = client;
		mServerInfo = serverInfo;
		mPort = port;
		mAttempts = attempts;
		mClosed = false;
	}
	
	/*
	 * Runner
	 */
	@Override
	public void run() {
		for(int i=0; i<mAttempts; i++) {
			if(Thread.currentThread().isInterrupted() || mClosed)
				break;
			try {
				final Socket socket = new Socket();
				socket.connect(new InetSocketAddress(mServerInfo.Address, mPort), 2000);
				socket.setTcpNoDelay(true);
				
				if(multigear.communication.tcp.base.Utils.SOCKET_RECV_BUFFER_SIZE > 0)
					socket.setReceiveBufferSize(multigear.communication.tcp.base.Utils.SOCKET_RECV_BUFFER_SIZE);
				if(multigear.communication.tcp.base.Utils.SOCKET_SEND_BUFFER_SIZE > 0)
					socket.setSendBufferSize(multigear.communication.tcp.base.Utils.SOCKET_SEND_BUFFER_SIZE);
				
				
				
				if(multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Client: Waiting for connected. Sent Message to Server.");
				
				
				// Create outputStream
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				
				// Create buffered Reader
				final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				out.println(multigear.communication.tcp.base.Utils.makeSocketMessage(multigear.communication.tcp.base.Utils.CODE_SERVER_WAITCONNECT, mClient.getName()));
				
				mClient.releaseConnectThread();
				mClient.onConnected(socket, mServerInfo, in, out);
				return;
			} catch (Exception e) {
			}
		}
		mClient.releaseConnectThread();
		mClient.onConnectedFailed();
	}
	
	/*
	 * Fecha a Thread
	 */
	final public void close() {
		mClosed = true;
		boolean flag = false;
		while(!flag) {
			try {
				this.join();
				flag = true;
			} catch (Exception e) {
			}
		}
	}
}
