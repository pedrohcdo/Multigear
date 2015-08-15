package com.org.multigear.mginterface.graphics.drawable.tilemap;

import com.org.multigear.general.utils.Vector2;

/**
 * Lauyer
 * 
 * @author user
 * 
 */
final public class TileMapLayer {

	// Private Variables
	final protected int mWidth;
	final protected int mHeight;
	final protected int[] mData;

	/**
	 * Constructor
	 * 
	 * @param size
	 */
	public TileMapLayer(final Vector2 size) {
		mWidth = (int)size.x;
		mHeight = (int)size.y;
		mData = new int[(int) (size.x * size.y)];
	}
	
	/**
	 * Set Tile
	 * 
	 * @param x
	 * @param y
	 */
	public void setTile(int x, int y, int tileId) {
		mData[x+y*mWidth] = tileId;
	}
	
	/**
	 * Set Tile
	 * 
	 * @param row
	 */
	public void setTile(int row, int tileId) {
		mData[row] = tileId;
	}
	
	/**
	 * Get length
	 * @return
	 */
	final public int length() {
		return mData.length;
	}
}