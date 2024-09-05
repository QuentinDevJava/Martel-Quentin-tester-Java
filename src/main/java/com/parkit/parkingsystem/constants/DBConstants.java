package com.parkit.parkingsystem.constants;

public class DBConstants {

	private DBConstants() {
		super();
	}

	public static String getPassword() {
		return PASSWORD;
	}

	// Connection
	public static final String URLMYSQL = "jdbc:mysql://localhost:3306/mysql";
	public static final String URLTEST = "jdbc:mysql://localhost:3306/test";
	public static final String URLPROD = "jdbc:mysql://localhost:3306/prod";
	public static final String LOGIN = "root";
	private static final String PASSWORD = "";

	// Request SQL
	public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
	public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";
	public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
	public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
	public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.ID DESC limit 1";
	public static final String NB_TICKET = "select count(VEHICLE_REG_NUMBER) FROM ticket t where t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is not null";
	public static final String TICKET_AWAITS_RELEASE = "select count(VEHICLE_REG_NUMBER) FROM ticket t where t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is null";

}
// private static final String URLMYSQL = "jdbc:mysql://localhost:3307/mysql";
// private static final String URLTEST = "jdbc:mysql://localhost:3306/test";
// private static final String URLPROD = "jdbc:mysql://localhost:3307/prod";
// private static final String LOGIN = "root";
// private static final String PASSWORD = "root";