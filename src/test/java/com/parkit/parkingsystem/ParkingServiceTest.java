package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

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

	/*
	 * @BeforeEach public void setUpPerTestParkingServiceTest() { try {
	 * lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(
	 * "ABCDEF");
	 * 
	 * ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false); //
	 * 
	 * Ticket ticket = new Ticket();// creation du ticket ticket.setInTime(new
	 * Date(System.currentTimeMillis() - (60 * 60 * 1000)));
	 * ticket.setParkingSpot(parkingSpot); ticket.setVehicleRegNumber("ABCDEF");
	 * 
	 * lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
	 * lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
	 * lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).
	 * thenReturn(true);
	 * 
	 * parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO,
	 * ticketDAO);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); throw new
	 * RuntimeException("Failed to set up test mock objects"); } }
	 */

	@Nested
	@DisplayName("Test de la sortie d'un véhicule")
	class testExitingVehicle {
		@Test
		public void processExitingVehicleCarTest() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
			// Creation du ticket
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK
			when(ticketDAO.getNbTicket(anyString())).thenReturn(0); // Regular user
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);// OK
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);// OK

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

			assertFalse(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void processExitingVehicleBikeTest() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
			// Creation du ticket
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK
			when(ticketDAO.getNbTicket(anyString())).thenReturn(0); // Regular user
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);// OK
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);// OK

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

			assertFalse(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void processExitingVehicleCarWithRegularUserTest() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
			// Creation du ticket
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK
			when(ticketDAO.getNbTicket(anyString())).thenReturn(2); // Regular user
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);// OK
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);// OK

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

			assertTrue(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void processExitingVehicleBikeWithRegularUserTest() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
			// Creation du ticket
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK
			when(ticketDAO.getNbTicket(anyString())).thenReturn(2); // Regular user
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);// OK
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);// OK

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
			verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

			assertTrue(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void processExitingVehicleTestUnableUpdateTicket() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}

			// Creation du ticket
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			ticket.setOutTime(null);

			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK
			when(ticketDAO.updateTicket(ticket)).thenReturn(false);// OK

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		}

		@Test
		public void testProcessExitingVehicleIfRegistrationNumbeError() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber())
						.thenThrow(new RuntimeException("Unable to process exiting vehicle"));

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			// WHEN
			parkingService.processExitingVehicle();
			// THEN
			verify(ticketDAO, Mockito.times(0)).getTicket(anyString());
		}

		@Test
		public void testProcessExitingVehicleIfTimeOutNotNull() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}

			// Creation du ticket
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			ticket.setOutTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));// Error not null
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));
		}

		@Test
		public void testProcessExitingVehicleIfTimeInGreaterThanTimeOut() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}

			// Creation du ticket
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() + (60 * 60 * 1000))); // greater than out time
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket); // OK

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processExitingVehicle();

			// THEN
			verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));
		}
	}

	@Nested
	@DisplayName("Test de l'entrée d'un véhicule")
	class testIncomingVehicle {

		@Test
		public void testProcessIncomingVehicleCarWithRegularUser() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
				when(inputReaderUtil.readSelection()).thenReturn(1);// Car
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}

			when(ticketDAO.getNbTicket(anyString())).thenReturn(2); // Regular user
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1); // Parking spot 1
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
			assertTrue(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void testProcessIncomingVehicleBikeWithRegularUser() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
				when(inputReaderUtil.readSelection()).thenReturn(2); // Bike
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}

			when(ticketDAO.getNbTicket(anyString())).thenReturn(2); // Regular user
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);// Parking spot 1
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
			assertTrue(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void testProcessIncomingVehicleCar() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
				when(inputReaderUtil.readSelection()).thenReturn(1);// Car
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}

			when(ticketDAO.getNbTicket(anyString())).thenReturn(0); // Not Regular user
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1); // Parking spot 1
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
			assertFalse(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void testProcessIncomingVehicleBike() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// Vehicle Registration
				when(inputReaderUtil.readSelection()).thenReturn(2); // Bike
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}

			when(ticketDAO.getNbTicket(anyString())).thenReturn(0); // Not Regular user
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);// Parking spot 1
			when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot((any(ParkingType.class)));
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
			assertFalse(parkingService.isRegularUser("ABCDEF"));
		}

		@Test
		public void testProcessIncomingVehicleErrorReadVehicleRegistrationNumber() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber())
						.thenThrow(new RuntimeException("Unable to process incoming vehicle"));// Error
				when(inputReaderUtil.readSelection()).thenReturn(1);// Car
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processIncomingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
		}

		@Test
		public void testProcessIncomingVehicleErrorRegistrationNumberIsAlreadyInDatabase() {
			// GIVEN
			try {
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
				when(inputReaderUtil.readSelection()).thenReturn(1);// Car
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to set up test mock objects");
			}
			when(ticketDAO.ticketIsInDatabaseWithOutTimeNull(anyString())).thenReturn(true);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			parkingService.processIncomingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
			verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
		}
	}

	@Nested
	@DisplayName("Test d'attribution du numero de place de parking")
	class testGetNextParkingNumber {

		@Test
		public void testGetNextParkingNumberIfAvailableForCar() {
			// GIVEN
			ParkingSpot parkingSpotCarTrue = new ParkingSpot(1, ParkingType.CAR, true);
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
			assertThat(parkingSpotCarTrue).isEqualTo(parkingSpotTest);
		}

		@Test
		public void testGetNextParkingNumberIfAvailableForBike() {
			// GIVEN
			ParkingSpot parkingSpotCarTrue = new ParkingSpot(4, ParkingType.BIKE, true);
			when(inputReaderUtil.readSelection()).thenReturn(2);
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// WHEN
			ParkingSpot parkingSpotTest = parkingService.getNextParkingNumberIfAvailable();

			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
			assertThat(parkingSpotCarTrue).isEqualTo(parkingSpotTest);
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
		public void testGetNextParkingNumberIfAvailableError() {
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(1); // WHEN
			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR))
					.thenThrow(new RuntimeException("Error fetching next available slot"));
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			// THEN
			assertNull(parkingService.getNextParkingNumberIfAvailable());
		}
	}

}
