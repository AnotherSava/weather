package com.crossover.trial.weather.airport;

import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.AtmosphericInformation;
import org.glassfish.jersey.spi.Contract;

/**
 * Atmospheric information database.
 */
@Contract
public interface AirportAtmosphericInformation
{
	/**
	 * Get last information state for particular airport, or null if none present
	 *
	 * @param airportData airport, where atmospheric information is requested
	 * @return last information state for particular airport, or null if none present
	 */
	AtmosphericInformation getAtmosphericInformation(AirportData airportData);

	/**
	 * Update current airport information with a new one. Only last state is stored.
	 *
	 * @param airportData            airport, where atmospheric information is updated
	 * @param atmosphericInformation current atmospheric information
	 */
	void updateAtmosphericInformation(AirportData airportData, AtmosphericInformation atmosphericInformation);

	/**
	 * Clear atmospheric information for particular airport.
	 * Not very common scenario, used when airport is not tracked anymore.
	 *
	 * @param airportData airport, where atmospheric information should be cleared
	 */
	void clearAtmosphericInformation(AirportData airportData);

	/**
	 * Get total size of valid data points (last updated within stated period of time)
	 *
	 * @validTimePeriod time period in ms
	 */
	int size(long validTimePeriod);
}
