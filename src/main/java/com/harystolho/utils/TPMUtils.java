package com.harystolho.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.harystolho.Main;
import com.harystolho.twitter.AccountManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class TPMUtils {

	private static Logger logger = Logger.getLogger(TPMUtils.class.getName());

	public static Parent loadFXML(String name) {

		Parent p = null;

		try {
			p = FXMLLoader.load(ClassLoader.getSystemResource(name));
		} catch (IOException e) {
			logger.severe("Couldn't load the fxml file=" + name);
			System.exit(1);
		}

		return p;

	}

	public static void start() {
		logger.info("Starting applicaiton.");

	}

	public static void close() {
		logger.info("Exiting application.");

		AccountManager.saveAccounts(Main.getApplication().getMainController().getAccountList());

	}

}
