package com.parkit.parkingsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.service.InteractiveShell;

public class App {
	private static final Logger logger = LogManager.getLogger(App.class);

	public static void main(String[] args) {
		initializeDatabases();
		initializeParkingSystem();

	}

	private static void initializeDatabases() {
		logger.info("Initializing databases");
		if (!DataBaseConfig.isConnected() && !DataBaseConfig.createDatabases()) {
			logger.error("Error:The parking system cannot start without databases system.");
			System.exit(1);
		}

	}

	private static void initializeParkingSystem() {
		logger.info("Initializing Parking System");
		InteractiveShell.loadInterface();
	}
}