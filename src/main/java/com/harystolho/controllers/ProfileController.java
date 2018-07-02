package com.harystolho.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.harystolho.Main;
import com.harystolho.twitter.TwitterAccount;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ProfileController {

	private static Logger logger = Logger.getLogger(ProfileController.class.getName());

	@FXML
	private Label stats;

	@FXML
	private Label followers;

	@FXML
	private Label settings;

	@FXML
	private ImageView avatar;

	@FXML
	private Label username;

	@FXML
	private Label tweets;

	@FXML
	private Label following;

	@FXML
	private Label likes;

	@FXML
	void initialize() {

		Main.getApplication().setProfileController(this);

		loadEventHandler();

	}

	private void loadEventHandler() {

	}

	public void loadProfile(TwitterAccount acc) {
		logger.log(Level.INFO, "Loading profile: " + acc.getUsername());

		Document profilePage = null;
		
		/*Response profilePageResponse = Jsoup.connect("https://twitter.com/TidderJail")
				.cookies(loginResponse.cookies()).referrer("https://twitter.com/TidderJail").method(Method.GET)
				.execute();

		Document profile = Jsoup.parse(profilePageResponse.body());*/
		
		try {
			Connection conn = Jsoup.connect("https://twitter.com/" + acc.getUsername()).cookies(acc.getCookies());

			Response res = conn.execute();

			System.out.println(res.cookies());

			try {
				Files.write(Paths.get("test.txt"), res.bodyAsBytes(), StandardOpenOption.CREATE);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// profilePage = conn.get();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't open profile.");
			return;
		}

		if (profilePage == null) {
			logger.log(Level.SEVERE, "Profile page is null.");
			return;
		}

		fillProfileInformation(profilePage);

	}

	private void fillProfileInformation(Document profilePage) {

		try {
			Files.write(Paths.get("test.txt"), profilePage.outerHtml().getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		username.setText(profilePage.selectFirst(".ProfileHeaderCard-nameLink").text());

		tweets.setText(
				profilePage.selectFirst("li.ProfileNav-item:nth-child(1) > a:nth-child(1) > span:nth-child(3)").text());

	}

}
