package com.crossover.trial.weather.performance;

import com.crossover.trial.weather.data.AirportData;
import org.glassfish.jersey.spi.Contract;

import java.util.Map;

/**
 * Performance counters to better understand most requested information, this map can be improved but
 * for now provides the basis for future performance optimizations.
 */
@Contract
public interface PerformanceMonitor
{
	/**
	 * Log request for particular radius
	 *
	 * @param radius radius
	 */
	void recordRadiusRequest(double radius);

	/**
	 * Log request for particular airport
	 *
	 * @param airportData airport
	 */
	void recordAirportRequest(AirportData airportData);

	/**
	 * Clear performance log for particular airport.
	 * Not very common scenario, used when airport is not tracked anymore.
	 *
	 * @param airportData airport, where performance log should be cleared
	 */
	void clearPerformanceLog(AirportData airportData);

	/**
	 * Provide statistics for radius requests in form of histogram:
	 * number of requests in each 1 km step from 0 to max recorded radius
	 *
	 * @return number of requests in each 1 km step from 0 to max recorded radius
	 */
	int[] getRadiusHistogram();

	/**
	 * Provide statistics for airport request
	 *
	 * @return frequency of each airport in all requests
	 */
	Map<AirportData, Double> getAirportFrequencyStats();
}
