package com.harystolho;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class FXMLLoaderTest {

	@Test
	public void loadMainFXML() {
		assertNotNull(ClassLoader.getSystemResource("main.fxml"));
	}

	@Test
	public void loadLoginFXML() {
		assertNotNull(ClassLoader.getSystemResource("login.fxml"));
	}

	@Test
	public void loadMenuFXML() {
		assertNotNull(ClassLoader.getSystemResource("menu.fxml"));
	}

}
