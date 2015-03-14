package multigear.mginterface.graphics.drawable.tilemap;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.animations.AnimationSet;
import multigear.mginterface.graphics.animations.AnimationStack;
import multigear.mginterface.graphics.opengl.drawer.BlendFunc;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Component;
import multigear.mginterface.scene.components.receivers.Drawable;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * TileMap
 * 
 * @author user
 *
 */
public class TileMap implements Drawable, Component {
	
	/**
	 * 
	 * @author user
	 *
	 */
	final private class LayerInfo {
		
		/**
		 * Constructor
		 * 
		 * @author user
		 *
		 */
		final private class Buffer {
			
			// Private Variables
			final private FloatBuffer mElementsVertex;
			final private FloatBuffer mTextureVertex;
			final private Texture mTexture;
			final private int mTilesetId;
			final private int mTilesQuantity;
			
			/**
			 * Constructor
			 */
			private Buffer(final int quantity, final Texture texture, final int id) {
				mTilesQuantity = quantity;
				mElementsVertex = GeneralUtils.createFloatBuffer(quantity * 12);
				mTextureVertex = GeneralUtils.createFloatBuffer(quantity * 12);
				mTexture = texture;
				mTilesetId = id;
			}
		}
		
		// Private Variables
		final List<Buffer> mBuffers = new ArrayList<TileMap.LayerInfo.Buffer>();
		final Vector2 mLayerSize;
		
		/**
		 * COnstructor
		 */
		private LayerInfo(final Vector2 size) {
			mLayerSize = size;
		}
		
		/**
		 * Prepare Buffer
		 * 
		 * @param tilesetId
		 */
		final private void prepareBuffer(TileInfo info) {
			for(Buffer buffer : mBuffers) {
				if(buffer.mTilesetId == info.mTilesetId) {
					return;
				}
			}
			final int layerWidth = (int)mLayerSize.x;
			final int layerHeight = (int)mLayerSize.y;
			mBuffers.add(new Buffer(layerWidth*layerHeight, info.mTileset.mTexture, info.mTilesetId));
		}
		
		/**
		 * Get Buffer
		 * @param info
		 * @return
		 */
		final private Buffer getBuffer(TileInfo info) {
			for(Buffer buffer : mBuffers) {
				if(buffer.mTilesetId == info.mTilesetId) {
					return buffer;
				}
			}
			throw new RuntimeException("Unexpected error occurred during the buffer TileMap.");
		}
		
		/**
		 * Pack buffers
		 */
		final private void pack() {
			for(Buffer buffer : mBuffers) {
				buffer.mElementsVertex.position(0);
				buffer.mTextureVertex.position(0);
			}
		}
	}
	
	
	// Final Private Variables
	final private float mFinalTransformation[] = new float[] { 0, 0, 0, 0, 0, 0, 0, 0, 1 };
	final private AnimationStack mAnimationStack;
	
	// Private Variables
	protected Rect mViewport;
	private LayerInfo mLayersInfo[] = new LayerInfo[0];
	
	// Public Variables
	protected Vector2 mScale = new Vector2(1, 1);
	protected Vector2 mPosition = new Vector2(0, 0);
	protected Vector2 mCenter = new Vector2(0, 0);
	protected float mAngle = 0;
	protected float mOpacity = 1;
	protected boolean mTouchable = true;
	protected boolean mFixedSpace = false;
	protected boolean mMirror[] = { false, false };
	protected int mZ = 0;
	protected int mId = 0;
	protected BlendFunc mBlendFunc = BlendFunc.ONE_MINUS_SRC_ALPHA;
	
	/**
	 * Constructor
	 */
	public TileMap() {
		mAnimationStack = new AnimationStack();
		mViewport = null;
		mFixedSpace = false;
	}
	

	/**
	 * Set Viewport
	 * 
	 * @param left
	 *            Left
	 * @param top
	 *            Top
	 * @param width
	 *            Width
	 * @param height
	 *            Height
	 */
	final public void setViewport(final int left, final int top,final int width, final int height) {
		mViewport = new Rect(left, top, width, height);
	}

	/**
	 * Retorna a pilha de animações
	 * 
	 * @return
	 */
	final public AnimationStack getAnimationStack() {
		return mAnimationStack;
	}
	
	/**
	 * Add Layer Info
	 * @return
	 */
	final private LayerInfo addLayerInfo(final Vector2 size) {
		LayerInfo layerInfo = new LayerInfo(size);
		final LayerInfo layers[] = mLayersInfo;
		mLayersInfo = new LayerInfo[layers.length+1];
		System.arraycopy(layers, 0, mLayersInfo, 0, layers.length);
		mLayersInfo[layers.length] = layerInfo;
		return layerInfo;
	}
	
	/**
	 * Add model to TileMap
	 */
	final public void addModel(final TileMapModel model) {		
		int mapTileWidth = (int)model.getTileSize().x;
		int mapTileHeight = (int)model.getTileSize().y;
		
		for(int i=0; i<model.getLayersCount(); i++) {
			
			final TileMapLayer layer = model.getLayer(i);
			
			int width = (int)Math.min(layer.mWidth, model.getMapSize().x);
			int height = (int)Math.min(layer.mHeight, model.getMapSize().y);
			
			LayerInfo layerInfo = addLayerInfo(new Vector2(width, height));
			
			for(int x=0; x<width; x++) {
				for(int y=0; y<height; y++) {
					final int tileId = layer.mData[x+y*width];
					if(tileId == 0)
						continue;
					
					final TileInfo tileInfo = model.getTileInfo(tileId);
					
					// Prepare Buffer
					layerInfo.prepareBuffer(tileInfo);
					
					// Get Buffer
					LayerInfo.Buffer buff = layerInfo.getBuffer(tileInfo);
					
					// Get Rect Area
					RectF textureArea = tileInfo.getTextureArea();
					RectF textureAreaU = GeneralUtils.divideRect(textureArea, tileInfo.getTexture().getSize());
					
					Vector2 tileSize = tileInfo.getSize();
					final int tileWidth = (int)tileSize.x;
					final int tileHeight = (int)tileSize.y;
					
					// Put Element buffer
					buff.mElementsVertex.put(x * mapTileWidth);
					buff.mElementsVertex.put(y * mapTileHeight);
					buff.mElementsVertex.put(x * mapTileWidth + tileWidth);
					buff.mElementsVertex.put(y * mapTileHeight);
					buff.mElementsVertex.put(x * mapTileWidth + tileWidth);
					buff.mElementsVertex.put(y * mapTileHeight + tileHeight);
					buff.mElementsVertex.put(x * mapTileWidth);
					buff.mElementsVertex.put(y * mapTileHeight);
					buff.mElementsVertex.put(x * mapTileWidth);
					buff.mElementsVertex.put(y * mapTileHeight + tileHeight);
					buff.mElementsVertex.put(x * mapTileWidth + tileWidth);
					buff.mElementsVertex.put(y * mapTileHeight + tileHeight);
					
					// Put Texture Buffer
					buff.mTextureVertex.put(textureAreaU.left);
					buff.mTextureVertex.put(textureAreaU.top);
					buff.mTextureVertex.put(textureAreaU.right);
					buff.mTextureVertex.put(textureAreaU.top);
					buff.mTextureVertex.put(textureAreaU.right);
					buff.mTextureVertex.put(textureAreaU.bottom);
					buff.mTextureVertex.put(textureAreaU.left);
					buff.mTextureVertex.put(textureAreaU.top);
					buff.mTextureVertex.put(textureAreaU.left);
					buff.mTextureVertex.put(textureAreaU.bottom);
					buff.mTextureVertex.put(textureAreaU.right);
					buff.mTextureVertex.put(textureAreaU.bottom);
				}
			}
			
			layerInfo.pack();
		}
	}
	
	/**
	 * Clear all models added to this tilemap
	 */
	final public void clearModels() {
		mLayersInfo = new LayerInfo[0];
	}
	
	/**
	 * Invert in Vertical
	 * 
	 * @param inverted
	 */
	final public void setMirror(final boolean mirrorX, final boolean mirrorY) {
		mMirror[0] = mirrorX;
		mMirror[1] = mirrorY;
	}

	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final Vector2 scale) {
		mScale = scale.clone();
	}

	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final float scaleX, final float scaleY) {
		mScale = new Vector2(scaleX, scaleY);
	}

	/**
	 * Set Scale
	 * 
	 * @param scale
	 *            Float Scale
	 */
	final public void setScale(final float scale) {
		mScale = new Vector2(scale, scale);
	}

	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link Vector2} Position
	 */
	final public void setPosition(final Vector2 position) {
		mPosition = position.clone();
	}

	/**
	 * Set depth
	 * 
	 * @param z Depth
	 */
	public void setZ(final int z) {
		mZ = z;
	}
	
	/**
	 * Set Blend Func
	 * 
	 * @param blendFunc
	 */
	final public void setBlendFunc(final BlendFunc blendFunc) {
		mBlendFunc = blendFunc;
	}

	/**
	 * Set identifier
	 * 
	 * @param id Identifier
	 */
	public void setId(int id) {
		mId = id;
	}

	/**
	 * Set drawable opacity
	 * 
	 * @param opacity
	 *            [in] Opacity
	 */
	final public void setOpacity(final float opacity) {
		mOpacity = Math.max(Math.min(opacity, 1.0f), 0.0f);
	}

	/**
	 * Set center .
	 * 
	 * @param center
	 *            {@link Vector2} Center
	 */
	final public void setCenter(final Vector2 center) {
		mCenter = center.clone();
	}

	/**
	 * Set Angle.
	 * 
	 * @param angle
	 *            {@link Vector2} Angle
	 */
	final public void setAngle(final float angle) {
		mAngle = angle;
	}

	/**
	 * Set Touchable.
	 * 
	 * @param touchable
	 *            Boolean Touchable
	 */
	final public void setTouchable(final boolean touchable) {
		mTouchable = touchable;
	}

	/**
	 * Set Fixed Space.
	 * 
	 * @param fixed
	 *            Boolean Fixed
	 */
	final public void setFixedSpace(final boolean fixed) {
		mFixedSpace = fixed;
	}

	/**
	 * Invert in Vertical
	 * 
	 * @param inverted
	 */
	final public boolean[] getMirror() {
		return mMirror.clone();
	}

	/**
	 * Get Viewport
	 */
	final public Rect getViewport() {
		return mViewport;
	}

	/**
	 * Get Scale
	 */
	final public Vector2 getScale() {
		return mScale.clone();
	}

	/**
	 * Return Position
	 * 
	 * @return {@link Vector2} Position
	 */
	final public Vector2 getPosition() {
		return mPosition.clone();
	}

	/**
	 * Return Real Position
	 * <p>
	 * Get Position with animations modify.
	 * 
	 * @return {@link Vector2} Position
	 */
	final public Vector2 getRealPosition() {
		final AnimationSet animationSet = getAnimationStack().prepareAnimation().animate();
		Vector2 position = mPosition.clone();
		position.sum(animationSet.getPosition());
		return position;
	}

	/**
	 * Get depth
	 * 
	 * @return Return Depth
	 */
	public int getZ() {
		return mZ;
	}

	/**
	 * Get Blend Func
	 * 
	 * @return Get Blend Func
	 */
	final public BlendFunc getBlendFunc() {
		return mBlendFunc;
	}
	
	/**
	 * Get identifier
	 * 
	 * @return Return Indentifier
	 */
	public int getId() {
		return mId;
	}
	
	/**
	 * Get drawable opacity
	 * 
	 * @return Return drawable opacity
	 */
	final public float getOpacity() {
		return mOpacity;
	}

	/**
	 * Get center .
	 * 
	 * @return {@link Vector2} Center
	 */
	final public Vector2 getCenter() {
		return mCenter.clone();
	}

	/**
	 * Get Angle.
	 * 
	 * @return {@link Vector2} Angle
	 */
	final public float getAngle() {
		return mAngle;
	}

	/**
	 * Get Touchable.
	 * 
	 * @return Boolean Touchable
	 */
	final public boolean getTouchable() {
		return mTouchable;
	}

	/**
	 * Get Fixed Space.
	 * 
	 * @param fixed
	 *            Boolean Fixed
	 */
	final public boolean getFixedSpace() {
		return mFixedSpace;
	}

	/*
	 * Prepara para desenho. Utiliza AnimationStack.
	 */
	@Override
	public void draw(final Drawer drawer) {

		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.prepareAnimation().animate();
		
		// Get final Opacity
		final float opacity = animationSet.getOpacity() * getOpacity();
		
		// Not Update
		if (opacity <= 0)
			return;
		
		// Get Infos
		final Vector2 scale = Vector2.scale(mScale, animationSet.getScale());
		final Vector2 translate = animationSet.getPosition();
		final float rotate = mAngle + animationSet.getRotation();

		// Calc values
		final float ox = mCenter.x * scale.x;
		final float oy = mCenter.y * scale.y;
		float sx = scale.x;
		float sy = scale.y;
		
		final float tX = mPosition.x + translate.x;
		final float tY = mPosition.y + translate.y;
		float six = -ox;
		float siy = -oy;

		if (mMirror[0]) {
			six += sx;
			sx *= -1;
		}
		if (mMirror[1]) {
			siy += sy;
			sy *= -1;
		}


		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();

		// Push Matrix
		matrixRow.push();

		
		// Translate and Rotate Matrix with correction
		float rad = (float) GeneralUtils.degreeToRad(rotate);
		float c = (float) Math.cos(-rad);
		float s = (float) Math.sin(-rad);
		mFinalTransformation[0] = sx * c;
		mFinalTransformation[1] = sy * s;
		mFinalTransformation[2] = c * six + s * siy + tX;
		mFinalTransformation[3] = -sx * s;
		mFinalTransformation[4] = sy * c;
		mFinalTransformation[5] = -s * six + c * siy + tY;
		matrixRow.preConcatf(mFinalTransformation);

		// Prepare blend mod
		drawer.begin();
		drawer.setOpacity(opacity);
		drawer.setBlendFunc(mBlendFunc);
		drawer.snip(mViewport);
		
		// Draw Layers and sub layers
		for(LayerInfo info : mLayersInfo) {
			for(LayerInfo.Buffer buff : info.mBuffers) {
				// Set Texture
				drawer.setTexture(buff.mTexture);
				drawer.setElementVertex(buff.mElementsVertex);
				drawer.setTextureVertex(buff.mTextureVertex);
				// Draw tilemap
				drawer.drawTileMap(buff.mTilesQuantity * 6);
			}
		}
		
		// End Drawer
		drawer.end();
		
		// pop
		matrixRow.pop();
	}
}
