package com.org.multigear.mginterface.graphics.drawable.polygon;

import java.nio.FloatBuffer;

import com.org.multigear.general.utils.Color;
import com.org.multigear.general.utils.GeneralUtils;
import com.org.multigear.general.utils.Vector2;
import com.org.multigear.mginterface.graphics.animations.AnimationSet;
import com.org.multigear.mginterface.graphics.animations.AnimationStack;
import com.org.multigear.mginterface.graphics.opengl.BlendFunc;
import com.org.multigear.mginterface.graphics.opengl.drawer.Drawer;
import com.org.multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import com.org.multigear.mginterface.graphics.opengl.texture.Texture;
import com.org.multigear.mginterface.scene.Component;
import com.org.multigear.mginterface.scene.components.receivers.Drawable;

import android.graphics.Rect;
import android.util.Log;

/**
 * Polygon<br>
 * <br>
 * <b>Note:</b> This class do not support texture animation in AnimationStack
 * animations.
 * 
 * @author user
 * 
 */
final public class Polygon implements Drawable, Component {

	// For Draw
	private float mPreparedOpacity;

	// Final Private Static Variables
	final private static int POLYGON_MODE_NORMAL = 0;
	final private static int POLYGON_MODE_OPTIMIZED_CIRCLE = 1;
	final private static int POLYGON_MODE_OPTIMIZED_ELLIPSE = 2;

	// Final Public Static Variables
	final public static int TEXTURE_MAP_REPEAT = 0;
	final public static int TEXTURE_MAP_STRETCH = 1;
	
	// Final Private Variables
	final private float mFinalTransformation[] = new float[] { 0, 0, 0, 0, 0,0, 0, 0, 1 };
	final private AnimationStack mAnimationStack;

	// Private Variables
	protected Rect mViewport;
	private Texture mTexture;
	private Color mColor = Color.WHITE;
	private int mTextureMapMode = TEXTURE_MAP_STRETCH;
	
	// Public Variables
	protected Vector2 mScale = new Vector2(1, 1);
	protected Vector2 mPosition = new Vector2(0, 0);
	protected Vector2 mCenter = new Vector2(0, 0);
	protected float mAngle = 0;
	protected float mOpacity = 1;
	protected boolean mTouchable = true;
	protected boolean mFixedSpace = false;
	protected boolean mMirror[] = { false, false };
	private BlendFunc mBlendFuncs[] = new BlendFunc[] {BlendFunc.ONE, BlendFunc.ONE_MINUS_SRC_ALPHA, BlendFunc.ONE, BlendFunc.ZERO};
	protected FloatBuffer mVertices = GeneralUtils.createFloatBuffer(0);
	protected FloatBuffer mTextureVertex = GeneralUtils.createFloatBuffer(0);
	protected int mVerticesCount = 0;
	protected int mPolygonMode = POLYGON_MODE_NORMAL;
	protected float mPolygonFloatExtra = 0;
	protected Vector2 mPolygonVectorExtra = new Vector2();
	protected FloatBuffer mPolygonFloatBufferExtra = null;
	private int mZ = 0;
	private int mID = 0;
	private Vector2 mSize = new Vector2();
	
	/**
	 * Constructor
	 * 
	 * @param scene
	 */
	public Polygon() {
		mTexture = null;
		mAnimationStack = new AnimationStack();
		mViewport = null;
		mFixedSpace = false;
	}
	
	/**
	 * Create polygon with same vertices and optimizations of polygon passed on arguments
	 * @param polygon
	 */
	public Polygon(final Polygon polygon) {
		mTexture = null;
		mAnimationStack = new AnimationStack();
		mViewport = null;
		mFixedSpace = false;
		
		// Copy optimizations
		mPolygonMode = polygon.mPolygonMode;
		mPolygonFloatExtra = polygon.mPolygonFloatExtra;
		mPolygonVectorExtra = polygon.mPolygonVectorExtra.clone();
		
		// Copy Data
		addVertices(polygon);
	}

	/**
	 * Create Line
	 * @param start
	 * @param end
	 * @param size
	 * @return
	 */
	final public static Polygon createLine(final Vector2 start, final Vector2 end, final float size)  {
		final float ang = Vector2.angle(start, end);
		final Vector2 move = Vector2.direction(ang + 90, size/2.0f);
		final Polygon line = new Polygon();
		line.addVertice(Vector2.sum(start, move));
		line.addVertice(Vector2.sum(end, move));
		line.addVertice(Vector2.sub(end, move));
		line.addVertice(Vector2.sub(start, move));
		return line;
	}
	
	/**
	 * Create Bounded square
	 * 
	 * @param radius
	 * @return
	 */
	final public static Polygon createRoundedSquare(final float sides, float radius, final float detailDeg) {
		radius = Math.min(radius, sides / 2.0f);
		final double detail = detailDeg * Math.PI / 180.0f;
		final Polygon roundedSquare = new Polygon();
		for (double i = 0; i < Math.PI / 2; i += detail) {
			final float x = (float) (Math.cos(i) * radius) + (sides - radius);
			final float y = (float) (((Math.sin(i) * -1) + 1) * radius);
			roundedSquare.addVertice(x, y);
		}
		for (double i = Math.PI / 2; i < Math.PI; i += detail) {
			final float x = (float) ((Math.cos(i) + 1) * radius);
			final float y = (float) (((Math.sin(i) * -1) + 1) * radius);
			roundedSquare.addVertice(x, y);
		}
		for (double i = Math.PI; i < Math.PI * 1.5; i += detail) {
			final float x = (float) ((Math.cos(i) + 1) * radius);
			final float y = (float) (Math.sin(i) * radius * -1)
					+ (sides - radius);
			roundedSquare.addVertice(x, y);
		}
		for (double i = Math.PI * 1.5; i < Math.PI * 2; i += detail) {
			final float x = (float) (Math.cos(i) * radius) + (sides - radius);
			final float y = (float) (Math.sin(i) * radius * -1)
					+ (sides - radius);
			roundedSquare.addVertice(x, y);
		}
		return roundedSquare;
	}

	/**
	 * Create Bounded square
	 * 
	 * @param radius
	 * @return
	 */
	final public static Polygon createRoundedRectangle(final Vector2 size, float radius, final float detailDeg) {
		radius = Math.min(Math.min(radius, size.x / 2.0f), size.y / 2.0f);
		final double detail = detailDeg * Math.PI / 180.0f;
		final Polygon roundedRectangle = new Polygon();
		for (double i = 0; i < Math.PI / 2; i += detail) {
			final float x = (float) (Math.cos(i) * radius) + (size.x - radius);
			final float y = (float) (((Math.sin(i) * -1) + 1) * radius);
			roundedRectangle.addVertice(x, y);
		}
		for (double i = Math.PI / 2; i < Math.PI; i += detail) {
			final float x = (float) ((Math.cos(i) + 1) * radius);
			final float y = (float) (((Math.sin(i) * -1) + 1) * radius);
			roundedRectangle.addVertice(x, y);
		}
		for (double i = Math.PI; i < Math.PI * 1.5; i += detail) {
			final float x = (float) ((Math.cos(i) + 1) * radius);
			final float y = (float) (Math.sin(i) * radius * -1)
					+ (size.y - radius);
			roundedRectangle.addVertice(x, y);
		}
		for (double i = Math.PI * 1.5; i < Math.PI * 2; i += detail) {
			final float x = (float) (Math.cos(i) * radius) + (size.x - radius);
			final float y = (float) (Math.sin(i) * radius * -1)
					+ (size.y - radius);
			roundedRectangle.addVertice(x, y);
		}
		return roundedRectangle;
	}

	/**
	 * Create Square polygon
	 * 
	 * @param radius
	 * @return
	 */
	final public static Polygon createSquare(final float sides) {
		final Polygon circle = new Polygon();
		circle.addVertice(0, 0);
		circle.addVertice(sides, 0);
		circle.addVertice(sides, sides);
		circle.addVertice(0, sides);
		return circle;
	}

	/**
	 * Create Square polygon
	 * 
	 * @param radius
	 * @return
	 */
	final public static Polygon createRectangle(final Vector2 size) {
		final Polygon circle = new Polygon();
		circle.addVertice(0, 0);
		circle.addVertice(size.x, 0);
		circle.addVertice(size.x, size.y);
		circle.addVertice(0, size.y);
		return circle;
	}

	/**
	 * Create Circle polygon
	 * 
	 * @param radius
	 *            Radius
	 * @return
	 */
	final public static Polygon createCircle(final float radius,final float detail) {
		final Polygon circle = new Polygon();
		final double rad = GeneralUtils.degreeToRad(detail);
		for (double i = 0; i < Math.PI * 2; i += rad) {
			float x = (float) (Math.cos(i) * radius) + radius;
			float y = (float) (Math.sin(i) * radius) + radius;
			circle.addVertice(new Vector2(x, y));
		}
		return circle;
	}

	/**
	 * Create Sector of Circle
	 * 
	 * @param radius
	 *            Radius
	 * @return
	 */
	final public static Polygon createSector(final float radius, final float detail, final float ang) {
		final Polygon circle = new Polygon();
		final double radIncr = GeneralUtils.degreeToRad(detail);
		final double radAng = GeneralUtils.degreeToRad(ang);
		circle.addVertice(new Vector2(radius, radius));
		for (double i = 0; i < radAng; i += radIncr) {
			float x = (float) (Math.cos(i) * radius) + radius;
			float y = (float) (Math.sin(i) * radius) + radius;
			circle.addVertice(new Vector2(x, y));
		}
		return circle;
	}
	
	/**
	 * Creates an optimized circular polygon that does not require vertices, The
	 * vertices not affect the final result.<br>
	 * <b>Note: This method creates the circle in the optimization mode, drawing
	 * a perfect circle without detailing.</b>
	 * 
	 * @param radius
	 *            Radius
	 * @return Returns the polygon containing the vertices to a circle.
	 */
	final public static Polygon createCircle(final float radius) {
		final Polygon circle = new Polygon();
		circle.mPolygonMode = POLYGON_MODE_OPTIMIZED_CIRCLE;
		circle.mPolygonFloatExtra = radius;
		return circle;
	}

	/**
	 * Create Ellipse
	 * 
	 * @param radius
	 *            Radius
	 * @param detail
	 *            Step of the ellipse of vertices in degree.
	 * @return Returns the polygon containing the vertices to a circle.
	 */
	final public static Polygon createEllipse(final Vector2 radius, final float detail) {
		final Polygon circle = new Polygon();
		final double rad = GeneralUtils.degreeToRad(detail);
		for (double i = 0; i < Math.PI * 2; i += rad) {
			float x = (float) (Math.cos(i) * radius.x) + radius.x;
			float y = (float) (Math.sin(i) * radius.y) + radius.y;
			circle.addVertice(new Vector2(x, y));
		}
		return circle;
	}

	/**
	 * Creates an optimized ellipse polygon that does not require vertices, The
	 * vertices not affect the final result.<br>
	 * <b>Note: This method creates the ellipse in the optimization mode,
	 * drawing a perfect ellipse without detailing.</b>
	 * 
	 * @param radius
	 *            Radius
	 * @return Returns the polygon containing the vertices to a circle.
	 */
	final public static Polygon createEllipse(final Vector2 radius) {
		final Polygon circle = new Polygon();
		circle.mPolygonMode = POLYGON_MODE_OPTIMIZED_ELLIPSE;
		circle.mPolygonVectorExtra.set(radius);
		return circle;
	}

	/**
	 * Create N-gon
	 * 
	 * @param radius
	 *            Radius
	 * @param detail
	 *            Step of the ellipse of vertices in degree.
	 * @return Returns the polygon containing the vertices to a circle.
	 */
	final public static Polygon createNgon(final float radius,
			final float sides) {
		final Polygon circle = new Polygon();
		final double rad = (Math.PI * 2) / sides;
		final double align = -Math.PI / 2;
		for (int i = 0; i < sides; i++) {
			float x = (float) (Math.cos(i * rad + align) * radius) + radius;
			float y = (float) (Math.sin(i * rad + align) * radius) + radius;
			circle.addVertice(new Vector2(x, y));
		}
		return circle;
	}

	/**
	 * Create N-gon
	 * 
	 * @param radius
	 *            Radius
	 * @param detail
	 *            Step of the ellipse of vertices in degree.
	 * @return Returns the polygon containing the vertices to a circle.
	 */
	final public static Polygon createNgon(final Vector2 radius,
			final float sides) {
		final Polygon circle = new Polygon();
		final double rad = (Math.PI * 2) / sides;
		final double align = -Math.PI / 2;
		for (int i = 0; i < sides; i++) {
			float x = (float) (Math.cos(i * rad + align) * radius.x) + radius.x;
			float y = (float) (Math.sin(i * rad + align) * radius.y) + radius.y;
			circle.addVertice(new Vector2(x, y));
		}
		return circle;
	}

	/**
	 * Refresh Polygon Size
	 * 
	 * @param vec
	 */
	final private void refreshSize(final Vector2 vec) {
		mSize.x = Math.max(mSize.x, vec.x);
		mSize.y = Math.max(mSize.y, vec.y);
	}
	
	/**
	 * Refresh Polygon Size
	 * 
	 * @param vec
	 */
	final private void refreshSize(final float x, final float y) {
		mSize.x = Math.max(mSize.x, x);
		mSize.y = Math.max(mSize.y, y);
	}
	
	/**
	 * Add Vertice
	 * 
	 * @param vector
	 */
	final public void addVertice(final Vector2 vector) {
		refreshSize(vector);
		mVertices.position(mVerticesCount * 2);
		mTextureVertex.position(mVerticesCount * 2);
		if ((mVertices.position() + 2) > mVertices.limit()) {
			FloatBuffer last = mVertices;
			FloatBuffer lastTextureVertex = mTextureVertex;
			last.position(0);
			lastTextureVertex.position(0);
			mVertices = GeneralUtils.createFloatBuffer(mVertices.limit()
					+ (int) ((mVertices.limit() + 1) * 0.2f) + 10);
			mTextureVertex = GeneralUtils.createFloatBuffer(mVertices.limit());
			for (int i = 0; i < mVerticesCount; i++) {
				mVertices.put(last.get());
				mVertices.put(last.get());
			}
			for (int i = 0; i < mVerticesCount; i++) {
				mTextureVertex.put(lastTextureVertex.get());
				mTextureVertex.put(lastTextureVertex.get());
			}
			mVertices.put(vector.x);
			mVertices.put(vector.y);
		} else {
			mVertices.put(vector.x);
			mVertices.put(vector.y);
		}
		mVerticesCount += 1;
		mapTexture();
	}

	/**
	 * Add Vertices of polygon
	 * 
	 * @param polygon
	 */
	final public void addVertices(final Polygon polygon) {
		mVertices.position(mVerticesCount * 2);
		mTextureVertex.position(mVerticesCount * 2);

		final int verticesCount = polygon.mVerticesCount * 2;

		if ((mVertices.position() + verticesCount) > mVertices.limit()) {
			FloatBuffer last = mVertices;
			FloatBuffer lastTextureVertex = mTextureVertex;

			last.position(0);
			lastTextureVertex.position(0);

			// 10 is an extra size
			final int newLimit = (int) ((mVertices.limit() + verticesCount) * 1.2f + 10);
			mVertices = GeneralUtils.createFloatBuffer(newLimit);
			mTextureVertex = GeneralUtils.createFloatBuffer(newLimit);

			for (int i = 0; i < mVerticesCount; i++) {
				mVertices.put(last.get());
				mVertices.put(last.get());
				mTextureVertex.put(lastTextureVertex.get());
				mTextureVertex.put(lastTextureVertex.get());
			}
		}
		polygon.mVertices.position(0);
		for (int i = 0; i < polygon.mVerticesCount; i++) {
			final float x = polygon.mVertices.get();
			final float y = polygon.mVertices.get();
			refreshSize(x, y);
			mVertices.put(x);
			mVertices.put(y);
		}
		mVerticesCount += polygon.mVerticesCount;
		mapTexture();
	}

	/**
	 * Get Vertices Count
	 * 
	 * @return
	 */
	final public int getVerticesCount() {
		return mVerticesCount;
	}

	/**
	 * Get Vertices
	 * 
	 * @param start
	 *            First Vertice
	 * @param count
	 *            Vertices count
	 * @param out
	 *            Out of Vertices with null values or instances
	 * @param offset
	 *            Offset of array
	 */
	final public void getVertices(final int start, final int count,
			final Vector2[] out, final int offset) {
		if (start < 0 || (start + count) > mVerticesCount)
			throw new IndexOutOfBoundsException();
		if (offset < 0 || (offset + count) > out.length)
			throw new ArrayIndexOutOfBoundsException();
		mVertices.position(offset * 2);
		for (int i = start; i < (start + count); i++) {
			float x = mVertices.get();
			float y = mVertices.get();
			if (out[i] == null)
				out[i] = new Vector2(x, y);
			else
				out[i].set(x, y);
		}
		mVertices.position(mVerticesCount * 2);
	}

	/**
	 * Get Vertice
	 * 
	 */
	final public Vector2 getVertice(final int index) {
		Vector2[] out = new Vector2[1];
		getVertices(index, 1, out, 0);
		return out[0];
	}

	/**
	 * Remove Vertices
	 * 
	 * @param index
	 */
	final public void removeVertices(final int start, final int count) {
		if (start < 0 || (start + count) > mVerticesCount)
			throw new IndexOutOfBoundsException();
		mSize.set(0, 0);
		mVertices.position(0);
		for(int i=0; i<start; i++) {
			final float x = mVertices.get();
			final float y = mVertices.get();
			refreshSize(x, y);
		}
		mVertices.position(start * 2);
		for (int i = (start + count); i < mVerticesCount; i++) {
			final float x = mVertices.get(i * 2);
			final float y = mVertices.get(i * 2 + 1);
			refreshSize(x, y);
			mVertices.put(x);
			mVertices.put(y);
		}
		mVerticesCount -= count;
		mapTexture();
	}

	/**
	 * Remove Vertice
	 * 
	 * @param index
	 */
	final public void removeVertice(final int index) {
		removeVertices(index, 1);
	}
	
	/**
	 * Remove All Vertices
	 */
	final public void clearVertices() {
		mSize.set(0, 0);
		mVertices.position(0);
		mVerticesCount = 0;
	}

	/**
	 * Add Vertice
	 * 
	 * @param x
	 *            Position X
	 * @param y
	 *            Position Y
	 */
	final public void addVertice(final float x, final float y) {
		addVertice(new Vector2(x, y));
	}

	/**
	 * Remap Polygon texture
	 * 
	 * @param mode
	 */
	final public void setTextureMapMode(final int mode) {
		if(mode != TEXTURE_MAP_REPEAT && mode != TEXTURE_MAP_STRETCH)
			throw new IllegalArgumentException("This mode not exist.");
		mTextureMapMode = mode;
		mapTexture();
	}
	
	/**
	 * Map Texture
	 */
	final private void mapTexture() {
		if (mTexture == null)
			return;
		final Vector2 size = mTexture.getSize();
		switch (mPolygonMode) {
		case POLYGON_MODE_NORMAL:
			mVertices.position(0);
			mTextureVertex.position(0);
			if(mTextureMapMode == TEXTURE_MAP_REPEAT) {
				for (int i = 0; i < mVerticesCount; i++) {
					final float x = mVertices.get() / size.x;
					final float y = mVertices.get() / size.y;
					mTextureVertex.put(x);
					mTextureVertex.put(y);
				}
			} else if(mTextureMapMode == TEXTURE_MAP_STRETCH) {
				for (int i = 0; i < mVerticesCount; i++) {
					final float x = mVertices.get() / mSize.x;
					final float y = mVertices.get() / mSize.y;
					mTextureVertex.put(x);
					mTextureVertex.put(y);
				}
			}
			mTextureVertex.position(0);
			break;
		case POLYGON_MODE_OPTIMIZED_CIRCLE:
			if(mTextureMapMode == TEXTURE_MAP_REPEAT) {
				final float xfw = (mPolygonFloatExtra * 2) / size.x;
				final float yfh = (mPolygonFloatExtra * 2) / size.y;
				mTextureVertex = GeneralUtils.createFloatBuffer(new float[] { 0, 0, xfw, 0, xfw, yfh, 0, yfh });
			} else if(mTextureMapMode == TEXTURE_MAP_STRETCH) {
				final float xfw = (mPolygonFloatExtra * 2) / mSize.x;
				final float yfh = (mPolygonFloatExtra * 2) / mSize.y;
				mTextureVertex = GeneralUtils.createFloatBuffer(new float[] { 0, 0, xfw, 0, xfw, yfh, 0, yfh });
			}
			break;
		case POLYGON_MODE_OPTIMIZED_ELLIPSE:
			if(mTextureMapMode == TEXTURE_MAP_REPEAT) {
				final float xvw = (mPolygonVectorExtra.x * 2) / size.x;
				final float yvh = (mPolygonVectorExtra.y * 2) / size.y;
				mTextureVertex = GeneralUtils.createFloatBuffer(new float[] { 0, 0, xvw, 0, xvw, yvh, 0, yvh });
			} else if(mTextureMapMode == TEXTURE_MAP_STRETCH) {
				final float xvw = (mPolygonVectorExtra.x * 2) / mSize.x;
				final float yvh = (mPolygonVectorExtra.y * 2) / mSize.y;
				mTextureVertex = GeneralUtils.createFloatBuffer(new float[] { 0, 0, xvw, 0, xvw, yvh, 0, yvh });
			}
			break;
		}

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
	final public void setViewport(final int left, final int top,
			final int width, final int height) {
		mViewport = new Rect(left, top, width, height);
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
	final public void setViewport(final Rect rect) {
		mViewport = new Rect(rect);
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
	 * Set Texture
	 * 
	 * @param texture
	 *            {@link com.org.multigear.mginterface.graphics.opengl.texture.Texture}
	 */
	final public void setTexture(final Texture texture) {
		mTexture = texture;
		mapTexture();
	}

	/**
	 * Set Color
	 * 
	 * @param
	 */
	final public void setColor(final Color color) {
		mColor = color;
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
	 * Set Blend Func
	 * 
	 * @param blendFunc
	 */
	final public void setBlendFunc(final BlendFunc sFactor, final BlendFunc dFactor) {
		mBlendFuncs = new BlendFunc[] {sFactor, dFactor, BlendFunc.ONE, BlendFunc.ZERO};
	}
	
	/**
	 * Set Blend Func
	 * 
	 * @param blendFunc
	 */
	final public void setBlendFuncSeparate(final BlendFunc sFactor, final BlendFunc dFactor, final BlendFunc sAlphaFactor, final BlendFunc dAlphaFactor) {
		mBlendFuncs = new BlendFunc[] {sFactor, dFactor, sAlphaFactor, dAlphaFactor};
	}

	/**
	 * Set identifier
	 * 
	 * @param id
	 *            Identifier
	 */
	public void setId(int id) {
		mID = id;
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
	 * Set Z depth
	 * 
	 * @param z
	 *            Depth
	 */
	final public void setZ(final int z) {
		mZ = z;
	}


	/**
	 * Get framed size
	 * 
	 * @return
	 */
	final public Vector2 getFramedSize() {
		switch (mPolygonMode) {
		case POLYGON_MODE_NORMAL:
			return mSize;
		case POLYGON_MODE_OPTIMIZED_CIRCLE:
			return new Vector2(mPolygonFloatExtra*2, mPolygonFloatExtra*2);
		case POLYGON_MODE_OPTIMIZED_ELLIPSE:
			return Vector2.scale(mPolygonVectorExtra, 2);
		}
		return new Vector2();
	}
	
	
	/**
	 * Get Color
	 * 
	 * @return Color
	 */
	final public Color getColor() {
		return mColor;
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
		final AnimationSet animationSet = getAnimationStack().animateFrame();
		Vector2 position = mPosition.clone();
		position.sum(animationSet.getPosition());
		return position;
	}

	/**
	 * Get Blend Func
	 * 
	 * @return Get Blend Func [sFactor, dFactor]
	 */
	final public BlendFunc[] getBlendFunc() {
		return mBlendFuncs.clone();
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

	/**
	 * Get Z Depth
	 * 
	 * @return Depth
	 */
	@Override
	final public int getZ() {
		return mZ;
	}

	/**
	 * Get Id
	 * 
	 * @return Id
	 */
	@Override
	final public int getId() {
		return mID;
	}

	/**
	 * Get texture map mode
	 * 
	 * @param mode
	 */
	final public int getTextureMapMode() {
		return mTextureMapMode;
	}
	
	/**
	 * Set Matrix Transformations for this Layer
	 * <p>
	 * 
	 * @param matrixRow
	 *            MatrixRow
	 * @return True if need Draw
	 */
	final public void draw(final Drawer drawer) {

		// Prepare Animation
		final AnimationSet animationSet = mAnimationStack.animateFrame();

		// Get final Opacity
		mPreparedOpacity = animationSet.getOpacity() * mOpacity;

		// Get Infos
		final Vector2 scale = Vector2.scale(mScale, animationSet.getScale());
		final Vector2 translate = animationSet.getPosition();
		final float rotate = mAngle + animationSet.getRotation();

		// Calc values
		final float ox = mCenter.x * scale.x;
		final float oy = mCenter.y * scale.y;
		float sx = scale.x;
		float sy = scale.y;
		float tX = mPosition.x + translate.x;
		float tY = mPosition.y + translate.y;
		float six = ox;
		float siy = oy;
		float mx = 1;
		float my = 1;
		float mtx = 0;
		float mty = 0;
		
		if (mMirror[0]) {
			mx = -1;
			mtx = 1;
		}
		
		if (mMirror[1]) {
			my = -1;
			mty = 1;
		}

		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();

		// Push Matrix
		matrixRow.push();

		
		// Translate and Rotate Matrix with correction
		float rad = (float) GeneralUtils.degreeToRad(rotate);
		float c = (float) Math.cos(rad);
		float s = (float) Math.sin(rad);
		mFinalTransformation[0] = c * sx * mx;
		mFinalTransformation[1] = -s * sy * my;
		mFinalTransformation[2] = c * (sx * mtx - six) + -s * (sy * mty - siy) + tX;
		mFinalTransformation[3] = s * sx * mx;
		mFinalTransformation[4] = c * sy * my;
		mFinalTransformation[5] = s * (sx * mtx - six) + c * (sy * mty - siy) + tY;
		matrixRow.preConcatf(mFinalTransformation);
		

		// Prepare Vertex
		// note: TextureVertex always already in the position 0
		mVertices.position(0);

		// Set Texture
		drawer.begin();
		drawer.setOpacity(mPreparedOpacity);
		drawer.setBlendFunc(mBlendFuncs[0], mBlendFuncs[1], mBlendFuncs[2], mBlendFuncs[3]);
		drawer.setColor(mColor);
		drawer.setTexture(mTexture);
		drawer.snip(mViewport);
		drawer.setElementVertex(mVertices);
		drawer.setTextureVertex(mTextureVertex);

		// Draw
		switch (mPolygonMode) {
		case POLYGON_MODE_NORMAL:
			drawer.drawPolygon(mVerticesCount);
			break;
		case POLYGON_MODE_OPTIMIZED_CIRCLE:
			drawer.drawCircle(mPolygonFloatExtra);
			break;
		case POLYGON_MODE_OPTIMIZED_ELLIPSE:
			drawer.drawEllipse(mPolygonVectorExtra);
			break;
		}

		// End Drawer
		drawer.end();

		// Pop Matrix
		matrixRow.pop();
	}
}
