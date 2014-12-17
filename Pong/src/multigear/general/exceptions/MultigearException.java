package multigear.general.exceptions;

/**
 * 
 * Utilisado para lan�amento de erro quando uma textura n�o existe no pacote de texturas.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class MultigearException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Private Variables
	final private String mMessage;
	
	/*
	 * Construtor
	 */
	public MultigearException(final int id, final String message) {
		mMessage = message;
	}
	
	/*
	 * Retorna a mensagem de erro
	 */
	final public String getMessage() {
		return mMessage;
	}
}
