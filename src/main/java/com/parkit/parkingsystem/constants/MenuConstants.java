package com.parkit.parkingsystem.constants;

public class MenuConstants {

	private MenuConstants() {
		super();
	}

	public static final String STARTMENU = """

			Please select an option. Simply enter the number to choose an action:
			   1. New Vehicle Entering - Allocate Parking Space.
			   2. Vehicle Exiting - Generate Ticket Price.
			   3. Shutdown System.
			""";

	public static final String SELECTMENU = """

			Please select vehicle type from menu:
			   1 CAR
			   2 BIKE
			""";
}
