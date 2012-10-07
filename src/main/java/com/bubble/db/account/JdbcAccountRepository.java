package com.bubble.db.account;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class JdbcAccountRepository implements AccountRepository {

	private final JdbcTemplate jdbcTemplate;
	
	private final static Logger logger = LoggerFactory.getLogger(AccountRepository.class);
	
	@Inject
	public JdbcAccountRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	public void createAccount(Account user) throws UsernameAlreadyInUseException {
		try {
			jdbcTemplate.update(
					"insert into account (firstName, lastName, " +
					"username, age, location, email) values (?, ?, ?, ?, ?, ?)",
					user.getFirstName(), 
					user.getLastName(), 
					user.getUsername(),
					user.getAge(),
					user.getLocation(),
					user.getEmail());
			logger.info("New account created in Bubble database for "
					+user.getFirstName()+" "+user.getLastName());
		} catch (DuplicateKeyException e) {
			throw new UsernameAlreadyInUseException(user.getUsername());
		}
	}

	public Account findAccountByUsername(String username) {
		return jdbcTemplate.queryForObject(
				"select username, firstName, lastName, age," +
				" location, email from account where username = ?",
				new RowMapper<Account>() {
					public Account mapRow(ResultSet rs, int rowNum) 
							throws SQLException {
						return new Account(rs.getString("username"),
								rs.getString("firstName"),
								rs.getString("lastName"),
								rs.getInt("age"),
								rs.getString("location"),
								rs.getString("email"));
					}
				}, username);
	}

}
