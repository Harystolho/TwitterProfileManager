package com.harystolho;

import java.util.logging.Logger;

import com.harystolho.controllers.MainController;
import com.harystolho.controllers.ProfileController;
import com.harystolho.utils.TPMUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Manages the main window.
 * 
 * @author Harystolho
 *
 */
public class TPMApplication extends Application {

	private final Logger logger = Logger.getLogger(TPMApplication.class.getName());

	private Stage window;

	private MainController mainController;
	private ProfileController profileController;

	private static final int WIDTH = 1280;
	private static final int HEIGHT = 745;

	@Override
	public void start(Stage window) throws Exception {

		Main.setApplication(this);

		this.window = window;

		window.setWidth(WIDTH);
		window.setHeight(HEIGHT);

		window.setResizable(false);

		Scene scene = createMainScene();

		window.setScene(scene);

		loadEventHandler();

		window.show();

	}

	private void loadEventHandler() {

		window.setOnCloseRequest((e) -> {
			TPMUtils.close();
		});

	}

	/**
	 * Creates the main scene for this application.
	 * 
	 * @return
	 */
	private Scene createMainScene() {
		Scene scene = new Scene(TPMUtils.loadFXML("main.fxml"));

		scene.getStylesheets().add(ClassLoader.getSystemResource("style.css").toString());

		return scene;
	}

	public Stage getWindow() {
		return window;
	}

	public void setWindow(Stage window) {
		this.window = window;
	}

	public MainController getMainController() {
		return mainController;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public ProfileController getProfileController() {
		return profileController;
	}

	public void setProfileController(ProfileController profileController) {
		this.profileController = profileController;
	}

	/**
	 * This method is called from the Main Thread to open the JavaFX window.
	 * 
	 * @param args
	 */
	public void init(String... args) {
		launch(args);
	}

}
