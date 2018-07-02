package com.harystolho.controllers;

import java.io.IOException;
import java.net.CookieManager;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import com.harystolho.Main;
import com.harystolho.twitter.AccountManager;
import com.harystolho.twitter.TwitterAccount;
import com.harystolho.utils.TPMUtils;
import com.harystolho.utils.WebEngineBridge;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class MainController {

	@FXML
	private Button addNewAccount;

	@FXML
	private ListView<TwitterAccount> accountList;

	@FXML
	private Pane rightPane;

	@FXML
	void initialize() {

		Main.getApplication().setMainController(this);

		loadUserProfilePane();

		loadAccounts();

		loadEventHandler();

	}

	private void loadEventHandler() {

		addNewAccount.setOnAction((e) -> {

			// openLoginView();
			openWebView();

		});

		accountList.getSelectionModel().selectedItemProperty().addListener((ob, oldValue, newValue) -> {
			Main.getApplication().getProfileController().loadProfile(newValue);
		});

	}

	private void loadAccounts() {
		setAccountList(AccountManager.loadAccounts());
	}

	/**
	 * Loads information about the account in the right pane.
	 */
	private void loadUserProfilePane() {

		Pane pane = (Pane) TPMUtils.loadFXML("menu.fxml");

		rightPane.getChildren().add(pane);

	}

	public void loginUser(String username, String password) {

		try {

			// To get the auth token.
			Response loginToken = Jsoup.connect("https://twitter.com/login").execute();

			Document loginPage = Jsoup.parse(loginToken.body());

			String authToken = loginPage
					.selectFirst("form.t1-form:nth-child(2) > fieldset:nth-child(1) > input:nth-child(4)").val();

			// Make a POST request to get the cookies.
			Response loginResponse = Jsoup.connect("https://twitter.com/sessions").method(Method.POST)
					.cookies(loginToken.cookies()).data("session[username_or_email]", username)
					.data("session[password]", password).data("remember_me", "1").data("authenticity_token", authToken)
					.execute();

			TwitterAccount ta = new TwitterAccount();

			ta.setUsername(username);
			ta.setCookie(loginResponse.cookies());

			loadUserProfilePane();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openWebView() {

		WebView view = new WebView();
		WebEngine engine = view.getEngine();

		engine.setJavaScriptEnabled(true);

		engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {

			if (newState == State.SUCCEEDED) {

				JSObject window = (JSObject) engine.executeScript("window");

				window.setMember("bridge", new WebEngineBridge());

				// now wait for the user to login. When the user presses "login"
				// It will call the loginUser(); method.
			}

		});

		view.setPrefWidth(rightPane.getWidth());
		view.setPrefHeight(rightPane.getHeight());

		rightPane.getChildren().add(view);

		loadLoginPage(engine);

	}

	private void loadLoginPage(WebEngine engine) {
		Document loginPage = null;
		try {
			loginPage = Jsoup.parse(ClassLoader.getSystemResourceAsStream("login.html"), null, "/");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (loginPage == null) {
			System.out.println("Page is null.");
			return;
		}

		engine.loadContent(loginPage.outerHtml());

	}

	public ObservableList<TwitterAccount> getAccountList() {
		return accountList.getItems();
	}

	public void setAccountList(List<TwitterAccount> accountList) {
		this.accountList.getItems().setAll(accountList);
	}

}
