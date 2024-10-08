package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.MenuConstants;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class InteractiveShell {

	private InteractiveShell() {
		super();
	}

	private static final Logger logger = LogManager.getLogger("InteractiveShell");

	public static void loadInterface() {
		logger.info("App initialized!!!");
		System.out.println("Welcome to Parking System!");

		boolean continueApp = true;
		InputReaderUtil inputReaderUtil = new InputReaderUtil();
		ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
		TicketDAO ticketDAO = new TicketDAO();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		while (continueApp) {
			loadMenu();
			int option = inputReaderUtil.readSelection();
			switch (option) {
			case 1 -> parkingService.processIncomingVehicle();
			case 2 -> parkingService.processExitingVehicle();
			case 3 -> {
				System.out.println("""

						Exiting from the system!""");
				continueApp = false;
			}
			default ->
				System.out.println("Unsupported option. Please enter a number corresponding to the provided menu");
			}
		}
	}

	private static void loadMenu() {
		System.out.println(MenuConstants.STARTMENU);
	}

}
