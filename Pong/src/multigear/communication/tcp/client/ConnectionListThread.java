package multigear.communication.tcp.client;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

/**
 * Connection Thread
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ConnectionListThread extends Thread {
	
	// Final Private Variables
	final private multigear.communication.tcp.client.Client mClient;
	final private String[] mHostList;
	final private int mPort;
	
	// Private Variables
	private boolean mClosed;
	
	/*
	 * Constutor
	 */
	public ConnectionListThread(final multigear.communication.tcp.client.Client client, final int connectionAttempts) {
		mClient = client;
		mPort = client.getConnectionPort();
		WifiManager wifiManager = (WifiManager) client.getActivity().getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		final String dns = multigear.communication.tcp.client.Utils.intToIpString(dhcpInfo.dns1);
		final int[] ipAddress = multigear.communication.tcp.client.Utils.intToIpVInt(dhcpInfo.ipAddress);
		
		mHostList = multigear.communication.tcp.client.Utils.createConnectionsList(ipAddress, dns, connectionAttempts);
		mClosed = false;
	}
	
	/*
	 * Runner
	 */
	@Override
	public void run() {
		final multigear.communication.tcp.client.ServersList serverList = new multigear.communication.tcp.client.ServersList();
		for(final String host : mHostList) {
			if(Thread.currentThread().isInterrupted() || mClosed)
				break;
			final multigear.communication.tcp.client.ConnectionAttempt connectionAttempt = new multigear.communication.tcp.client.ConnectionAttempt(host, mPort);
			final multigear.communication.tcp.client.ConnectionAttempt.Result result = connectionAttempt.attemptingConnect();
			if(result.getSuccess())
				serverList.addServerInfo(result.getServerName(), host);
		}
		mClient.releaseListThread();
		mClient.onListed(serverList);
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
