package com.org.multigear.general.exceptions;

/**
 * 
 * Utilisado para lan�amento de erro quando uma textura n�o existe no pacote de texturas.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ResourceNotFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Private Variables
	final private int mID;
	
	/*
	 * Construtor
	 */
	public ResourceNotFound(final int id) {
		mID = id;
	}
	
	/*
	 * Retorna a mensagem de erro
	 */
	final public String getMessage() {
		return "An error occurred loading a certain resource " + mID + ". This resource does not exist in the package.";
	}
}
