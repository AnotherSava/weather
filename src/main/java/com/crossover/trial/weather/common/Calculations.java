package com.crossover.trial.weather.common;

import java.text.ParseException;

/**
 * Calculations and formatting helper
 */
public class Calculations
{
	/**
	 * Earth radius in km
	 */
	public static final double R = 6372.8;

	/* Number of milliseconds in one day */
	public static final long MS_IN_ONE_DAY = 86400000;

	/**
	 * Haversine distance between two points on Earth.
	 *
	 * @param latitude1  latitude of first point in degrees
	 * @param longitude1 longitude of first point in degrees
	 * @param latitude2  latitude of second point in degrees
	 * @param longitude2 longitude of second point in degrees
	 * @return the distance in KM
	 */
	public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2)
	{
		double deltaLat = Math.toRadians(latitude2 - latitude1);
		double deltaLon = Math.toRadians(longitude2 - longitude1);
		double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
				* Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2));
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	/**
	 * If we need to tune number formatting, it can be done here.
	 * There is no point in using system locale, since we need to parse unformatted file anyway.
	 * If we format numbers into US local using NumberFormat, then fraction part is truncated beyond third digit
	 * Therefore we use toString and parseDouble to maintain precession
	 *
	 * @param s string containing double value
	 * @return double value
	 */
	public static double parseDouble(String s) throws ParseException
	{
		// NumberFormat format = NumberFormat.getInstance(Locale.US);
		// Number number = format.parse(s);
		// return number.doubleValue();
		try
		{
			return Double.parseDouble(s);
		} catch (NumberFormatException e)
		{
			throw new ParseException(e.getMessage(), -1);
		}
	}

	/**
	 * If we need to tune number formatting, it can be done here.
	 * There is no point in using system locale, since we need to parse unformatted file anyway.
	 * If we format numbers into US local using NumberFormat, then fraction part is truncated beyond third digit
	 * Therefore we use toString and parseDouble to maintain precession
	 *
	 * @param d double value
	 * @return string representation in US-locale
	 */
	public static String formatDouble(double d)
	{
		// NumberFormat format = NumberFormat.getInstance(Locale.US);
		// return format.format(d);
		return Double.toString(d);
	}
}
