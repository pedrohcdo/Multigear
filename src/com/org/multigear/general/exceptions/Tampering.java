package com.org.multigear.general.exceptions;

/**
 * 
 * Utilisado para lançamento de erro quando modificação ou alteração não é permitida.
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class Tampering extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Private Variables
	final private String mError;
	
	/*
	 * Construtor
	 */
	public Tampering(final String error) {
		mError = error;
	}
	
	/*
	 * Retorna a mensagem de erro
	 */
	final public String getMessage() {
		return mError;
	}
}
