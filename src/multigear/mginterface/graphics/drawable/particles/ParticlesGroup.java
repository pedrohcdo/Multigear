package multigear.mginterface.graphics.drawable.particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Color;
import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.graphics.opengl.drawer.BlendFunc;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Component;
import multigear.mginterface.scene.components.receivers.Drawable;
import android.graphics.Rect;
import android.util.Log;

/**
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ParticlesGroup implements Drawable, Component {
	
	// 
	/** 
	 * This mode uses the same structure as a sprite for each particle released. For a high amount of particles it is not recommended.
	 */
	final public static int MODE_SPRITE = 1;
	/**
	 * This mode uses a fast structure for a greater amount of particle, as it is composed of only one point does not support particles rotation.
	 */
	final public static int MODE_POINT = 2;
	
	// Final Private Variables
	final public int mMode;
	
	// Private Variables
	private Texture mTexture;
	private ParticlesHelper mParticlesHelper;
	private float mHelperFrequency = 100;
	private int mParticlesLimit;
	private long mLastTime = GlobalClock.currentTimeMillis();
	private List<Particle> mParticles = new ArrayList<Particle>();
	private Rect mViewport;
	private Vector2 mPosition = new Vector2(0, 0);
	private int mId = 0;
	private int mZ = 0;
	private float mOpacity = 1.0f;
	private boolean mAutoHelper = true;
	private BlendFunc mBlendFunc = BlendFunc.ONE_MINUS_SRC_ALPHA;
	private float mScale = 1.0f;
	private Color mColor = Color.WHITE;
	
	// Buffers
	private FloatBuffer mParticlesBuffer;
	
	/**
	 * Constructor
	 * 
	 * @param room
	 */
	public ParticlesGroup(final int mode) {
		mMode = mode;
		if(mMode != MODE_POINT && mMode != MODE_SPRITE)
			throw new IllegalArgumentException("This mode does not exist.");
		setParticlesLimit(100);
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
	 * Set Particle group texture
	 * 
	 * @param texture
	 */
	final public void setTexture(final Texture texture) {
		mTexture = texture;
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link Vector2} Position
	 */
	final public void setPosition(final Vector2 position) {
		mPosition = position;
	}
	
	/**
	 * Set Scale
	 * 
	 * @param scale
	 */
	final public void setScale(final float scale) {
		mScale = scale;
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
	final public void setViewport(final int left, final int top, final int width, final int height) {
		mViewport = new Rect(left, top, width, height);
	}
	
	/**
	 * Set Particles Helper.
	 * <p>
	 * 
	 * @param particlesHelper
	 */
	final public void setHelper(final ParticlesHelper particlesHelper) {
		mParticlesHelper = particlesHelper;
	}
	
	/**
	 * Set Particles Helper Frequency.
	 * <p>
	 * 
	 * @param fps
	 */
	final public void setHelperFrequency(final float fps) {
		mHelperFrequency = 1000.0f / fps;
	}
	
	/**
	 * Set Depth
	 * 
	 * @param z [in] Depth
	 */
	final public void setZ(final int z) {
		mZ = z;
	}

	/**
	 * Set Identifier
	 * 
	 * @param id [in] Identifier
	 */
	final public void setId(final int id) {
		mId = id;
	}
	
	/**
	 * Set Opacity
	 * 
	 * @param opacity Opacity
	 */
	final public void setOpacity(final float opacity) {
		mOpacity = Math.max(Math.min(opacity, 1.0f), 0.0f);
	}
	
	/**
	 * Set Color
	 * 
	 * @param color Color
	 */
	final public void setColor(final Color color) {
		mColor = color;
	}
	
	/**
	 * Disable Auto Helper.
	 * <p>
	 * Note: To call Helper invoke "requestHelper()".
	 */
	final public void disableAutoHelper() {
		mAutoHelper = false;
	}
	
	/**
	 * Enable Auto Helper.
	 */
	final public void enableAutoHelper() {
		mLastTime = GlobalClock.currentTimeMillis();
		mAutoHelper = true;
	}
	
	/**
	 * Set Particles Limit and allocate spaces. 
	 * Allocates all clearances, for performance reasons it is not
	 *  recommended to invoke this method often.
	 * <p>
	 * 
	 * @param limit
	 */
	final public void setParticlesLimit(final int limit) {
		if(limit < 0)
			throw new IllegalArgumentException("The amount can not be negative.");
		switch(mMode) {
		default:
		case MODE_POINT:
			mParticlesLimit = limit;
			mParticlesBuffer = GeneralUtils.createFloatBuffer(limit * 4);
			break;
		case MODE_SPRITE:
			mParticlesLimit = limit;
			mParticlesBuffer = GeneralUtils.createFloatBuffer(limit * 48);
		}
		
	}
	
	/**
	 * Set Particles Buffer same as 'setParticlesLimit(int)'. The amount of particles is 
	 * calculated from the operating mode, if the points mode, this will be "buffer.Length () / 4", 
	 * but if you are in sprite mode shall be calculated as "buffer.Length () / 48".
	 * <p>
	 * 
	 * @param limit
	 */
	final public void setParticlesBuffer(final FloatBuffer buffer) {
		switch(mMode) {
		default:
		case MODE_POINT:
			mParticlesLimit = buffer.limit() / 4;
			break;
		case MODE_SPRITE:
			mParticlesLimit = buffer.limit() / 48;
		}
		mParticlesBuffer = buffer;
	}
	
	/**
	 * Get Particle group texture
	 * 
	 * @param texture
	 */
	final public Texture getTexture() {
		return mTexture;
	}
	
	/**
	 * Return Position
	 * 
	 * @return {@link Vector2} Position
	 */
	final public Vector2 getPosition() {
		return mPosition;
	}
	
	/**
	 * Get Scale
	 * 
	 * @return
	 */
	final public float getScale() {
		return mScale;
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
	 * Get Viewport
	 */
	final public Rect getViewport() {
		return mViewport;
	}
	
	/**
	 * Get Particles Helper.
	 * <p>
	 * 
	 * @param particlesHelper
	 */
	final public ParticlesHelper getHelper() {
		return mParticlesHelper;
	}
	
	/**
	 * Get Particles Helper Frequency.
	 * <p>
	 * 
	 * @param fps
	 */
	final public float getHelperFrequency(final int fps) {
		return mHelperFrequency;
	}
	
	/**
	 * Get Particles Limit
	 * <p>
	 * 
	 * @param limit
	 */
	final public int getParticlesLimit(final int limit) {
		return mParticlesLimit;
	}

	/**
	 * Get Color
	 * 
	 * @param color Color
	 */
	final public Color getColor() {
		return mColor;
	}
	
	/**
	 * Get Depth
	 * 
	 * @return Depth
	 */
	@Override
	public int getZ() {
		return mZ;
	}

	/**
	 * Get identifier
	 * 
	 * @return Identifier
	 */
	@Override
	public int getId() {
		return mId;
	}
	
	/**
	 * Get Opacity
	 * 
	 * @return Opacity
	 */
	final public float getOpacity() {
		return mOpacity;
	}
	
	/**
	 * Update Widget
	 */
	@Override
	final public void draw(final Drawer drawer) {
		
		// Get Opacity
		final float opacityGroup = mOpacity;
		
		// Not has draw content
		if(opacityGroup <= 0 || mTexture == null) {
			// Update Particles
			updateParticles();
			return;
		}
		
		// Disable Scissor
		boolean disableScissor = false;
		
		// Get Matrix Row
		final WorldMatrix matrixRow = drawer.getWorldMatrix();

		// Push Matrix
		//matrixRow.push();
		
		
		// Clear buffers
		mParticlesBuffer.clear();
		
		// Draw
		for (int i=0; i<mParticles.size(); i++) {
			
			// Get particle
			final Particle particle = mParticles.get(i);
			
			// Update Particle
			particle.update();
			
			// Get Values
			final Vector2 position = particle.getFinalPosition();
			final float finalOpacity = particle.getOpacity() * opacityGroup;

			float scale = particle.getScale() * getScale() * mTexture.getSize().x;
			
			switch(mMode) {
			default:
			case MODE_POINT:
				// Put Vertexes to buffer
				mParticlesBuffer.put(position.x);
				mParticlesBuffer.put(position.y);
				
				// Put Opacity to buffer
				mParticlesBuffer.put(finalOpacity);
				
				// Put Scale to buffer
				mParticlesBuffer.put(scale);
				
				break;
			case MODE_SPRITE:
				scale /= 2;
				
				float angle = particle.getAngle();
				
				mParticlesBuffer.put(-scale);
				mParticlesBuffer.put(-scale);
				mParticlesBuffer.put(position.x);
				mParticlesBuffer.put(position.y);
				mParticlesBuffer.put(0);
				mParticlesBuffer.put(0);
				mParticlesBuffer.put(finalOpacity);
				mParticlesBuffer.put(angle);
				
				mParticlesBuffer.put(scale);
				mParticlesBuffer.put(-scale);
				mParticlesBuffer.put(position.x);
				mParticlesBuffer.put(position.y);
				mParticlesBuffer.put(1);
				mParticlesBuffer.put(0);
				mParticlesBuffer.put(finalOpacity);
				mParticlesBuffer.put(angle);
				
				mParticlesBuffer.put(scale);
				mParticlesBuffer.put(scale);
				mParticlesBuffer.put(position.x);
				mParticlesBuffer.put(position.y);
				mParticlesBuffer.put(1);
				mParticlesBuffer.put(1);
				mParticlesBuffer.put(finalOpacity);
				mParticlesBuffer.put(angle);
				
				
				mParticlesBuffer.put(-scale);
				mParticlesBuffer.put(-scale);
				mParticlesBuffer.put(position.x);
				mParticlesBuffer.put(position.y);
				mParticlesBuffer.put(0);
				mParticlesBuffer.put(0);
				mParticlesBuffer.put(finalOpacity);
				mParticlesBuffer.put(angle);
				
				mParticlesBuffer.put(-scale);
				mParticlesBuffer.put(scale);
				mParticlesBuffer.put(position.x);
				mParticlesBuffer.put(position.y);
				mParticlesBuffer.put(0);
				mParticlesBuffer.put(1);
				mParticlesBuffer.put(finalOpacity);
				mParticlesBuffer.put(angle);
				
				mParticlesBuffer.put(scale);
				mParticlesBuffer.put(scale);
				mParticlesBuffer.put(position.x);
				mParticlesBuffer.put(position.y);
				mParticlesBuffer.put(1);
				mParticlesBuffer.put(1);
				mParticlesBuffer.put(finalOpacity);
				mParticlesBuffer.put(angle);
				
			}
		}
		
		// Set Buffers Position
		mParticlesBuffer.position(0);
		
		// Prepare Drawer
		drawer.begin();
		drawer.setTexture(mTexture);
		drawer.setOpacity(1);
		drawer.setBlendFunc(mBlendFunc);
		drawer.setElementVertex(mParticlesBuffer);
		drawer.setColor(mColor);
		drawer.snip(mViewport);
		
		// Set Begin
		switch(mMode) {
		default:
		case MODE_POINT:
			drawer.drawPointParticles(mParticles.size());
			break;
		case MODE_SPRITE:
			drawer.drawSpriteParticles(mParticles.size());
		}
		
		// End
		drawer.end();
		
		// Update Particles
		updateParticles();
		
		// Pop
		//matrixRow.pop();
	}
	
	/**
	 * Update Particles
	 */
	final private void updateParticles() {
		final long time = GlobalClock.currentTimeMillis();
		if (mAutoHelper) {
			final long timeDiff = (time - mLastTime);
			if (timeDiff > mHelperFrequency) {
				if (mParticlesHelper != null) {
					for(int i=0; i<(timeDiff / mHelperFrequency); i++)
						mParticlesHelper.onGenerate(this);
				}
				mLastTime = time;
			}
		}
		final Iterator<Particle> itr = mParticles.iterator();
		while (itr.hasNext()) {
			final Particle particle = itr.next();
			particle.timeNow(time);
			if (particle.isTimeOut())
				itr.remove();
			else
				particle.update();
		}
	}
	
	/**
	 * Requeste Helper.
	 */
	final public void requestHelper() {
		mParticlesHelper.onGenerate(this);
		mLastTime = GlobalClock.currentTimeMillis();
	}
	
	/**
	 * Add new Particle
	 */
	final public void addParticle(Particle particle, final boolean top) {
		if (mParticlesLimit != -1)
			if (mParticles.size() >= mParticlesLimit)
				return;
		particle = particle.prepareToInsert();
		particle.onCreated(GlobalClock.currentTimeMillis());
		particle.setPosition(Vector2.sum(particle.getPosition(), getPosition()));
		if (top)
			mParticles.add(particle);
		else
			mParticles.add(0, particle);
	}
}
