package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@Disabled
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	public static void setUpParkingDataBaseIT() {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();

	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		Ticket testTicket = ticketDAO.getTicket("ABCDEF");// remplacer par un spy du ticket fausse les test
		boolean parkingisavailability = testTicket.getParkingSpot().isAvailable();
		int spotNumber = testTicket.getParkingSpot().hashCode();

		assertEquals(1, spotNumber); // Parking table update
		assertEquals(false, parkingisavailability);// Spot availability
		assertNotNull(testTicket);// Ticket table update

	}

	@Test
	public void testParkingLotExit() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		Ticket testTicket = ticketDAO.getTicket("ABCDEF"); // remplacer par un spy du ticket fausse les test
		boolean parkingisavailability = testTicket.getParkingSpot().isAvailable();

		assertEquals(true, parkingisavailability);// Spot availability
		assertNotNull(testTicket.getOutTime());
		assertNotEquals(0, ticketDAO.getNbTicket("ABCDEF"));
		assertNotNull(testTicket.getPrice());

	}

	@Disabled("A faire")
	@Test
	public void testParkingLotExitReccurringUser() {
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		// si je passe par la methode
		// pour avoir un prix realiste je doit modifier le inTime entre l'entree et la
		// sortie

		// TODO: check that the discount fare generated in DB
	}

}