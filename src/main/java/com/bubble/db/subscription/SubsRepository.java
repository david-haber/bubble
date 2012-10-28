package com.bubble.db.subscription;

public interface SubsRepository {
	
	public void createSubscription(long topic, String user, boolean subscribed);

	public boolean isSubscribed(String user, long topic);

}
