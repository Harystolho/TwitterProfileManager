package com.harystolho.controllers;

import java.util.logging.Logger;

import com.harystolho.Main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

	private static Logger logger = Logger.getLogger(LoginController.class.getName());

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private Button login;

	@FXML
	void initialize() {

		loadEventHandler();

	}

	private void loadEventHandler() {

		login.setOnAction((e) -> {

			Main.getApplication().getMainController().loginUser(username.getText(), password.getText());

		});

	}

}
