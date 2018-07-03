package com.harystolho.utils;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TPMCookieStore implements CookieStore {

	Map<URI, List<HttpCookie>> store;

	public TPMCookieStore() {
		store = new HashMap<>();
	}

	@Override
	public void add(URI uri, HttpCookie cookie) {
		System.out.println(uri + "," + cookie);
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		return store.get(uri);
	}

	@Override
	public List<HttpCookie> getCookies() {
		List<HttpCookie> allCookies = new ArrayList<>();

		for (List<HttpCookie> l : store.values()) {
			for (HttpCookie hc : l) {
				allCookies.add(hc);
			}
		}

		return allCookies;
	}

	@Override
	public List<URI> getURIs() {
		return new ArrayList<>(store.keySet());
	}

	@Override
	public boolean remove(URI uri, HttpCookie cookie) {
		return store.remove(uri, cookie);
	}

	@Override
	public boolean removeAll() {
		store.clear();
		return true;
	}

}
