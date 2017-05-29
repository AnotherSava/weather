package com.crossover.trial.weather.common;

import javax.ws.rs.core.Response;

/**
 * An internal exception marker
 */
public class WeatherException extends Exception
{
	private Response.StatusType status;

	public WeatherException(String message)
	{
		this(message, Response.Status.INTERNAL_SERVER_ERROR);
	}

	public WeatherException(String message, Response.StatusType status)
	{
		super(message);
		this.status = status;
	}

	public Response createResponse()
	{
		return Response.status(status).entity(getMessage()).build();
	}
}
