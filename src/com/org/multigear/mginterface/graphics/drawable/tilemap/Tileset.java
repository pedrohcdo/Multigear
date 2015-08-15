package com.org.multigear.mginterface.graphics.drawable.tilemap;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.opengl.texture.Texture;

/**
 * Layer
 * 
 * @author user
 * 
 */
final public class Tileset {

	// Private Variables
	protected int mStartId;
	protected Vector2 mTileSize = new Vector2(0, 0);
	protected Vector2 mSpaceMargin = new Vector2(0, 0);
	protected Texture mTexture;
	
	/**
	 * Constructor
	 * 
	 * @param texture
	 * @param startId
	 * @param tileSize
	 * @param margin
	 * @param grid
	 */
	public Tileset(final Texture texture, final int startId, final Vector2 tileSize, final int grid, final int margin) {
		mTexture = texture;
		mTileSize = tileSize.clone();
		mSpaceMargin = new Vector2(grid, margin);
		mStartId = startId;
	}
	
	/**
	 * Get Start id
	 * 
	 * @return
	 */
	final public int getStartId() {
		return mStartId;
	}

	/**
	 * Get Tile Size
	 * 
	 * @return
	 */
	final public Vector2 getTileSize() {
		return mTileSize.clone();
	}
	
	/**
	 * Get grid
	 * 
	 * @return
	 */
	final public int getGrid() {
		return (int)mSpaceMargin.x;
	}
	
	/**
	 * Get Margin
	 * 
	 * @return
	 */
	final public int getMargin() {
		return (int)mSpaceMargin.y;
	}
	
	/**
	 * Get Texture
	 * 
	 * @return
	 */
	final public Texture getTexture() {
		return mTexture;
	}
}
