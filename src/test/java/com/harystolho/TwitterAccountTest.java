package com.harystolho;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.harystolho.twitter.TwitterAccount;

public class TwitterAccountTest {

	TwitterAccount acc;

	@Before
	public void createAccunt() {
		acc = new TwitterAccount();
	}

	@Test
	public void testUsername() {
		acc.setUsername("user_name");

		assertEquals(acc.getUsername(), "user_name");
		assertEquals(acc.toString(), "@user_name");
	}

	@Test
	public void testCookies() {
		acc.setCookie("id=10");
		assertEquals(acc.getCookies().get("id"), "10");
	}

}
