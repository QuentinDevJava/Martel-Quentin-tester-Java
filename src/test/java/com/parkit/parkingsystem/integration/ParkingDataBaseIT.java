package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

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
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
		ticketDAO = new TicketDAO();
		ticketDAO.setDataBaseConfig(dataBaseTestConfig);
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

	@DisplayName("Test l'entrée d'un véhicule")
	@Test
	public void testParkingACar() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		Ticket ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
		boolean parkingIsAvailability = ticket.getParkingSpot().isAvailable();
		int spotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		assertAll("Error Test Parking A Car",
				() -> Assertions.assertEquals(2, spotNumber, "Error Parking table not update"),
				() -> Assertions.assertEquals(false, parkingIsAvailability, "Error Spot availability true"),
				() -> Assertions.assertNull((ticket.getOutTime()), "Error ticket time out not null"),
				() -> Assertions.assertNotNull(ticket, "Error Ticket table not update"));
	}

	@DisplayName("Test la sortie d'un véhicule")
	@Test
	public void testParkingLotExit() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		Ticket testTicket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
		int parkingSpotTest = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertAll("Error Test Parking Exit",
				() -> Assertions.assertEquals(1, parkingSpotTest, "Error parking Parking table not update"),
				() -> Assertions.assertNotNull((testTicket.getOutTime()), "Error ticket out time = null"));
	}

	@DisplayName("Test l'entrée d'un véhicule avec un utilisateur récurrent.")
	@Test
	public void testParkingLotExitReccurringUser() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		Ticket firstTicket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

		assertThat(firstTicket.hasDiscount()).isFalse();
		assertThat(firstTicket.getOutTime()).isNotNull();

		parkingService.processIncomingVehicle();

		// WHEN
		parkingService.processExitingVehicle();

		// THEN
		Ticket currentTicket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

		assertThat(currentTicket.hasDiscount()).isTrue();
	}

}