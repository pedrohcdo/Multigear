package multigear.mginterface.graphics.drawable.tilemap;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.texture.Texture;
import android.graphics.RectF;
import android.util.Log;

/**
 * Tileinfo
 * 
 * @author user
 *
 */
final public class TileInfo {
	
	// Private Variables
	final protected Tileset mTileset;
	final protected int mId;
	final protected int mTilesetId;
	
	/**
	 * Constructor
	 * 
	 * @param tileset
	 * @param id
	 */
	public TileInfo(final Tileset tileset, final int id, final int tilesetId) {
		mTileset = tileset;
		mId = id;
		mTilesetId = tilesetId;
	}
	
	/**
	 * Get Tileset Id
	 * @return
	 */
	final public int getId() {
		return mId;
	}
	
	/**
	 * Get Tileset Id
	 * 
	 * @return
	 */
	final public int getTilesetId() {
		return mTilesetId;
	}
	
	/**
	 * Get Tile Texture
	 * @return
	 */
	final public Texture getTexture() {
		return mTileset.mTexture;
	}
	
	/**
	 * Get Tile rect area
	 * @return
	 */
	final public RectF getTextureArea() {
		final float tilesetWidth = mTileset.mTexture.getSize().x;
		final float tileWidth = mTileset.mTileSize.x;
		final float tileHeight = mTileset.mTileSize.y;
		final int tileQW = (int)((tilesetWidth+2) / (tileWidth+2));
		final int x = mId % tileQW;
		final int y = mId / tileQW;
		final float dx = x * (tileWidth + 2);
		final float dy = y * (tileHeight + 2);
		return new RectF(dx, dy, (dx + tileWidth), (dy + tileHeight));
	}
	
	/**
	 * Get Tile Size
	 * @return
	 */
	final Vector2 getSize() {
		return new Vector2(mTileset.mTileSize.x, mTileset.mTileSize.y);
	}
}
