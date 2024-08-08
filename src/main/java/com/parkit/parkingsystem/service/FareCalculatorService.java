package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	public void calculateFare(Ticket ticket) {
		calculateFare(ticket, false);
	}

	public void calculateFare(Ticket ticket, boolean discount) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		double duration = (double) (TimeUnit.MILLISECONDS.toMinutes(outHour - inHour)) / 60;

		if (duration <= 0.5) {
			ticket.setPrice(0);
		} else {
			if (discount == false) {
				switch (ticket.getParkingSpot().getParkingType()) {
				case CAR: {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
					break;
				}
				case BIKE: {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
					break;
				}
				default:
					throw new IllegalArgumentException("Unkown Parking Type");
				}
			} else { // if discount true then 5% discount
				switch (ticket.getParkingSpot().getParkingType()) {
				case CAR: {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * 0.95);
					break;
				}
				case BIKE: {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * 0.95);
					break;
				}
				default:
					throw new IllegalArgumentException("Unkown Parking Type");
				}

			}
		}
	}
}