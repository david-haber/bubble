package com.bubble.db.topic;

public class TopicAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TopicAlreadyExistsException(String title) {
		super("Topic already exists: " + title);
	}

}
