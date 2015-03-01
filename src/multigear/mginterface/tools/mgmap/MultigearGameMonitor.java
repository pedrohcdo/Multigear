package multigear.mginterface.tools.mgmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Vector2;
import multigear.mginterface.tools.mgmap.MultigearGame.Adjust;
import multigear.mginterface.tools.mgmap.MultigearGame.Player;

/**
 * MultigearGame
 * 
 * @author user
 *
 */
final public class MultigearGameMonitor {
	
	/**
	 * Game Message
	 * 
	 * @author user
	 *
	 */
	final private class GameMessage {
		
		int code;
	}
	
	// Conts
	final private static int MONITOR_SYNC_PLAYERS_FRAME = 1;
	
	// Final Private Variables
	final private List<GameMessage> mGameMessages = new ArrayList<GameMessage>();
	final private MultigearGame mMultigearGame;
	
	// Private Variables
	private boolean mSyncPlayersFrame = false;
	
	/**
	 * Private Constructor
	 */
	protected MultigearGameMonitor(final MultigearGame duoMap) {
		mMultigearGame = duoMap;
	}
	
	/**
	 * 
	 * @param playerSide
	 */
	final public void syncPlayersFrame() {
		mSyncPlayersFrame = true;
		mMultigearGame.sendMessage(mMultigearGame.prepareMonitorMessage(MONITOR_SYNC_PLAYERS_FRAME).build());
		while(mSyncPlayersFrame) {
			mMultigearGame.getScene().getComManager().update();
			// Search message
			final Iterator<GameMessage> itr = mGameMessages.iterator();
			brace: while(itr.hasNext()) {
				final GameMessage message = itr.next();
				// Message
				switch(message.code) {
				case MONITOR_SYNC_PLAYERS_FRAME:
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
	 * Returns true if the player on the other side 
	 * waiting for this sync.
	 * @return
	 */
	final public boolean hasPlayerSync() {
		for(final GameMessage message : mGameMessages) {
			if(message.code == MONITOR_SYNC_PLAYERS_FRAME)
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
		case MONITOR_SYNC_PLAYERS_FRAME:
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
			// Message
			switch(message.code) {
			case MONITOR_SYNC_PLAYERS_FRAME:
				break;
			}
		}
	}
}
