package com.parkit.parkingsystem.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.DBConstants;

public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	public Connection getConnection() throws SQLException {
		logger.info("Create DB connection");
		return DriverManager.getConnection(DBConstants.URLPROD, DBConstants.LOGIN, DBConstants.PASSWORD);
	}

	public static boolean isConnected() {
		try (Connection connection = DriverManager.getConnection(DBConstants.URLPROD, DBConstants.LOGIN,
				DBConstants.PASSWORD)) {
			logger.info("Connection to prod and test databases established successfully.");
			return true;
		} catch (SQLException e) {
			logger.warn("Failed to connect to the databases (prod and test): {}", e.getMessage());
			return false;
		}
	}

	public static boolean createDatabases() {
		logger.info("Starting database creation (prod and test) in MySQL.");
		if (!areDatabasesCreated()) {
			logger.error("The databases could not be created.");
			return false;
		} else {
			logger.info("The databases are working.");
			return true;
		}
	}

	private static boolean areDatabasesCreated() {
		try (Connection connection = DriverManager.getConnection(DBConstants.URLMYSQL, DBConstants.LOGIN,
				DBConstants.PASSWORD)) {
			executeSqlFromFile(connection, "Data.sql");
			logger.info("Databases created successfully.");
			return true;
		} catch (SQLException e) {
			logger.error("Error: The databases could not be created: {}", e.getMessage());
			return false;
		}
	}

	private static void executeSqlFromFile(Connection con, String filePath) {
		try (Statement stmt = con.createStatement();
				InputStream inputStream = DataBaseConfig.class.getClassLoader().getResourceAsStream(filePath);
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

			if (inputStream == null) {
				logger.info("File not found: " + filePath);
				return;
			}

			String strCurrentLine;
			while ((strCurrentLine = br.readLine()) != null) {
				stmt.addBatch(strCurrentLine);
			}

			stmt.executeBatch();

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
