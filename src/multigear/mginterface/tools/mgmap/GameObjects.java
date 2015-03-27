package multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.communication.tcp.support.objectmessage.ObjectMessage;
import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import multigear.general.utils.Vector2;
import multigear.mginterface.tools.mgmap.MultigearGame.Player;


/**
 * DuoMap Manager
 * 
 * @author user
 *
 */
public class GameObjects {
	
	/**
	 * Object Type
	 * 
	 * @author user
	 *
	 */
	public enum ObjectType {
		
		/* Conts */
		DYNAMIC,
		STATIC;
	}
	
	/**
	 * Game Object
	 * 
	 * @author user
	 *
	 */
	final private class GameObject {
		
		int id;
		MultigearGame.Player player;
		Vector2 position = new Vector2();
		Vector2 size = new Vector2();
		boolean visibleControl;
		ObjectType type = ObjectType.STATIC;
		ObjectMessage extraPackage = ObjectMessage.create(0).build();
		{
			extraPackage.translate();
		}
	}
	
	/**
	 * Game Message
	 * 
	 * @author user
	 *
	 */
	final private class GameMessage {
		
		int code;
		Object object1, object2, object3;
	}
	
	// Conts
	final private static int OBJECT_REGISTERED = 1;
	final private static int OBJECT_INFORM_POSITION = 2;
	final private static int OBJECT_INFORM_SIZE = 3;
	final private static int OBJECT_INFORM_TRANSACT = 4;
	final private static int OBJECT_INFORM_VISIBLE = 5;
	final private static int OBJECT_INFORM_EXTRA_PACKAGE = 6;
	
	// Final Private Variables
	final private MultigearGame mGame;
	final private GameState mMonitor;
	final private List<GameObject> mGameObjects = new ArrayList<GameObject>();
	final private List<GameMessage> mGameMessages = new ArrayList<GameMessage>();
	
	
	// Private Variables
	private int mIdCounter = 0;
	
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
	 * Create Game Object
	 * 
	 * @param playerSide
	 * @return Object Index 
	 */
	final public int registerObject(final MultigearGame.Player playerSide, final ObjectType type) {
		final GameObject gameObject = new GameObject();
		gameObject.player = playerSide;
		gameObject.type = type;
		gameObject.id = mGame.getState().maskUnsignedInt(mIdCounter++);
		gameObject.visibleControl = false;
		mGameObjects.add(gameObject);
		// Register in other side
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_REGISTERED);
		builder.add(gameObject.id);
		builder.add(playerSide.ordinal());
		builder.add(type.ordinal());
		mGame.sendMessage(builder.build());
		// Return index
		return mGameObjects.size()-1;
	}
	
	/**
	 * Return Objects Count
	 * @return
	 */
	final public int getCount() {
		return mGameObjects.size();
	}
	
	/**
	 * Get Object
	 * 
	 * @param id
	 * @return
	 */
	final private GameObject getObject(final int id) {
		for(final GameObject object : mGameObjects) {
			if(id == object.id)
				return object;
		}
		return null;
	}
	
	/**
	 * Check Transact Object
	 * @param object
	 */
	final public boolean checkTransactObject(final GameObject object) {
		// If player in this side
		float mapDivision = mMonitor.getMapDivision();
		
		switch(mMonitor.getPlayer()) {
		default:
		case Player1:
			// Transacted to other side
			if(object.position.x >= mapDivision)
				return true;
			break;
		case Player2:
			// Transacted to other side
			if(object.position.x + object.size.x < mapDivision)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Check Visible Object
	 * @param object
	 */
	final public boolean checkVisibleObject(final GameObject object) {
		// If player in this side
		float mapDivision = mMonitor.getMapDivision();
			
		switch(mMonitor.getPlayer()) {
		default:
		case Player1:
			// Visible in other side
			if(object.position.x + object.size.x >= mapDivision)
				return true;
			break;
		case Player2:
			// Visible in other side
			if(object.position.x < mapDivision)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Inform Transaction
	 * 
	 * @param object
	 */
	final public void informTransaction(final GameObject object) {
		// Invisible in this side after transact
		// Switch
		object.visibleControl = false;
		object.player = mMonitor.getParentPlayer();
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_TRANSACT);
		builder.add(object.id);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Position
	 * 
	 * @param object
	 */
	final public void informPosition(final GameObject object) {
		// Set position in other side
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_POSITION);
		builder.add(object.id);
		builder.add(object.position);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Resized
	 * 
	 * @param object
	 */
	final public void informSize(final GameObject object) {
		// Set position in other side
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_SIZE);
		builder.add(object.id);
		builder.add(object.size);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Visible
	 * 
	 * @param object
	 */
	final public void informVisible(final GameObject object) {
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_VISIBLE);
		builder.add(object.id);
		builder.add(object.visibleControl);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Inform Extra Package
	 * 
	 * @param object
	 */
	final public void informExtraPackage(final GameObject object) {
		// Transact
		final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_INFORM_EXTRA_PACKAGE);
		builder.add(object.id);
		builder.add(object.extraPackage);
		mGame.sendMessage(builder.build());
	}
	
	/**
	 * Set Object Position.<br>
	 * <b>Note:</b> If this object in other side and object type is Dynamic is ignored.
	 * 
	 * @param objectId
	 * @param position
	 */
	final public void setObjectPosition(final int index, final Vector2 position) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		
		final GameObject object = mGameObjects.get(index);
		
		// If player in this side
		if(object.type == ObjectType.STATIC || object.player == mMonitor.getPlayer()) {
			// Set Position
			object.position = position;
			
			// Transact Object
			if(checkTransactObject(object)) {
				informPosition(object);
				informSize(object);
				informExtraPackage(object);
				informTransaction(object);
			} else {
				
				// Switch visibility
				if(checkVisibleObject(object)) {
					if(!object.visibleControl) {
						object.visibleControl = true;
						informPosition(object);
						informSize(object);
						informExtraPackage(object);
						informVisible(object);
					// Constant Sending
					} else {
						informPosition(object);
					}
				} else if(object.visibleControl) {
					object.visibleControl = false;
					informVisible(object);
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
	final public void setObjectSize(final int index, final Vector2 size) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		
		final GameObject object = mGameObjects.get(index);
		
		// If player in this side
		if(object.type == ObjectType.STATIC || object.player == mMonitor.getPlayer()) {
			// Set Position
			object.size = size;
			
			// Transact Object
			if(checkTransactObject(object)) {
				informPosition(object);
				informSize(object);
				informExtraPackage(object);
				informTransaction(object);
			} else {
				
				// Set visibility control
				if(checkVisibleObject(object)) {
					if(!object.visibleControl) {
						object.visibleControl = true;
						informPosition(object);
						informSize(object);
						informExtraPackage(object);
						informVisible(object);
					// Constant sending
					} else {
						informSize(object);
					}
				} else if(object.visibleControl) {
					object.visibleControl = false;
					informVisible(object);
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
	final public void setObjectExtraPackage(final int index, final ObjectMessage extraPackage) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		final GameObject object = mGameObjects.get(index);
		// If player in this side
		if(object.type == ObjectType.STATIC || object.player == mMonitor.getPlayer()) {
			// Set extra package
			object.extraPackage = extraPackage;
			object.extraPackage.translate();
		}
	}
	
	/**
	 * Get GameObject Position in map
	 * @param objectId Game Object Id
	 * @return Vector2 Position
	 */
	final public Vector2 getObjectPosition(final int index) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		final GameObject object = mGameObjects.get(index);
		return object.position.clone();
	}
	
	/**
	 * Get GameObject size
	 * @param objectId Game Object Id
	 * @return Vector2 Size
	 */
	final public Vector2 getObjectSize(final int index) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		final GameObject object = mGameObjects.get(index);
		return object.size.clone();
	}
	
	/**
	 * Get GameObject flag
	 * @param objectId Game Object Id
	 * @return Vector2 Size
	 */
	final public ObjectMessage getObjectExtraPackage(final int index) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		final GameObject object = mGameObjects.get(index);
		return object.extraPackage;
	}
	
	/**
	 * Check if is visible
	 * 
	 * @param index
	 * @return
	 */
	final public boolean isVisible(final int index) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		final GameObject object = mGameObjects.get(index);
		return (object.player == mMonitor.getPlayer()) || object.visibleControl;
	}
	
	/**
	 * Return player on object side
	 * @return
	 */
	final public Player getObjectPlayer(final int index) {
		if(index >= mGameObjects.size())
			throw new IndexOutOfBoundsException();
		final GameObject object = mGameObjects.get(index);
		return object.player;
	}
	
	/**
	 * On Message
	 * @param values
	 */
	final protected void message(int code, final List<Object> values) {
		final GameMessage message = new GameMessage();
		message.code = code;
		switch(code) {
		case OBJECT_REGISTERED:
			message.object1 = values.get(0);
			message.object2 = values.get(1);
			message.object3 = values.get(2);
			break;
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
			Integer id;
			GameObject object;
			Player player;
			ObjectType type;
			// Message
			switch(message.code) {
			case OBJECT_REGISTERED:
				
				id = (Integer)message.object1;
				player = Player.values()[(Integer)message.object2];
				type = ObjectType.values()[(Integer)message.object3];
				object = new GameObject();
				object.id = id;
				object.player = player;
				object.type = type;
				object.visibleControl = false;
				
				mGameObjects.add(object);
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_TRANSACT:
				id = (Integer)message.object1;
				object = getObject(id);
				if(object != null)
					object.player = mMonitor.getPlayer();
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_POSITION:
				id = (Integer)message.object1;
				object = getObject(id);
				if(object != null)
					object.position = (Vector2)message.object2;
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_SIZE:
				id = (Integer)message.object1;
				object = getObject(id);
				if(object != null)
					object.size = (Vector2)message.object2;
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_EXTRA_PACKAGE:
				id = (Integer)message.object1;
				object = getObject(id);
				if(object != null)
					object.extraPackage = ((ObjectMessage)message.object2);
				// Consume msg
				itr.remove();
				break;
			case OBJECT_INFORM_VISIBLE:
				id = (Integer)message.object1;
				object = getObject(id);
				if(object != null)
					object.visibleControl = (Boolean)message.object2;
				// Consume msg
				itr.remove();
				break;
			}
		}
	}
}
