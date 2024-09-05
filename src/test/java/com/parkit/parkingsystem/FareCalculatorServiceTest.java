package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	public static void setUpFareCalculatorServiceTest() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	public void setUpPerTest() {
		ticket = new Ticket();
	}

	@AfterEach
	public void undefFareCalculatorService() {
		ticket = null;
	}

	@Nested
	@DisplayName("Test la gestion des erreurs lors du calcul du prix ")
	class ErrorOfFareCalculatorService {

		@ParameterizedTest
		@EnumSource(value = ParkingType.class, names = "NO_PARKING_TYPETEST", mode = EnumSource.Mode.EXCLUDE)

		public void calculateFareVehicleWithFutureInTime(ParkingType parkingType) {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // In Time > out time
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// THEN
			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
		}

		@ParameterizedTest
		@EnumSource(value = ParkingType.class, names = "NO_PARKING_TYPETEST", mode = EnumSource.Mode.EXCLUDE)

		public void calculateFareCarWithOutTimeNull(ParkingType parkingType) {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(null);
			ticket.setParkingSpot(parkingSpot);

			// THEN
			assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
		}

		@ParameterizedTest
		@EnumSource(value = ParkingType.class, names = { "CAR", "BIKE" }, mode = EnumSource.Mode.EXCLUDE)

		public void calculateFareVehicleTypeError(ParkingType parkingType) {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // In Time > out time
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// THEN
			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix pour une durée de 1h")
	class calculateFareFor1h {

		@ParameterizedTest
		@MethodSource("provideParkingAndFareParameters")
		public void calculateFareVehicleFor1h(ParkingType parkingType, double expectedFare) {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));// Time 1h
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// WHEN
			fareCalculatorService.calculateFare(ticket);

			// THEN
			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(expectedFare).replace(',', '.')), ticket.getPrice());
		}

		private static Stream<Arguments> provideParkingAndFareParameters() {
			return Stream.of(Arguments.of(ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR),
					Arguments.of(ParkingType.CAR, Fare.CAR_RATE_PER_HOUR));
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix pour 45 minutes")
	class calculateFareWithLessThanOneHourParkingTime {

		@ParameterizedTest
		@MethodSource("provideParkingAndFareParameters")
		public void calculateFareVehicleWithLessThanOneHourParkingTime(ParkingType parkingType, double expectedFare) {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// WHEN
			fareCalculatorService.calculateFare(ticket);

			// THEN
			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(expectedFare).replace(',', '.')), ticket.getPrice());
		}

		private static Stream<Arguments> provideParkingAndFareParameters() {
			return Stream.of(Arguments.of(ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR * 0.75),
					Arguments.of(ParkingType.CAR, Fare.CAR_RATE_PER_HOUR * 0.75));
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix pour 24h")
	class calculateFareWithMoreThanADayParkingTime {

		@ParameterizedTest
		@MethodSource("provideParkingAndFareParameters")
		public void calculateFareVehicleWithMoreThanADayParkingTime(ParkingType parkingType, double expectedFare) {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// Time 24h
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// WHEN
			fareCalculatorService.calculateFare(ticket);

			// THEN
			assertEquals((expectedFare), ticket.getPrice());
		}

		private static Stream<Arguments> provideParkingAndFareParameters() {
			return Stream.of(Arguments.of(ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR * 24),
					Arguments.of(ParkingType.CAR, Fare.CAR_RATE_PER_HOUR * 24));
		}

	}

	@Nested
	@DisplayName("Test le calcul du prix pour moins de 30 minutes")
	class calculateFareWithLessThan30minutesParkingTime {

		@ParameterizedTest
		@MethodSource("provideParkingAndFareParameters")
		public void calculateFareVehicleWithLessThan30minutesParkingTime(ParkingType parkingType, double expectedFare) {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000) / 2);
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			
			// THEN
			assertEquals(expectedFare, ticket.getPrice());
		}

		private static Stream<Arguments> provideParkingAndFareParameters() {
			return Stream.of(Arguments.of(ParkingType.BIKE, 0), Arguments.of(ParkingType.CAR, 0));
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix avec la reduction de 5%")
	class calculateFareWithDiscount {
		@ParameterizedTest
		@MethodSource("provideParkingAndFareParameters")
		public void calculateFareVehicleWithDiscount(ParkingType parkingType, double expectedFare) { // 5% discount
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			ticket.setDiscount(true);
			
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			
			// THEN
			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(expectedFare).replace(',', '.')), ticket.getPrice());
		}

		private static Stream<Arguments> provideParkingAndFareParameters() {
			return Stream.of(Arguments.of(ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR * 0.95),
					Arguments.of(ParkingType.CAR, Fare.CAR_RATE_PER_HOUR * 0.95));
		}

	}

}