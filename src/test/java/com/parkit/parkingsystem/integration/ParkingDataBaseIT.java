package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

	@DisplayName("Test l'entrée d'un veichule")
	@Test
	public void testParkingACar() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		Ticket testTicket = ticketDAO.getTicket("ABCDEF");
		boolean parkingisavailability = testTicket.getParkingSpot().isAvailable();
		int spotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		assertAll("Error Test Parking A Car",
				() -> Assertions.assertEquals(2, spotNumber, "Error Parking table not update"),
				() -> Assertions.assertEquals(false, parkingisavailability, "Error Spot availability true"),
				() -> Assertions.assertNull((testTicket.getOutTime()), "Error ticket time out not null"),
				() -> Assertions.assertNotNull(testTicket, "Error Ticket table not update"));
	}

	@DisplayName("Test la sortie d'un veichule inferieur a 30 min")
	@Test
	public void testParkingLotExit() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		Ticket testTicket = ticketDAO.getTicket("ABCDEF");
		int parkingSpotTest = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertAll("Error Test Parking Exit",
				() -> Assertions.assertEquals(1, parkingSpotTest, "Error parking Parking table not update"),
				() -> Assertions.assertNotNull((testTicket.getOutTime()), "Error ticket out time = null"));
	}

	@DisplayName("Test la sortie d'un veichule supperieur a 30 min")
	@Test
	public void testParkingExit1h() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		Ticket testTicket = ticketDAO.getTicket("ABCDEF"); // loads data from the exit ticket
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		testTicket.setOutTime(null); // update out time
		testTicket.setInTime(inTime); // update in time
		ticketDAO.saveTicket(testTicket);

		parkingService.processExitingVehicle();

		Ticket verifTicket = ticketDAO.getTicket("ABCDEF");
		int parkingSpotTest = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertAll("Error Test Parking Exit",
				() -> Assertions.assertEquals(1, parkingSpotTest, "Error parking Parking table not update"),
				() -> Assertions.assertNotNull((verifTicket.getOutTime()), "Error ticket out time = null"));
	}

	@DisplayName("Test l'entrée d'un veichiule dans le cas d’un utilisateur récurrent.")
	@Test
	public void testParkingLotExitReccurringUser() {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		// GIVEN
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

//TODO verification de la methode a faire voir si possible de modifier l'heure d'entree lors de parkingService.processIncomingVehicle()

		Ticket testTicket = ticketDAO.getTicket("ABCDEF"); // loads data from the exit ticket
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		testTicket.setOutTime(null); // update out time
		testTicket.setInTime(inTime); // update in time
		ticketDAO.saveTicket(testTicket);
		// To simulate entry, I add a fake ticket to the database that would have
		// entered the parking lot an hour ago to test the price.

		// WHEN
		parkingService.processExitingVehicle();
		testTicket = ticketDAO.getTicket("ABCDEF"); // loads data from the exit ticket with discount

		// THEN
		assertTrue(testTicket.getPrice() >= 0);
	}
}