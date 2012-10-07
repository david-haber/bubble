package com.bubble.db.comment;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class CommentTree {
	
	private Comment comment;
	private List<CommentTree> replies;
	
	public CommentTree(Comment comment, List<CommentTree> replies) {
		this.comment = comment;
		this.replies = replies;
	}
	
	public CommentTree() {
		replies = new CopyOnWriteArrayList<CommentTree>();
	}
	
	public CommentTree(Comment comment) {
		this.comment = comment;
		replies = new CopyOnWriteArrayList<CommentTree>();
	}		
	
	public Comment getComment() {
		return comment;
	}
	
	public List<CommentTree> getReplies() {
		return replies;
	}
	
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	public void setReplies(List<CommentTree> replies) {
		this.replies = replies;
	}
	
	public int numReplies() {
		return replies.size();
	}
	
	public void addReply(CommentTree reply) {
		replies.add(reply);
	}
}
