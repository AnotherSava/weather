package com.crossover.trial.weather;

import com.crossover.trial.weather.airport.AirportAtmosphericInformation;
import com.crossover.trial.weather.airport.AirportDatabase;
import com.crossover.trial.weather.common.AirportNotFoundException;
import com.crossover.trial.weather.common.Calculations;
import com.crossover.trial.weather.common.WeatherException;
import com.crossover.trial.weather.data.*;
import com.crossover.trial.weather.performance.PerformanceMonitor;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint
{
	public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

	@Inject
	/* Gson object for serialization and deserialization of basic project data types, configured for such operations */
	private Gson gson;

	@Inject
	/* Airport information provider */
	private AirportDatabase airportDatabase;

	@Inject
	/* Atmospheric information provider */
	private AirportAtmosphericInformation airportAtmosphericInformation;

	@Inject
	/* Performance logging and monitoring system */
	private PerformanceMonitor performanceMonitor;

	@Override
	public Response ping()
	{
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@Override
	public Response updateWeather(String iataCode,
	                              String pointType,
	                              String datapointJson)
	{
		try
		{
			addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
		} catch (WeatherException e)
		{
			LOGGER.warning(e.getMessage());
			return e.createResponse();
		}
		return Response.ok().build();
	}


	@Override
	public Response getAirports()
	{
		return Response.ok().entity(airportDatabase.getAllAirportIata()).build();
	}


	@Override
	public Response getAirport(String iata)
	{
		AirportData airportData = airportDatabase.getAirportData(iata);
		if (airportData == null)
			return new AirportNotFoundException(iata).createResponse();

		return Response.ok().entity(airportData).build();
	}


	@Override
	public Response addAirport(String iata,
	                           String latString,
	                           String longString)
	{
		double latitude, longitude;
		try
		{
			latitude = Calculations.parseDouble(latString);
			longitude = Calculations.parseDouble(longString);
		} catch (ParseException e)
		{
			LOGGER.warning(e.getMessage());
			WeatherException exception = new WeatherException("Number format exception, latitude: '" + latString + "', longitude: '" + longString + "'", Response.Status.BAD_REQUEST);
			LOGGER.warning(exception.getMessage());
			return exception.createResponse();
		}
		airportDatabase.addAirport(new AirportData(iata, latitude, longitude));
		return Response.status(Response.Status.OK).build();
	}


	@Override
	public Response deleteAirport(String iata)
	{
		AirportData airport = airportDatabase.getAirportData(iata);
		if (airport == null)
		{
			WeatherException exception = new AirportNotFoundException(iata);
			LOGGER.warning(exception.getMessage());
			return exception.createResponse();
		}

		performanceMonitor.clearPerformanceLog(airport);
		airportAtmosphericInformation.clearAtmosphericInformation(airport);
		airportDatabase.removeAirport(airport);
		return Response.ok().build();
	}

	@Override
	public Response exit()
	{
		System.exit(0);
		return Response.noContent().build();
	}

	//
	// Internal support methods
	//

	/**
	 * Update the airports weather data with the collected data.
	 *
	 * @param iataCode  the 3 letter IATA code
	 * @param pointType the point type {@link DataPointType}
	 * @param dp        a datapoint object holding pointType data
	 * @throws WeatherException if the update can not be completed
	 */
	private void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException
	{
		AirportData airportData = airportDatabase.getAirportData(iataCode);
		if (airportData == null)
			throw new AirportNotFoundException(iataCode);
		AtmosphericInformation atmosphericInformation = airportAtmosphericInformation.getAtmosphericInformation(airportData);
		if (atmosphericInformation == null)
			atmosphericInformation = new AtmosphericInformationHolder();
		updateAtmosphericInformation(atmosphericInformation, pointType, dp);
		airportAtmosphericInformation.updateAtmosphericInformation(airportData, atmosphericInformation);
	}

	/**
	 * update atmospheric information with the given data point for the given point type
	 *
	 * @param ai        the atmospheric information object to update
	 * @param pointType the data point type as a string
	 * @param dp        the actual data point
	 */
	private void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws WeatherException
	{
		for (DataPointType type : DataPointType.values())
		{
			if (!type.name().equalsIgnoreCase(pointType))
				continue;

			if ((dp.getMean() >= AtmosphericInformationConstraints.getLowBound(type)) &&
					(dp.getMean() < AtmosphericInformationConstraints.getUpperBound(type)))
			{
				switch (type)
				{
					case WIND:
						ai.setWind(dp);
						return;
					case HUMIDITY:
						ai.setHumidity(dp);
						return;
					case PRESSURE:
						ai.setPressure(dp);
						return;
					case CLOUDCOVER:
						ai.setCloudCover(dp);
						return;
					case TEMPERATURE:
						ai.setTemperature(dp);
						return;
					case PRECIPITATION:
						ai.setPrecipitation(dp);
						return;
				}
			}
			throw new WeatherException("Data point mean value is outside of regular bounds for " + pointType, Response.Status.BAD_REQUEST);
		}
		throw new WeatherException("Data point type is not recognized: '" + pointType + "'", Response.Status.BAD_REQUEST);
	}
}
