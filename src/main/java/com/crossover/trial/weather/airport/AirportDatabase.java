package com.crossover.trial.weather.airport;

import com.crossover.trial.weather.data.AirportData;
import org.jvnet.hk2.annotations.Contract;

import java.util.Collection;
import java.util.List;

@Contract
public interface AirportDatabase
{

	/**
	 * Add airport to the database
	 *
	 * @param airportData airport
	 */
	void addAirport(AirportData airportData);

	/**
	 * Remove airport from database
	 *
	 * @param airport airport to remove
	 */
	void removeAirport(AirportData airport);

	/**
	 * Get airport by iata code
	 *
	 * @param iata iata code
	 * @return airport data for specific iata, or null if none present
	 */
	AirportData getAirportData(String iata);

	/**
	 * Get all airports around particular one withing stated distance
	 *
	 * @param airport  airport to measure distance to
	 * @param distance max distance to particular airport (including)
	 * @return list of the airports
	 */
	List<AirportData> getAirportsAround(AirportData airport, double distance);

	Collection<String> getAllAirportIata();
}
