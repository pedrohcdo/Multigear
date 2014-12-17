package multigear.services;

/**
 * Service Exception
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public ServiceException() {
    }

    /**
     * Constructor
     */
    public ServiceException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public ServiceException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
