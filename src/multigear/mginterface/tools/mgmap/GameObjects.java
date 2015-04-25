package multigear.mginterface.tools.mgmap;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.tools.mgmap.MultigearGame.Player;
import multigear.mginterface.tools.mgmap.MultigearGame.RegisterMode;
import multigear.services.ServiceControl;
import multigear.services.ServiceRunnable;
import android.util.Log;


/**
 * DuoMap Manager
 * 
 * @author user
 *
 */
public class GameObjects {
	
	/**
	 * Game Message
	 * 
	 * @author user
	 *
	 */
	final private class GameMessage {
		
		int code;
		Object object1, object2, object3, object4, object5, object6;
	}
	
	/**
	 * Game Object
	 * 
	 * @author user
	 *
	 */
	final public class GameObject {
		
		// Private Variables
		private int mId;
		
		private RegisterMode mRegisterMode;
		private MultigearGame.Player mPlayer;
		
		private Vector2 mPosition = new Vector2();
		private Vector2 mSize = new Vector2();
		private Vector2 mDirection = new Vector2();
		
		private boolean mDeleted = false;
		private int mFlags = 0;
		private boolean mReleased = false;
		private int mTransactionControl = 0;
		
		/**
		 * Set object position
		 */
		final public void setPosition(final Vector2 position) {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente");
			// If not released
			if(!mReleased) {
				// Set Position
				mPosition = position;
			// If player in this side
			} else if(mPlayer == mMonitor.getPlayer() || mRegisterMode == RegisterMode.FREE) {
				// Set Position
				mPosition = position;
				// Transact Object
				if(checkTransactObject(this))
					informTransaction(this);
			}
		}
		
		/**
		 * Set object size
		 */
		final public void setSize(final Vector2 size) {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente");
			// If not released
			if(!mReleased) {
				// Set Position
				mSize = size;
			} else {
				throw new RuntimeException("Error on set size..");
			}
		}
		
		/**
		 * Set object direction
		 */
		final public void setDirection(final Vector2 direction) {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente");
			// If not released
			if(!mReleased) {
				// Set Position
				mDirection = direction;
			// If player in this side
			} else if(mPlayer == mMonitor.getPlayer() || mRegisterMode == RegisterMode.FREE) {
				// Set Position
				mDirection = direction;
			}
		}
		
		/**
		 * Update Object
		 */
		final public void update() {
			if(mPlayer != mMonitor.getPlayer()) {
				if(checkVisibleObject(this)) {
					mPosition.x += mDirection.x * GlobalClock.elapsedFramedTime();
					mPosition.y += mDirection.y * GlobalClock.elapsedFramedTime();
				}
			}
		}
		
		/**
		 * Release Object for all players<br>
		 * <b>Note:</b> This method is optimized, and run only once.
		 */
		final public void release() {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente.");
			
			// Not release many times
			if(mReleased)
				return;
			
			// If create and release to other side
			if(mPlayer != mMonitor.getPlayer() || checkTransactObject(this)) {
				informCreateAndTransact(this);
			// If create and release in this side
			} else {
				informCreate(this);
			}
			
			// Set Released
			mReleased = true;
		}
		
		/**
		 * Info other side this position, size, extra package if this object in this side or is free
		 */
		final public void inform() {
			// If create and release to other side
			if(mPlayer == mMonitor.getPlayer() && mReleased) {
				GameObjects.this.inform(this);
			}
		}
		
		/**
		 * Get Id
		 * 
		 * @return
		 */
		final public int getId() {
			return mId;
		}
		
		/**
		 * Get Player side
		 * 
		 * @return
		 */
		final public Player getSide() {
			return mPlayer;
		}
		
		/**
		 * Get Position
		 * 
		 * @return
		 */
		final public Vector2 getPosition() {
			return mPosition.clone();
		}
		
		/**
		 * Get Size
		 * 
		 * @return
		 */
		final public Vector2 getSize() {
			return mSize.clone();
		}
		
		/**
		 * Get Direction
		 * @return
		 */
		final public Vector2 getDirection() {
			return mDirection;
		}
		
		/**
		 * Is Visible
		 * 
		 * @return
		 */
		final public boolean isVisible() {
			return (mPlayer == mMonitor.getPlayer() || mRegisterMode == RegisterMode.FREE);
		}
		
		/**
		 * Get Object Flags
		 * @return
		 */
		final public int getFlags() {
			return mFlags;
		}
	}
	
	/**
	 * Feedback
	 * 
	 * @author user
	 *
	 */
	public interface Feedback {
		
		/** 
		 * Object Created 
		 * 
		 * @param object GameObject
		 * */
		public void onObjectCreated(final GameObject object);
		
		/**
		 * Object Deleted
		 * 
		 * @param object GameObject
		 */
		public void onObjectDeleted(final GameObject object);
	}
	
	
	// Conts
	final private static int OBJECT_CREATE = 1;
	final private static int OBJECT_DELETE = 2;
	final private static int OBJECT_TRANSACT= 3;
	final private static int OBJECT_CREATE_AND_TRANSACT = 4;
	final private static int OBJECT_INFORM = 5;
	
	// Compensation screen percent
	final private static float COMPENSATION_SCREEN_PERCENT = .6f;
	
	
	// Final Private Variables
	final private MultigearGame mGame;
	final private GameState mMonitor;
	final private List<GameObject> mGameObjects = new ArrayList<GameObject>();
	final private List<GameMessage> mGameMessages = new ArrayList<GameMessage>();
	
	
	// Private Variables
	private int mIdCounter = 0;
	private Feedback mFeedback = null;
	
	/**
	 * Constructor
	 * 
	 * @param game
	 */
	protected GameObjects(final MultigearGame game, final GameState monitor) {
		mGame = game;
		mMonitor = monitor;
	}
	
	/**
	 * Set Feedback
	 * @param feedback
	 */
	final public void setFeedback(final Feedback feedback) {
		mFeedback = feedback;
	}
	
	/**
	 * Get Feedback
	 * @return
	 */
	final public Feedback getFeedback() {
		return mFeedback;
	}
	
	/**
	 * Create Game Object
	 * 
	 * @param playerSide
	 * @return Object Index 
	 */
	final public GameObject createObject(final MultigearGame.Player playerSide, final RegisterMode type, final int flags) {
		final GameObject gameObject = new GameObject();
		gameObject.mPlayer = playerSide;
		gameObject.mId = mGame.getState().maskUnsignedInt(mIdCounter++);
		gameObject.mFlags = flags;
		gameObject.mRegisterMode = type;
		mGameObjects.add(gameObject);
		// Feedback
		if(mFeedback != null)
			mFeedback.onObjectCreated(gameObject);
		// Return index
		return gameObject;
	}
	
	/**
	 * Delete Object with Id
	 * @param id
	 */
	final public void deleteObjectAtIndex(final int index) {
		if(index >= getCount())
			throw new IndexOutOfBoundsException();
		final GameObject object = getObjectAtIndex(index);
		// If object in this side or static
		if(object.mPlayer == mMonitor.getPlayer() || object.mRegisterMode == RegisterMode.FREE) {
			final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_DELETE);
			builder.add(object.mId);
			mGame.sendMessage(builder.build());
			// remove object
			object.mDeleted = true;
			mGameObjects.remove(object);
			// Feedback
			if(mFeedback != null)
				mFeedback.onObjectDeleted(object);
		}
	}
	
	/**
	 * Delete Object with Id
	 * @param id
	 */
	final public void deleteObjectById(final int id) {
		final GameObject object = getObjectById(id);
		// If object in this side or static
		if(object.mPlayer == mMonitor.getPlayer() || object.mRegisterMode == RegisterMode.FREE) {
			final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_DELETE);
			builder.add(object.mId);
			mGame.sendMessage(builder.build());
			// remove object
			object.mDeleted = true;
			mGameObjects.remove(object);
			// Feedback
			if(mFeedback != null)
				mFeedback.onObjectDeleted(object);
		}
	}
	
	/**
	 * Get Object at index
	 * 
	 * @param id
	 * @return
	 */
	final public GameObject getObjectAtIndex(final int index) {
		if(index >= getCount())
			throw new IndexOutOfBoundsException();
		int count = 0;
		// Search
		for(final GameObject object : mGameObjects) {
			if(object.mReleased) {
				// Get object with index
				if(count == index)
					return object;
				//
				count++;
			}
		}
		return mGameObjects.get(index);
	}
	
	/**
	 * Get Object with id
	 * 
	 * @param id
	 * @return
	 */
	final public GameObject getObjectById(final int id) {
		for(final GameObject object : mGameObjects)
			if(object.mId == id && object.mReleased)
				return object;
		throw new IllegalArgumentException("This object not exist or not released");
	}
	
	/**
	 * Get Object with id
	 * 
	 * @param id
	 * @return
	 */
	final private GameObject getObjectByIdSafe(final int id) {
		for(final GameObject object : mGameObjects)
			if(object.mId == id)
				return object;
		return null;
	}
	 
	/**
	 * Return Objects Count
	 * @return
	 */
	final public int getCount() {
		int count = 0;
		for(final GameObject object : mGameObjects) {
			if(object.mReleased)
				count++;
		}
		return count;
	}
	
	/**
	 * Check Transact Object
	 * @param object
	 */
	final private boolean checkTransactObject(final GameObject object) {
		// If player in this side
		float mapDivision = mMonitor.getMapDivision();
		
		switch(mMonitor.getPlayer()) {
		default:
		case Player1:
			// Transacted to other side
			if((object.mPosition.x + object.mSize.x >= (mapDivision * COMPENSATION_SCREEN_PERCENT)) && object.mDirection.x >= 0)
				return true;
			break;
		case Player2:
			// Transacted to other side
			if((object.mPosition.x < (mapDivision * (2 - COMPENSATION_SCREEN_PERCENT))) && object.mDirection.x <= 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Check visibile
	 * @param object
	 */
	final private boolean checkVisibleObject(final GameObject object) {
		// If player in this side
		float mapDivision = mMonitor.getMapDivision();
		
		switch(mMonitor.getPlayer()) {
		default:
		case Player1:
			// Transacted to other side
			if(object.mPosition.x >= mapDivision)
				return false;
			break;
		case Player2:
			// Transacted to other side
			if(object.mPosition.x + object.mSize.x < mapDivision)
				return false;
		}
		return true;
	}
	
	/**
	 * Inform Object created
	 * @param object
	 */
	final private void informCreate(final GameObject object) {
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_CREATE);
		builder.add(object.mId);
		builder.add(object.mFlags);
		builder.add(object.mRegisterMode.ordinal());
		builder.add(object.mPosition);
		builder.add(object.mSize);
		builder.add(object.mDirection);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Transaction
	 * 
	 * @param object
	 */
	final private void informTransaction(final GameObject object) {
		// Invisible in this side after transact
		// Switch
		object.mPlayer = mMonitor.getParentPlayer();
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_TRANSACT);
		builder.add(object.mId);
		builder.add(object.mPosition);
		builder.add(object.mDirection);
		builder.add(System.nanoTime());
		builder.add(object.mTransactionControl);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Object created and transact
	 * @param object
	 */
	final private void informCreateAndTransact(final GameObject object) {
		// Invisible in this side
		object.mPlayer = mMonitor.getParentPlayer();
		// Create and transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_CREATE_AND_TRANSACT);
		builder.add(object.mId);
		builder.add(object.mFlags);
		builder.add(object.mRegisterMode.ordinal());
		builder.add(object.mPosition);
		builder.add(object.mSize);
		builder.add(object.mDirection);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform 
	 * 
	 * @param object
	 */
	final private void inform(final GameObject object) {
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM);
		builder.add(object.mId);
		builder.add(object.mPosition);
		builder.add(object.mSize);
		builder.add(object.mDirection);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * On Message
	 * @param values
	 */
	final protected void message(int code, final List<Object> values) {
		final GameMessage message = new GameMessage();
		message.code = code;
		switch(code) {
		case OBJECT_CREATE:
		case OBJECT_CREATE_AND_TRANSACT:
			message.object1 = values.get(0);
			message.object2 = values.get(1);
			message.object3 = values.get(2);
			message.object4 = values.get(3);
			message.object5 = values.get(4);
			message.object6 = values.get(5);
			break;
		case OBJECT_INFORM:
			message.object1 = values.get(0);
			message.object2 = values.get(1);
			message.object3 = values.get(2);
			message.object4 = values.get(3);
			break;
		case OBJECT_TRANSACT:
			message.object1 = values.get(0);
			message.object2 = values.get(1);
			message.object3 = values.get(2);
			message.object4 = values.get(3);
			message.object5 = values.get(4);
			break;
		case OBJECT_DELETE:
			message.object1 = values.get(0);
			break;
		}
		mGameMessages.add(message);
	}
	
	/**
	 * On Update
	 */
	final protected void update() {
		final Iterator<GameMessage> itr = mGameMessages.iterator();
		while(itr.hasNext()) {
			final GameMessage message = itr.next();
			// Vars
			Integer id, flags, tcontrol;
			GameObject object;
			Vector2 pos, size, dir;
			long nano;
			// Message
			switch(message.code) {
			case OBJECT_CREATE:
				
				id = (Integer)message.object1;
				flags = (Integer)message.object2;
				pos = (Vector2)message.object4;
				size = (Vector2)message.object5;
				dir = (Vector2)message.object6;
				
				object = new GameObject();
				object.mId = id;
				object.mPlayer = mMonitor.getParentPlayer();
				object.mFlags = flags;
				object.mRegisterMode = RegisterMode.values()[(Integer)message.object3];
				object.mPosition = pos;
				object.mSize = size;
				object.mDirection = dir;
				object.mReleased = true;
				
				mGameObjects.add(object);
				
				// Remove message
				itr.remove();
				
				// Feedback
				if(mFeedback != null)
					mFeedback.onObjectCreated(object);

				break;
			case OBJECT_CREATE_AND_TRANSACT:
				
				id = (Integer)message.object1;
				flags = (Integer)message.object2;
				pos = (Vector2)message.object4;
				size = (Vector2)message.object5;
				dir = (Vector2)message.object6;
				
				object = new GameObject();
				object.mId = id;
				object.mFlags = flags;
				object.mRegisterMode = RegisterMode.values()[(Integer)message.object3];
				object.mPosition = pos;
				object.mSize = size;
				object.mDirection = dir;
				object.mPlayer = mMonitor.getPlayer();
				object.mReleased = true;
				
				mGameObjects.add(object);
				
				// Remove message
				itr.remove();
				
				// Feedback
				if(mFeedback != null)
					mFeedback.onObjectCreated(object);
				
				break;
			case OBJECT_TRANSACT:
				
				id = (Integer)message.object1;
				pos = (Vector2)message.object2;
				dir = (Vector2)message.object3;
				nano = (Long)message.object4;
				tcontrol = (Integer)message.object5;
				
				object = getObjectByIdSafe(id);
				
				if(object != null && tcontrol >= object.mTransactionControl) {
					long timeDiff = Math.min(Math.abs(mMonitor.getParentNanoTimes() - nano), 150000000);
					Vector2 newDir = Vector2.scale(dir,  timeDiff / 17000000.0f);
					object.mPosition = Vector2.sum(pos, newDir);
					object.mDirection = dir;
					object.mPlayer = mMonitor.getPlayer();
					object.mReleased = true;
					object.mTransactionControl = tcontrol + 1;
				}
				
				// Remove message
				itr.remove();
				
				break;
				
			case OBJECT_INFORM:
				
				id = (Integer)message.object1;
				pos = (Vector2)message.object2;
				size = (Vector2)message.object3;
				dir = (Vector2)message.object4;
				
				object = getObjectByIdSafe(id);
				
				if(object != null) {
					object.mPosition = pos;
					object.mSize = size;
					object.mDirection = dir;
				}
				
				// Remove message
				itr.remove();
				
				break;
			case OBJECT_DELETE:
				// Remove object
				id = (Integer)message.object1;
				object = getObjectByIdSafe(id);
				// If has object
				if(object != null) {
					object.mDeleted = true;
					mGameObjects.remove(object);
					
					// Remove message
					itr.remove();
					
					// Feedback
					if(mFeedback != null)
						mFeedback.onObjectDeleted(object);
				} else {
					// Remove message
					itr.remove();
				}
				break;
			}
		}
	}
}
