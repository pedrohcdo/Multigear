package multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import multigear.communication.tcp.support.objectmessage.ObjectMessageBuilder;
import multigear.general.utils.Vector2;


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
		
		MultigearGame.Player player;
		int id;
		boolean modified = false;
		Vector2 position = new Vector2();
		Vector2 size = new Vector2();
		ObjectType type = ObjectType.STATIC;
	}
	
	/**
	 * Game Message
	 * 
	 * @author user
	 *
	 */
	final private class GameMessage {
		
		int code;
		Object object1, object2;
	}
	
	// Conts
	final private static int OBJECT_MOVED = 1;
	final private static int OBJECT_RESIZED = 2;
	
	// Final Private Variables
	final private MultigearGame mGame;
	final private GameState mMonitor;
	final private List<GameObject> mGameObjects = new ArrayList<GameObject>();
	final private List<GameMessage> mGameMessages = new ArrayList<GameMessage>();
	
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
	 * @return
	 */
	final public void registerObject(final MultigearGame.Player playerSide, final int objectId, final ObjectType type) {
		if(getObject(objectId) != null)
			throw new RuntimeException("This id in use");
		final GameObject gameObject = new GameObject();
		gameObject.player = playerSide;
		gameObject.id = objectId;
		gameObject.type = type;
		mGameObjects.add(gameObject);
	}
	
	/**
	 * Get Game Object
	 * 
	 * @param id
	 * @return
	 */
	final private GameObject getObject(final int id) {
		for(final GameObject gameObject : mGameObjects) {
			if(gameObject.id == id)
				return gameObject;
		}
		return null;
	}
	
	/**
	 * Set Object Position.<br>
	 * <b>Note:</b> If this object in other side and object type is Dynamic is ignored.
	 * 
	 * @param objectId
	 * @param position
	 */
	final public void setObjectPosition(final int objectId, final Vector2 position) {
		final GameObject object = getObject(objectId);
		if(object == null)
			throw new RuntimeException("This object id was not registered.");
		// If player in this side
		if(object.type == ObjectType.STATIC || object.player == mMonitor.getPlayer()) {
			// Set Position
			object.position = position;
			// Set position in other side
			final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_MOVED);
			builder.add(objectId);
			builder.add(position);
			mGame.sendMessage(builder.build());
		}
	}
	
	/**
	 * Set Object size.<br>
	 * <b>Note:</b> If this object in other side and object type is Dynamic is ignored.
	 * 
	 * @param objectId
	 * @param position
	 */
	final public void setObjectSize(final int objectId, final Vector2 size) {
		final GameObject object = getObject(objectId);
		if(object == null)
			throw new RuntimeException("This object id was not registered.");
		// If player in this side
		if(object.type == ObjectType.STATIC || object.player == mMonitor.getPlayer()) {
			// Set Size
			object.size = size;
			// Set position in other side
			final ObjectMessageBuilder builder = mGame.prepareObjectMessage(OBJECT_RESIZED);
			builder.add(objectId);
			builder.add(size);
			mGame.sendMessage(builder.build());
		}
	}
	
	/**
	 * Get GameObject Position in map
	 * @param objectId Game Object Id
	 * @return Vector2 Position
	 */
	final public Vector2 getObjectPosition(final int objectId) {
		final GameObject object = getObject(objectId);
		if(object == null)
			throw new RuntimeException("This object id was not registered.");
		return mMonitor.positionToMap(object.position);
	}
	
	/**
	 * Get GameObject size
	 * @param objectId Game Object Id
	 * @return Vector2 Size
	 */
	final public Vector2 getObjectSize(final int objectId) {
		final GameObject object = getObject(objectId);
		if(object == null)
			throw new RuntimeException("This object id was not registered.");
		return object.size;
	}
	
	/**
	 * Returns whether the object was modified and 
	 * changes the value back to false.
	 * @param objectId Game Object Id
	 * @return True/False
	 */
	final public boolean isObjectModified(final int objectId) {
		final GameObject object = getObject(objectId);
		if(object == null)
			throw new RuntimeException("This object id was not registered.");
		final boolean modified = object.modified;
		object.modified = false;
		return modified;
	}
	
	/**
	 * On Message
	 * @param values
	 */
	final protected void message(int code, final List<Object> values) {
		final GameMessage message = new GameMessage();
		message.code = code;
		switch(code) {
		case OBJECT_MOVED:
		case OBJECT_RESIZED:
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
			// Message
			switch(message.code) {
			case OBJECT_MOVED:
				id = (Integer)message.object1;
				object = getObject(id);
				// If object exist, consume message
				if(object != null) {
					object.position = (Vector2)message.object2;
					object.modified = true;
					itr.remove();
				}
				break;
			case OBJECT_RESIZED:
				id = (Integer)message.object1;
				object = getObject(id);
				// If object exist, consume message
				if(object != null) {
					object.size = (Vector2)message.object2;
					object.modified = true;
					itr.remove();
				}
				break;
			}
		}
	}
}
