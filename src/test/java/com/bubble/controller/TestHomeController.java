package com.bubble.controller;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestHomeController {

	private static HomeController h;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		h = new HomeController();
	}
	
	@Test
	public void testSigninReturnsSignInView() {
		assertEquals(h.home(), "signin");
	}

	@Test
	public void testSignoutWorks() {
		assertEquals(h.signout(),"redirect:/");
	}

}
