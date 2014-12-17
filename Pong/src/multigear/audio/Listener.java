package multigear.audio;

/**
 * Audio Support Listener
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public interface Listener {
	
	/** On Sample Load Complete withouth error. */
	public void onSampleLoadComplete(final int resourceId);
	
	/** On Sample Load Error. */
	public void onSampleLoadError(final int resourceId);
}
