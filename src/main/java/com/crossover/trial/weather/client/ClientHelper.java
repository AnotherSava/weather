package com.crossover.trial.weather.client;

import com.crossover.trial.weather.common.WeatherGsonFactory;
import com.google.gson.Gson;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Client helper for basic interface requests. Divided into collect and query parts accordingly.
 */
public class ClientHelper
{
	/* Gson object for serialization and deserialization of basic project data types, configured for such operations */
	protected final Gson gson;

	/* A client interface target identified by the resource URI */
	protected final WebTarget target;

	protected ClientHelper(WebTarget target)
	{
		this.target = target;
		gson = WeatherGsonFactory.createGson();
	}

	/**
	 * Process error response statuses. Exceptions are used to preserve response string.
	 *
	 * @param responseStatus status returned with response
	 * @param responseString message returned with response
	 */
	protected void processResponse(int responseStatus, String responseString)
	{
		if (responseStatus == Response.Status.BAD_REQUEST.getStatusCode())
			throw new BadRequestException(responseString);
		if (responseStatus != Response.Status.OK.getStatusCode())
			throw new WebApplicationException(responseString);
	}
}
