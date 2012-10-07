package com.bubble.config;

/**
 * Configuration for Spring Social
 */

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import com.bubble.signin.SignInUtils;
import com.bubble.signin.SimpleSignInAdapter;

@Configuration
public class SocialConfig {

	@Inject
	private DataSource dataSource;

	@Bean
	@Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES) 
	public ConnectionFactoryLocator connectionFactoryLocator() {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(
				new TwitterConnectionFactory("AF6N6nfR6qQHXE9sTbhipA",
				"lDMQQvTIk7n0ON4wYZA4uwAgxuoORDKu4hWhoU"));
		registry.addConnectionFactory(
				new FacebookConnectionFactory("160597920712932",
						"6daa2d8b2340a16e05a40b38e0d60f59"));
		return registry;
	}

	@Bean
	@Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES) 
	public UsersConnectionRepository usersConnectionRepository() {
		JdbcUsersConnectionRepository repository = 
				new JdbcUsersConnectionRepository(dataSource, 
						connectionFactoryLocator(), 
						Encryptors.noOpText());
		return repository;

	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public ConnectionRepository connectionRepository() {
		Authentication authentication = 
				SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalStateException("Unable to get a " +
					"ConnectionRepository: no user signed in");
		}
		return usersConnectionRepository().
				createConnectionRepository(authentication.getName());
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Twitter twitter() {
		Connection<Twitter> twitter = connectionRepository().
				findPrimaryConnection(Twitter.class);
		return twitter != null ? twitter.getApi() : new TwitterTemplate();
	}
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)	
	public Facebook facebook() {
		Connection<Facebook> facebook = connectionRepository().
				findPrimaryConnection(Facebook.class);
		return facebook != null ? facebook.getApi() : new FacebookTemplate();
	}
	
	@Bean
	public SignInUtils signInUtils() {
		return new SignInUtils(twitter(), facebook());
	}

	@Bean
	public ProviderSignInController providerSignInController(RequestCache cache) {
		ProviderSignInController p =  
				new ProviderSignInController(connectionFactoryLocator(), 
						usersConnectionRepository(), 
						new SimpleSignInAdapter(cache, signInUtils()));
		p.setPostSignInUrl("/");
		return p;
	}

}
