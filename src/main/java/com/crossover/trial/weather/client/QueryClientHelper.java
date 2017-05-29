package com.crossover.trial.weather.client;

import com.crossover.trial.weather.common.Calculations;
import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.data.PingData;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Client helper for query interface requests. Easy request formatting and various high lever result representation/
 */
public class QueryClientHelper extends ClientHelper
{
	public QueryClientHelper(WebTarget target)
	{
		super(target);
	}

	/**
	 * Retrieve the most up to date atmospheric information from the given airport and other airports in the given
	 * radius.
	 *
	 * @param iata   the three letter airport code
	 * @param radius the radius, in km, from which to collect weather data
	 * @return list of {@link AtmosphericInformation} from the requested airport and airports in the given radius
	 */
	public List<AtmosphericInformation> weatherGet(String iata, double radius)
	{
		Type listType = new TypeToken<ArrayList<AtmosphericInformation>>()
		{
		}.getType();

		return gson.fromJson(weatherGetString(iata, radius), listType);
	}

	/**
	 * Retrieve the most up to date atmospheric information from the given airport and other airports in the given
	 * radius. Provides raw result in Json string.
	 *
	 * @param iata   the three letter airport code
	 * @param radius the radius, in km, from which to collect weather data
	 * @return Json string with list of {@link AtmosphericInformation} from the requested airport and airports in the given radius
	 */
	public String weatherGetString(String iata, double radius)
	{
		Response response = target.path(String.format("query/weather/%s/%s", iata, Calculations.formatDouble(radius))).request().get();
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		return responseString;
	}

	/**
	 * Retrieve health and status information for the the query api. Returns information about the number
	 * of datapoints currently held in memory, the frequency of requests for each IATA code and the frequency of
	 * requests for each radius.
	 *
	 * @return high-level data structure with health information.
	 */
	public PingData pingGet()
	{
		return gson.fromJson(pingGetString(), PingData.class);
	}

	/**
	 * Retrieve health and status information for the the query api. Returns information about the number
	 * of datapoints currently held in memory, the frequency of requests for each IATA code and the frequency of
	 * requests for each radius. Provides raw result in Json string.
	 *
	 * @return a JSON formatted dict with health information.
	 */
	public String pingGetString()
	{
		Response response = target.path("query/ping").request().get();
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		return responseString;
	}
}
