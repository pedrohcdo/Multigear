package com.org.multigear.general.exceptions;

/**
 * 
 * Utilisado para lan�amento de erro quando modifica��o ou altera��o n�o � permitida.
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
