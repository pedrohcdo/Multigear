package multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import multigear.general.utils.Vector2;
import multigear.mginterface.tools.mgmap.MultigearGame.Player;
import multigear.mginterface.tools.mgmap.MultigearGame.RegisterMode;


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
		Object object1, object2, object3, object4;
	}
	
	/**
	 * 
	 * @author user
	 *
	 */
	final public static class ExtraPackage {
		
		// Private Variables
		final private ObjectMessageBuilder mBuilder = ObjectMessage.create(0);
		final private List<Object> mObjects = new ArrayList<Object>();
		
		
		/**
		 * Private COnstructor
		 */
		private ExtraPackage() {};
		
		/**
		 * Create Extra Package
		 * @return
		 */
		final static public ExtraPackage create() {
			return new ExtraPackage();
		}
		
		/**
		 * Create with ObjectMessage
		 * 
		 * @param message
		 * @return
		 */
		final static private ExtraPackage create(final ObjectMessage message) {
			final ExtraPackage extraPackage = new ExtraPackage();
			for(int i = 0; i<message.size(); i++) {
				final Object object = message.getValue(i);
				if(object.getClass() == Integer.class) {
					extraPackage.add((Integer) object);
				} else if(object.getClass() == Short.class) {
					extraPackage.add((Short) object);
				} else if(object.getClass() == Long.class) {
					extraPackage.add((Long) object);
				} else if(object.getClass() == Float.class) {
					extraPackage.add((Float) object);
				} else if(object.getClass() == Double.class) {
					extraPackage.add((Double) object);
				} else if(object.getClass() == Boolean.class) {
					extraPackage.add((Boolean) object);
				} else if(object.getClass() == String.class) {
					extraPackage.add((String) object);
				} else if(object.getClass() == Vector2.class) {
					extraPackage.add((Vector2) object);
				}
			}
			return extraPackage;
		}
		
		/**
		 * Get Object in index
		 * @param index
		 * @return
		 */
		final public Object get(final int index) {
			return mObjects.get(index);
		}
		
		/**
		 * Add Integer Value
		 * @param value Integer Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final short value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Add Integer Value
		 * @param value Integer Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final int value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Add Integer Value
		 * @param value Integer Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final long value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Add Float Value
		 * @param value Float Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final float value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Add Double Value
		 * @param value Double Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final double value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Add Boolean Value
		 * @param value Boolean Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final boolean value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Add String Value
		 * @param value String Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final String value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Add Ref2D Value
		 * @param value Boolean Value
		 * @return This ObjectMessage reference
		 */
		final public ExtraPackage add(final Vector2 value) {
			mBuilder.add(value);
			mObjects.add(value);
			return this;
		}
		
		/**
		 * Get Object Message
		 * 
		 * @return
		 */
		final private ObjectMessage getObjectMessage() {
			return mBuilder.build();
		}
		
		/**
		 * Get package size
		 * @return
		 */
		final public int getCount() {
			return mObjects.size();
		}
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
		private MultigearGame.Player mPlayer;
		private Vector2 mPosition = new Vector2();
		private Vector2 mSize = new Vector2();
		private boolean mVisibleControl;
		private RegisterMode mType = RegisterMode.LOCK;
		private ExtraPackage mExtraPackage = ExtraPackage.create();
		private boolean mDeleted = false;
		private int mFlags = 0;
		private boolean mReleased = false;
		
		/**
		 * Set object position
		 */
		final public void setPosition(final Vector2 position) {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente");
			// If player in this side
			if(mType == RegisterMode.FREE || mPlayer == mMonitor.getPlayer()) {
				// Set Position
				mPosition = position;
				
				// Wait for release
				if(!mReleased)
					return;
				
				// If free and not in side inform to other side control
				if(mPlayer != mMonitor.getPlayer()) {
					informPosition(this);
					return;
				}
				
				
				// Transact Object
				if(checkTransactObject(this)) {
					Log.d("LogTest", "Transact");
					informPosition(this);
					informSize(this);
					informExtraPackage(this);
					informTransaction(this); // sets object visible
				} else {
					
					// Switch visibility
					if(checkVisibleObject(this)) {
						if(!mVisibleControl) {
							mVisibleControl = true;
							informPosition(this);
							informSize(this);
							informExtraPackage(this);
							informVisible(this);
						// Constant Sending
						} else {
							informPosition(this);
						}
					} else if(mVisibleControl) {
						mVisibleControl = false;
						informVisible(this);
					}
					
				}
			}
		}
		
		/**
		 * Set Object size.<br>
		 * <b>Note:</b> If this object in other side and object type is Dynamic is ignored.
		 * 
		 * @param objectId
		 * @param position
		 */
		final public void setSize(final Vector2 size) {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente");
			
			// If player in this side
			if(mType == RegisterMode.FREE || mPlayer == mMonitor.getPlayer()) {
				// Set Position
				mSize = size;
				
				// Wait for release
				if(!mReleased)
					return;
				
				// If free and not in side inform to other side control
				if(mPlayer != mMonitor.getPlayer()) {
					informSize(this);
					return;
				}
				
				// Transact Object
				if(checkTransactObject(this)) {
					informPosition(this);
					informSize(this);
					informExtraPackage(this);
					informTransaction(this);
				} else {
					
					// Set visibility control
					if(checkVisibleObject(this)) {
						if(!mVisibleControl) {
							mVisibleControl = true;
							informPosition(this);
							informSize(this);
							informExtraPackage(this);
							informVisible(this);
						// Constant sending
						} else {
							informSize(this);
						}
					} else if(mVisibleControl) {
						mVisibleControl = false;
						informVisible(this);
					}
					
				}
			}
		}
		
		/**
		 * Set Object size.<br>
		 * <b>Note:</b> If this object in other side and object type is Dynamic is ignored.
		 * 
		 * @param objectId
		 * @param position
		 */
		final public void setExtraPackage(final ExtraPackage extraPackage, boolean forceToInform) {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente");
			
			// If player in this side
			if(mType == RegisterMode.FREE || (mPlayer == mMonitor.getPlayer())) {
				// Set extra package
				mExtraPackage = extraPackage;
				
				// Wait for release
				if(!mReleased)
					return;
				
				// If visible
				if(mVisibleControl || forceToInform || (mPlayer != mMonitor.getPlayer()))
					informExtraPackage(this);;
			}
		}
		
		/**
		 * Set Object Extra PAckage.<br>
		 * <b>Note:</b> If this object in other side and object type is Dynamic is ignored.
		 * 
		 * @param objectId
		 * @param position
		 */
		final public void setExtraPackage(final ExtraPackage extraPackage) {
			setExtraPackage(extraPackage, false);
		}
		
		/**
		 * Release Object for all players<br>
		 * <b>Note:</b> This method is optimized, and run only once.
		 */
		final public void release() {
			//
			if(mDeleted)
				throw new RuntimeException("This object was deleted anteriormente");
			// Not relase many times
			if(mReleased)
				return;
			
			// Set Released
			mReleased = true;
			
			// If player in this side
			informCreate(this);
			
			// If this side or free
			if(mType == RegisterMode.FREE || (mPlayer == mMonitor.getPlayer())) {
				// Reset
				setPosition(mPosition);
				setSize(mSize);
				setExtraPackage(mExtraPackage);
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
		 * Is Visible
		 * 
		 * @return
		 */
		final public boolean isVisible() {
			return mVisibleControl || (mPlayer == mMonitor.getPlayer());
		}
		
		/**
		 * Get Object Flags
		 * @return
		 */
		final public int getFlags() {
			return mFlags;
		}
		
		/**
		 * Get Object Type
		 * @return
		 */
		final public RegisterMode getType() {
			return mType;
		}
		
		/**
		 * Get Extra Package
		 * 
		 * @return
		 */
		final public ExtraPackage getExtraPackage() {
			return mExtraPackage;
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
	final private static int OBJECT_INFORM_POSITION = 3;
	final private static int OBJECT_INFORM_SIZE = 4;
	final private static int OBJECT_INFORM_TRANSACT = 5;
	final private static int OBJECT_INFORM_VISIBLE = 6;
	final private static int OBJECT_INFORM_EXTRA_PACKAGE = 7;
	
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
		gameObject.mType = type;
		gameObject.mId = mGame.getState().maskUnsignedInt(mIdCounter++);
		gameObject.mVisibleControl = false;
		gameObject.mFlags = flags;
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
	final public void deleteObjectAt(final int index) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		
		final GameObject object = getObjectAt(index);
		
		// Remove Object if not released
		if(!object.mReleased) {
			// Set deleted
			object.mDeleted = true;
			// Feedback
			if(mFeedback != null)
				mFeedback.onObjectDeleted(object);
			mGameObjects.remove(object);
			return;
		}
		
		// If object in this side or static
		if(object.mType == RegisterMode.FREE || (object.mPlayer == mMonitor.getPlayer())) {
			final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_DELETE);
			builder.add(object.mId);
			mGame.sendMessage(builder.build());
			// remove object
			object.mDeleted = true;
			// Feedback
			if(mFeedback != null)
				mFeedback.onObjectDeleted(object);
			mGameObjects.remove(object);
		}
	}
	
	/**
	 * Delete Object with Id
	 * @param id
	 */
	final public void deleteObject(final int id) {
		final GameObject object = getObject(id);
		
		// Remove Object if not released
		if(!object.mReleased) {
			// Set deleted
			object.mDeleted = true;
			// Feedback
			if(mFeedback != null)
				mFeedback.onObjectDeleted(object);
			mGameObjects.remove(object);
			return;
		}
		
		// If object in this side or static
		if(object.mType == RegisterMode.FREE || (object.mPlayer == mMonitor.getPlayer())) {
			final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_DELETE);
			builder.add(object.mId);
			mGame.sendMessage(builder.build());
			// remove object
			object.mDeleted = true;
			// Feedback
			if(mFeedback != null)
				mFeedback.onObjectDeleted(object);
			mGameObjects.remove(object);
		}
	}
	
	/**
	 * Get Object at index
	 * 
	 * @param id
	 * @return
	 */
	final public GameObject getObjectAt(final int index) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		return mGameObjects.get(index);
	}
	
	/**
	 * Get Object with id
	 * 
	 * @param id
	 * @return
	 */
	final public GameObject getObject(final int id) {
		for(final GameObject object : mGameObjects)
			if(object.mId == id)
				return object;
		throw new IllegalArgumentException("This object not exist");
	}
	
	/**
	 * Get Object with id
	 * 
	 * @param id
	 * @return
	 */
	final private GameObject getObjectSafe(final int id) {
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
		return mGameObjects.size();
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
			if(object.mPosition.x >= mapDivision)
				return true;
			break;
		case Player2:
			// Transacted to other side
			if(object.mPosition.x + object.mSize.x < mapDivision)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Check Visible Object
	 * @param object
	 */
	final private boolean checkVisibleObject(final GameObject object) {
		// If player in this side
		float mapDivision = mMonitor.getMapDivision();
			
		switch(mMonitor.getPlayer()) {
		default:
		case Player1:
			// Visible in other side
			if(object.mPosition.x + object.mSize.x >= mapDivision)
				return true;
			break;
		case Player2:
			// Visible in other side
			if(object.mPosition.x < mapDivision)
				return true;
		}
		return false;
	}
	
	/**
	 * Inform Object created
	 * @param object
	 */
	final private void informCreate(final GameObject object) {
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_CREATE);
		builder.add(object.mId);
		builder.add(object.mPlayer.ordinal());
		builder.add(object.mType.ordinal());
		builder.add(object.mFlags);
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
		object.mVisibleControl = false;
		object.mPlayer = mMonitor.getParentPlayer();
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_TRANSACT);
		builder.add(object.mId);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Position
	 * 
	 * @param object
	 */
	final private void informPosition(final GameObject object) {
		// Set position in other side
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_POSITION);
		builder.add(object.mId);
		builder.add(object.mPosition);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Resized
	 * 
	 * @param object
	 */
	final private void informSize(final GameObject object) {
		// Set position in other side
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_SIZE);
		builder.add(object.mId);
		builder.add(object.mSize);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Visible
	 * 
	 * @param object
	 */
	final private void informVisible(final GameObject object) {
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_VISIBLE);
		builder.add(object.mId);
		builder.add(object.mVisibleControl);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Extra Package
	 * 
	 * @param object
	 */
	final private void informExtraPackage(final GameObject object) {
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_EXTRA_PACKAGE);
		builder.add(object.mId);
		builder.add(object.mExtraPackage.getObjectMessage());
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
			message.object1 = values.get(0);
			message.object2 = values.get(1);
			message.object3 = values.get(2);
			message.object4 = values.get(3);
			break;
		case OBJECT_DELETE:
		case OBJECT_INFORM_TRANSACT:
			message.object1 = values.get(0);
			break;
		case OBJECT_INFORM_POSITION:
		case OBJECT_INFORM_SIZE:
		case OBJECT_INFORM_EXTRA_PACKAGE:
		case OBJECT_INFORM_VISIBLE:
			message.object1 = values.get(0);
			message.object2 = values.get(1);
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
			Integer id, flags;
			GameObject object;
			Player player;
			RegisterMode type;
			// Message
			switch(message.code) {
			case OBJECT_CREATE:
				
				id = (Integer)message.object1;
				player = Player.values()[(Integer)message.object2];
				type = RegisterMode.values()[(Integer)message.object3];
				flags = (Integer)message.object4;
				
				object = new GameObject();
				object.mId = id;
				object.mPlayer = player;
				object.mType = type;
				object.mVisibleControl = false;
				object.mFlags = flags;
				object.mReleased = true;
				mGameObjects.add(object);
				
				// Remove message
				itr.remove();
				
				// Feedback
				if(mFeedback != null)
					mFeedback.onObjectCreated(object);
				break;
			case OBJECT_DELETE:
				// Remove object
				id = (Integer)message.object1;
				object = getObjectSafe(id);
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
			case OBJECT_INFORM_TRANSACT:
				id = (Integer)message.object1;
				object = getObjectSafe(id);
				if(object != null) {
					object.mPlayer = mMonitor.getPlayer();
				}
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_POSITION:
				id = (Integer)message.object1;
				object = getObjectSafe(id);
				if(object != null) {
					object.mPosition = (Vector2)message.object2;
				}
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_SIZE:
				id = (Integer)message.object1;
				object = getObjectSafe(id);
				if(object != null) {
					object.mSize = (Vector2)message.object2;
				}
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_EXTRA_PACKAGE:
				id = (Integer)message.object1;
				object = getObjectSafe(id);
				if(object != null) {
					object.mExtraPackage = ExtraPackage.create((ObjectMessage)message.object2);
				}
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_VISIBLE:
				id = (Integer)message.object1;
				object = getObjectSafe(id);
				if(object != null) {
					object.mVisibleControl = (Boolean)message.object2;
				}
				// Consume msg
				itr.remove();
				break;
			}
		}
	}
}
