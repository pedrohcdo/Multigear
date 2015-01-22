package multigear.mginterface.graphics.drawable.tilemap;

import multigear.general.utils.Vector2;

/**
 * 
 * @author user
 * 
 */
public interface TileMapModel {

	/** Add layer if support and return layer id or 0 if fail */
	public TileMapLayer addLayer(final Vector2 rowsSize);
	
	/** Clear layers if support */
	public void clearLayers();
	
	/** Get Layers */
	public TileMapLayer getLayer(final int layerId);
	
	/** Get Layers Size */
	public int getLayersCount();
	
	/** Get tilesize */
	public Vector2 getTileSize();
	
	/** Get Map Size */
	public Vector2 getMapSize();
	
	/** Get Tile Info */
	public TileInfo getTileInfo(final int tileId);
}
