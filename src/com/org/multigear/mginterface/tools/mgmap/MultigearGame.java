package com.org.multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.List;

import com.org.multigear.communication.tcp.support.ComManager;
import com.org.multigear.communication.tcp.support.ConnectionInfo;
import com.org.multigear.communication.tcp.support.ParentAttributes;
import com.org.multigear.communication.tcp.support.SupportMessage;
import com.org.multigear.communication.tcp.support.objectmessage.ObjectMessage;
import com.org.multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.scene.Scene;
import com.org.multigear.mginterface.scene.components.UpdatableListener;

import android.util.Log;

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
		FREE,
		LOCK,
		SILENT;
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
	// :)
	final private static int TIMESTAMP_SYNC = 3;
	final private static int TIMESTAMP_RESULT = 4;
	final private static int TIMESTAMP_END = 5;
	
	final private static int GAME_OBJECT_MESSAGE = 6;
	final private static int GAME_VARIABLES_MESSAGE = 7;
	final private static int GAME_MONITOR_MESSAGE = 8;
	
	final public static int NO_ERROR = 0;
	/** Same Players in two side. */
	final public static int ERROR_CONNECTION_REJECTED_1 = 1;
	
	// Final Private Variables
	final private Scene mScene;
	final private ComManager mComManager;
	final private int mComCode;
	final private MultigearGameListener mDuoMapListener;
	final private GameState mGameState = new GameState(this);
	final private GameObjects mGameObjects = new GameObjects(this, mGameState);
	final private GameVariables mGameVariables = new GameVariables(this);
	final private GameMonitor mGameMonitor = new GameMonitor(this);
	final private AlignMode mAlignMode;
	
	// Private Variables
	private GameDensityParser mGameDensityParser;
	private boolean mConnectionEstablished = false;
	private boolean mConnectionFinish = false;
	private boolean mConnecting = false;
	private Player mPlayer;
	private int mErrorCode;
	
	
	private int mTimeStampSyncCount = 0;
	private List<long[]> mTimeStampList = new ArrayList<long[]>();
	private int mTimeStampSyncResult = -1;
	private long mTimeStampSyncResultDiff = Long.MAX_VALUE;
	private long mTimeStampSyncResultP1 = 0;
	private long mTimeStampSyncResultP2 = 0;
	
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
		mTimeStampSyncCount = 0;
		mTimeStampSyncResult = -1;
		mTimeStampSyncResultDiff = Long.MAX_VALUE;
		mTimeStampSyncResultP1 = 0;
		mTimeStampSyncResultP2 = 0;
		mTimeStampList.clear();
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
			// Final sets
			mGameState.prepare(mPlayer, mapSize, screenDivision, adjust, attributes, mTimeStampSyncResultP1, mTimeStampSyncResultP2);
			
			if(adjust == Adjust.ADJUST_EQUAL || adjust == Adjust.ADJUST_MINOR) {
				mGameDensityParser = new GameDensityParser(mScene, mScene.getScreenSize());
			} else {
				mGameDensityParser = new GameDensityParser(mScene, attributes.getScreenSize());
			}
			
			// Connected
			mConnectionFinish = true;
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
					// Send
					if(mPlayer == Player.Player1) {
						mComManager.sendForAll(ObjectMessage.create(mComCode).add(TIMESTAMP_SYNC).add(System.nanoTime()).build());
					}
				}
				break;
			case TIMESTAMP_SYNC:
				final long timeStamp = (Long) objectMessage.getValue(1);
				final long thisTimeStamp = System.nanoTime();
				mTimeStampList.add(new long[] {timeStamp, thisTimeStamp});
				mComManager.sendForAll(ObjectMessage.create(mComCode).add(TIMESTAMP_RESULT).add(timeStamp).add(thisTimeStamp).build());
				break;
			case TIMESTAMP_RESULT:
				final long newThisTime = System.nanoTime();
				final long receivedThis = (Long) objectMessage.getValue(1);
				final long receivedOther = (Long) objectMessage.getValue(2);
				final long diff = (newThisTime-receivedThis);
				if(diff < mTimeStampSyncResultDiff) {
					mTimeStampSyncResult = mTimeStampSyncCount;
					mTimeStampSyncResultDiff = diff;
					mTimeStampSyncResultP1 = receivedThis;
					mTimeStampSyncResultP2 = receivedOther;
				}
				mTimeStampSyncCount++;
				// Send Again
				if((mTimeStampSyncCount <= 25 || mTimeStampSyncResultDiff >= 18000000) && mTimeStampSyncCount <= 100) {
					try {
						Thread.sleep(2);
					} catch (Exception e) {}
					mComManager.sendForAll(ObjectMessage.create(mComCode).add(TIMESTAMP_SYNC).add(System.nanoTime()).build());
				} else {
					// End
					mComManager.sendForAll(ObjectMessage.create(mComCode).add(TIMESTAMP_END).add(mTimeStampSyncResult).build());
					mComManager.prepareParentsAttributes();
				}
				break;
			// End sync
			case TIMESTAMP_END:
				final int resultId = (Integer) objectMessage.getValue(1);
				long[] result = mTimeStampList.get(resultId);
				// Set Result
				mTimeStampSyncResultP1 = result[0];
				mTimeStampSyncResultP2 = result[1];
				// Prepare Attributes
				mComManager.prepareParentsAttributes();
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
	final protected void sendMessage(ObjectMessage built) {
		mComManager.sendForAll(built);
	}
	

	/**
	 * Config Scene
	 */
	final public void configScene(final Scene scene) {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		scene.enable(Scene.FUNC_VIRTUAL_DPI);
		scene.setPosition(mGameState.getAlignMapPosition());
	}
	
	/**
	 * Get DuoMap Monitor.<br>
	 * <b>Note:</b> Monitor available after connection finish
	 */
	final public GameState getState() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameState;
	}
	
	/**
	 * Get Game Variables
	 * 
	 * @return
	 */
	final public GameVariables getVariables() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameVariables;
	}
	
	/**
	 * Get Game Objects.<br>
	 * <b>Note:</b> Game Objects available after connection finish
	 * @return
	 */
	final public GameObjects getObjects() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameObjects;
	}
	
	/**
	 * Get Game Monitor.<br>
	 * <b>Note:</b> Game Monitor available after connection finish
	 * @return
	 */
	final public GameMonitor getMonitor() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameMonitor;
	}
	
	/**
	 * Get Game Density Parser.<br>
	 * <b>Note:</b> Game Density Parser available after connection finish
	 * @return
	 */
	final public GameDensityParser getDensityParser() {
		if(!mConnectionFinish)
			throw new RuntimeException("Not Connected.");
		return mGameDensityParser;
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
