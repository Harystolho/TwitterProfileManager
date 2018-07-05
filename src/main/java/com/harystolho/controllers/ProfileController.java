package com.harystolho.controllers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
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
	private Label following;

	@FXML
	private Label settings;

	@FXML
	private Group paneGroup;

	@FXML
	private Label tweets;

	@FXML
	private Label followersCount;

	@FXML
	private Label followingCount;

	@FXML
	private Label likes;

	@FXML
	private Label username;

	@FXML
	private ImageView avatar;

	@FXML
	private Pane statsPane;

	@FXML
	private Pane followersPane;

	@FXML
	private Pane followingPane;

	@FXML
	private ScrollPane followerScrollPane;

	@FXML
	private ScrollPane followingScrollPane;

	private List<Label> menuLabels;

	private FlowPane flowPane;

	private TwitterAccount currentAccount;

	private String followPosition;

	private boolean requestSent = false;

	private static enum follow {
		follower, following
	};

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

			flowPane = (FlowPane) followerScrollPane.getContent();

			flowPane.getChildren().clear();

			getFollowList(follow.follower);
			getMoreFollow(follow.follower);
		});

		following.setOnMouseClicked((e) -> {
			setMenuLabel(following, followingPane);

			flowPane = (FlowPane) followingScrollPane.getContent();

			flowPane.getChildren().clear();

			getFollowList(follow.following);
			getMoreFollow(follow.following);

		});

		settings.setOnMouseClicked((e) -> {
			setMenuLabel(settings, null);
		});

		followerScrollPane.vvalueProperty().addListener((obv, oldValue, newValue) -> {
			if (newValue.intValue() == 1) {
				if (!requestSent) {
					requestSent = true;

					getMoreFollow(follow.follower);
				}
			}
		});

		followingScrollPane.vvalueProperty().addListener((obv, oldValue, newValue) -> {
			if (newValue.intValue() == 1) {
				if (!requestSent) {
					requestSent = true;

					getMoreFollow(follow.following);
				}
			}
		});

	}

	/**
	 * This list exists to iterate through the menu labels.
	 */
	private void createMenuLabeList() {
		menuLabels = new LinkedList<>();

		menuLabels.add(stats);
		menuLabels.add(followers);
		menuLabels.add(following);
		menuLabels.add(settings);
	}

	/**
	 * Iterates through {@link #menuLabels} to remove the bottom border and adds the
	 * bottom border to the <code>label</code>. Then shows the <code>pane</code>.
	 * 
	 * @param label
	 * @param pane
	 */
	private void setMenuLabel(Label label, Pane pane) {

		for (Label l : menuLabels) {
			l.getStyleClass().remove("selectedLabel");
		}

		for (Node node : paneGroup.getChildren()) {
			node.setVisible(false);
		}

		// Add bottom border effect.
		label.getStyleClass().add("selectedLabel");

		if (pane != null) {
			pane.setVisible(true);
		}
	}

	/**
	 * Sends a request to twitter asking for the followers list. It will return the
	 * first 18 followers here. First use <code>this</code> method then use
	 * <code>getMoreFollowers()</code> to request the others.
	 * 
	 * @param follower
	 */
	private void getFollowList(follow follow) {

		followPosition = "";

		String mode = "followers";

		if (follow == follow.following) {
			mode = "following";
		}

		try {
			Document followPage = Jsoup.connect("https://twitter.com/" + getCurrentAccount().getUsername() + "/" + mode)
					.cookies(getCurrentAccount().getCookies()).referrer("https://twitter.com").get();

			Element followGrid = followPage.getElementsByClass("GridTimeline-items").first();

			if (followGrid == null) { // 0 Followers or 0 Following
				return;
			}

			followPosition = followGrid.attr("data-min-position");

			Elements followList = followGrid.getElementsByClass("Grid--withGutter");

			displayFollows(followList);

		} catch (IOException e) {
			logger.severe("Couldn't get followers list.");
			// TODO show alert ?
		}

	}

	/**
	 * Request followers list using <code>followersPosition</code>. It will return
	 * and add to the ScrollPane 18 followers every time <code>this method</code> is
	 * called.
	 * 
	 * @param follower
	 */
	private void getMoreFollow(follow follow) {

		String mode = "followers";

		if (follow == follow.following) {
			mode = "following";
		}

		try {
			// Returns JSON
			Response res = Jsoup
					.connect("https://twitter.com/" + getCurrentAccount().getUsername() + "/" + mode
							+ "/users?include_available_features=1&include_entities=1" + "&max_position="
							+ followPosition + "&reset_error_state=false")
					.referrer("https://twitter.com").ignoreContentType(true).cookies(getCurrentAccount().getCookies())
					.execute();

			// HTML starts with <DOCTYPE HTML>
			if (res.body().startsWith("<")) { // If you have 0 followers/following, it will return HTML instead of JSON.
				return;
			}

			JSONObject json = new JSONObject(res.body());

			// Update min_position
			followPosition = json.getString("min_position");

			Document moreFollowers = Jsoup.parse(json.getString("items_html"));

			displayFollows(moreFollowers.getElementsByClass("Grid--withGutter"));

			requestSent = false;

		} catch (IOException e) {
			logger.severe("Couldn't load more followers.");
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
	private void displayFollows(Elements followers) {

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

		Pane followerPane;

		Label name;
		Label twitterUsername;

		Button followUnfollow;

		// 2 followers per column. fullWidth / 2
		double halfFlowPaneWidth = flowPane.getWidth() / 2;

		followerPane = new Pane();
		followerPane.setPrefWidth(halfFlowPaneWidth - 5);
		followerPane.setPrefHeight(0.1 * halfFlowPaneWidth);
		// followerPane.setId(follower.attr("data-user-id"));

		// Name
		name = new Label(follower.getElementsByClass("btn-group").first().attr("data-name"));
		name.setPrefWidth(followerPane.getPrefWidth());

		// @Username
		twitterUsername = new Label("@" + follower.getElementsByClass("btn-group").first().attr("data-screen-name"));
		twitterUsername.setTranslateY(25);
		twitterUsername.setPrefWidth(followerPane.getPrefWidth());
		twitterUsername.getStyleClass().add("twitterAccount");

		// Follow/Unfollow
		followUnfollow = new Button("Not Following");
		followUnfollow.setTranslateX(halfFlowPaneWidth - 105);
		followUnfollow.getStyleClass().add("followButton");

		if (follower.getElementsByClass("btn-group").first().hasClass("following")) {
			followUnfollow.setText("Following");
		}

		followerPane.getChildren().addAll(name, twitterUsername, followUnfollow);

		flowPane.getChildren().add(followerPane);

	}

	/**
	 * Retrieves the user profile page and displays information about it.
	 * 
	 * @param acc
	 */
	public void loadProfile(TwitterAccount acc) {

		if (acc == null) {
			return;
		}

		logger.info("Loading profile: " + acc.getUsername());

		currentAccount = acc;

		Document profilePage = null;

		try {
			Response profilePageResponse = Jsoup.connect("https://twitter.com/" + acc.getUsername())
					.cookies(acc.getCookies()).referrer("https://twitter.com").method(Method.GET).execute();

			profilePage = Jsoup.parse(profilePageResponse.body());
		} catch (Exception e) {
			logger.severe("Could't display profile information. profile=" + acc.getUsername());
		}

		showProfileInformation(profilePage);

	}

	/**
	 * Shows information about user profile.
	 * 
	 * @param profilePage
	 */
	private void showProfileInformation(Document profilePage) {

		try {
			avatar.setImage(new Image(profilePage.selectFirst(".ProfileAvatar-image").attr("src")));

			username.setText(profilePage.selectFirst(".ProfileHeaderCard-nameLink").text());

			tweets.setText(profilePage
					.selectFirst("li.ProfileNav-item:nth-child(1) > a:nth-child(1) > span:nth-child(3)").text());

			followersCount.setText(profilePage
					.selectFirst("li.ProfileNav-item:nth-child(3) > a:nth-child(1) > span:nth-child(3)").text());

			followersCount.setTooltip(new Tooltip(
					profilePage.selectFirst("li.ProfileNav-item:nth-child(3) > a:nth-child(1) > span:nth-child(3)")
							.attr("data-count")));

			followingCount.setText(profilePage
					.selectFirst("li.ProfileNav-item:nth-child(2) > a:nth-child(1) > span:nth-child(3)").text());

			likes.setText(profilePage
					.selectFirst("li.ProfileNav-item:nth-child(4) > a:nth-child(1) > span:nth-child(3)").text());

		} catch (Exception e) {
			// Do nothing.
		}

	}

	private TwitterAccount getCurrentAccount() {
		return currentAccount;
	}

}
