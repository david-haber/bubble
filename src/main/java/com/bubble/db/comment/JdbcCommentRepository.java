package com.bubble.db.comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import com.bubble.db.account.JdbcAccountRepository;
import com.bubble.db.subscription.SubsRepository;
import com.bubble.db.vote.JdbcVoteRepository;

@Repository
public class JdbcCommentRepository implements CommentRepository {

	private static final Logger logger = LoggerFactory.getLogger(CommentRepository.class);

	private final JdbcTemplate jdbcTemplate;
	private JdbcVoteRepository voteRepo;
	private JdbcAccountRepository accountRepo;
	private SubsRepository subsRepo;

	@Inject
	public JdbcCommentRepository(JdbcTemplate jdbcTemplate, 
			JdbcVoteRepository voteRepo, 
			SubsRepository subsRepo, 
			JdbcAccountRepository accountRepo) {
		this.voteRepo = voteRepo;
		this.accountRepo = accountRepo;
		this.jdbcTemplate = jdbcTemplate;
		this.subsRepo = subsRepo;
	}

	public boolean createComment(Comment comment) {
		final String sql = 
				"INSERT INTO comment (topic, commenter, " +
						"text, level, parent, agreeing, sentiment, rank) " +
						"VALUES (?,?,?,?,?,?,'"+comment.getSentiment()+"',?);";
		try {
			final Comment newComment = (Comment) comment.clone();
			KeyHolder keyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, 
							Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, newComment.getTopic());
					ps.setString(2, newComment.getCommenter().getUsername());
					ps.setString(3, newComment.getText());
					ps.setInt(4, newComment.getLevel());
					ps.setInt(5, newComment.getParentId());
					ps.setBoolean(6, newComment.isAgreeing());
					ps.setDouble(7, newComment.getRank());
					return ps;
				}
			};

			boolean result = (jdbcTemplate.update(psc, keyHolder) == 1);

			Number generatedId = (Number) keyHolder.getKeyList().get(0).get("id");
			Long l = generatedId.longValue();
			comment.setId(l);
			logger.debug("Inserted new comment into database from "
					+newComment.getCommenter()+" about topidID "+newComment.getTopic());
			subsRepo.createSubscription(generatedId.intValue(), 
					comment.getCommenter().getUsername(), true);
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Comment switchSentiment(final int id) {
		List<Comment> comments = jdbcTemplate.query(
				"SELECT * FROM comment WHERE id = " + id
				, new RowMapper<Comment>() {
					// row mapper that extracts the comment's contents
					public Comment mapRow(ResultSet rs, int rowNum) 
							throws SQLException {
						SimpleDateFormat dateParser = 
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = dateParser.parse(rs.getString("datecreated"), 
								new ParsePosition(0));
						Comment comment = new Comment(rs.getLong("id"), 
								rs.getLong("topic"), 
								accountRepo.findAccountByUsername(rs.getString("commenter")), 
								rs.getString("text"),
								rs.getInt("level"), 
								rs.getInt("parent"), date, null, rs.getBoolean("agreeing"), 
								rs.getString("sentiment"),ArgumentGame.trunc(rs.getDouble("rank")));
						return comment;
					}
				});
		if (comments.size() == 1) {
			Comment comment = comments.get(0);
			String sentiment = comment.getSentiment();
			logger.debug("Sentiment: " + sentiment);
			if (sentiment.equals("positive")) {
				sentiment = "negative";
			} else {
				sentiment = "positive";
			}
			// update sentiment
			comment.setSentiment(sentiment);
			//update database
			final String sql = "UPDATE comment SET sentiment = '" 
					+ sentiment + "' WHERE id = ?";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, 
							Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, id);
					return ps;
				}
			};

			if (jdbcTemplate.update(psc, keyHolder) == 1) {
				// return comment
				return comment;
			}
		} 
		return null;
	}




	public boolean deleteComment(int commentId) {
		logger.debug("Deleted comment with id "+commentId);
		return jdbcTemplate.update("DELETE FROM comment WHERE id = ?", commentId) == 1;
	}


	public Map<Long, CommentTree> getCommentsAsMap(int topicID, final String username) {			

		List<Comment> comments = getCommentsAsList(topicID, username);

		Map<Long, CommentTree> treeMaker = new HashMap<Long, CommentTree>();				

		Iterator<Comment> itr = comments.iterator();
		while(itr.hasNext()) {
			Comment comment = itr.next();
			CommentTree commentNode = new CommentTree(comment);
			treeMaker.put(new Long(comment.getId()), commentNode);

			if(comment.getLevel() != 0) { 
				CommentTree parent = treeMaker.get(new Long(comment.getParentId()));
				parent.addReply(commentNode);
			}			
		}		
		return treeMaker;
	}

	public CommentTree getCommentsAsTree(int topicID, final String username) {			
		List<Comment> comments = getCommentsAsList(topicID, username);
		Map<Long, CommentTree> treeMaker = new HashMap<Long, CommentTree>();				
		CommentTree forest = new CommentTree();		
		Iterator<Comment> itr = comments.iterator();
		while(itr.hasNext()) {
			Comment comment = itr.next();
			CommentTree commentNode = new CommentTree(comment);
			treeMaker.put(new Long(comment.getId()), commentNode);

			if(comment.getLevel() == 0) 
				forest.addReply(commentNode);
			else { 
				CommentTree parent = treeMaker.get(new Long(comment.getParentId()));
				parent.addReply(commentNode);
			}			
		}		
		return forest;
	}


	public List<Comment> getCommentsAsList(int topicID, final String username) {
		List<Comment> comments = jdbcTemplate.query(
				"SELECT * FROM comment INNER JOIN userconnection " +
						"ON (comment.commenter = userconnection.userid) " +
						"WHERE topic = " + topicID + 
						"ORDER BY id, parent, datecreated DESC;", new RowMapper<Comment>() {
					// row mapper that extracts the comment's contents
					public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
						SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = dateParser.parse(rs.getString("datecreated"), new ParsePosition(0));
						int positiveVotes = voteRepo.getPositiveVotesForComment(rs.getInt("id"));
						int negativeVotes = voteRepo.getNegativeVotesForComment(rs.getInt("id"));
						int userVote = voteRepo.getUserVote(rs.getInt("id"), username);
						Comment comment = new Comment(rs.getLong("id"), rs.getLong("topic"), 
								accountRepo.findAccountByUsername(rs.getString("commenter")), 
								rs.getString("text"), rs.getInt("level"), rs.getInt("parent"), 
								date, rs.getString("imageurl"), rs.getBoolean("agreeing"), 
								rs.getString("sentiment"),ArgumentGame.trunc(rs.getDouble("rank")));
						comment.setNegativeVotes(negativeVotes);
						comment.setPositiveVotes(positiveVotes);
						comment.setUserVote(userVote);
						return comment;
					}
				});

		return comments;
	}

	public int getLevel(int commentId) {
		int level = jdbcTemplate.queryForInt(
				"SELECT level FROM comment WHERE id = ?;", commentId);
		return level;
	}

	public void updateRankInDB(int id, double outcome) {
		jdbcTemplate.update(
				"update comment set rank = ? WHERE id = ?", new Object[] {outcome, id});
	}

	public int getCommentsForUser(String user) {
		return jdbcTemplate.queryForInt(
				"SELECT count(*) FROM comment WHERE commenter = ?;", user);
	}

	public int getRankForUser(String username, int comments) {
		if (comments == 0) {
			return 0;
		}
		int rank = jdbcTemplate.queryForInt(
				"SELECT sum(rank) FROM comment WHERE commenter = ?;", username);
		System.out.println(rank);
		return rank/comments;
	}
}
