package com.bubble.db.vote;

public class UserAlreadyVotedException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserAlreadyVotedException() {
		super("You have already voted for this comment!");
	}
}
