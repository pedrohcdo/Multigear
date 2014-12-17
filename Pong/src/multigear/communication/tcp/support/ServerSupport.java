package multigear.communication.tcp.support;



/**
 * Server Support
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ServerSupport {
	
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
			mStarted = true;
			final multigear.communication.tcp.support.SupportMessage message = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.SERVER_STARTED, ServerSupport.this);
			mComSupport.recvMessageForSupport(message);
		}

		/*
		 * Server Failed
		 */
		@Override
		public void onStartServerFailed() {
			final multigear.communication.tcp.support.SupportMessage message = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.SERVER_CONNECTIONFAILED, null);
			mComSupport.recvMessageForSupport(message);
		}

		/*
		 * Client Connected
		 */
		@Override
		public void onClientConnected(final multigear.communication.tcp.base.BaseConnected connectedClient) {
			final multigear.communication.tcp.support.ConnectionInfo connectionInfo = new multigear.communication.tcp.support.ConnectionInfo(connectedClient.getName(), connectedClient.getAddress());
			final multigear.communication.tcp.support.SupportMessage message = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.SERVER_CLIENTCONNECTED, connectionInfo);
			mComSupport.addSupport(connectedClient);
			mComSupport.recvMessageForSupport(message);
		}
	}
	
	// Final Private Variables
	final private multigear.communication.tcp.server.Server mServer;
	final private multigear.communication.tcp.support.ComManager mComSupport;
	final private multigear.mginterface.engine.Manager mManager;
	
	// Private Variables
	private boolean mStarted;
	private boolean mStarting;
	private boolean mFinish;
	
	/*
	 * Construtor
	 */
	public ServerSupport(final multigear.communication.tcp.support.ComManager comSupport, final multigear.mginterface.engine.Manager manager, final String name, final int port) {
		mComSupport = comSupport;
		mServer = new multigear.communication.tcp.server.Server(manager, name, port);
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
	final public void waitForClient() {
		if(!mStarted)
			multigear.general.utils.KernelUtils.error(mManager.getEngine().getActivity(), "ServerSupport: An error ocurred while calling 'waitForClient'. The Server was not correctly started.", 0x9);
		mServer.waitForClient();
	}
	
	/*
	 * Retorna true caso esteja finalizado
	 */
	final public boolean isFinish() {
		return mFinish;
	}
	
	/*
	 * Finish Server Support
	 */
	final protected void finish() {
		mServer.close();
		mFinish = true;
	}
}
