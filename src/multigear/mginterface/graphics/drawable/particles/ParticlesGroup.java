package multigear.mginterface.graphics.drawable.particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Color;
import multigear.general.utils.GeneralUtils;
import multigear.general.utils.Vector2;
import multigear.mginterface.graphics.drawable.BaseDrawable;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.texture.Loader;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.util.Log;

/**
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
final public class ParticlesGroup extends BaseDrawable {
	
	// Private Variables
	private Texture mTexture;
	private ParticlesHelper mParticlesHelper;
	private ParticlesGenerator mParticlesGenerator;
	private int mHelperFrequency = 100;
	private int mParticlesLimit;
	private long mLastTime;
	private List<Particle> mParticles = new ArrayList<Particle>();
	private Rect mViewport;
	private Vector2 mPosition = new Vector2(0, 0);
	private boolean mAutoHelper = true;
	
	// Buffers
	private FloatBuffer mParticlesPositionBuffer;
	private FloatBuffer mParticlesOpacityBuffer;
	private FloatBuffer mParticlesScaleBuffer;
	private Color mParticlesColorBuffer[];
	
	/**
	 * Constructor
	 * 
	 * @param room
	 */
	public ParticlesGroup(Scene room) {
		super(room);
		mLastTime = getAttachedRoom().getThisTime();
		mParticlesGenerator = new ParticlesGenerator(this);
		// 
		setParticlesLimit(10);
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
		mHelperFrequency = 1000 / fps;
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
		mParticlesPositionBuffer = GeneralUtils.createFloatBuffer(limit * 2);;
		mParticlesOpacityBuffer = GeneralUtils.createFloatBuffer(limit);;
		mParticlesScaleBuffer = GeneralUtils.createFloatBuffer(limit);;
		mParticlesColorBuffer = new Color[limit];
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
	 * Get Particles Generator
	 * @return
	 */
	final public ParticlesGenerator getParticlesGenerator() {
		return mParticlesGenerator;
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
	final public int getHelperFrequency(final int fps) {
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
	 * Get Texture Loader
	 * 
	 * @return
	 */
	final protected Loader getTextureLoaderImpl() {
		return getAttachedRoom().getTextureLoader();
	}
	
	/**
	 * Update Widget
	 */
	@Override
	final public void updateAndDraw(final Drawer drawer, final float preOpacity) {
		
		// Get Opacity
		final float opacityGroup = getOpacity() * preOpacity;
		
		// Not has draw content
		if(opacityGroup <= 0 || mTexture == null) {
			// Update Particles
			updateParticles();
			
			return;
		}

		// Disable Scissor
		boolean disableScissor = false;
		
		// Set Scisor
		if (mViewport != null) {
			final int screenHeight = (int) getAttachedRoom().getScreenSize().y;
			final int top = screenHeight - mViewport.bottom;
			final int bottom = screenHeight - mViewport.top - top;
			GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
			GLES20.glScissor(mViewport.left, top, mViewport.right, bottom);
			disableScissor = true;
		}
		
		// Clear buffers
		mParticlesPositionBuffer.clear();
		mParticlesOpacityBuffer.clear();
		mParticlesScaleBuffer.clear();
		
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
			
			// Put to Vertex buffer
			mParticlesPositionBuffer.put(position.x + getPosition().x);
			mParticlesPositionBuffer.put(position.y + getPosition().y);
			
			// Put to Opacity buffer
			mParticlesOpacityBuffer.put(finalOpacity);
			
			// Put to Scale buffer
			mParticlesScaleBuffer.put(mTexture.getSize().x * scale);
		}
		
		// Set Buffers Position
		mParticlesPositionBuffer.position(0);
		mParticlesOpacityBuffer.position(0);
		mParticlesScaleBuffer.position(0);
		
		// draw Particles
		drawer.drawParticles(mTexture, mParticles.size(), mParticlesPositionBuffer, mParticlesOpacityBuffer, mParticlesScaleBuffer);
		
		// Disable Scissor
		if (disableScissor)
			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		
		// Update Particles
		updateParticles();
	}
	
	/**
	 * Update Particles
	 */
	final private void updateParticles() {
		final long time = getAttachedRoom().getThisTime();
		if (mAutoHelper) {
			if ((time - mLastTime) > mHelperFrequency) {
				if (mParticlesHelper != null)
					mParticlesHelper.onGenerate(mParticlesGenerator);
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
		mParticlesHelper.onGenerate(mParticlesGenerator);
		mLastTime = System.currentTimeMillis();
	}
	
	/**
	 * Add new Particle
	 */
	final protected void addParticle(final Particle particle, final boolean top) {
		if (mParticlesLimit != -1)
			if (mParticles.size() > mParticlesLimit + 1)
				return;
		
		particle.onCreated(getAttachedRoom().getThisTime());
		if (top)
			mParticles.add(particle);
		else
			mParticles.add(0, particle);
	}
}
