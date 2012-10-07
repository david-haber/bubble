package com.bubble.db.topic;

public class TopicForm {
	
	private String title;
	private String initialComment;
	
	public String getInitialComment() {
		return initialComment;
	}
	public void setInitialComment(String initialComment) {
		this.initialComment = initialComment;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean validate() {
		return !(title.trim().isEmpty() || initialComment.trim().isEmpty());
	}

}
