package com.bubble.account;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bubble.db.account.Account;

public class TestAccount {

	@Test
	public void testAccount() {
		String userName = "oliwilks";
		String firstName = "Oliver";
		String lastName = "Wilkie";
		int age = 21;
		String location = "England";
		String email = "ozzyoli@googlemail.com";
		Account newAcc = new Account(userName, firstName, lastName, age, location, email);
		assertEquals(userName, newAcc.getUsername());
		assertEquals(firstName, newAcc.getFirstName());
		assertEquals(lastName, newAcc.getLastName());
		assertEquals(age, newAcc.getAge());
		assertEquals(location, newAcc.getLocation());
		assertEquals(email, newAcc.getEmail());
	}

}
