package com.bubble.signup;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.bubble.db.account.Account;
import com.bubble.db.account.AccountRepository;
import com.bubble.db.account.UsernameAlreadyInUseException;
import com.bubble.signin.SignInUtils;

@Controller
public class SignupController {

	private final AccountRepository accountRepository;

	private final SignInUtils signInUtils;

	@Inject
	public SignupController(AccountRepository accountRepository, SignInUtils utils) {
		this.accountRepository = accountRepository;
		this.signInUtils = utils;
	}

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public SignupForm signupForm(WebRequest request, Model model) {
		Connection<?> connection = ProviderSignInUtils.getConnection(request);
		if (connection != null) {
			return SignupForm.fromProviderUser(connection.fetchUserProfile());
		} else {
			return new SignupForm();
		}
	}

	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(HttpSession session, @Valid SignupForm form, 
			BindingResult formBinding, WebRequest request) {
		if (formBinding.hasErrors()) {
			return null;
		}
		Account account = createAccount(form, formBinding);
		if (account != null) {
			ProviderSignInUtils.handlePostSignUp(account.getUsername(), request);
			signInUtils.signin(account.getUsername(), session);
			return "redirect:/";
		}
		return null;
	}

	// internal helpers
	private Account createAccount(SignupForm form, BindingResult formBinding) {
		try {
			Account account = new Account(form.getUsername(), 
					form.getFirstName(), 
					form.getLastName(), 
					form.getAge(),
					form.getLocation(),
					form.getEmail());
			accountRepository.createAccount(account);
			return account;
		} catch (UsernameAlreadyInUseException e) {
			formBinding.rejectValue("username", "user.duplicateUsername", "already in use");
			return null;
		}
	}

}
