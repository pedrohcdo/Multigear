package multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.tools.mgmap.MultigearGame.Player;

/**
 * MultigearGame
 * 
 * @author user
 *
 */
final public class GameMonitor {
	
	/**
	 * Game Message
	 * 
	 * @author user
	 *
	 */
	final private class GameMessage {
		
		int code;
		Object object1;
		Object object2;
	}
	
	// Conts
	final private static int SYNC_PLAYERS_FRAME = 1;
	final private static int TEXTURE_INFO_OF_PLAYER_REQUEST = 2;
	final private static int TEXTURE_INFO_OF_PLAYER_RECEIPT = 3;
	
	// Final Private Variables
	final private List<GameMessage> mGameMessages = new ArrayList<GameMessage>();
	final private MultigearGame mMultigearGame;
	
	// Private Variables
	private boolean mSyncPlayersFrame = false;
	private int mControl = 0;
	
	/**
	 * Private Constructor
	 */
	protected GameMonitor(final MultigearGame duoMap) {
		mMultigearGame = duoMap;
	}
	
	/**
	 * 
	 * @param playerSide
	 */
	final public void syncPlayersFrame() {
		mSyncPlayersFrame = true;
		mMultigearGame.sendMessage(mMultigearGame.prepareMonitorMessage(SYNC_PLAYERS_FRAME).build());
		while(mSyncPlayersFrame) {
			// Force update
			mMultigearGame.getScene().getComManager().update();
			update();
			// Search message
			final Iterator<GameMessage> itr = mGameMessages.iterator();
			brace: while(itr.hasNext()) {
				final GameMessage message = itr.next();
				// Message
				switch(message.code) {
				case SYNC_PLAYERS_FRAME:
					if(mSyncPlayersFrame) {
						mSyncPlayersFrame = false;
						itr.remove();
						break brace;
					}
					break;
				}
			}
		}
		mMultigearGame.update();
	}
	
	/**
	 * Load texture in player side.
	 * @return
	 */
	final public Texture loadTextureOf(Player player, final int resId) {
		// Texture and Control
		final Texture texture = mMultigearGame.getScene().getTextureLoader().load(resId);
		// Check player
		final GameState state = mMultigearGame.getState();
		if(player == state.getPlayer()) {
			return texture;
		} else {
			final int control = mControl++;
			// Request info
			mMultigearGame.sendMessage(mMultigearGame.prepareMonitorMessage(TEXTURE_INFO_OF_PLAYER_REQUEST).add(control).add(resId).build());
			// Wait info
			brace: while(true) {
				// Force update
				mMultigearGame.getScene().getComManager().update();
				update();
				// Search message
				final Iterator<GameMessage> itr = mGameMessages.iterator();
				while(itr.hasNext()) {
					// Variables
					int controlReceipt = 0;
					Vector2 textureSize = null;
					final GameMessage message = itr.next();
					// Message
					switch(message.code) {
					case TEXTURE_INFO_OF_PLAYER_RECEIPT:
						controlReceipt = (Integer) message.object1;
						if(controlReceipt == control) {
							textureSize = (Vector2) message.object2;
							// Stretch texture to player size
							texture.stretch(textureSize);
							itr.remove();
							break brace;
						}
						break;
					}
				}
			}
		}
		return texture;
	}
	
	/**
	 * Returns true if the player on the other side 
	 * waiting for this sync.
	 * @return
	 */
	final public boolean hasPlayerSync() {
		for(final GameMessage message : mGameMessages) {
			if(message.code == SYNC_PLAYERS_FRAME)
				return true;
		}
		return false;
	}
	
	/**
	 * On Message
	 * @param values
	 */
	final protected void message(int code, final List<Object> values) {
		final GameMessage message = new GameMessage();
		message.code = code;
		switch(code) {
		case SYNC_PLAYERS_FRAME:
			break;
		// Send/Recv
		case TEXTURE_INFO_OF_PLAYER_REQUEST:
		case TEXTURE_INFO_OF_PLAYER_RECEIPT:
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
			// Variables
			Texture texture = null;
			int control = 0, resId = 0;
			final GameMessage message = itr.next();
			// Message
			switch(message.code) {
			// Read in method
			case SYNC_PLAYERS_FRAME:
				break;
			case TEXTURE_INFO_OF_PLAYER_REQUEST:
				control = (Integer) message.object1;
				resId = (Integer) message.object2;
				texture = mMultigearGame.getScene().getTextureLoader().load(resId);
				mMultigearGame.sendMessage(mMultigearGame.prepareMonitorMessage(TEXTURE_INFO_OF_PLAYER_RECEIPT).add(control).add(texture.getSize()).build());
				// Remove Message
				itr.remove();
				break;
			// Read in method
			case TEXTURE_INFO_OF_PLAYER_RECEIPT:
				break;
			}
		}
	}
}
