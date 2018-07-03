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
import javafx.scene.image.Image;
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

		try {
			Response profilePageResponse = Jsoup.connect("https://twitter.com/" + acc.getUsername())
					.cookies(acc.getCookies()).referrer("https://twitter.com").method(Method.GET).execute();

			profilePage = Jsoup.parse(profilePageResponse.body());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Couldn't display profile information.");
		}

		fillProfileInformation(profilePage);

	}

	private void fillProfileInformation(Document profilePage) {

		try {
			avatar.setImage(new Image(profilePage.selectFirst(".ProfileAvatar-image").attr("src")));

			username.setText(profilePage.selectFirst(".ProfileHeaderCard-nameLink").text());

			tweets.setText(profilePage
					.selectFirst("li.ProfileNav-item:nth-child(1) > a:nth-child(1) > span:nth-child(3)").text());

			followers.setText(
					profilePage.selectFirst("li.ProfileNav-item:nth-child(3) > a:nth-child(1) > span:nth-child(3)")
							.attr("data-count"));

			following.setText(
					profilePage.selectFirst("li.ProfileNav-item:nth-child(2) > a:nth-child(1) > span:nth-child(3)")
							.attr("data-count"));

		} catch (Exception e) {
			// Do nothing.
		}

	}

}
