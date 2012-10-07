package com.bubble.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for the application home page.
 */

@Controller
public class HomeController {

	/**
	 * Selects the home view to render by returning its name.
	 */
	@RequestMapping("/signin")
	public String home() {
		return "signin";
	}
	
	@RequestMapping("/signout")
	public String signout() {
		return "redirect:/";
	}
	
}
