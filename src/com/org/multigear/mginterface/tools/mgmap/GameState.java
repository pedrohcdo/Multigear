package com.org.multigear.mginterface.tools.mgmap;

import com.org.multigear.communication.tcp.support.ParentAttributes;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.tools.mgmap.MultigearGame.Adjust;
import com.org.multigear.mginterface.tools.mgmap.MultigearGame.Player;
import com.org.multigear.mginterface.tools.sharedtouch.SharedTouchOffset;

import android.graphics.RectF;

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
	private long mP1Time, mP2Time;
	
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
	final protected void prepare(final MultigearGame.Player player, final Vector2 mapSize, final float screenDivision, final MultigearGame.Adjust adjust, final ParentAttributes parentAttributes, final long p1time, final long p2time) {
		mPlayer = player;
		mMapSize = mapSize;
		mScreenDivision = screenDivision;
		mAdjust = adjust;
		mParentAttributes = parentAttributes;
		mP1Time = p1time;
		mP2Time = p2time;
	}
	
	/**
	 * Get Map Size
	 */
	final public Vector2 getMapSize() {
		return mMapSize;
	}
	
	/**
	 * Get Parent time in Milli
	 * @return
	 */
	final public long getParentNanoTimes() {
		switch(mPlayer) {
		default:
		case Player1:
			return (System.nanoTime() - mP1Time) + mP2Time;
		case Player2:
			return (System.nanoTime() - mP2Time) + mP1Time;
		}
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
	 * Get Parent Player
	 * 
	 * @return {@link MultigearGame.Player} Player
	 */
	final public MultigearGame.Player getParentPlayer() {
		return mPlayer == Player.Player1 ? Player.Player2 : Player.Player1;
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
	 * Mask unsigned int
	 * @param number
	 * @return
	 */
	final public int maskUnsignedInt(final int value) {
		switch(mPlayer) {
		default:
		case Player1:
			return value & Integer.MAX_VALUE;
		case Player2:
			return (value & Integer.MAX_VALUE) | 0x80000000;
		}
	}
	
	/**
	 * Unmask unsigned int
	 * @param value
	 * @return
	 */
	final public int unmaskUnsignedInt(final int value) {
		return value & Integer.MAX_VALUE;
	}
	
	/**
	 * Converts screen position to map position based on connections
	 * 
	 * @param postion
	 * @return
	 */
	final private Vector2 positionInMap(final Vector2 position) {
		switch(mPlayer) {
		default:
		case Player1:
			return position;
		case Player2:
			return new Vector2(position.x - mScreenDivision, position.y);
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
	 * Get Visible Map Rect
	 * @return
	 */
	final public RectF getVisibleMapRect() {
		final Vector2 mapSize = getMapSize();
		switch(mPlayer) {
		case Player1:
		default:
			return new RectF(0, 0, mScreenDivision, mapSize.y);
		case Player2:
			return new RectF(mScreenDivision, 0, mapSize.x, mapSize.y);
		}
	}
	
	/**
	 * Return device position in map
	 * @return
	 */
	final public float getMapOffset() {
		switch(mPlayer) {
		case Player1:
		default:
			return 0;
		case Player2:
			return mScreenDivision;
		}
	}
	
	/**
	 * Get Align Position
	 * @return
	 */
	final protected Vector2 getAlignMapPosition() {
		return positionInMap(new Vector2(0, (mMG.getScene().getSpaceParser().getScreenSize().y - getMapSize().y) / 2));
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
