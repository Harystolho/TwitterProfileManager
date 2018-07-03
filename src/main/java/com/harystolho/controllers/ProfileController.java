package com.harystolho.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.harystolho.Main;
import com.harystolho.twitter.TwitterAccount;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ProfileController {

	private static Logger logger = Logger.getLogger(ProfileController.class.getName());

	@FXML
	private Label stats;

	@FXML
	private Label followers;

	@FXML
	private Label settings;

	@FXML
	private Pane statsPane;

	@FXML
	private Group paneGroup;

	@FXML
	private Label tweets;

	@FXML
	private Label followersCount;

	@FXML
	private Label following;

	@FXML
	private Label likes;

	@FXML
	private Label username;

	@FXML
	private ImageView avatar;

	@FXML
	private Pane followersPane;

	private List<Label> menuLabels;

	@FXML
	void initialize() {

		Main.getApplication().setProfileController(this);

		createMenuLabeList();
		setMenuLabel(stats, statsPane);

		loadEventHandler();

	}

	private void loadEventHandler() {

		stats.setOnMouseClicked((e) -> {
			setMenuLabel(stats, statsPane);

		});

		followers.setOnMouseClicked((e) -> {
			setMenuLabel(followers, followersPane);
		});

		settings.setOnMouseClicked((e) -> {
			setMenuLabel(settings, null);

		});
	}

	private void createMenuLabeList() {
		menuLabels = new LinkedList<>();

		menuLabels.add(stats);
		menuLabels.add(followers);
		menuLabels.add(settings);
	}

	private void setMenuLabel(Label label, Pane pane) {

		for (Label l : menuLabels) {
			l.getStyleClass().remove("selectedLabel");
		}

		for (Node node : paneGroup.getChildren()) {
			node.setVisible(false);
		}

		label.getStyleClass().add("selectedLabel");

		if (pane != null) {
			pane.setVisible(true);
		}
	}

	/**
	 * Connects to an user profile page and retrieves the page.
	 * 
	 * @param acc
	 */
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

	/**
	 * Shows information about user profile.
	 * 
	 * @param profilePage
	 */
	private void fillProfileInformation(Document profilePage) {

		try {
			avatar.setImage(new Image(profilePage.selectFirst(".ProfileAvatar-image").attr("src")));

			username.setText(profilePage.selectFirst(".ProfileHeaderCard-nameLink").text());

			tweets.setText(profilePage
					.selectFirst("li.ProfileNav-item:nth-child(1) > a:nth-child(1) > span:nth-child(3)").text());

			followersCount.setText(
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
