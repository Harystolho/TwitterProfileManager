package com.harystolho.controllers;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.harystolho.Main;
import com.harystolho.twitter.TwitterAccount;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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

	@FXML
	private ScrollPane scrollPane;

	private List<Label> menuLabels;

	private TwitterAccount currentAccount;

	private String followersPosition;

	private boolean requestSent = false;

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
			getFollowersList();
		});

		settings.setOnMouseClicked((e) -> {
			setMenuLabel(settings, null);
		});

		scrollPane.vvalueProperty().addListener((obv, oldValue, newValue) -> {

			if (newValue.intValue() == 1) {
				if (!requestSent) {
					requestSent = true;
					getMoreFollowers();
				}
			}
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
	 * Sends a request to twitter asking for the followers list. It will return the
	 * first 18 followers here. First use <code>this</code> method then use
	 * <code>getMoreFollowers()</code> to request the others.
	 */
	private void getFollowersList() {

		followersPosition = "";

		try {
			Document followersPage = Jsoup
					.connect("https://twitter.com/" + getCurrentAccount().getUsername() + "/following")
					.cookies(getCurrentAccount().getCookies()).referrer("https://twitter.com").get();

			Element followersGrid = followersPage.getElementsByClass("GridTimeline-items").get(0);

			followersPosition = followersGrid.attr("data-min-position");

			Elements followersList = followersGrid.getElementsByClass("Grid--withGutter");

			displayFollower(followersList);

		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't get followers list.");
			// TODO show alert ?
		}

	}

	/**
	 * Request followers list using <code>followersPosition</code>. It will return
	 * and add to the ScrollPane 18 followers every time <code>this method</code> is
	 * called.
	 */
	private void getMoreFollowers() {

		try {
			Response res = Jsoup
					.connect("https://twitter.com/" + getCurrentAccount().getUsername()
							+ "/following/users?include_available_features=1&include_entities=1" + "&max_position="
							+ followersPosition + "&reset_error_state=false")
					.referrer("https://twitter.com").ignoreContentType(true).cookies(getCurrentAccount().getCookies())
					.execute();

			JSONObject json = new JSONObject(res.body());

			followersPosition = json.getString("min_position");

			Document moreFollowers = Jsoup.parse(json.getString("items_html"));

			displayFollower(moreFollowers.getElementsByClass("Grid--withGutter"));

			requestSent = false;

		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't laod more followers.");
			return;
		}

	}

	/**
	 * <pre>
	 * {@code
	 * <div class="Grid Grid--withGutter" data-component-context="user" role=
	"presentation">
	 * 	<div class="Grid-cell u-size1of2 u-lg-size1of3 u-mb10" data-test-selector=ProfileTimelineUser" role="presentation"></div>
	 * 	<div class="Grid-cell u-size1of2 u-lg-size1of3 u-mb10" data-test-selector=ProfileTimelineUser" role="presentation"></div>
	 * 	<div class="Grid-cell u-size1of2 u-lg-size1of3 u-mb10" data-test-selector=ProfileTimelineUser" role="presentation"></div>
	 * 	<div class="Grid-cell u-size1of2 u-lg-size1of3 u-mb10" data-test-selector=ProfileTimelineUser" role="presentation"></div>
	 * 	<div class="Grid-cell u-size1of2 u-lg-size1of3 u-mb10" data-test-selector=ProfileTimelineUser" role="presentation"></div>
	 * </div>
	 * }
	 * </pre>
	 * 
	 * The node must contain the ".Grid--withGutter" class.
	 * 
	 * @param followers
	 *            a node containing many follower's node inside it.
	 */
	private void displayFollower(Elements followers) {

		for (Element followersGroup : followers) {
			// TOOD optimize this if needed.
			for (Element follower : followersGroup.getElementsByClass("Grid-cell")) {

				Element user = follower.getElementsByClass("ProfileCard").first();
				addFollowerToScrollPane(user);
			}

		}

	}

	// TODO optimize this class
	/**
	 * Uses a <code>html node</code> to get information about the follower and
	 * displays it on the scroll pane.
	 * 
	 * @param follower
	 */
	private void addFollowerToScrollPane(Element follower) {

		FlowPane flowPane = (FlowPane) scrollPane.getContent();

		// 2 followers per column. fullWidth/2
		double halfFlowPaneWidth = flowPane.getWidth() / 2;

		Pane followerPane = new Pane();
		followerPane.setPrefWidth(halfFlowPaneWidth - 5);
		followerPane.setPrefHeight(0.3 * halfFlowPaneWidth);
		followerPane.setId(follower.attr("data-user-id"));

		// Username
		Label username = new Label(follower.getElementsByClass("btn-group").first().attr("data-name"));
		username.setPrefWidth(followerPane.getPrefWidth());

		// @Username
		Label twitterAcc = new Label("@" + follower.getElementsByClass("btn-group").first().attr("data-screen-name"));
		twitterAcc.setTranslateY(25);
		twitterAcc.setPrefWidth(followerPane.getPrefWidth());
		twitterAcc.getStyleClass().add("twitterAccount");

		// Follow/Unfollow
		Button followUnfollow = new Button("Follow");
		followUnfollow.setTranslateX(halfFlowPaneWidth - 75);

		
		followerPane.getChildren().addAll(username, twitterAcc, followUnfollow);

		flowPane.getChildren().add(followerPane);

	}

	/**
	 * Connects to an user profile page and retrieves the page.
	 * 
	 * @param acc
	 */
	public void loadProfile(TwitterAccount acc) {
		logger.log(Level.INFO, "Loading profile: " + acc.getUsername());

		currentAccount = acc;

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

	private TwitterAccount getCurrentAccount() {
		return currentAccount;
	}

}
