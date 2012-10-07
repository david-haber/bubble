package com.bubble.db.notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcNotificationRepository implements NotificationRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
	@Inject
	public
	JdbcNotificationRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Notification> getAllNotifs(String user) {
		List<Notification> notifs = jdbcTemplate.query("" +
				"select voter as user, imageurl, votes.datecreated as datecreated, " +
				"comment.topic as topic, null as commentid, topic.title, " +
				"comment.text, comment.commenter as commenter from votes, " +
				"comment, userconnection, topic, subscriptions " +
					"where votes.commentid = comment.id and comment.topic = " +
					"subscriptions.topic and subscriptions.userid = '" + user + 
					"' and comment.topic = topic.id and votes.voter = " +
					"userconnection.userid and votes.datecreated > subscriptions.datecreated union " +
				"select comment.commenter, imageurl, comment.datecreated, comment.topic, " +
				"comment.id, topic.title, comment.text, null from comment, userconnection, " +
				"topic, subscriptions " + 
					"where comment.commenter = userconnection.userid and '" + 
				user + "'= subscriptions.userid and comment.datecreated > subscriptions.datecreated " +
						"and comment.topic = subscriptions.topic and comment.topic = " +
						"topic.id and subscriptions.subscribed = 't' " + 
				" order by datecreated DESC limit 10;", new RowMapper<Notification>() {
			public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
				int commentId = rs.getInt("commentid");
				if (rs.wasNull()) {
					commentId = -1;
				}
				SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = dateParser.parse(rs.getString("datecreated"), new ParsePosition(0));
				Notification notif = new Notification(rs.getString("user"), 
						rs.getString("text"), rs.getString("imageurl"), 
						rs.getInt("topic"), rs.getString("title"), 
						commentId, date, rs.getString("commenter"));
				return notif;
				
			}
		});
		return notifs;
	}
}
