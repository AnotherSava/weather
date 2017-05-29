package com.crossover.trial.weather.common;

import javax.ws.rs.core.Response;

/**
 * Most typical exceptional situation in this project
 */
public class AirportNotFoundException extends WeatherException
{
	public AirportNotFoundException(String iata)
	{
		super(String.format("Airport not found: '%s'", iata), Response.Status.BAD_REQUEST);
	}
}
