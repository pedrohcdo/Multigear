package multigear.communication.tcp.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.util.Log;

/**
 * Base Connected
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class BaseConnected {
	
	// Constants
	final static private int CODE_ERROR_READMESSAGE_BLOCKED = 0x8;
	
	// Final Private Variables
	final private Socket mSocket;
	final private BufferedReader mIn;
	final private PrintWriter mOut;
	final private Activity mActivity;
	final private String mName;
	final private String mAddress;
	
	// Private Variables
	private boolean mReadBlocked;
	
	final private List<Message> mMessages = new ArrayList<Message>();
	final private List<String> mSends = new ArrayList<String>();
	
	// Worker threads
	private ConducerSendThread mSendThread;
	private ConducerReceiveThread mReceiveThread;
	
	// Security
	final private Object mSyncSecurityStackerMessage = new Object();
	final private AtomicBoolean mLockSecuritySleep = new AtomicBoolean();
	
	
	/**
	 * Conducer Thread
	 * 
	 * @author user
	 *
	 */
	final private class ConducerSendThread extends Thread {
		
		// Private Variables
		private volatile boolean mInterrupted;
		
		/**
		 * Runner
		 */
		@Override
		public void run() {
			// State
			mInterrupted = false;
			
			// 
			while(true) {
				// Read All Messages
				while(mSends.size() > 0) {
					
					String send = null;
					// Sync Security Stacker
					synchronized(mSyncSecurityStackerMessage) {
						// If Have message
						if(mSends.size() > 0)
							send = mSends.remove(0);
					}
					// If hand on message
					if(send != null) {
						try {
							mOut.println(send);
							mOut.flush();
						} catch (Exception e) {
						}
					}
					
					// Break if interrupted
					if(mInterrupted)
						break;
				}
				
				// Acquire Lock
				if(mLockSecuritySleep.getAndSet(true) == false) {
					
					// If interrupted
					if(mInterrupted)
						break;
					
					// If have messages
					if(mSends.size() > 0) {
						// Release Lock
						mLockSecuritySleep.set(false);
						continue;
					}
					
					// Sleep
					try {
						Thread.sleep(5000);
					} catch(InterruptedException e){}
					
					// release Lock
					mLockSecuritySleep.set(false);
				}
				
				// If interrupted
				if(mInterrupted) {
					break;
				}
			}
		}
		
		/**
		 * Close Conducer
		 */
		final private void close() {
			boolean interrupting = true;
			long time = System.currentTimeMillis();
			while(interrupting) {
				if((System.currentTimeMillis()-time) >= 3000)
					break;
				mInterrupted = true;
				this.interrupt();
				try {
					this.join(500);
					if(getState() != State.BLOCKED && getState() != State.TIMED_WAITING && getState() != State.RUNNABLE && getState() != State.WAITING) {
						interrupting = false;
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	
	/**
	 * Conducer Receive Thread Message
	 * 
	 * @author user
	 *
	 */
	final private class ConducerReceiveThread extends Thread {
		
		// Private Variables
		private volatile boolean mInterrupted;
		
		/**
		 * Runner
		 */
		@Override
		public void run() {
			// State
			mInterrupted = false;
			// Block Read
			while(true) {
				// Read Line
				String stream = null;
				try {
					if(mIn.ready())
						stream = mIn.readLine();
				} catch(IOException e) {
				}
				// If valid stream
				if (stream != null) {
					Message message = multigear.communication.tcp.base.Utils.translateSocketMessages(stream);
					synchronized(mMessages) {
						mMessages.add(message);
					}
				} else {
					try {
						Thread.sleep(1);
					} catch(Exception e){}
				}
				// If interrupted
				if(mInterrupted || Thread.currentThread().isInterrupted()) {
					break;
				}
			}
		}
		
		/**
		 * Close Conducer
		 */
		final private void close() {
			boolean interrupting = true;
			long time = System.currentTimeMillis();
			while(interrupting) {
				if((System.currentTimeMillis()-time) >= 3000)
					break;
				mInterrupted = true;
				this.interrupt();
				try {
					this.join();
					interrupting = false;
				} catch (InterruptedException e) {}
			}
		}
	}
	
	/*
	 * Construtor
	 */
	public BaseConnected(final Activity activity, final String name, final Socket socket, final BufferedReader in, final PrintWriter out) {
		mActivity = activity;
		mName = name;
		mAddress = socket.getInetAddress().getHostAddress();
		mSocket = socket;
		mIn = in;
		mOut = out;
		
		mSendThread = new ConducerSendThread();
		mReceiveThread = new ConducerReceiveThread();
		
		mSendThread.setPriority(Thread.MAX_PRIORITY);
		mSendThread.setName("Connection Sender: " + name);
		mReceiveThread.setPriority(Thread.MAX_PRIORITY);
		mReceiveThread.setName("Connection Receiver: " + name);
		
		mSendThread.start();
		mReceiveThread.start();
	}
	
	/**
	 * Get Socket
	 * @return
	 */
	final public Socket getSocket() {
		return mSocket;
	}
	
	/*
	 * Retorna o nome
	 */
	final public String getName() {
		return mName;
	}
	
	/*
	 * Retorna o endereço
	 */
	final public String getAddress() {
		return mAddress;
	}
	
	/**
	 * Block 'readMessage'. This method called by Engine.
	 */
	final public void blockRead() {
		mReadBlocked = true;
	}
	
	/**
	 * Put message to send Thread
	 * @param message
	 */
	final private void putMessageToSendThread(final String message) {
		// Sync stacker
		synchronized(mSyncSecurityStackerMessage) {
			mSends.add(message);
		}
		// Try to acquire lock
		while(true) {
			// Acquired lock
			boolean acquired = mLockSecuritySleep.getAndSet(true) == false;
			// Release lock if acquired
			if(acquired) {
				mLockSecuritySleep.set(false);
				break;
			// If not acquired lock	
			} else {
				// If Thread Sleeping
				if(mSendThread.getState() == Thread.State.WAITING || mSendThread.getState() == Thread.State.TIMED_WAITING) {
					mSendThread.interrupt();
					break;
				}
				// If thread interrupted or dead
				if(mSendThread.getState() == Thread.State.TERMINATED || !mSendThread.isAlive()) {
					if(!Thread.currentThread().isInterrupted())
						mSendThread.start();
					mLockSecuritySleep.set(false);
					break;
				}
			}
			// If interrupted
			if(Thread.currentThread().isInterrupted())
				break;
		}
	}
	
	/*
	 * Envia uma mensagem
	 */
	final public void sendMessage(final int code, final String message) {
		if (message.matches(".*(?:\\[|\\]).*"))
			Log.d("LogTest", "Client: Warning, do not use any of these characters: []|");
		final String messageSocket = multigear.communication.tcp.base.Utils.makeSocketMessage(code, message);
		putMessageToSendThread(messageSocket);
	}
	
	/*
	 * Envia uma mensagem vazia
	 */
	final public void sendMessage(final int code) {
		final String messageSocket = multigear.communication.tcp.base.Utils.makeSocketMessage(code);
		putMessageToSendThread(messageSocket);
	}
	
	
	/*
	 * Le todas as mensagens em espera
	 */
	final public int readMessage(final Message[] out) {
		if (mReadBlocked)
			multigear.general.utils.KernelUtils.error(mActivity, "BaseConnected: An error occurred while calling the 'ReadMessage' function. The same was blocked, probably by a top object, such as ConSupport.", CODE_ERROR_READMESSAGE_BLOCKED);
		if(out.length == 0)
			return 0;
		int count = 0;
		synchronized(mMessages) {
			for(int i=0; i<out.length; i++) {
				if(mMessages.size() > 0) {
					out[i] = mMessages.remove(0);
					count++;
				} else
					break;
			}
		}
		return count;
	}
	
	/**
	 * Wait for all services<br>
	 * <b>Note:</b> This method NOT! is thread safe, call only all services is paused
	 */
	final public void forceToSendAll() {
		// Read All Messages
		while(mSends.size() > 0) {
			// Get message
			String send = mSends.remove(0);
			// If hand on message
			if(send != null) {
				try {
					mOut.println(send);
					mOut.flush();
				} catch (Exception e) {
				}
			}
		}
	}
	
	/**
	 * Pause Connection
	 * 
	 */
	final public void pause() {
		mSendThread.close();
		mReceiveThread.close();
		mLockSecuritySleep.set(false);
	}
	
	/**
	 * Resume Connection
	 * 
	 */
	final public void resume() {
		mSendThread = new ConducerSendThread();
		mReceiveThread = new ConducerReceiveThread();
		mSendThread.start();
		mReceiveThread.start();
	}
	
	/**
	 * Close connection
	 */
	final public void close() {
		if(!mSocket.isClosed()) {
			try {
				mSocket.close();
			} catch(Exception e) {}
		}
	}
}
