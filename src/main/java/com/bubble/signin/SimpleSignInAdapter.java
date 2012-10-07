package com.bubble.signin;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

public class SimpleSignInAdapter implements SignInAdapter {

	private final RequestCache requestCache;
	private final SignInUtils signInUtils;

	@Inject
	public SimpleSignInAdapter(RequestCache requestCache, SignInUtils signInUtils) {
		this.requestCache = requestCache;
		this.signInUtils = signInUtils;
	}

	public String signIn(String localUserId, Connection<?> connection, 
			NativeWebRequest request) {
		HttpSession session = 
				request.getNativeRequest(HttpServletRequest.class).getSession(false);
		signInUtils.signin(localUserId, session); 
		return extractOriginalUrl(request);
	}

	private String extractOriginalUrl(NativeWebRequest request) {
		HttpServletRequest nativeReq = 
				request.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse nativeRes = 
				request.getNativeResponse(HttpServletResponse.class);
		SavedRequest saved = requestCache.getRequest(nativeReq, nativeRes);
		if (saved == null) {
			return null;
		}
		requestCache.removeRequest(nativeReq, nativeRes);
		removeAutheticationAttributes(nativeReq.getSession(false));
		return saved.getRedirectUrl();
	}

	private void removeAutheticationAttributes(HttpSession session) {
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}


}
