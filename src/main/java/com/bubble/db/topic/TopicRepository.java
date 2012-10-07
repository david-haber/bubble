package com.bubble.db.topic;

import java.util.List;

public interface TopicRepository {
	
	public List<Topic> getAllTopics(String wildcard);
	
	public long createTopic(String title) throws TopicAlreadyExistsException;
	
	public boolean deleteTopic(String title);

	public Topic getTopic(int topicID);
	
	public Topic getTopic(String title);

	public List<Topic> getPopularTopics();
	
	public List<Topic> getRecentTopics();

	public List<Topic> getOrdererTopics(String order);
}
