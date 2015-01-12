package multigear.exts.tmx;

import java.io.IOException;
import java.io.InputStream;

import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.drawable.tilemap.TileInfo;
import multigear.mginterface.graphics.drawable.tilemap.TileMapLayer;
import multigear.mginterface.graphics.drawable.tilemap.TileMapModel;
import multigear.mginterface.graphics.drawable.tilemap.Tileset;
import multigear.mginterface.graphics.opengl.texture.Loader;
import multigear.mginterface.graphics.opengl.texture.Texture;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AssetManager;
import android.util.Xml;

/**
 * TMX TilemapModel ext
 * @author user
 *
 */
final public class TMXTileMapModel implements TileMapModel {
	
	/**
	 * Tileset Temp
	 * @author user
	 *
	 */
	final static private class TilesetTemp {

		// Private Variables
		protected int mStartId;
		protected Vector2 mTileSize = new Vector2(0, 0);
		protected Vector2 mSpaceMargin = new Vector2(0, 0);
		protected Tileset mTileset = null;
	}

	// Private Variables
	protected Vector2 mMapSize;
	protected Vector2 mTileSize;
	protected TilesetTemp[] mTempTilesets = new TilesetTemp[0];
	protected TileMapLayer[] mLayers = new TileMapLayer[0];
	
	/**
	 * Constructor
	 */
	private TMXTileMapModel() {
	}

	/**
	 * Add layer
	 */
	@Override
	final public TileMapLayer addLayer(final Vector2 rowsSize) {
		TileMapLayer layer = new TileMapLayer(rowsSize);
		final TileMapLayer[] layers = mLayers;
		mLayers = new TileMapLayer[layers.length + 1];
		System.arraycopy(layers, 0, mLayers, 0, layers.length);
		mLayers[mLayers.length - 1] = layer;
		return layer;
	}
	
	/**
	 * Clear Layers
	 */
	@Override
	final public void clearLayers() {
		mLayers = new TileMapLayer[0];
	}
	
	/**
	 * Return Map size
	 * 
	 * @return
	 */
	@Override
	final public Vector2 getMapSize() {
		return mMapSize;
	}

	/**
	 * Return Tile size
	 */
	@Override
	final public Vector2 getTileSize() {
		return mTileSize.clone();
	}
	
	/**
	 * Get Layer
	 */
	@Override
	final public TileMapLayer getLayer(final int layerId) {
		return mLayers[layerId];
	}
	
	/**
	 * Get Layers size
	 */
	@Override
	final public int getLayersSize() {
		return mLayers.length;
	}
	
	/**
	 * Tile Info
	 * 
	 * @param tileId
	 * @return
	 */
	@Override
	final public TileInfo getTileInfo(final int tileId) {
		TileInfo info = null;
		int count = 0;
		for(final TilesetTemp tilesetTemp : mTempTilesets) {
			Tileset tileset = tilesetTemp.mTileset;
			if(tileset == null)
				throw new IllegalArgumentException("This tile does not prepared.");
			if(tileId >= tileset.getStartId()) {
				info = new TileInfo(tileset, tileId - tileset.getStartId(), count);
			}
			count++;
		}
		if(info == null)
			throw new IllegalArgumentException("This tile does not exist.");
		return info;
	}
	
	/**
	 * Prepare Tileset
	 * 
	 * @param tilesetId
	 * @param resId
	 */
	final public void prepareTileset(final int tilesetId, final int resId, final Loader loader) {
		if(tilesetId >= mTempTilesets.length)
			throw new ArrayIndexOutOfBoundsException();
		TilesetTemp tilesetTemp = mTempTilesets[tilesetId];
		Texture texture =  loader.loadTileset(resId, tilesetTemp.mTileSize, (int)tilesetTemp.mSpaceMargin.x, (int)tilesetTemp.mSpaceMargin.y);
		tilesetTemp.mTileset  = new Tileset(texture, tilesetTemp.mStartId, tilesetTemp.mTileSize, (int)tilesetTemp.mSpaceMargin.x, (int)tilesetTemp.mSpaceMargin.y);
	}

	/**
	 * 
	 * @param assetManager
	 * @param fileName
	 */
	final private static InputStream openStream(AssetManager assetManager,
			final String fileName) throws IOException {
		return assetManager.open(fileName);
	}

	/**
	 * 
	 */
	final private static void closeStream(final InputStream input) throws IOException {
		input.close();
	}

	/**
	 * Read TileMap
	 * 
	 * @param assetManager
	 * @param fileName
	 * @param format
	 * @return
	 */
	final static public TMXTileMapModel readFromAsset(AssetManager assetManager, final String fileName) {
		try {
			InputStream stream = openStream(assetManager, fileName);
			TMXTileMapModel model = TmxXmlReader.reader(stream);
			closeStream(stream);
			return model;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Tmx Xml Reader
	 * 
	 * @author user
	 * 
	 */
	final private static class TmxXmlReader {

		/**
		 * Constructor
		 * 
		 * @param input
		 */
		final static private TMXTileMapModel reader(final InputStream input) throws XmlPullParserException, IOException {
			TMXTileMapModel tile = new TMXTileMapModel();
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(input, null);
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "map");
			readMapAttributes(parser, tile);
			parser.nextTag();
			int event = 0;
			//
			while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					String name = parser.getName();
					if (name.equalsIgnoreCase("tileset")) {
						readTileset(parser, tile);
					} else if (name.equalsIgnoreCase("layer")) {
						readLayer(parser, tile);
					}
					// Skip sub tags
					skipTag(parser);
				}
				parser.next();
			}
			return tile;
		}

		/**
		 * Skip tag
		 * 
		 * @param parser
		 */
		final static private void skipTag(final XmlPullParser parser)
				throws XmlPullParserException, IOException {
			if (parser.getEventType() == XmlPullParser.END_TAG)
				return;
			int depth = parser.getDepth();
			while (true) {
				parser.next();
				if (parser.getDepth() == depth
						&& parser.getEventType() == XmlPullParser.END_TAG)
					break;
			}
		}

		/**
		 * Read Map Attributes
		 * 
		 * @param parser
		 * @param tile
		 */
		final static private void readMapAttributes(final XmlPullParser parser, final TMXTileMapModel tile) {
			int width = Integer.parseInt(parser.getAttributeValue(null, "width"));
			int height = Integer.parseInt(parser.getAttributeValue(null, "height"));
			int tileWidth = Integer.parseInt(parser.getAttributeValue(null, "tilewidth"));
			int tileHeight = Integer.parseInt(parser.getAttributeValue(null, "tileheight"));
			tile.mMapSize = new Vector2(width, height);
			tile.mTileSize = new Vector2(tileWidth, tileHeight);
		}

		/**
		 * Read Tilemap
		 * 
		 * @param pasrer
		 * @param tile
		 */
		final static private void readTileset(final XmlPullParser parser, final TMXTileMapModel tile) {
			int firstId = Integer.parseInt(parser.getAttributeValue(null,"firstgid"));
			int tileWidth = Integer.parseInt(parser.getAttributeValue(null, "tilewidth"));
			int tileHeight = Integer.parseInt(parser.getAttributeValue(null, "tileheight"));
			String spacingAttr = parser.getAttributeValue(null, "spacing");
			String marginAttr = parser.getAttributeValue(null, "margin");
			int space = 0;
			int margin = 0;
			if(spacingAttr != null)
				space = Integer.parseInt(spacingAttr);
			if(marginAttr != null)
				margin = Integer.parseInt(marginAttr);
			final TilesetTemp tileset = new TilesetTemp();
			tileset.mStartId = firstId;
			tileset.mTileSize = new Vector2(tileWidth, tileHeight);
			tileset.mSpaceMargin = new Vector2(space, margin);
			// Add new Layer
			final TilesetTemp[] layers = tile.mTempTilesets;
			tile.mTempTilesets = new TilesetTemp[layers.length + 1];
			System.arraycopy(layers, 0, tile.mTempTilesets, 0, layers.length);
			tile.mTempTilesets[tile.mTempTilesets.length - 1] = tileset;
		}

		/**
		 * Read Layer
		 * 
		 * @param parser
		 * @param tile
		 */
		final static private void readLayer(final XmlPullParser parser, final TMXTileMapModel tile) throws XmlPullParserException, IOException {
			// Layer Attributes
			int layerWidth = Integer.parseInt(parser.getAttributeValue(null, "width"));
			int layerHeight = Integer.parseInt(parser.getAttributeValue(null, "height"));
			final TileMapLayer layer = new TileMapLayer(new Vector2(layerWidth, layerHeight));
			int depth = parser.getDepth();
			int event = 0;
			boolean data = false;
			int count = 0;
			// Read tilemap
			while (!(((event = parser.getEventType()) == XmlPullParser.END_TAG) && (parser
					.getDepth() == depth))) {
				if (event == XmlPullParser.START_TAG) {
					String name = parser.getName();
					if (name.equalsIgnoreCase("data")) {
						data = true;
					} else if (name.equalsIgnoreCase("tile") && data) {
						int tileId = Integer.parseInt(parser.getAttributeValue(
								null, "gid"));
						if (count >= layer.length()) {
							throw new RuntimeException(
									"Unable to load the map, there is a much greater definition than the tiles map size.");
						} else {
							layer.setTile(count++, tileId);
						}
					}
				} else if (event == XmlPullParser.END_TAG) {
					if (parser.getName().equalsIgnoreCase("data"))
						data = false;
				}
				parser.next();
			}
			// insert Layer
			final TileMapLayer[] layers = tile.mLayers;
			tile.mLayers = new TileMapLayer[layers.length + 1];
			System.arraycopy(layers, 0, tile.mLayers, 0, layers.length);
			tile.mLayers[tile.mLayers.length - 1] = layer;
		}
	}
}
