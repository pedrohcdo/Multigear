package multigear.communication.tcp.support;

import multigear.communication.tcp.base.BaseConnected;
import multigear.communication.tcp.client.ServersList;


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
			final multigear.communication.tcp.support.ConnectionInfo connectionInfo = new multigear.communication.tcp.support.ConnectionInfo(serverCon.getName(), serverCon.getAddress());
			final multigear.communication.tcp.support.SupportMessage message = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.CLIENT_CONNECTEDTOSERVER, connectionInfo);
			mComSupport.addSupport(serverCon);
			mComSupport.recvMessageForSupport(message);
		}

		/*
		 * Client Connect to Server failed.
		 */
		@Override
		public void onConnectionFailed() {
			final multigear.communication.tcp.support.SupportMessage message = new multigear.communication.tcp.support.SupportMessage(multigear.communication.tcp.support.SupportMessage.CLIENT_CONNECTFAILED, null);
			mComSupport.recvMessageForSupport(message);
		}
	}
	
	// Final Private Variables
	final private multigear.communication.tcp.client.Client mClient;
	final private multigear.communication.tcp.support.ComManager mComSupport;
	final private multigear.mginterface.engine.Manager mManager;
	
	// Private Variables
	private boolean mConnecting;
	private boolean mFinish;
	
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
		if(mConnecting)
			multigear.general.utils.KernelUtils.error(mManager.getEngine().getActivity(), "ClientSupport: An error ocurred while calling 'connect'. The client is already trying to connect.", 0x11);
		mConnecting = true;
		mClient.connect(serverInfo, attempts);
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
		mClient.close();
		mFinish = true;
	}
}
