package com.bubble.db.vote;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bubble.db.subscription.SubsRepository;

@Repository
public class JdbcVoteRepository implements VoteRepository {

	private final static Logger logger = LoggerFactory.getLogger(VoteRepository.class);

	private final JdbcTemplate jdbcTemplate;
	private SubsRepository subsRepo;

	@Inject
	public JdbcVoteRepository(JdbcTemplate jdbcTemplate, SubsRepository subsRepo) {
		this.jdbcTemplate = jdbcTemplate;
		this.subsRepo = subsRepo;
	}

	public void createVote(Vote vote) throws UserAlreadyVotedException {
		try {
			jdbcTemplate.update(
					"insert into votes (voter, commentid, upvote) values (?, ?, ?)",
					vote.getVoter(), vote.getCommentId(), vote.isUpvote());
			logger.debug(vote.getVoter()+" voted on comment with id: +" +
					vote.getCommentId() + " thumbsUp? " + vote.isUpvote());
			int topicId = jdbcTemplate.queryForInt("SELECT topic FROM comment WHERE id = ?", 
					vote.getCommentId());
			subsRepo.createSubscription(topicId, vote.getVoter(), true);
		} catch (DuplicateKeyException e) {
			throw new UserAlreadyVotedException();
		}
	}

	public int getPositiveVotesForComment(int commentId) {
		int positiveVotes = jdbcTemplate.queryForInt("SELECT count(*) FROM votes " +
				"WHERE commentid = ? and upvote='t';", commentId);
		return positiveVotes;
	}

	public int getNegativeVotesForComment(int commentId) {
		int negativeVotes = jdbcTemplate.queryForInt("SELECT count(*) FROM votes " +
				"WHERE commentid = ?and upvote='f';", commentId);
		return negativeVotes;
	}

	public int getUserVote(int commentId, String user) {
		try {
			Boolean agreeing = jdbcTemplate.queryForObject("SELECT (upvote) FROM votes " +
					"WHERE commentid = ?and voter=?;", 
					Boolean.class, commentId, user);
			if(agreeing) {
				return 1;
			}
			return -1;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	public int getVotesForUser(String username) {
		return jdbcTemplate.queryForInt("select count(commentid) from votes " +
				"where voter= ?;", username);
	}

}
