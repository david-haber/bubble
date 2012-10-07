package com.bubble.db.vote;

public class Vote {
	
	private int commentId;
	private String voter;
	private boolean upvote;
	
	public Vote(int commentId, String voter, boolean upvote) {
		this.commentId = commentId;
		this.voter = voter;
		this.upvote = upvote;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getVoter() {
		return voter;
	}

	public void setVoter(String voter) {
		this.voter = voter;
	}

	public boolean isUpvote() {
		return upvote;
	}

	public void setUpvote(boolean upvote) {
		this.upvote = upvote;
	}

}
