package com.harystolho.utils;

import com.harystolho.Main;

public class WebEngineBridge {

	public void login(String username, String password) {
		Main.getApplication().getMainController().loginUser(username, password);
	}

}
