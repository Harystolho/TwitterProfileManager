package com.harystolho.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TPMUtils {

	private static Logger logger = Logger.getLogger(TPMUtils.class.getName());

	public static void start() {
		logger.log(Level.INFO, "Starting application.");

	}

	public static void close() {
		logger.log(Level.INFO, "Closing application.");
	}

}
