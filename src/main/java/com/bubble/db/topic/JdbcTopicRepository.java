package com.bubble.db.topic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcTopicRepository implements TopicRepository {

	private final static Logger logger = LoggerFactory.getLogger(TopicRepository.class);

	private final JdbcTemplate jdbcTemplate;

	@Inject
	public
	JdbcTopicRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Return a list containing all the topics stored in the database
	 */
	public List<Topic> getAllTopics(String wildcard) {
		String query = "SELECT * FROM topic";
		if(wildcard != null) {
			query += " WHERE title LIKE '"+ wildcard + "%'"; 
		}
		query += " ORDER BY dateCreated DESC limit 5;";
		List<Topic> topics = jdbcTemplate.query(query, new RowMapper<Topic>() {
			// row mapper that extracts the title
			public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
				Topic topic = new Topic(rs.getString("title"), rs.getInt("id"));
				return topic;
			}
		});
		return topics;
	}

	public Topic getTopic(int topicID) {
		Topic topic = jdbcTemplate.queryForObject("SELECT * FROM topic WHERE id = " + 
				topicID + ";", new RowMapper<Topic>() {
			public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Topic(rs.getString("title"), rs.getInt("id"));
			}
		});

		return topic;
	}

	public Topic getTopic(String title) {
		Topic topic = jdbcTemplate.queryForObject("SELECT * FROM topic WHERE title = '" + 
				title + "';", new RowMapper<Topic>() {
			public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Topic(rs.getString("title"), rs.getInt("id"));
			}
		});

		return topic;
	}


	@Transactional
	public long createTopic(final String title) throws TopicAlreadyExistsException {
		final String sql = "INSERT INTO topic (title) VALUES (?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, title);
				return ps;
			}
		};
		boolean result = false;
		try {
			result = (jdbcTemplate.update(psc, keyHolder) == 1);
		} catch (Exception e) {
			throw new TopicAlreadyExistsException(title);
		}
		if (!result) {
			throw new TopicAlreadyExistsException(title);
		}
		logger.info("New topic created: "+title);
		Map<String,Object> keys = keyHolder.getKeys();
		Map.Entry<String, Object> entry = keys.entrySet().iterator().next();
		Number n = (Number) entry.getValue();
		return n.longValue();
	}

	@Transactional
	public boolean deleteTopic(String title) {
		return jdbcTemplate.update("DELETE FROM topic WHERE title = ?;", title) == 1;
	}

	public List<Topic> getRecentTopics() {
		List<Topic> topics = jdbcTemplate.query("SELECT comments, votes, topic, topic.title FROM " +
				"			(SELECT COUNT(comment.id) AS comments, COUNT(voter) AS votes, topic FROM comment LEFT JOIN votes ON comment.id = votes.commentid GROUP BY topic) AS t, topic " +
				"			WHERE topic.id = t.topic ORDER BY datecreated DESC LIMIT 5;", new RowMapper<Topic>() {
			// row mapper that extracts the title
			public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
				Topic topic = new Topic(rs.getString("title"), rs.getInt("topic"), rs.getInt("comments"), rs.getInt("votes"));
				return topic;
			}
		});
		return topics;
	}

	public List<Topic> getPopularTopics() {
		List<Topic> topics = jdbcTemplate.query("SELECT comments, votes, topic, topic.title FROM (" +
				"		SELECT COUNT(comment.id) AS comments, count(voter) AS votes, topic FROM comment LEFT JOIN votes ON comment.id = votes.commentid GROUP BY topic) AS t, topic " +
				"		WHERE topic.id = t.topic ORDER BY (comments + votes) desc LIMIT 5;", new RowMapper<Topic>() {
			// row mapper that extracts the title
			public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
				Topic topic = new Topic(rs.getString("title"), rs.getInt("topic"), rs.getInt("comments"), rs.getInt("votes"));
				return topic;
			}
		});
		return topics;
	}

	public List<Topic> getOrdererTopics(String order) {
		List<Topic> topics;
		if(order.equals("pop")) {
			topics = jdbcTemplate.query("SELECT comments, votes, topic, topic.title FROM (" +
					"		SELECT COUNT(comment.id) AS comments, count(voter) AS votes, topic FROM comment LEFT JOIN votes ON comment.id = votes.commentid GROUP BY topic) AS t, topic " +
					"		WHERE topic.id = t.topic ORDER BY (comments + votes) desc;", new RowMapper<Topic>() {
				// row mapper that extracts the title
				public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
					Topic topic = new Topic(rs.getString("title"), rs.getInt("topic"), rs.getInt("comments"), rs.getInt("votes"));
					return topic;
				}
			});
		} else {
			topics = jdbcTemplate.query("SELECT comments, votes, topic, topic.title FROM " +
					"			(SELECT COUNT(comment.id) AS comments, COUNT(voter) AS votes, topic FROM comment LEFT JOIN votes ON comment.id = votes.commentid GROUP BY topic) AS t, topic " +
					"			WHERE topic.id = t.topic ORDER BY datecreated DESC;", new RowMapper<Topic>() {
				// row mapper that extracts the title
				public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
					Topic topic = new Topic(rs.getString("title"), rs.getInt("topic"), rs.getInt("comments"), rs.getInt("votes"));
					return topic;
				}
			});
		}
		return topics;
	}
}
