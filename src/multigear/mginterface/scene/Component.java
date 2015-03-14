package multigear.mginterface.scene;


/**
 * Component
 * 
 * @author user
 *
 */
public interface Component {
	
	/**
	 * Depth used for organization of all drawable.
	 * @return Depth
	 */
	public int getZ();
	
	/**
	 * Identifier of a drawable.
	 * @return Integer used to identify a drawable.
	 */
	public int getId();
}
