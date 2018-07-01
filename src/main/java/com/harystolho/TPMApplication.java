package com.harystolho;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TPMApplication extends Application {

	private final Logger logger = Logger.getLogger(TPMApplication.class.getName());

	private Stage window;

	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;

	@Override
	public void start(Stage window) throws Exception {

		Main.setApplication(this);

		this.window = window;

		window.setWidth(WIDTH);
		window.setHeight(HEIGHT);

		Scene scene = createMainScene();

		window.setScene(scene);

		window.show();

	}

	private Scene createMainScene() {
		Scene scene = new Scene(loadFXML("main.fxml"));

		scene.getStylesheets().add(ClassLoader.getSystemClassLoader().getResource("style.css").toString());

		return scene;
	}

	private Parent loadFXML(String name) {

		Parent p = null;

		try {
			p = FXMLLoader.load(ClassLoader.getSystemResource(name));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Couldn't load the fxml file: " + name, e);
			System.exit(1);
		}

		return p;

	}

	/**
	 * This method is called from the Main Thread to open the javafx window.
	 * 
	 * @param args
	 */
	public void init(String... args) {
		launch(args);
	}

}
