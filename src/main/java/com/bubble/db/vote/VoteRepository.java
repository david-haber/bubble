package com.bubble.db.vote;

public interface VoteRepository {
	
	public void createVote(Vote vote) throws UserAlreadyVotedException;
	
	public int getPositiveVotesForComment(int commentId);
	
	public int getNegativeVotesForComment(int commentId);
	
	public int getUserVote(int commentId, String user);
}
