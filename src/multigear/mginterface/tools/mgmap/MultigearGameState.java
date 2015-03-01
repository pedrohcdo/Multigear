package multigear.mginterface.tools.mgmap;

import multigear.general.utils.Vector2;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.tools.mgmap.MultigearGame.Adjust;
import multigear.mginterface.tools.mgmap.MultigearGame.Player;

/**
 * MultigearGame
 * 
 * @author user
 *
 */
final public class MultigearGameState {
	

	// Private Variables
	private Vector2 mMapSize;
	private float mScreenDivision;
	private MultigearGame.Adjust mAdjust = MultigearGame.Adjust.NOT_SET;
	private MultigearGame.Player mPlayer;
	
	/**
	 * Private Constructor
	 */
	protected MultigearGameState() {}
	
	/**
	 * Prepare Monitor
	 * @param mapSize Map Size
	 */
	final protected void prepare(final MultigearGame.Player player, final Vector2 mapSize, final float screenDivision, final MultigearGame.Adjust adjust) {
		mPlayer = player;
		mMapSize = mapSize;
		mScreenDivision = screenDivision;
		mAdjust = adjust;
	}
	
	/**
	 * Get Map Size
	 */
	final public Vector2 getMapSize() {
		return mMapSize;
	}
	
	/**
	 * Get Device Adjust
	 * @return Device Adjust
	 */
	final public MultigearGame.Adjust getAdjust() {
		return mAdjust;
	}
	
	/**
	 * Get Player
	 * 
	 * @return {@link MultigearGame.Player} Player
	 */
	final public MultigearGame.Player getPlayer() {
		return mPlayer;
	}
	
	/**
	 * Get Major Player
	 * 
	 * @return {@link MultigearGame.Player} Player
	 */
	final public MultigearGame.Player getMajorPlayer() {
		switch(mPlayer) {
		default:
		case Player1:
			if(mAdjust == Adjust.ADJUST_MAJOR || mAdjust == Adjust.ADJUST_EQUAL)
				return Player.Player1;
			else
				return Player.Player2;
		case Player2:
			if(mAdjust == Adjust.ADJUST_MAJOR || mAdjust == Adjust.ADJUST_EQUAL)
				return Player.Player2;
			else
				return Player.Player1;
		}
	}
	
	/**
	 * Get Minor Player
	 * 
	 * @return {@link MultigearGame.Player} Player
	 */
	final public MultigearGame.Player getMinorPlayer() {
		switch(mPlayer) {
		default:
		case Player1:
			if(mAdjust == Adjust.ADJUST_MINOR || mAdjust == Adjust.ADJUST_EQUAL)
				return Player.Player1;
			else
				return Player.Player2;
		case Player2:
			if(mAdjust == Adjust.ADJUST_MINOR || mAdjust == Adjust.ADJUST_EQUAL)
				return Player.Player2;
			else
				return Player.Player1;
		}
	}
	
	/**
	 * Converts position in map position based on connections
	 * 
	 * @param postion
	 * @return
	 */
	final public Vector2 positionToMap(final Vector2 position) {
		switch(mPlayer) {
		default:
		case Player1:
			return position;
		case Player2:
			return new Vector2(position.x - mScreenDivision, position.y);
		}
	}
}
