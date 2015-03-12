package multigear.mginterface.graphics.drawable.particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.engine.eventsmanager.GlobalClock;
import multigear.mginterface.graphics.opengl.drawer.BlendFunc;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.WorldMatrix;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.components.receivers.Component;
import multigear.mginterface.scene.components.receivers.Drawable;
import android.graphics.Rect;

/**
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ParticlesGroup implements Drawable, Component {
	
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
	private float mOpacity = 1;
	private boolean mAutoHelper = true;
	protected BlendFunc mBlendFunc = BlendFunc.ONE_MINUS_SRC_ALPHA;
	
	// Buffers
	private FloatBuffer mParticlesBuffer;
	
	/**
	 * Constructor
	 * 
	 * @param room
	 */
	public ParticlesGroup() {
		setParticlesLimit(10);
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
	final public void setHelperFrequency(final int fps) {
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
		mParticlesLimit = limit;
		mParticlesBuffer = GeneralUtils.createFloatBuffer(limit * 4);
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
			final Vector2 position = particle.getPosition();
			final float scale = particle.getScale();
			final float finalOpacity = particle.getOpacity() * opacityGroup;
			
			// Put Vertexes to buffer
			mParticlesBuffer.put(position.x);
			mParticlesBuffer.put(position.y);
			
			// Put Opacity to buffer
			mParticlesBuffer.put(finalOpacity);
			
			// Put Scale to buffer
			mParticlesBuffer.put(mTexture.getSize().x * scale);
		}
		
		// Set Buffers Position
		mParticlesBuffer.position(0);
		
		// Prepare Drawer
		drawer.begin();
		drawer.setTexture(mTexture);
		drawer.setOpacity(1);
		drawer.setBlendFunc(mBlendFunc);
		drawer.setElementVertex(mParticlesBuffer);
		drawer.snip(mViewport);
		
		// Set Begin
		drawer.drawParticles(mParticles.size());
		
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
		mLastTime = System.currentTimeMillis();
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
