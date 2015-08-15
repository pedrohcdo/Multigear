package com.org.multigear.communication.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

/**
 * Client Connection Thread
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ValidateClientThread extends Thread {
	
	/**
	 * Result
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final private class Result {
		
		// Final Private Variables
		final int Code;
		final String Message;
		
		/*
		 * Construtor
		 */
		public Result(final int code, final String message) {
			Code = code;
			Message = message;
		}
		
		/*
		 * Construtor
		 */
		public Result(final int code) {
			this(code, "empty");
		}
	}
	
	// Constants
	final static private int COMMAND_EMPTY = 0;
	final static private int COMMAND_SERVERNAME = 1;
	final static private int COMMAND_WAITCONNECT = 2;
	final static private int COMMAND_DISCONNECT = 3;
	
	// Final Private Variables
	final private com.org.multigear.communication.tcp.server.Server mServer;
	final private ServerSocket mServerSocket;
	
	// Private Variables
	private boolean mClosed;
	
	/*
	 * Constutor
	 */
	public ValidateClientThread(final com.org.multigear.communication.tcp.server.Server server) {
		mServer = server;
		mServerSocket = server.getServerSocket();
		mClosed = false;
		setName("Server Client Holder");
	}
	
	/*
	 * Runner
	 */
	@Override
	public void run() {
		// Used Variables
		boolean connected = false;
		Socket client = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String name = "--";
		
		// Labeled loop
		start: while ((!Thread.currentThread().isInterrupted() && !mClosed && !mServerSocket.isClosed())) {
			try {
				if(com.org.multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Server: Wait For Client ..");
				

				// Wait a new Client
				client = mServerSocket.accept();
				client.setTcpNoDelay(true);
				
				if(com.org.multigear.communication.tcp.base.Utils.SOCKET_RECV_BUFFER_SIZE > 0)
					client.setReceiveBufferSize(com.org.multigear.communication.tcp.base.Utils.SOCKET_RECV_BUFFER_SIZE);
				if(com.org.multigear.communication.tcp.base.Utils.SOCKET_SEND_BUFFER_SIZE > 0)
					client.setSendBufferSize(com.org.multigear.communication.tcp.base.Utils.SOCKET_SEND_BUFFER_SIZE);
				
				// Open outputStream
				out = new PrintWriter(client.getOutputStream(), true);
				
				// Open buffered Reader
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				// Get Client Connect Command
				if(com.org.multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Server: Found Client, but waiting for command ..");
				
				final Result result = getClientCommand(in);
				final int command = result.Code;
				
				if(com.org.multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Server: Client asking command: " + command + ".");
				
				// Check this command
				switch (command) {
					// Ok..
					case COMMAND_EMPTY:
						in.close();
						out.close();
						client.close();
						if(com.org.multigear.general.utils.KernelUtils.DEBUG)
							Log.d("LogTest", "Server: Client no ask especial command. Client Closed.");
						break;
					// Ok
					case COMMAND_SERVERNAME:
						// Send Server Name
						out.println(com.org.multigear.communication.tcp.base.Utils.makeSocketMessage(com.org.multigear.communication.tcp.base.Utils.CODE_CLIENT_SERVERNAME, mServer.getServerName()));
						// Disconnect Client
						if(com.org.multigear.general.utils.KernelUtils.DEBUG)
							Log.d("LogTest", "Server: Waiting client disconnect message ..");
						int command2 = getClientCommand(in).Code;
						if (command2 == COMMAND_DISCONNECT) {
							in.close();
							out.close();
							client.close();
							if(com.org.multigear.general.utils.KernelUtils.DEBUG)
								Log.d("LogTest", "Server: Client disconnected ..");
						}
						break;
					// ...
					case COMMAND_WAITCONNECT:
						out.println(com.org.multigear.communication.tcp.base.Utils.makeSocketMessage(com.org.multigear.communication.tcp.base.Utils.CODE_CLIENT_CONNECTED));
						name = result.Message;
						connected = true;
						if(com.org.multigear.general.utils.KernelUtils.DEBUG)
							Log.d("LogTest", "Server: Client Connected. Sent message to client that connected.");
						break start;
				}
			} catch (IOException e) {
				if(com.org.multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "IOException. " + e.getMessage());
			}
			
		}
				
		// If client is connected
		if (connected) {
			com.org.multigear.communication.tcp.base.BaseConnected connectedClient = new com.org.multigear.communication.tcp.base.BaseConnected(mServer.getActivity(), name, client, in, out);
			mServer.onClientConnected(connectedClient);
		} else {
			mServer.onClientConnected(null);
		}
		
		
		
	}
	
	/*
	 * Fecha a Thread
	 */
	final public void close() {
		mClosed = true;
		this.interrupt();
		boolean flag = false;
		while (!flag) {
			try {
				this.join();
				flag = true;
			} catch (Exception e) {
			}
		}
	}
	
	/*
	 * Get Client Command
	 */
	final private Result getClientCommand(final BufferedReader bufferedReader) {
		
		final long startedTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startedTime) <= 1000) {
			if(com.org.multigear.general.utils.KernelUtils.DEBUG)
				Log.d("LogTest", "Server: Started get Messages Client.");
			
			String line = "";
			try {
				if(bufferedReader.ready())
					line = bufferedReader.readLine();
			} catch (IOException e) {
				if(com.org.multigear.general.utils.KernelUtils.DEBUG)
					Log.d("LogTest", "Server: Error on readline. Exception => " + e.getMessage());
			}
			
			if (line == null)
				continue;
			
			com.org.multigear.communication.tcp.base.Message message = com.org.multigear.communication.tcp.base.Utils.translateSocketMessages(line);
			
			if(com.org.multigear.general.utils.KernelUtils.DEBUG)
				Log.d("LogTest", "Server: Message geted. Start Read all.");
			
			while (message != null) {
				if (message.getCode() == com.org.multigear.communication.tcp.base.Utils.CODE_SERVER_GETNAME) {
					if(com.org.multigear.general.utils.KernelUtils.DEBUG)
						Log.d("LogTest", "Server: Read Message (Server Get Name).");
					return new Result(COMMAND_SERVERNAME);
				} else if (message.getCode() == com.org.multigear.communication.tcp.base.Utils.CODE_SERVER_WAITCONNECT) {
					if(com.org.multigear.general.utils.KernelUtils.DEBUG)
						Log.d("LogTest", "Server: Read Message (Server Wait Connect).");
					return new Result(COMMAND_WAITCONNECT, message.getMessage());
				} else if (message.getCode() == com.org.multigear.communication.tcp.base.Utils.CODE_SERVER_DISCONNECT) {
					if(com.org.multigear.general.utils.KernelUtils.DEBUG)
						Log.d("LogTest", "Server: Read Message (Client Disconnected).");
					return new Result(COMMAND_DISCONNECT);
				}
				message = message.next();
			}
			
		}		
		return new Result(COMMAND_EMPTY);
	}
}
