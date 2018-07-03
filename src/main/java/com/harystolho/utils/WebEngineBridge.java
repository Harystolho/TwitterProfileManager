package com.harystolho.utils;

import com.harystolho.Main;

/**
 * This method is called from the login page using JS.
 * 
 * @author Harystolho
 *
 */
public class WebEngineBridge {

	public void login(String username, String password) {
		Main.getApplication().getMainController().loginUser(username, password);
	}

}
