package com.org.multigear.exts.tmx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.drawable.tilemap.TileInfo;
import com.org.multigear.mginterface.graphics.drawable.tilemap.TileMapLayer;
import com.org.multigear.mginterface.graphics.drawable.tilemap.TileMapModel;
import com.org.multigear.mginterface.graphics.drawable.tilemap.Tileset;
import com.org.multigear.mginterface.graphics.opengl.texture.Loader;
import com.org.multigear.mginterface.graphics.opengl.texture.Texture;

import android.content.res.AssetManager;
import android.graphics.Path;
import android.provider.MediaStore.Files;
import android.util.Log;
import android.util.Xml;

/**
 * TMX TilemapModel ext
 * @author user
 *
 */
final public class TMXTileMapModel implements TileMapModel {

	// Private Variables
	protected Vector2 mMapSize;
	protected Vector2 mTileSize;
	protected Tileset[] mTilesets = new Tileset[0];
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
	final public int getLayersCount() {
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
		for(final Tileset tileset : mTilesets) {
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
	final static public TMXTileMapModel readFromAsset(AssetManager assetManager, final String fileName, final Loader loader) {
		try {
			InputStream stream = openStream(assetManager, fileName);
			TMXTileMapModel model = TmxXmlReader.reader(stream, assetManager, loader);
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
		final static private TMXTileMapModel reader(final InputStream input, final AssetManager asset, final Loader loader) throws XmlPullParserException, IOException {
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
						readTileset(parser, tile, asset, loader);
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
		final static private void readTileset(final XmlPullParser parser, final TMXTileMapModel tile, final AssetManager asset, final Loader loader) throws XmlPullParserException, IOException  {
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
			// Get filename
			String filename = null;
			int depth = parser.getDepth();
			
			while(true) {
				parser.next();
				
				if(parser.getDepth() == depth && parser.getEventType() == XmlPullParser.END_TAG)
					break;
				
				if(parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equalsIgnoreCase("image")) {
					String path = parser.getAttributeValue(null, "source");
					File file = new File(path);
					filename = file.getName();
				}
			}
			// Get Texture
			if(filename == null) 
				throw new RuntimeException("It was not indicated a texture for the tileset.");
			//
			asset.open(filename).close();
			// Load Tileset
			Texture texture = loader.loadTilesetFromAsset(filename, new Vector2(tileWidth, tileHeight), space, margin);
			Tileset tileset = new Tileset(texture, firstId, new Vector2(tileWidth, tileHeight), space, margin);
			// Set Tileset
			final Tileset[] tilesets = tile.mTilesets;
			tile.mTilesets = new Tileset[tilesets.length + 1];
			System.arraycopy(tilesets, 0, tile.mTilesets, 0, tilesets.length);
			tile.mTilesets[tilesets.length] = tileset;
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
			while (!(((event = parser.getEventType()) == XmlPullParser.END_TAG) && (parser.getDepth() == depth))) {
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
