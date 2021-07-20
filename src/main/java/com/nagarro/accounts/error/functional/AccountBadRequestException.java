package com.nagarro.accounts.error.functional;

/**
 * Functional Exception for account controller bad request 
 * @author claud
 *
 */
public class AccountBadRequestException extends RuntimeException {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public AccountBadRequestException(String message) {
        super(message);
    }
   

}