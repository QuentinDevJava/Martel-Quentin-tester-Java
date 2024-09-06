package com.parkit.parkingsystem.model;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;

public class Ticket {

	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private Date inTime;
	private Date outTime;
	private boolean discount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		DecimalFormat df = new DecimalFormat("#.##");
		this.price = Double.parseDouble(df.format(price).replace(',', '.'));
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;

	}

	public Date getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}

	public void setDiscount(boolean discount) {
		this.discount = discount;
	}

	public boolean hasDiscount() {
		return discount;
	}

	public boolean isInvalidTime() {
		return this.outTime == null || this.outTime.before(this.inTime);
	}

	public void computePrice(double ratePerHour) {
		double duration = getDuration();

		if (isLessThanOrEqualTo30Min(duration)) {
			this.setPrice(0);
		} else {
			this.setPrice(this.discount ? duration * ratePerHour * Fare.FARE_DISCOUNT : duration * ratePerHour);
		}
	}

	private double getDuration() {
		long inHour = this.inTime.getTime();
		long outHour = this.outTime.getTime();
		return (double) (TimeUnit.MILLISECONDS.toSeconds(outHour - inHour)) / 3600;
	}

	private boolean isLessThanOrEqualTo30Min(double duration) {
		return duration <= 0.5;
	}

}
