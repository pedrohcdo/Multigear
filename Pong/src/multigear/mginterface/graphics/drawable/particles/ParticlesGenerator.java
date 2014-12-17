package multigear.mginterface.graphics.drawable.particles;

import multigear.mginterface.graphics.opengl.texture.Loader;

/**
 * Particles Generator.
 * <p>
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ParticlesGenerator {
	
	// Final Private Variables
	final private ParticlesGroup mParticles;
	
	/**
	 * Constructor
	 */
	protected ParticlesGenerator(final ParticlesGroup particles) {
		mParticles = particles;
	}
	
	/**
	 * Get Texture Loader
	 * @return
	 */
	final public Loader getTextureLoader() {
		return mParticles.getTextureLoaderImpl();
	}
	
	/**
	 * Add new Particle
	 * @return
	 */
	final public void addParticle(final Particle particle, final boolean top) {
		mParticles.addParticle(particle, top);
	}
}
