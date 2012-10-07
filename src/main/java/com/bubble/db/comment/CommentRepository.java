package com.bubble.db.comment;

public interface CommentRepository {
	
	public boolean createComment(Comment comment);
	
	public boolean deleteComment(int commentId);
	
	public Comment switchSentiment(int id);

}
