package multigear.mginterface.graphics.opengl.drawer;

import java.util.ArrayList;
import java.util.List;

import multigear.general.utils.Color;
import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.opengl.Renderer;
import multigear.mginterface.graphics.opengl.programs.RepeatTextureRenderer;
import multigear.mginterface.graphics.opengl.programs.StretchTextureRenderer;
import multigear.mginterface.graphics.opengl.programs.TransitionTextureRenderer;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import multigear.mginterface.scene.SpaceParser;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Drawer Container
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class TextureContainer {
	
	/**
	 * Blt Group used for optimize
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final public static class BltGroup {
		
		/**
		 * Group
		 * 
		 * @author PedroH, RaphaelB
		 *
		 * Property Createlier.
		 */
		final private class GroupSet {
			
			// Final Private Variables
			final RectF mSource;
			final RectF mDest;
			
			/**
			 * Constructor
			 */
			public GroupSet(final RectF src, final RectF dst) {
				mSource = src;
				mDest = dst;
			}
		}
		
		// Final Private Variables
		final List<GroupSet> mGroup = new ArrayList<GroupSet>();
		
		/**
		 * Blt
		 * 
		 * @param src
		 * @param dst
		 */
		final public void blt(Rect src, Rect dst) {
			mGroup.add(new GroupSet(new RectF(src), new RectF(dst)));
		}
		
		/**
		 * Blt
		 * 
		 * @param src
		 * @param dst
		 */
		final public void blt(RectF src, RectF dst) {
			mGroup.add(new GroupSet(src, dst));
		}
		
		/**
		 * Clear
		 */
		final public void clear() {
			mGroup.clear();
		}
	}
	
	// Final Private Variables
	final private Drawer mDrawer;
	final private float[] mFloatBounds = new float[4];
	final private SpaceParser mSpaceParser;
	
	// Private Variables
	private Vector2 mRecipientSize;
	private Texture mRecipientTexture;
	private Color mBlendColor = Color.WHITE;
	private Scene mMainScene;
	
	/**
	 * Conainer
	 */
	protected TextureContainer(final Drawer drawer, final Scene mainScene) {
		mDrawer = drawer;
		mMainScene = mainScene;
		mSpaceParser = mMainScene.getSpaceParser();
	}
	
	/**
	 * Set Blend Color
	 * 
	 * @param rgba
	 */
	final public void setBlendColor(final Color color) {
		mBlendColor = color;
	}
	
	/**
	 * Prepare Container for Draw
	 * <p>
	 * @param recipientTexture The recipient Texture used for draw
	 * @param recipientSize Size of the recipient
	 * @param opacity Pre opacity used for draw
	 */
	final protected void prepare(final Texture recipientTexture, final Vector2 recipientSize) {
		mRecipientTexture = recipientTexture;
		mRecipientSize = recipientSize;
		mBlendColor = Color.WHITE;
	}
	
	/**
	 * Prepare Container for Unsized Draw
	 * <p>
	 * @param recipientSize Size of the recipient
	 * @param opacity Pre opacity used for draw
	 */
	final protected void prepareUnsized(final Texture recipientTexture) {
		mRecipientTexture = recipientTexture;
		mBlendColor = Color.WHITE;
	}
	
	/**
	 * Get recipient Texture
	 * @return
	 */
	final public Texture getRecipientTexture() {
		return mRecipientTexture;
	}
	
	/**
	 * Set Recipient Size
	 * @param size
	 */
	final protected void setRecipientSize(final Vector2 size) {
		mRecipientSize = size.clone();
	}
	
	/**
	 * Get recipient Size
	 * @return
	 */
	final public Vector2 getRecipientSize() {
		return mRecipientSize.clone();
	}
	
	/**
	 * Blit texture recipient to destination.
	 */
	final public void blt(final Rect src, final Rect dst) {
		final float[] elementsBuffer = GeneralUtils.mapRectToFloat(dst, mRecipientSize);
		final float[] textureBuffer = GeneralUtils.mapRectToFloat(src, mRecipientTexture.getSize());
		StretchTextureRenderer renderer = (StretchTextureRenderer)mDrawer.begin(Renderer.STRETCH_TEXTURE_RENDERER, mBlendColor);
		renderer.setBuffers(elementsBuffer, textureBuffer);
		renderer.render();
	}
	
	/**
	 * Blit texture recipient to destination.
	 */
	final public void blt(final RectF src, final RectF dst) {
		final float[] elementsBuffer = GeneralUtils.mapRectToFloat(dst, mRecipientSize);
		final float[] textureBuffer = GeneralUtils.mapRectToFloat(src, mRecipientTexture.getSize());
		StretchTextureRenderer renderer = (StretchTextureRenderer)mDrawer.begin(Renderer.STRETCH_TEXTURE_RENDERER, mBlendColor);
		renderer.setBuffers(elementsBuffer, textureBuffer);
		renderer.render();
	}
	
	/**
	 * Blit texture recipient to destination.
	 */
	final public void bltUnsized(final BltGroup bltGroup, final Vector2 textureSize) {
		final int size = bltGroup.mGroup.size();
		final float[] elementsBufferPack = new float[size * 6 * 2];
		final float[] textureBufferPack = new float[size * 6 * 2];
		final float tW = textureSize.x;
		final float tH = textureSize.y;
		for(int i=0; i<bltGroup.mGroup.size(); i++) {
			BltGroup.GroupSet group = bltGroup.mGroup.get(i);
			RectF src = group.mSource;
			RectF dst = group.mDest;
			// Incr
			int src_incr = i * 12;
			int dst_incr = i * 12;
			// Left/ Bottom
			textureBufferPack[src_incr++] = src.left / tW;
			textureBufferPack[src_incr++] = src.bottom / tH;
			elementsBufferPack[dst_incr++] = dst.left;
			elementsBufferPack[dst_incr++] = dst.bottom;
			// Left/ Top
			textureBufferPack[src_incr++] = src.left / tW;
			textureBufferPack[src_incr++] = src.top / tH;
			elementsBufferPack[dst_incr++] = dst.left;
			elementsBufferPack[dst_incr++] = dst.top;
			// Right/ Top
			textureBufferPack[src_incr++] = src.right / tW;
			textureBufferPack[src_incr++] = src.top / tH;
			elementsBufferPack[dst_incr++] = dst.right;
			elementsBufferPack[dst_incr++] = dst.top;
			// Right/ Top
			textureBufferPack[src_incr++] = src.right / tW;
			textureBufferPack[src_incr++] = src.top / tH;
			elementsBufferPack[dst_incr++] = dst.right;
			elementsBufferPack[dst_incr++] = dst.top;
			// Right/ Bottom
			textureBufferPack[src_incr++] = src.right / tW;
			textureBufferPack[src_incr++] = src.bottom / tH;
			elementsBufferPack[dst_incr++] = dst.right;
			elementsBufferPack[dst_incr++] = dst.bottom;
			// Left/ Bottom
			textureBufferPack[src_incr++] = src.left / tW;
			textureBufferPack[src_incr++] = src.bottom / tH;
			elementsBufferPack[dst_incr++] = dst.left;
			elementsBufferPack[dst_incr++] = dst.bottom;
		}
		StretchTextureRenderer renderer = (StretchTextureRenderer)mDrawer.begin(Renderer.STRETCH_TEXTURE_RENDERER, mBlendColor);
		renderer.setBuffers(elementsBufferPack, textureBufferPack);
		renderer.renderTriangles(size * 6);
	}
	
	/**
	 * Blit texture recipient to destination with repeat option.
	 */
	final public void bltRepeat(final Rect src, final Rect dst, final boolean horizontalRepeat, final boolean verticalRepeat) {
		final float[] elementsBuffer = GeneralUtils.mapRectToFloat(dst, mRecipientSize);
		final float[] textureBuffer = GeneralUtils.mapRectToFloat(src, mRecipientTexture.getSize());
		GeneralUtils.getFloatBounds(textureBuffer, mFloatBounds);
		RepeatTextureRenderer renderer = (RepeatTextureRenderer)mDrawer.begin(Renderer.REPEAT_TEXTURE_RENDERER, mBlendColor);
		renderer.setBuffers(elementsBuffer, textureBuffer);
		renderer.render(mFloatBounds, mSpaceParser.parseToBase(new Vector2(src.width(), src.height())), horizontalRepeat, verticalRepeat);
	}
	
	/**
	 * Blit texture recipient to destination with repeat option.
	 */
	final public void bltRepeat(final RectF src, final RectF dst, final boolean horizontalRepeat, final boolean verticalRepeat) {
		final float[] elementsBuffer = GeneralUtils.mapRectToFloat(dst, mRecipientSize);
		final float[] textureBuffer = GeneralUtils.mapRectToFloat(src, mRecipientTexture.getSize());
		GeneralUtils.getFloatBounds(textureBuffer, mFloatBounds);
		RepeatTextureRenderer renderer = (RepeatTextureRenderer)mDrawer.begin(Renderer.REPEAT_TEXTURE_RENDERER, mBlendColor);
		renderer.setBuffers(elementsBuffer, textureBuffer);
		renderer.render(mFloatBounds, mSpaceParser.parseToBase(new Vector2(src.width(), src.height())), horizontalRepeat, verticalRepeat);
	}
	
	/**
	 * Fill transition result to destination.
	 * 
	 * @param transtionTexture Transition texture used for execute transition
	 * @param finalTexture Final texture used for interact transtion
	 * @param delataTime Time control used for control transition, 0 is begin and 1 is final.
	 */
	final public void transitionTo(final Texture transtionTexture, final Texture finalTexture, final float deltaTime) {
		TransitionTextureRenderer renderer = (TransitionTextureRenderer)mDrawer.begin(Renderer.TRANSITION_TEXTURE_RENDERER, mBlendColor);
		renderer.setTextures(mRecipientTexture, transtionTexture, finalTexture);
		renderer.render(Math.max(0, Math.min(1.0f, deltaTime)));
	}
	
	/**
	 * Fill texture recipient to destination.
	 */
	final public void fill() {
		StretchTextureRenderer renderer = (StretchTextureRenderer)mDrawer.begin(Renderer.STRETCH_TEXTURE_RENDERER, mBlendColor);
		renderer.setDefaultBuffers();
		renderer.render();
	}
}
