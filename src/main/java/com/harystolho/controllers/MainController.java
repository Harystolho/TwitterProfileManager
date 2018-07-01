package com.harystolho.controllers;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.harystolho.Main;
import com.harystolho.twitter.AccountManager;
import com.harystolho.twitter.TwitterAccount;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

		loadAccounts();

		loadEventHandler();

	}

	private void loadAccounts() {
		setAccountList(AccountManager.loadAccounts());
	}

	private void loadEventHandler() {

		addNewAccount.setOnAction((e) -> {

			openWebView();

		});

	}

	private void openWebView() {

		WebView view = new WebView();
		WebEngine engine = view.getEngine();

		engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {

			if (newState == State.SUCCEEDED) {

				// If you logged in successfully
				if (engine.getLocation().equals("https://twitter.com/")) {

					TwitterAccount ta = new TwitterAccount();

					ta.setCookie((String) engine.executeScript("document.cookie"));

					Document twitterPage = Jsoup
							.parse((String) engine.executeScript("document.documentElement.outerHTML"));

					ta.setUsername(twitterPage.select("b.u-linkComplex-target").get(0).text());

					accountList.getItems().add(ta);

					rightPane.getChildren().clear();

				}
			}

		});

		view.setPrefWidth(rightPane.getWidth());
		view.setPrefHeight(rightPane.getHeight());

		rightPane.getChildren().add(view);

		engine.load("https://twitter.com/login/");

	}

	public ObservableList<TwitterAccount> getAccountList() {
		return accountList.getItems();
	}

	public void setAccountList(List<TwitterAccount> accountList) {
		this.accountList.getItems().setAll(accountList);
	}

}
