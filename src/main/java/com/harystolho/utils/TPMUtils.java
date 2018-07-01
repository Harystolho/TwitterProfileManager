package com.harystolho.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class TPMUtils {

	private static Logger logger = Logger.getLogger(TPMUtils.class.getName());

	public static Parent loadFXML(String name) {

		Parent p = null;

		try {
			p = FXMLLoader.load(ClassLoader.getSystemResource(name));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't load the fxml file: " + name, e);
			System.exit(1);
		}

		return p;

	}

	public static void start() {
		logger.log(Level.INFO, "Starting application.");

	}

	public static void close() {
		logger.log(Level.INFO, "Closing application.");
	}

}
