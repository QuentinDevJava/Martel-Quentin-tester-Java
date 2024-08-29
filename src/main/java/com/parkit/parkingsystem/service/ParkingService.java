package com.parkit.parkingsystem.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.MenuConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	public void processIncomingVehicle() {
		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehicleRegNumber();
				if (!ticketDAO.ticketIsInDatabaseWithOutTimeNull(vehicleRegNumber)) {// The ticket not exists
					// allocated parking spot
					parkingSpot.setAvailable(false);
					parkingSpotDAO.updateParking(parkingSpot);
					// Create ticket
					Date inTime = new Date();
					Ticket ticket = new Ticket();
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(0);
					ticket.setInTime(inTime);
					SimpleDateFormat formatOutput = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					String dateFormatted = formatOutput.format(inTime);
					ticket.setOutTime(null);

					// TODO utiliser Log4J
					if (isRegularUser(vehicleRegNumber)) {
						ticket.setDiscount(true);
						System.out.println(
								"Welcome back! As a regular user of our parking lot, you will receive a 5% discount.");
					}
					ticketDAO.saveTicket(ticket);
					System.out.println("Generated Ticket and saved in DB");
					System.out.println("Please park your vehicle in spot number: " + parkingSpot.getId());
					System.out.println(
							"Recorded in-time for vehicle number: " + vehicleRegNumber + " is: " + dateFormatted);

				} else { // The ticket exists

					System.out.println(
							"Error registering your ticket in the database. Your registration number is already in the database.");
					throw new IllegalArgumentException(
							"Error registering your ticket in the database. Your registration number is already in the database");
				}

			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	public void processExitingVehicle() {
		try {
			String vehicleRegNumber = getVehicleRegNumber();
			Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
			if (ticket.getOutTime() != null) {
				throw new IllegalArgumentException(
						"Vehicle registration number does not match any vehicle in the database");
			}

			Date outTime = new Date();
			ticket.setOutTime(outTime);
			SimpleDateFormat formatOutput = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String dateFormatted = formatOutput.format(outTime);

			if (isRegularUser(vehicleRegNumber)) {
				ticket.setDiscount(true);
			}

			fareCalculatorService.calculateFare(ticket);

			if (ticketDAO.updateTicket(ticket)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);
				System.out.println("Please pay the parking fare:" + ticket.getPrice());
				System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:"
						+ dateFormatted);
			} else {
				System.out.println("Unable to update ticket information. Error occurred");
			}
		} catch (IllegalArgumentException | NullPointerException e) {
			System.out.println("""
					Vehicle registration number does not match any vehicle in the database.
					Unable to process exiting vehicle.
					""");
			logger.error("Unable to process exiting vehicle", e);
		}
	}

	private String getVehicleRegNumber() throws IllegalArgumentException {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				System.out.println("Error fetching parking number from DB. Parking slots might be full");
				throw new IllegalArgumentException(
						"Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	private ParkingType getVehichleType() {
		System.out.println(MenuConstants.SELECTMENU);
		int input = inputReaderUtil.readSelection();
		return switch (input) {
		case 1 -> ParkingType.CAR;
		case 2 -> ParkingType.BIKE;
		default -> {
			System.out.println("Incorrect input provided");
			throw new IllegalArgumentException("Entered input is invalid");
		}
		};

	}

	public boolean isRegularUser(String vehicleRegNumber) {
		return ticketDAO.getNbTicket(vehicleRegNumber) >= 1;
	}
}
