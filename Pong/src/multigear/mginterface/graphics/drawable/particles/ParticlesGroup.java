package multigear.mginterface.graphics.drawable.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multigear.general.utils.Ref2F;
import multigear.mginterface.graphics.drawable.BaseDrawable;
import multigear.mginterface.graphics.opengl.drawer.Drawer;
import multigear.mginterface.graphics.opengl.drawer.MatrixRow;
import multigear.mginterface.graphics.opengl.texture.Loader;
import multigear.mginterface.graphics.opengl.texture.Texture;
import multigear.mginterface.scene.Scene;
import android.graphics.Rect;
import android.opengl.GLES20;

/**
 * 
 * @author PedroH, RaphaelB
 * 
 *         Property Createlier.
 */
public class ParticlesGroup extends BaseDrawable {
	
	// Private Variables
	private ParticlesHelper mParticlesHelper;
	private ParticlesGenerator mParticlesGenerator;
	private int mHelperFrequency = 100;
	private int mParticlesLimit = 50;
	private long mLastTime;
	private List<Particle> mParticles = new ArrayList<Particle>();
	private Rect mViewport;
	private Ref2F mPosition = new Ref2F(0, 0);
	private boolean mAutoHelper = true;
	
	/**
	 * Constructor
	 * 
	 * @param room
	 */
	public ParticlesGroup(Scene room) {
		super(room);
		mLastTime = getAttachedRoom().getThisTime();
		mParticlesGenerator = new ParticlesGenerator(this);
	}
	
	/**
	 * Set Sprite Position
	 * 
	 * @param position
	 *            {@link multigear.general.utils.Ref2F} Position
	 */
	final public void setPosition(final multigear.general.utils.Ref2F position) {
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
	 * Set Particles Limit
	 * <p>
	 * 
	 * @param limit
	 */
	final public void setParticlesLimit(final int limit) {
		mParticlesLimit = limit;
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
	 * @return {@link multigear.general.utils.Ref2F} Position
	 */
	final public multigear.general.utils.Ref2F getPosition() {
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
		// Get Matrix Row
		final MatrixRow matrixRow = drawer.getMatrixRow();
		
		// Disable Scissor
		boolean disableScissor = false;
		
		// Set Scisor
		if (mViewport != null) {
			final int screenHeight = (int) getAttachedRoom().getScreenSize().YAxis;
			final int top = screenHeight - mViewport.bottom;
			final int bottom = screenHeight - mViewport.top - top;
			GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
			GLES20.glScissor(mViewport.left, top, mViewport.right, bottom);
			disableScissor = true;
		}
		
		// Get Opacity
		final float opacityGroup = getOpacity();
		
		// Draw
		for (final Particle particle : mParticles) {
			
			// Update Particle
			particle.update();
			
			// Get texture
			final Texture texture = particle.getTexture();
			
			// Get Values
			final Ref2F scale = particle.getScale();
			final float finalOpacity = particle.getOpacity() * opacityGroup;
			final float rotation = particle.getRotation();
			
			// Not Update
			if (texture == null || finalOpacity <= 0)
				continue;
			
			// Get Infos
			final float ox = (float) particle.getCenter().XAxis * scale.XAxis;
			final float oy = (float) particle.getCenter().YAxis * scale.YAxis;
			final float sx = (float) particle.getSize().XAxis * scale.XAxis;
			final float sy = (float) particle.getSize().YAxis * scale.YAxis;
			
			// Push Matrix
			matrixRow.push();
			
			// Scale Matrix
			matrixRow.postScalef(sx, sy);
			
			// Translate and Rotate Matrix
			matrixRow.postTranslatef(-ox, -oy);
			matrixRow.postRotatef(rotation);
			matrixRow.postTranslatef(ox, oy);
			
			// Animate Matrix
			// animation.animateMatrix(drawer.getTransformMatrix(),
			// multigear.general.utils.KernelUtils.ref2d(sx, sy));
			
			// Translate Matrix
			final float tX = (float) ((particle.getPosition().XAxis - ox) + mPosition.XAxis);
			final float tY = (float) ((particle.getPosition().YAxis - oy) + mPosition.YAxis);
			matrixRow.postTranslatef(tX, tY);
			
			// Draw Particle
			drawer.drawTexture(texture, particle.getSize(), finalOpacity);
			
			// Pop Matrix
			matrixRow.pop();
		}
		
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
			if (mParticles.size() < mParticlesLimit + 1)
				return;
		
		particle.onCreated(getAttachedRoom().getThisTime());
		if (top)
			mParticles.add(particle);
		else
			mParticles.add(0, particle);
	}
}
