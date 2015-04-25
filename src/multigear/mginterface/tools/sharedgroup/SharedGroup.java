package multigear.mginterface.tools.sharedgroup;

import multigear.communication.tcp.client.ServersList;
import multigear.communication.tcp.support.ClientSupport;
import multigear.communication.tcp.support.ServerSupport;
import multigear.communication.tcp.support.SupportMessage;
import multigear.mginterface.scene.Scene;
import multigear.services.Message;
import android.net.wifi.ScanResult;

/**
 * Shared Group
 * 
 * @author user
 *
 */
final public class SharedGroup {

	// Final Private Variables
	final private Scene mScene;
	
	// Private Variables
	private ClientSupport mClientSupport;
	private boolean mStarted;
	
	/**
	 * Constructor
	 * 
	 * @param scene
	 */
	public SharedGroup(final Scene scene) {
		mScene = scene;
	}

	/**
	 * Start Connections
	 */
	public void start(final int serverPort) {
		mStarted = true;
		mScene.getServicesManager().enableWifi();
		mScene.getComManager().setConnectionPort(serverPort);
	}
	
	/**
	 * Service Message
	 * @param message
	 */
	public void onServiceMessage(Message message) {
		if(!mStarted)
			return;
		scope:
		switch(message.getCode()) {
		case Message.WIFI_ENABLED:
			mScene.getServicesManager().scanAccessPoints();
			break;
		case Message.SCAN_ACCESS_POINT_COMPLETED:
			for(ScanResult scanResult : mScene.getServicesManager().getScanResult()) {
				if(scanResult.SSID.equals("RaphaelTest")) {
					int id = mScene.getServicesManager().addWPANetwork(scanResult.SSID, scanResult.BSSID, "caralha123");
					mScene.getServicesManager().connectTo(id);
					break scope;
				}
			}
			mScene.getServicesManager().enableHotspot("RaphaelTest", "caralha123");
			break;
		case Message.WIFI_CONNECTED:
			mClientSupport = mScene.getComManager().createClient("Client");
			mClientSupport.listServers(10);
			break;
		case Message.HOTSPOT_ENABLED:
			mScene.getComManager().createServer("Server").start();
			break;
		}
	}

	/**
	 * Com Message
	 * @param message
	 */
	public void onComMessage(SupportMessage message) {
		if(!mStarted)
			return;
		switch(message.Message) {
		case SupportMessage.SERVER_STARTED:
			ServerSupport serverSupport = (ServerSupport) message.Object;
			serverSupport.startWaitForClient();
			break;
		case SupportMessage.SERVER_CLIENTCONNECTED:
			
			break;
		case SupportMessage.CLIENT_LISTEDSERVERS:
			ServersList list = (ServersList) message.Object;
			if(list.size() == 0)
				mClientSupport.listServers(10);
			else {
				mClientSupport.connect(list.getServerInfo(0), 10);
			}
			break;
		case SupportMessage.CLIENT_CONNECTEDTOSERVER:
			
			break;
		}
	}
	
}
