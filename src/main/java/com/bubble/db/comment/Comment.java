package com.bubble.db.comment;

/**
 *  This object represents a comment in our system.
 *  A comment is made by a user and belongs to a particular topic.
 *  
 *  If the comment is a reply to another comment, its field 'parent' is set
 *  to the comment the user replied to.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bubble.db.account.Account;

public class Comment implements Cloneable {
	
	private long id;
	private long topic;
	private Account commenter;
	private String photoUrl;
	private String text;
	private Date dateCreated;
	private String isoDateCreated;
	private int level;
	private int parentId; // id of parent comment
	private boolean agreeing;
	private String sentiment;
	private int positiveVotes;
	private int negativeVotes;
	private int userVote;
	private double rank;
	
	public Comment(long id, long topic, Account commenter, String text,
			int level, int parentId, Date dateCreated, String photoUrl, 
			boolean agreeing, String sentiment, Double rank) {
		this.id = id;
		this.topic = topic;
		this.commenter = commenter;
		this.text = text;
		this.dateCreated = dateCreated;
		this.level = level;
		this.parentId = parentId;
		this.isoDateCreated = toIso(dateCreated);
		this.photoUrl = photoUrl;
		this.agreeing = agreeing;
		this.sentiment = sentiment;
		this.rank = rank;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Comment clone = (Comment)super.clone();
		return clone;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
		
	}

	public int getNegativeVotes() {
		return negativeVotes;
	}

	public void setNegativeVotes(int negativeVotes) {
		this.negativeVotes = negativeVotes;
	}

	public int getPositiveVotes() {
		return positiveVotes;
	}

	public void setPositiveVotes(int positiveVotes) {
		this.positiveVotes = positiveVotes;

	}

	public long getTopic() {
		return topic;
	}
	
	public void setTopic(long topic) {
		this.topic = topic;
	}
	
	public Account getCommenter() {
		return commenter;
	}
	
	public void setCommenter(Account commenter) {
		this.commenter = commenter;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	private String toIso(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(date);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getIsoDateCreated() {
		return isoDateCreated;
	}

	public void setIsoDateCreated(String isoDateCreated) {
		this.isoDateCreated = isoDateCreated;
	}

	public boolean isAgreeing() {
		return agreeing;
	}

	public void setAgreeing(boolean agreeing) {
		this.agreeing = agreeing;
	}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}

		public int getUserVote() {
		return userVote;
	}

	public void setUserVote(int userVote) {
		this.userVote = userVote;
	}
	
	public double getRank() {
		return rank;
	}
	
	public void setRank(double rank) {
		this.rank = rank;
	}
}
