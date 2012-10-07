package com.bubble.db.notification;

import java.util.Date;

public class Notification {
	
	private String user;
	private String photoUrl;
	private int topicId;
	private String topicTitle;
	private int notifId; //comment id or -1 if vote
	private String comment;
	private Date datecreated;
	private String commenter;
	
	public Notification(String user, String comment, String photoUrl, 
			int topicId, String topicTitle, int notifId,
			Date datecreated, String commenter) {
		this.setUser(user);
		this.setPhotoUrl(photoUrl);
		this.setTopicId(topicId);
		this.setTopicTitle(topicTitle);
		this.setNotifId(notifId);
		this.setDatecreated(datecreated);
		this.setComment(comment);
		this.setCommenter(commenter);
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public int getNotifId() {
		return notifId;
	}

	public void setNotifId(int notifId) {
		this.notifId = notifId;
	}

	public Date getDatecreated() {
		return datecreated;
	}

	public void setDatecreated(Date datecreated) {
		this.datecreated = datecreated;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommenter() {
		return commenter;
	}

	public void setCommenter(String commenter) {
		this.commenter = commenter;
	}
}
