package com.nagarro.accounts.error.functional;

/**
 * Functional Exception for auth controller bad request 
 * @author claud
 *
 */
public class AuthentificationBadRequestException extends RuntimeException {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public AuthentificationBadRequestException(String message) {
        super(message);
    }
   

}