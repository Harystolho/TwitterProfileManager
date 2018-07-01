package com.harystolho.twitter;

import java.util.HashMap;

public class TwitterAccount {

	private String username;
	private HashMap<String, String> cookies;

	public TwitterAccount() {
		cookies = new HashMap<>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setCookie(String cookies) {

		String[] cookieList = cookies.split(";");

		for (String ck : cookieList) {
			String[] key_value = ck.split("=", 2);
			this.cookies.put(key_value[0].trim(), key_value[1].trim());
		}

	}

	public HashMap<String, String> getCookies() {
		return cookies;
	}

	@Override
	public String toString() {
		return "@" + username;
	}

}
