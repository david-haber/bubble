package com.bubble.db.notification;

import java.util.List;

public interface NotificationRepository {
	
	public List<Notification> getAllNotifs(String user);

}
