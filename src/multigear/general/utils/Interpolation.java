package multigear.general.utils;

/**
 * Interpolation
 * 
 * @author user
 *
 */
public interface Interpolation {

	/**
	 * On Interpolate
	 * 
	 * @param delta Delta [0, 1]
	 * @return Interpolated value
	 */
	public float onInterpolate(final float delta);
}
