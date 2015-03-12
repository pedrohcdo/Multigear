package multigear.mginterface.tools.mgmap;

import android.util.Log;
import multigear.communication.tcp.support.ParentAttributes;
import multigear.general.utils.Vector2;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.tools.mgmap.MultigearGame.Adjust;
import multigear.mginterface.tools.mgmap.MultigearGame.Player;
import multigear.mginterface.tools.sharedtouch.SharedTouchOffset;

/**
 * MultigearGame
 * 
 * @author user
 *
 */
final public class GameState {
	
	// Final private Variables
	final private MultigearGame mMG;
	
	// Private Variables
	private Vector2 mMapSize;
	private float mScreenDivision;
	private MultigearGame.Adjust mAdjust = MultigearGame.Adjust.NOT_SET;
	private MultigearGame.Player mPlayer;
	private ParentAttributes mParentAttributes;
	
	/**
	 * Private Constructor
	 */
	protected GameState(final MultigearGame mg) {
		mMG = mg;
	}
	
	/**
	 * Prepare Monitor
	 * @param mapSize Map Size
	 */
	final protected void prepare(final MultigearGame.Player player, final Vector2 mapSize, final float screenDivision, final MultigearGame.Adjust adjust, final ParentAttributes parentAttributes) {
		mPlayer = player;
		mMapSize = mapSize;
		mScreenDivision = screenDivision;
		mAdjust = adjust;
		mParentAttributes = parentAttributes;
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
	 * Converts screen position to map position based on connections
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
	
	/**
	 * Converts map position to screen position based on connections
	 * 
	 * @param postion
	 * @return
	 */
	final public Vector2 positionToScreen(final Vector2 position) {
		switch(mPlayer) {
		default:
		case Player1:
			return position;
		case Player2:
			return new Vector2(position.x + mScreenDivision, position.y);
		}
	}
	
	/**
	 * Get Map division
	 * 
	 * @param postion
	 * @return
	 */
	final public float getMapDivision() {
		return mScreenDivision;
	}
	
	/**
	 * Get Align Position
	 * @return
	 */
	final protected Vector2 getAlignMapPosition() {
		return positionToMap(new Vector2(0, (mMG.getScene().getSpaceParser().getScreenSize().y - getMapSize().y) / 2));
	}
	
	
	/**
	 * Extension for Shared Touch Offset
	 * @return
	 */
	final public SharedTouchOffset getSharedTouchOffsetExt() {
		SharedTouchOffset offset = new SharedTouchOffset();
		Vector2 alignPosition = getAlignMapPosition();
		float scaleFactor = mMG.getScene().getSpaceParser().getScaleFactor();
		switch(mPlayer) {
		default:
		case Player1:
			offset.sourceOffset = new Vector2(-mMG.getScene().getScreenSize().x, -alignPosition.y);
			offset.receiveOffset = new Vector2(mMG.getScene().getScreenSize().x, alignPosition.y);
			offset.sourceAdjust = 1 / scaleFactor;
			offset.receivedAdjust = scaleFactor;
			break;
		case Player2:
			offset.sourceOffset = new Vector2(0, -alignPosition.y);
			offset.receiveOffset = new Vector2(0, alignPosition.y);
			offset.sourceAdjust = 1 / scaleFactor;
			offset.receivedAdjust = scaleFactor;
		}
		return offset;
	}
}
