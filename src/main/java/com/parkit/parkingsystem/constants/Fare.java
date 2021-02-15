package com.parkit.parkingsystem.constants;

/**
 * class representing the rate per hour for a type if vehicle.
 * @author tlili
 */
public class Fare {
    public static final double BIKE_RATE_PER_HOUR = 1.0;
    public static final double CAR_RATE_PER_HOUR = 1.5;
    public static final double RECURRING_USER = 0.95;

    /**
     * method to round Fare with 2 numbers after comma.
     *
     * @param price a price of type double
     * @return the price rounded with 2 numbers after the comma
     */
    public static double roundedFare(double price) {
        return (double) Math.round(price * 100) / 100;
    }
}
