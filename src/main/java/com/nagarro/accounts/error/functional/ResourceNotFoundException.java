package com.nagarro.accounts.error.functional;

public class ResourceNotFoundException extends AccountBadRequestException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
        super(message);
    }

}