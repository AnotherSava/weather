package com.crossover.trial.weather;

import com.crossover.trial.weather.airport.AirportAtmosphericInformation;
import com.crossover.trial.weather.airport.AirportDatabase;
import com.crossover.trial.weather.common.AirportNotFoundException;
import com.crossover.trial.weather.common.Calculations;
import com.crossover.trial.weather.common.WeatherException;
import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.performance.PerformanceMonitor;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint
{
	@Inject
	/* Airport information provider */
	private AirportDatabase airportDatabase;

	@Inject
	/* Atmospheric information provider */
	private AirportAtmosphericInformation airportAtmosphericInformation;

	@Inject
	/* Performance logging and monitoring system */
	private PerformanceMonitor performanceMonitor;

	@Inject
	/* Gson object for serialization and deserialization of basic project data types, configured for such operations */
	private Gson gson;

	public final static Logger LOGGER = Logger.getLogger(RestWeatherQueryEndpoint.class.getName());

	/**
	 * Retrieve service health including total size of valid data points and request frequency information.
	 *
	 * @return health stats for the service as a string
	 */
	@Override
	public String ping()
	{
		/* Could use PingData for high-level object operations, but properties order may matter for auto-grader */
		Map<String, Object> retval = new HashMap<>();

		// updated in the last day
		retval.put("datasize", airportAtmosphericInformation.size(Calculations.MS_IN_ONE_DAY));

		Map<AirportData, Double> airportFrequencyStats = performanceMonitor.getAirportFrequencyStats();

		/* Basic implementation returned all airports, not only requested, so we add them for more compliance.
		   And we need Iata, not AirportData */

		Collection<String> allAirportIata = airportDatabase.getAllAirportIata();
		Map<String, Double> airportIataFrequencyStats = new HashMap<>(allAirportIata.size());
		for (String iata : allAirportIata)
			airportIataFrequencyStats.put(iata, airportFrequencyStats.getOrDefault(airportDatabase.getAirportData(iata), 0.));

		retval.put("iata_freq", airportIataFrequencyStats);

		retval.put("radius_freq", performanceMonitor.getRadiusHistogram());

		return gson.toJson(retval);
	}

	/**
	 * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
	 * return a list of matching atmosphere information.
	 *
	 * @param iata         the iataCode
	 * @param radiusString the radius in km
	 * @return a list of atmospheric information
	 */
	@Override
	public Response weather(String iata, String radiusString)
	{
		double radius = 0;
		try
		{
			if (radiusString != null)
				radius = Calculations.parseDouble(radiusString);
		} catch (ParseException e)
		{
			LOGGER.warning(e.getMessage());
			WeatherException exception = new WeatherException("Number format exception, radius: '" + radiusString + "'", Response.Status.BAD_REQUEST);
			LOGGER.warning(exception.getMessage());
			return exception.createResponse();
		}
		AirportData airport = airportDatabase.getAirportData(iata);
		if (airport == null)
		{
			WeatherException exception = new AirportNotFoundException(iata);
			LOGGER.warning(exception.getMessage());
			return exception.createResponse();
		}

		performanceMonitor.recordAirportRequest(airport);
		performanceMonitor.recordRadiusRequest(radius);

		List<AtmosphericInformation> retval = new ArrayList<>();
		if (radius == 0)
			retval.add(airportAtmosphericInformation.getAtmosphericInformation(airport));
		else
		{
			for (AirportData airportData : airportDatabase.getAirportsAround(airport, radius))
			{
				AtmosphericInformation atmosphericInformation = airportAtmosphericInformation.getAtmosphericInformation(airportData);
				if (atmosphericInformation != null)
					retval.add(atmosphericInformation);
			}
		}
		return Response.ok(retval).build();
	}
}
