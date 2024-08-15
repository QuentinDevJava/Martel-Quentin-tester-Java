package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	public void setUpPerTestParkingServiceTest() {
		try {
			lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// numero de la plaque
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false); // place de parking N°, TYPE, LIBRE

			Ticket ticket = new Ticket();// creation du ticket
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
			lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Nested
	@DisplayName("Test de la sortie d'un véhicule")
	class testExitingVehicle {
		@Test
		public void processExitingVehicleTest() { // Test 1
			// GIVEN
			when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
			// WHEN
			parkingService.processExitingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).getNbTicket(anyString());
		}

		@Test
		public void processExitingVehicleTestUnableUpdate() { // Test 3
			// GIVEN
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
			// WHEN
			parkingService.processExitingVehicle();
			// THEN
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
			assertEquals(false, ticketDAO.updateTicket(any(Ticket.class)));
		}
	}

	@Nested
	@DisplayName("Test de l'entrée d'un véhicule")
	class testIncomingVehicle {
		@Test
		public void testProcessIncomingVehicle() { // Test 2
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
		}

		@Test
		public void testProcessIncomingVehicleBike() { // Test 2
			// GIVEN

			when(inputReaderUtil.readSelection()).thenReturn(2);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);
			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
		}

		@Test
		public void testProcessIncomingVehicleWithDiscount() { // Impossible a verifier a supp
			// GIVEN
			when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
		}

	}

	@Nested
	@DisplayName("Test d'attribution du numero de place de parking")
	class testGetNextParkingNumber {
		@Test
		public void testGetNextParkingNumberIfAvailable() { // Test 4
			// GIVEN
			ParkingSpot parkingSpotCarTrue = new ParkingSpot(1, ParkingType.CAR, true);
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			// WHEN
			ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();
			// THEN
			assertEquals(parkingSpotCarTrue, parkingSpotTest);
		}

		@Test
		public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {// Test 5
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
			// WHEN
			// THEN
			assertNull(parkingService.getNextParkingNumberIfAvailable());
		}

		@Test
		public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() { // Test 6
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(3);
			// WHEN
			// THEN
			assertNull(parkingService.getNextParkingNumberIfAvailable());
		}
	}

	@Nested
	@DisplayName("Erreur plaque d'immatriculation null")
	class exitingVehicleIfRegistrationNumberNotFound {
		@Test
		public void testProcessExitingVehicleIfRegistrationNumberNotFound() {
			// GIVEN
			when(ticketDAO.getTicket(anyString())).thenReturn(null);
			// WHEN
			parkingService.processExitingVehicle();
			// THEN
			verify(ticketDAO, Mockito.times(0)).getNbTicket(anyString());
		}
	}

}
