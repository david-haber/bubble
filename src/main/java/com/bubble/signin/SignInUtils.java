package com.bubble.signin;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.twitter.api.Twitter;

public class SignInUtils {

	private final Twitter twitter;
	private final Facebook facebook;

	@Inject
	public SignInUtils(Twitter twitter, Facebook facebook) {
		this.twitter = twitter;
		this.facebook = facebook;
	}

	/**
	 * Programmatically signs in the user with the given the user ID.
	 * 
	 * Has to be executed after userconnection has been added to database
	 */
	public void signin(String userId, HttpSession session) {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(userId, null, null));
		//Session
		String photoUrl = null;
		String socialNetworkLink = null;
		if (twitter.isAuthorized()) {
			photoUrl = twitter.userOperations().getUserProfile().getProfileImageUrl();
			socialNetworkLink = twitter.userOperations().getUserProfile().getProfileUrl();
		} else if (facebook.isAuthorized()) {
			String id = facebook.userOperations().getUserProfile().getId();
			photoUrl = "http://graph.facebook.com/"+id+"/picture?type=square";
			socialNetworkLink = facebook.userOperations().getUserProfile().getLink();
		}
		session.setAttribute("uname", userId);
		session.setAttribute("photoUrl", photoUrl);
		session.setAttribute("twitterUrl", socialNetworkLink);
	}

}
