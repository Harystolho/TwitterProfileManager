package com.harystolho.twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.ObservableList;

public class AccountManager {

	private static final Logger logger = Logger.getLogger(AccountManager.class.getName());

	public static void saveAccounts(ObservableList<TwitterAccount> observableList) {

		JSONArray accounts = new JSONArray();

		for (TwitterAccount acc : observableList) {
			accounts.put(generateAccountJSON(acc));
		}

		try (FileOutputStream fos = new FileOutputStream("accounts.json")) {

			fos.write(accounts.toString().getBytes());

		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Couldn't write to file when saving accounts.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "An exception occurred when writing to the file.");
			e.printStackTrace();
		}

	}

	public static List<TwitterAccount> loadAccounts() {

		List<TwitterAccount> accountList = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream("accounts.json")) {

			int len;
			byte[] b = new byte[1024];

			StringBuilder sb = new StringBuilder();

			while ((len = fis.read(b)) != -1) {
				sb.append(new String(b));
			}

			JSONArray accounts = new JSONArray(sb.toString());

			for (Object o : accounts) {
				JSONObject acc = (JSONObject) o;

				TwitterAccount ta = new TwitterAccount();

				ta.setUsername(acc.getString("username"));

				HashMap<String, String> cookies = new HashMap<>();

				String[] cookieArray = acc.getString("cookie").substring(1, acc.getString("cookie").length() - 1)
						.split(",");

				for (String s : cookieArray) {
					String[] ck = s.split("=", 2);
					cookies.put(ck[0].trim(), ck[1]);
				}

				ta.setCookie(cookies);

				accountList.add(ta);
			}

		} catch (FileNotFoundException e) {
			logger.log(Level.INFO, "Couldn't find a file to load.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "An exception occurred when reading to the file.");
		}

		return accountList;
	}

	private static JSONObject generateAccountJSON(TwitterAccount acc) {
		JSONObject json = new JSONObject();

		json.put("username", acc.getUsername());
		json.put("cookie", acc.getCookies().toString());

		return json;
	}

}
