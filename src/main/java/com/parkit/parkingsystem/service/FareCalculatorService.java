package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public static final String UNKNOWN_PARKING_TYPE = "Unknown Parking Type";

    public void calculateFare(Ticket ticket) {
        if (ticket.isInvalidTime()) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR -> ticket.computePrice(Fare.CAR_RATE_PER_HOUR);
            case BIKE -> ticket.computePrice(Fare.BIKE_RATE_PER_HOUR);
            default -> throw new IllegalArgumentException(UNKNOWN_PARKING_TYPE);
        }
    }

}