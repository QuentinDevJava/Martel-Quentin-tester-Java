package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger(TicketDAO.class.getName());

	private DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public DataBaseConfig getDataBaseConfig() {
		return dataBaseConfig;
	}

	public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}

	public boolean saveTicket(Ticket ticket) {
		try (Connection con = getDataBaseConfig().getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {

			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));

			return ps.execute();

		} catch (SQLException ex) {
			logger.error("Error fetching next available slot", ex);
		}
		return false;
	}

	public Ticket getTicket(String vehicleRegNumber) {
		Ticket ticket = null;
		try (Connection con = getDataBaseConfig().getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {

			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4));
				ticket.setOutTime(rs.getTimestamp(5));
			}

		} catch (SQLException ex) {
			logger.error("Error fetching next available slot", ex);
		}
		return ticket;
	}

	public boolean updateTicket(Ticket ticket) {
		try (Connection con = getDataBaseConfig().getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)) {

			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();

			return true;

		} catch (SQLException ex) {
			logger.error("Error saving ticket info", ex);
		}
		return false;
	}

	public int getNbTicket(String vehicleRegNumber) {
		int nbTicket = 0;
		try (Connection con = getDataBaseConfig().getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.NB_TICKET)) {

			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				nbTicket = rs.getInt("count(VEHICLE_REG_NUMBER)"); // result of column count(*)
			}

		} catch (SQLException ex) {
			logger.error("Errors when counting the number of times the car has already passed by", ex);
		}
		return nbTicket;
	}

	public boolean ticketIsInDatabaseWithOutTimeNull(String vehicleRegNumber) {
		int ticketRowExist = 0;
		try (Connection con = getDataBaseConfig().getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.TICKET_AWAITS_RELEASE)) {

			ps.setString(1, vehicleRegNumber);
			ResultSet ticketExist = ps.executeQuery();

			if (ticketExist.next()) {
				ticketRowExist = ticketExist.getInt("count(VEHICLE_REG_NUMBER)");
			}

			return (ticketRowExist == 1);

		} catch (SQLException ex) {
			logger.error("Error checking whether your ticket is in the database.", ex);
		}
		return true;
	}
}
