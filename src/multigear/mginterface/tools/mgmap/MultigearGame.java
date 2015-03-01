package multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import multigear.communication.tcp.support.ComManager;
import multigear.communication.tcp.support.ConnectionInfo;
import multigear.communication.tcp.support.ParentAttributes;
import multigear.communication.tcp.support.SupportMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilt;
import multigear.general.utils.Vector2;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.components.UpdatableListener;

/**
 * Multigear Map
 * @author user
 *
 */
public class MultigearGame {

	/**
	 * Player
	 * @author user
	 *
	 */
	public enum Player {
		
		/* Conts */
		Player1,
		Player2;
	}
	
	/**
	 * Device Adjust
	 * 
	 * @author user
	 */
	public enum Adjust {
		
		/** It is the device with higher screen, the same shall be adjusted to support the lesser amount. */
		ADJUST_MAJOR,
		/** It is the device with less screen, it will not be adjusted. */
		ADJUST_MINOR,
		/** The two devices are equal in screen amount of term. None will be adjusted. */
		ADJUST_EQUAL,
		/** If not connected. Or an error has occurred on the connection. */
		NOT_SET;
	}
	
	/**
	 * Align Mode
	 * 
	 * @author user
	 *
	 */
	public enum AlignMode {
		
		/** Adjust screen references to top. */
		TOP,
		/** Adjust screen references to bottom. */
		BOTTOM,
		/** Adjust screen references to center. */
		CENTER;
	}
	
	/**
	 * Register Mode
	 * 
	 * @author user
	 *
	 */
	public enum RegisterMode {
		
		/* Conts */
		DYNAMIC,
		STATIC;
	}
	
	/**
	 * Updater
	 * @author user
	 *
	 */
	final private UpdatableListener mUpdaterListener = new UpdatableListener() {

		/**
		 * Update
		 */
		@Override
		public void onUpdate(Scene scene) {
			update();
		}
	};
	
	// Final Private Constants
	/** Connections Conts*/
	final private static int CONNECTION_REQUEST = 0;
	final private static int CONNECTION_REQUEST_ACCEPTED = 1;
	final private static int CONNECTION_REQUEST_REJECTED_PLAYER = 2;
	
	final private static int GAME_OBJECT_MESSAGE = 3;
	final private static int GAME_VARIABLES_MESSAGE = 4;
	final private static int GAME_MONITOR_MESSAGE = 5;
	
	final public static int NO_ERROR = 0;
	/** Same Players in two side. */
	final public static int ERROR_CONNECTION_REJECTED_1 = 1;
	
	// Final Private Variables
	final private Scene mScene;
	final private ComManager mComManager;
	final private int mComCode;
	final private MultigearGameListener mDuoMapListener;
	final private MultigearGameState mGameState = new MultigearGameState();
	final private MultigearGameObjects mGameObjects = new MultigearGameObjects(this, mGameState);
	final private MultigearGameVariables mGameVariables = new MultigearGameVariables(this);
	final private MultigearGameMonitor mGameMonitor = new MultigearGameMonitor(this);
	final private AlignMode mAlignMode;
	
	// Private Variables
	private boolean mConnectionEstablished = false;
	private boolean mConnectionFinish = false;
	private boolean mConnecting = false;
	private Player mPlayer;
	private int mErrorCode;
	
	/**
	 * Constructor
	 * @param scene Scene to attach
	 * @param communicationCode Communication code used to create the
	 *  message, enter a code that you had used for other purposes, 
	 *  because it can be confused later. 
	 *  Read more: ObjectMessage.create(code)
	 */
	public MultigearGame(final Scene scene, final int communicationCode, final MultigearGameListener duoMapListener, final AlignMode formatMode) {
		mScene = scene;
		mComManager = scene.getComManager();
		mComCode = communicationCode;
		mScene.addUpdatableListener(mUpdaterListener);
		mDuoMapListener = duoMapListener;
		mErrorCode = 0;
		mAlignMode = formatMode;
	}
	
	/**
	 * Get Scene
	 * 
	 * @return
	 */
	final protected Scene getScene() {
		return mScene;
	}
	
	/**
	 * Get Align Mode
	 * @return
	 */
	final public AlignMode getAlignMode() {
		return mAlignMode;
	}
	
	/**
	 * Starts connect. If no map the wait, the same had been waiting.
	 * If connected it already is ignored.
	 */
	final public void connect(final Player player) {
		if(mConnectionEstablished)
			throw new RuntimeException("Are connected");
		mPlayer = player;
		mConnecting = true;
		mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST).add(mPlayer.ordinal()).build());
		mErrorCode = 0;
	}
	
	/**
	 * Return True if connected
	 * @return True/False
	 */
	final public boolean isConnected() {
		return mConnectionEstablished && mConnectionFinish;
	}
	
	/**
	 * Update
	 */
	final protected void update() {
		// Wait for connection accepted
		if(mConnecting)
			mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST).add(mPlayer.ordinal()).build());
		if(mConnectionFinish) {
			mGameObjects.update();
			mGameVariables.update();
			mGameMonitor.update();
		}
	}
	
	/**
	 * Communication Message
	 */
	final public void onComMessage(final SupportMessage message) {
		switch (message.Message) {
		// Prepare map after prepare Attributes
		case SupportMessage.PARENT_PREPAREDATTRIBUTES:
			final ParentAttributes attributes = (ParentAttributes) message.Object;
			final float thisScreenY = mScene.getPhysicalScreenSize().y;
			final float parentScreenY = attributes.getPhysicalScreenSize().y;
			Adjust adjust = Adjust.NOT_SET;
			if(thisScreenY > parentScreenY) {
				mScene.setBaseDpi(attributes.Dpi);
				adjust = Adjust.ADJUST_MAJOR;
			} else if(thisScreenY < parentScreenY)
				adjust = Adjust.ADJUST_MINOR;
			else {
				final float thisScreenDpi = mScene.getDPI();
				final float parentDpi = attributes.getDpi();
				if(thisScreenDpi > parentDpi) {
					mScene.setBaseDpi(attributes.Dpi);
					adjust = Adjust.ADJUST_MAJOR;
				} else if(thisScreenDpi < parentDpi)
					adjust = Adjust.ADJUST_MINOR;
				else
					adjust = Adjust.ADJUST_EQUAL;
			}
			Vector2 mapSize = new Vector2();
			float screenDivision = 0;
			switch(adjust) {
			case ADJUST_EQUAL:
				mapSize.x = mScene.getScreenSize().x + attributes.getScreenSize().x;
				mapSize.y = Math.min(mScene.getScreenSize().y, attributes.getScreenSize().y);
				switch(mPlayer) {
				default:
				case Player1:
					screenDivision = mScene.getScreenSize().x;
					break;
				case Player2:
					screenDivision = attributes.getScreenSize().x;
				}
				break;
			case ADJUST_MAJOR:
				mapSize.x = mScene.getSpaceParser().getScreenSize().x + attributes.getScreenSize().x;
				mapSize.y = attributes.getScreenSize().y;
				switch(mPlayer) {
				default:
				case Player1:
					screenDivision = mScene.getSpaceParser().getScreenSize().x;
					break;
				case Player2:
					screenDivision = attributes.WidthPixels;
				}
				break;
			case ADJUST_MINOR:
				final float scaleFactor = attributes.Dpi / mScene.getDPI();
				final float newParentScreenW = attributes.getScreenSize().x / scaleFactor;
				mapSize.x = mScene.getScreenSize().x + newParentScreenW;
				mapSize.y = mScene.getScreenSize().y;
				
				switch(mPlayer) {
				default:
				case Player1:
					screenDivision = mScene.getScreenSize().x;
					break;
				case Player2:
					screenDivision = newParentScreenW;
				}
				
				break;
			default:
			case NOT_SET:
				// Not possible
				break;
			}
			// Connected
			mConnectionFinish = true;
			mGameState.prepare(mPlayer, mapSize, screenDivision, adjust);
			mDuoMapListener.onConnect(true);
			break;
		}
	}
	
	/**
	 * Object Message
	 */
	final public void onMessage(final ConnectionInfo connectionInfo, final ObjectMessage objectMessage) {
		if(objectMessage.getCode() == mComCode) {
			// Vars
			List<Object> values;
			// Message
			switch((Integer)objectMessage.getValue(0)) {
			// Request Received
			case CONNECTION_REQUEST:
				final int player = (Integer) objectMessage.getValue(1);
				// Not accept same player
				if(player == mPlayer.ordinal())
					mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST_REJECTED_PLAYER).build());
				// Accepted
				else
					mComManager.sendForAll(ObjectMessage.create(mComCode).add(CONNECTION_REQUEST_ACCEPTED).build());
				break;
			// Request Received in other side
			// It is not necessary to continue with the request
			case CONNECTION_REQUEST_ACCEPTED:
				// Connection accepted
				if(mConnecting) {
					// Connected
					mConnecting = false;
					mConnectionEstablished = true;
					// Send PrepareAttributes and awaits feedback to end connect
					mComManager.prepareParentsAttributes();
				}
				break;
			// Connection Rejected
			case CONNECTION_REQUEST_REJECTED_PLAYER:
				if(mConnecting) {
					Log.d("LogTest", "Rejected");
					mDuoMapListener.onConnect(false);
					mConnecting = false;
					mErrorCode = ERROR_CONNECTION_REJECTED_1;
				}
				break;
			case GAME_OBJECT_MESSAGE:
				values = new ArrayList<Object>();
				for(int i=2; i<objectMessage.size(); i++)
					values.add(objectMessage.getValue(i));
				mGameObjects.message((Integer)objectMessage.getValue(1), values);
				break;
			case GAME_VARIABLES_MESSAGE:
				values = new ArrayList<Object>();
				for(int i=2; i<objectMessage.size(); i++)
					values.add(objectMessage.getValue(i));
				mGameVariables.message((Integer)objectMessage.getValue(1), values);
				break;
			case GAME_MONITOR_MESSAGE:
				values = new ArrayList<Object>();
				for(int i=2; i<objectMessage.size(); i++)
					values.add(objectMessage.getValue(i));
				mGameMonitor.message((Integer)objectMessage.getValue(1), values);
				break;
			}
		}
	}
	
	/**
	 * Prepare Message Builder
	 */
	final protected ObjectMessageBuilder prepareObjectMessage(final int code) {
		return ObjectMessage.create(mComCode).add(GAME_OBJECT_MESSAGE).add(code);
	}
	
	/**
	 * Prepare Message Builder
	 */
	final protected ObjectMessageBuilder prepareVariablesMessage(final int code) {
		return ObjectMessage.create(mComCode).add(GAME_VARIABLES_MESSAGE).add(code);
	}
	
	/**
	 * Prepare Message Builder
	 */
	final protected ObjectMessageBuilder prepareMonitorMessage(final int code) {
		return ObjectMessage.create(mComCode).add(GAME_MONITOR_MESSAGE).add(code);
	}
	
	/**
	 * Send Message Builder
	 * @param builder
	 */
	final protected void sendMessage(ObjectMessageBuilt built) {
		mComManager.sendForAll(built);
	}
	
	/**
	 * Get Align Position
	 * @return
	 */
	final private Vector2 getAlignMapPosition() {
		return mGameState.positionToMap(new Vector2(0, (mScene.getSpaceParser().getScreenSize().y - getState().getMapSize().y) / 2));
	}
	
	/**
	 * Config Scene
	 */
	final public void configScene(final Scene scene) {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		scene.enable(Scene.FUNC_VIRTUAL_DPI);
		scene.setPosition(getAlignMapPosition());
	}
	
	/**
	 * Get DuoMap Monitor.<br>
	 * <b>Note:</b> Monitor available after connection finish
	 */
	final public MultigearGameState getState() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameState;
	}
	
	/**
	 * Get Game Variables
	 * 
	 * @return
	 */
	final public MultigearGameVariables getVariables() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameVariables;
	}
	
	/**
	 * Get DuoMap Manager.<br>
	 * <b>Note:</b> Monitor available after connection finish
	 * @return
	 */
	final public MultigearGameObjects getObjects() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameObjects;
	}
	
	/**
	 * Get DuoMap Manager.<br>
	 * <b>Note:</b> Monitor available after connection finish
	 * @return
	 */
	final public MultigearGameMonitor getMonitor() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameMonitor;
	}
	
	/**
	 * Get Error
	 * @return
	 */
	final public int getErrorCode() {
		return mErrorCode;
	}
	
	/**
	 * Destroy object
	 */
	final public void destroy() {
		mScene.removeUpdatableListener(mUpdaterListener);
	}
}
