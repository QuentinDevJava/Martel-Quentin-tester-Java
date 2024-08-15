package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.DecimalFormat;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@AfterEach
	private void undefFareCalculatorService() {
		ticket = null;
	}

	@Nested
	@DisplayName("Test la gestion des erreurs lors du calcul du prix ")
	class ErrorOfFareCalculatorService {

		@Test
		public void calculateFareUnkownType() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));// Time 1h
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// THEN

			assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
		}

		@Test
		public void calculateFareBikeWithFutureInTime() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // In Time > out time
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// THEN

			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
		}

		@Test
		public void calculateFareCarWithFutureInTime() {
			// GIVEN
			Date inTime = new Date();
			Date outTime = new Date();
			outTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// THEN

			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
		}

		@Test
		public void calculateFareWithParkingTypeError() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.PLANE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// THEN

			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
		}

		@Test
		public void calculateFareDiscountWithParkingTypeError() { // 5% discount
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.PLANE, false);
			boolean discount = true;
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// THEN

			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, discount));
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix pour une durée de 1h")
	class calculateFareFor1h {

		@Test
		public void calculateFareCar() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));// Time 1h
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN
			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(Fare.CAR_RATE_PER_HOUR).replace(',', '.')), ticket.getPrice());
		}

		@Test
		public void calculateFareBike() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // Time 1h
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN

			assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix pour 45 minutes")
	class calculateFareWithLessThanOneHourParkingTime {
		@Test
		public void calculateFareBikeWithLessThanOneHourParkingTime() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN

			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(Fare.BIKE_RATE_PER_HOUR * 0.75).replace(',', '.')),
					ticket.getPrice());
		}

		@Test
		public void calculateFareCarWithLessThanOneHourParkingTime() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN

			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(Fare.CAR_RATE_PER_HOUR * 0.75).replace(',', '.')),
					ticket.getPrice());
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix pour 24h")
	class calculateFareWithMoreThanADayParkingTime {
		@Test
		public void calculateFareCarWithMoreThanADayParkingTime() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// Time 24h
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN

			assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
		}

		@Test
		public void calculateFareBikeWithMoreThanADayParkingTime() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// Time 24h
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN
			assertEquals((24 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix pour moins de 30 minutes")
	class calculateFareWithLessThan30minutesParkingTime {
		@Test
		public void calculateFareCarWithLessThan30minutesParkingTime() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000) / 2);
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN

			assertEquals(0, ticket.getPrice());
		}

		@Test
		public void calculateFareBikeWithLessThan30minutesParkingTime() {
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000) / 2);
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket);
			// THEN

			assertEquals(0, ticket.getPrice());
		}
	}

	@Nested
	@DisplayName("Test le calcul du prix avec la reduction de 5%")
	class calculateFareWithDiscount {
		@Test
		public void calculateFareCarWithDiscount() { // 5% discount
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			boolean discount = true;
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket, discount);
			// THEN

			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(Fare.CAR_RATE_PER_HOUR * 0.95).replace(',', '.')),
					ticket.getPrice());
		}

		@Test
		public void calculateFareBikeWithDiscount() { // 5% discount
			// GIVEN
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
			boolean discount = true;
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			// WHEN
			fareCalculatorService.calculateFare(ticket, discount);
			// THEN

			DecimalFormat df = new DecimalFormat("#.##");
			assertEquals(Double.parseDouble(df.format(Fare.BIKE_RATE_PER_HOUR * 0.95).replace(',', '.')),
					ticket.getPrice());
		}
	}

}