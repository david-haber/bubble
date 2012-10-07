package com.bubble.signup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.social.connect.UserProfile;

public class TestSignupForm {

	
	SignupForm signupForm;
	
	@Before
	public void setup() {
		signupForm = new SignupForm();
	}
	
	@Test
	public void testCorrectlyPopulatedByUserProvider() {
		UserProfile userProfile = mock(UserProfile.class);
		when(userProfile.getUsername()).thenReturn("oliwilks");
		when(userProfile.getFirstName()).thenReturn("Oliver");
		when(userProfile.getLastName()).thenReturn("Wilkie");
		when(userProfile.getEmail()).thenReturn("oli@gmail.com");
		SignupForm form = SignupForm.fromProviderUser(userProfile);
		assertEquals("oliwilks", form.getUsername());
		assertEquals("Oliver", form.getFirstName());
		assertEquals("Wilkie", form.getLastName());
		assertEquals("oli@gmail.com", form.getEmail());
	}

}
