package com.harystolho;

import com.harystolho.utils.TPMUtils;

/**
 * Java Application to manage your twitter account.
 *
 */
public class Main {

	private static TPMApplication application;

	public static void main(String[] args) {

		TPMUtils.start();

		new TPMApplication().init(args);
	}

	public static TPMApplication getApplication() {
		return application;
	}

	public static void setApplication(TPMApplication application) {
		Main.application = application;
	}

}
