package com.harystolho.controllers;

import java.util.List;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.harystolho.Main;
import com.harystolho.twitter.AccountManager;
import com.harystolho.twitter.TwitterAccount;
import com.harystolho.utils.TPMUtils;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class MainController {

	@FXML
	private Button addNewAccount;

	@FXML
	private Button removeAccount;

	@FXML
	private ListView<TwitterAccount> accountList;

	@FXML
	private Pane rightPane;

	@FXML
	private ImageView home;

	@FXML
	void initialize() {

		Main.getApplication().setMainController(this);

		loadAccounts();

		loadEventHandler();

	}

	private void loadEventHandler() {

		addNewAccount.setOnAction((e) -> {
			loadOnRightPage((Pane) TPMUtils.loadFXML("login.fxml"));
		});

		removeAccount.setOnAction((e) -> {
			accountList.getItems().remove(accountList.getSelectionModel().getSelectedItem());
		});

		accountList.getSelectionModel().selectedItemProperty().addListener((ob, oldValue, newValue) -> {
			loadProfilePane();
			Main.getApplication().getProfileController().loadProfile(newValue);
		});

		home.setOnMouseClicked((e) -> {
			accountList.getSelectionModel().clearSelection();
			rightPane.getChildren().clear();
		});

	}

	/**
	 * Loads accounts from <code>accounts.json</code> file.
	 */
	private void loadAccounts() {
		setAccountList(AccountManager.loadAccounts());
	}

	/**
	 * Tries to login the user using it's username/email and password. If Twitter
	 * authorizes the login it will create a new
	 * {@link #com.harystolho.twitter.TwitterAccount TwitterAccount} and add it to
	 * {@link #accountList}, otherwise it will display and error message.
	 * 
	 * @param username
	 *            username or email
	 * @param password
	 *            password
	 */
	public void loginUser(String username, String password) {

		try {

			// Connects to login page to get the authToken.
			Response login = Jsoup.connect("https://twitter.com/login").execute();

			Document loginPage = Jsoup.parse(login.body());

			String authToken = loginPage
					.selectFirst("form.t1-form:nth-child(2) > fieldset:nth-child(1) > input:nth-child(4)").val();

			// POST request to get the user cookies.
			Response loginResponse = Jsoup.connect("https://twitter.com/sessions").cookies(login.cookies())
					.data("session[username_or_email]", username).data("session[password]", password)
					.data("remember_me", "1").data("authenticity_token", authToken).method(Method.POST).execute();

			String accountName = Jsoup.connect("https://twitter.com/").referrer("https://twitter.com/login")
					.cookies(loginResponse.cookies()).get().selectFirst("b.u-linkComplex-target").text();

			TwitterAccount ta = new TwitterAccount();

			ta.setUsername(accountName);
			ta.setCookie(loginResponse.cookies());

			accountList.getItems().add(ta);

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);

			alert.setTitle("Login Error");
			alert.setContentText("An error happened. Checkyour username/email or your password and try again.");

			alert.showAndWait();
			return;
		}

		rightPane.getChildren().clear();

	}

	private void loadProfilePane() {
		loadOnRightPage((Pane) TPMUtils.loadFXML("menu.fxml"));
	}

	private void loadOnRightPage(Pane pane) {
		rightPane.getChildren().clear();
		rightPane.getChildren().add(pane);
	}

	public ObservableList<TwitterAccount> getAccountList() {
		return accountList.getItems();
	}

	public void setAccountList(List<TwitterAccount> accountList) {
		this.accountList.getItems().setAll(accountList);
	}

}
