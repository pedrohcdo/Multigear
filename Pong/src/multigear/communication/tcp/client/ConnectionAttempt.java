package multigear.communication.tcp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

/**
 * Connection Attempt
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class ConnectionAttempt {
	
	/**
	 * 
	 * Result
	 * 
	 * @author PedroH, RaphaelB
	 * 
	 *         Property Createlier.
	 */
	final public class Result {
		
		// Final Private Variables
		final private boolean mSuccess;
		final private String mServerName;
		
		/*
		 * Construtor
		 */
		public Result(final boolean sucess) {
			mSuccess = sucess;
			mServerName = "";
		}
		
		/*
		 * Construtor
		 */
		public Result(final boolean sucess, final String serverName) {
			mSuccess = sucess;
			mServerName = serverName;
		}
		
		/*
		 * Retorna o resultado do processo
		 */
		final public boolean getSuccess() {
			return mSuccess;
		}
		
		/*
		 * Retorna o nome do servidor
		 */
		final public String getServerName() {
			return mServerName;
		}
	}
	
	// Final Private Variables
	final private String mHost;
	final private int mPort;
	
	/*
	 * Construtor
	 */
	public ConnectionAttempt(final String host, final int port) {
		mHost = host;
		mPort = port;
	}
	
	/**
	 * Attempting to connect
	 */
	final protected ConnectionAttempt.Result attemptingConnect() {
		if (multigear.general.utils.KernelUtils.DEBUG)
			Log.d("LogTest", "Attemping Openned");
		try {
			InetAddress address = InetAddress.getByName(mHost);
			if (address.isReachable(300)) {
				final Socket socket = new Socket(mHost, mPort);
				socket.setTcpNoDelay(true);
				if(multigear.communication.tcp.base.Utils.SOCKET_RECV_BUFFER_SIZE > 0)
					socket.setReceiveBufferSize(multigear.communication.tcp.base.Utils.SOCKET_RECV_BUFFER_SIZE);
				if(multigear.communication.tcp.base.Utils.SOCKET_SEND_BUFFER_SIZE > 0)
					socket.setSendBufferSize(multigear.communication.tcp.base.Utils.SOCKET_SEND_BUFFER_SIZE);
				
			
				
				// Create outputStream
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				
				// Create buffered Reader
				final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				// Send request for Server
				out.println(multigear.communication.tcp.base.Utils.makeSocketMessage(multigear.communication.tcp.base.Utils.CODE_SERVER_GETNAME));
				out.flush();
				
				// If Any Error
				if (out.checkError()) {
					socket.close();
					out.close();
					in.close();
					return new ConnectionAttempt.Result(false);
				}
				
				// ..
				if (multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Client: Listed to server. Sent command to server. Waiting for name.");
				
				// Get Server Name
				String serverName = getServerName(out, in, socket);
				
				// Close Stream and Socket
				in.close();
				out.close();
				socket.close();
				
				// If Server no Wait for Connection
				if (serverName == null) {
					return new ConnectionAttempt.Result(false);
				}
				
				if (multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Client: Getted Name and close socket.");
				return new ConnectionAttempt.Result(true, serverName);
			} else {
				if (multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Attemping Closed Not Founded");
				return new ConnectionAttempt.Result(false);
			}
		} catch (Exception e) {
			if (multigear.general.utils.KernelUtils.DEBUG)
				Log.d("LogTest", "Attemping Closed With Error");
			return new ConnectionAttempt.Result(false);
		}
	}
	
	/*
	 * Retorna o nome do servidor
	 */
	final private String getServerName(final PrintWriter out, final BufferedReader in, final Socket socket) {
		final long startedTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startedTime) <= 500) {
			
			String line = "";
			
			try {
				if (in.ready())
					line = in.readLine();
			} catch (IOException e) {
				if (multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Client: Read line exception => " + e.getMessage());
			}
			
			if (line == null)
				continue;
			
			multigear.communication.tcp.base.Message message = multigear.communication.tcp.base.Utils.translateSocketMessages(line);
			
			while (message != null) {
				if (multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Client: Receive Message CODE: " + message.getCode() + ".");
				
				if (message.getCode() == multigear.communication.tcp.base.Utils.CODE_CLIENT_SERVERNAME) {
					if (multigear.general.utils.KernelUtils.DEBUG) {
						Log.d("LogTest", "Client: Received server name: " + message.getMessage());
						Log.d("LogTest", "Client: Disconnecting client from server.");
					}
					
					out.println(multigear.communication.tcp.base.Utils.makeSocketMessage(multigear.communication.tcp.base.Utils.CODE_SERVER_DISCONNECT));
					
					return message.getMessage();
				}
				
				message = message.next();
			}
		}
		return null;
	}
}
