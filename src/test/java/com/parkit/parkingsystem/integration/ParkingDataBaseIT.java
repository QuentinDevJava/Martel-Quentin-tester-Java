package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static TicketDAO ticketDAO;
	@Spy
	Ticket spyTicket = new Ticket();

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
		Ticket testTicket = ticketDAO.getTicket("ABCDEF");// a remplacer par un spy du ticket ?
		boolean parkingisavailability = testTicket.getParkingSpot().isAvailable();
		int spotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		assertAll("Error Test Parking A Car",
				() -> Assertions.assertEquals(2, spotNumber, "Error Parking table not update"),
				() -> Assertions.assertEquals(false, parkingisavailability, "Error Spot availability true"),
				() -> Assertions.assertNull((testTicket.getOutTime()), "Error ticket time out not null"),
				() -> Assertions.assertNotNull(testTicket, "Error Ticket table not update"));
	}

	@Test
	public void testParkingLotExit() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		// WHEN
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		Ticket testTicket = ticketDAO.getTicket("ABCDEF"); // a remplacer par un spy du ticket ?
		int parkingSpotTest = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertAll("Error Test Parking Exit",
				() -> Assertions.assertEquals(1, parkingSpotTest, "Error parking Parking table not update"),
				() -> Assertions.assertNotNull(testTicket.getPrice(), "Error ticket price = null "),
				() -> Assertions.assertNotNull((testTicket.getOutTime()), "Error ticket out time = null"));
	}

	@Test
	public void testParkingLotExitReccurringUser() {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		parkingService.processIncomingVehicle();

		int testNbTicket = ticketDAO.getNbTicket("ABCDEF");

		assertEquals(1, testNbTicket); // testNbTicket>=1 then discount fare

		// pour avoir un prix realiste a tester, je dois modifier le inTime entre
		// l'entree et la sortie du veichule
		// ou comme la valeur de getNbTicket est > 2 c'est ok ?
	}

}