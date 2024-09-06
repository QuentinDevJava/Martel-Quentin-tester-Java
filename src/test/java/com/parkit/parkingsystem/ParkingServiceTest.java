package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

	private ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	public void setUp() {
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	}

	public Ticket createTicket(int parkingNumber, ParkingType parkingType, boolean aviaible, Date inTime,
			Date outTime) {
		ParkingSpot parkingSpot = new ParkingSpot(parkingNumber, parkingType, aviaible);
		Ticket ticket = new Ticket();
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		return ticket;
	}

	@Nested
	@DisplayName("Test de la sortie d'un véhicule")
	class testExitingVehicle {

		@ParameterizedTest
		@MethodSource("provideParkingAndNbOfTicketParameters")
		public void processExitingVehicleTest(ParkingType parkingType, int nbOfTicket, Boolean isRegularUser)
				throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			Ticket ticket = createTicket(1, parkingType, false,
					(new Date(System.currentTimeMillis() - (60 * 60 * 1000))), null);

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			when(ticketDAO.getNbTicket(anyString())).thenReturn(nbOfTicket); // not regular user
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

			assertThat(isRegularUser).isEqualTo(parkingService.isRegularUser("ABCDEF"));
		}

		private static Stream<Arguments> provideParkingAndNbOfTicketParameters() {
			return Stream.of(Arguments.of(ParkingType.BIKE, 0, false), Arguments.of(ParkingType.CAR, 0, false),
					Arguments.of(ParkingType.BIKE, 2, true), Arguments.of(ParkingType.CAR, 2, true));
		}

		@Test
		public void processExitingVehicleTestUnableUpdateTicket() throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration

			Ticket ticket = createTicket(1, ParkingType.CAR, false,
					(new Date(System.currentTimeMillis() - (60 * 60 * 1000))), null);

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			when(ticketDAO.updateTicket(ticket)).thenReturn(false);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		}

		@Test
		public void processExitingVehicleTestUnableParkingType() throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			Ticket ticket = createTicket(1, ParkingType.NO_PARKING_TYPETEST, false,
					(new Date(System.currentTimeMillis() - (60 * 60 * 1000))), null);

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		}

		@Test
		public void testProcessExitingVehicleIfRegistrationNumbeError() throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber())
					.thenThrow(new IllegalArgumentException("Unable to process exiting vehicle"));
			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(0)).getTicket(anyString());
		}

		@Test
		public void testProcessExitingVehicleIfTimeOutNotNull() throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			Ticket ticket = createTicket(1, ParkingType.CAR, false,
					(new Date(System.currentTimeMillis() - (60 * 60 * 1000))),
					(new Date(System.currentTimeMillis() - (60 * 60 * 1000))));// OutTime not null

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));
		}

		@Test
		public void testProcessExitingVehicleIfTimeInGreaterThanTimeOut() throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			Ticket ticket = createTicket(1, ParkingType.CAR, false,
					(new Date(System.currentTimeMillis() + (60 * 60 * 1000))), null);

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));
		}
	}

	@Nested
	@DisplayName("Test de l'entrée d'un véhicule")
	class testIncomingVehicle {

		@ParameterizedTest
		@MethodSource("provideParkingAndNbOfTicketParameters")
		public void testProcessIncomingVehicle(int readerSelection, ParkingType parkingType, int nbOfTicket,
				Boolean isRegularUser) throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(inputReaderUtil.readSelection()).thenReturn(readerSelection);

			when(ticketDAO.getNbTicket(anyString())).thenReturn(nbOfTicket);
			when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(1);
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

			// WHEN
			parkingService.processIncomingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));

			assertThat(isRegularUser).isEqualTo(parkingService.isRegularUser("ABCDEF"));
		}

		private static Stream<Arguments> provideParkingAndNbOfTicketParameters() {
			return Stream.of(Arguments.of(2, ParkingType.BIKE, 0, false), Arguments.of(1, ParkingType.CAR, 0, false),
					Arguments.of(2, ParkingType.BIKE, 2, true), Arguments.of(1, ParkingType.CAR, 2, true));
		}

		@Test
		public void testProcessIncomingVehicleErrorReadVehicleRegistrationNumber() throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber())
					.thenThrow(new RuntimeException("Unable to process incoming vehicle"));// Error
			when(inputReaderUtil.readSelection()).thenReturn(1);// Car

			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processIncomingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
		}

		@Test
		public void testProcessIncomingVehicleErrorRegistrationNumberIsAlreadyInDatabase() throws Exception {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(inputReaderUtil.readSelection()).thenReturn(1);// Car

			when(ticketDAO.ticketIsInDatabaseWithOutTimeNull(anyString())).thenReturn(true);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

			// WHEN
			parkingService.processIncomingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
		}
	}

	@Nested
	@DisplayName("Test l'attribution d'un numero de place de parking")
	class testGetNextParkingNumber {

		@ParameterizedTest
		@MethodSource("provideParkingAndNextAvailableSlotParameters")
		public void testGetNextParkingNumberIfAvailable(ParkingType parkingType, int slotNumber) {
			// GIVEN
			ParkingSpot TrueParkingSpot = new ParkingSpot(slotNumber, parkingType, true);
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(slotNumber);

			// WHEN
			ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
			assertThat(TrueParkingSpot).isEqualTo(parkingSpotTest);
		}

		private static Stream<Arguments> provideParkingAndNextAvailableSlotParameters() {
			return Stream.of(Arguments.of(ParkingType.BIKE, 4), Arguments.of(ParkingType.CAR, 1));
		}

		@Test
		public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// THEN
			assertNull(parkingService.getNextParkingNumberIfAvailable());
		}

		@Test
		public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(3); // WHEN
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// THEN
			assertNull(parkingService.getNextParkingNumberIfAvailable());
		}

		@Test
		public void testGetNextParkingNumberIfGetNextAvailableSlotError() {
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(1); // WHEN
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR))
					.thenThrow(new RuntimeException("Error fetching next available slot"));

			// THEN
			assertNull(parkingService.getNextParkingNumberIfAvailable());
		}
	}

}
