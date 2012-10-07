package com.bubble.db.topic;

public class Topic {
	
	private String title;
	private long id;
	private int comments;
	private int votes;
	
	public Topic(String title, long id) {
		this.setId(id);
		this.setTitle(title);
		this.setComments(0);
		this.setVotes(0);
	}
	
	public Topic(String title, long id, int comments, int votes) {
		this.id = id;
		this.title = title;
		this.comments = comments;
		this.votes = votes;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}
}
