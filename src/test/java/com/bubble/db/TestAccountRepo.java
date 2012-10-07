package com.bubble.db;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.bubble.config.db.DBTestConfig;
import com.bubble.db.account.Account;
import com.bubble.db.account.AccountRepository;
import com.bubble.db.account.JdbcAccountRepository;
import com.bubble.db.account.UsernameAlreadyInUseException;

/*@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DBTestConfig.class}, loader=AnnotationConfigContextLoader.class)
@ActiveProfiles("test")*/
public class TestAccountRepo {
	
	@Autowired
	JdbcTemplate template;
	
	AccountRepository accountRepo;
	
	@Before
	public void beforeClass() {
		/*accountRepo = new JdbcAccountRepository(template);*/
	}
		

	@Test
	public void testNewAccountCanBeCreatedAndRetrieved() throws UsernameAlreadyInUseException {
	/*	Account newAccount = new Account("oliwilks", "Oliver", "Wilkie", 21, "Birmingham", "osw09.ic.ac.uk");
		accountRepo.createAccount(newAccount);
		Account retrievedAccount = accountRepo.findAccountByUsername("oliwilks");
		assertNotNull(retrievedAccount);
		assertEquals(newAccount, retrievedAccount);*/
	}
	
	@Test
	public void ensureCannotCreateTwoUsersWithSameUserName() throws UsernameAlreadyInUseException {
		/*Account person1 = new Account("ditto", "Oli", "Last", 32, "London", "o@d.com");
		Account person2 = new Account("ditto", "O", "W", 21, "Lo", "o@g.com");
		accountRepo.createAccount(person1);
		accountRepo.createAccount(person2);*/
	}

}
