package com.parkit.parkingsystem.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

	public static void initConnection() {
		try (Connection con = DriverManager.getConnection(DBConstants.URLPROD, DBConstants.LOGIN,
				DBConstants.PASSWORD)) {
			logger.info("prod and test databases exist");
		} catch (SQLException e) {
			logger.info("the databases do not exist, start creating the databases(prod and test) in mysql");

			try (Connection con = DriverManager.getConnection(DBConstants.URLMYSQL, DBConstants.LOGIN,
					DBConstants.PASSWORD)) {
				executeSqlFromFile(con, "src\\resources\\Data.sql");
				logger.info("the databases is created");
			} catch (SQLException e1) {
				e.printStackTrace();
			}

			try (Connection con = DriverManager.getConnection(DBConstants.URLPROD, DBConstants.LOGIN,
					DBConstants.PASSWORD)) {
				logger.info("the database works");
			} catch (SQLException e1) {
				logger.error("the database is not working");
				e1.printStackTrace();
			}
		}
	}

	private static void executeSqlFromFile(Connection con, String filePath) {
		try (Statement stmt = con.createStatement();
				FileReader fr = new FileReader(filePath);
				BufferedReader br = new BufferedReader(fr)) {

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
