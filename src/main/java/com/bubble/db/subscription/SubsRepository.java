package com.bubble.db.subscription;

public interface SubsRepository {
	
	public void createSubscription(int topic, String user, boolean subscribed);

	public boolean isSubscribed(String user, int topic);

}
