package multigear.general.exceptions;

/**
 * 
 * Utilisado para lançamento de erro quando uma textura não existe no pacote de texturas.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class TextureNotExist extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Private Variables
	final private int mID;
	
	/*
	 * Construtor
	 */
	public TextureNotExist(final int id) {
		mID = id;
	}
	
	/*
	 * Retorna a mensagem de erro
	 */
	final public String getMessage() {
		return "An error occurred loading a certain texture " + mID + ". This texture does not exist in the package of textures.";
	}
}
