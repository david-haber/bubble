package com.bubble.db.account;

public class UsernameAlreadyInUseException extends Exception {

	private static final long serialVersionUID = 1L;

	public UsernameAlreadyInUseException(String username) {
		super("The username '" + username + "' is already in use.");
	}
}
