package com.crossover.trial.weather.client;

import com.crossover.trial.weather.common.Calculations;
import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointType;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Client helper for collect interface requests. Easy request formatting and various high lever result representation/
 */
public class CollectClientHelper extends ClientHelper
{
	public CollectClientHelper(WebTarget target)
	{
		super(target);
	}

	/**
	 * Add a new airport to the known airport list.
	 *
	 * @param iata      the 3 letter airport code of the new airport
	 * @param latitude  the airport's latitude in degrees [-90, 90]
	 * @param longitude the airport's longitude in degrees [-180, 180]
	 * @return HTTP Response code for the add operation
	 */
	public int airportPost(String iata, double latitude, double longitude)
	{
		String requestString = String.format("/collect/airport/%s/%s/%s", iata, Calculations.formatDouble(latitude), Calculations.formatDouble(longitude));
		Response response = target.path(requestString).request().post(Entity.entity(null, MediaType.APPLICATION_JSON));
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		return response.getStatus();
	}


	/**
	 * Update the airports atmospheric information for a particular pointType with
	 * json formatted data point information.
	 *
	 * @param iata      the 3 letter airport code
	 * @param type      the point type, {@link DataPointType} for a complete list
	 * @param dataPoint a json dict containing mean, first, second, third and count keys
	 * @return HTTP Response code
	 */
	public int weatherPost(String iata, DataPointType type, DataPoint dataPoint)
	{
		Entity<DataPoint> dataPointEntity = Entity.entity(dataPoint, MediaType.APPLICATION_JSON);
		Response response = target.path(String.format("/collect/weather/%s/%s", iata, type.name())).request().post(dataPointEntity);
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		return response.getStatus();
	}

	/**
	 * Retrieve airport data, including latitude and longitude for a particular airport
	 *
	 * @param iata the 3 letter airport code
	 * @return airport information: iata, latitude, longitude
	 */
	public AirportData airportGet(String iata)
	{
		Response response = target.path("/collect/airport/" + iata).request().get();
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		return gson.fromJson(responseString, AirportData.class);
	}

	/**
	 * Return a list of known airports
	 *
	 * @return list of iata codes for known airports
	 */
	public List<String> airportsGet()
	{
		Response response = target.path("/collect/airports").request().get();
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		Type airportsType = new TypeToken<List<String>>()
		{
		}.getType();

		return gson.fromJson(responseString, airportsType);
	}

	/**
	 * Remove an airport from the known airport list
	 *
	 * @param iata the 3 letter airport code
	 * @return HTTP Repsonse code for the delete operation
	 */
	public int airportDelete(String iata)
	{
		Response response = target.path(String.format("/collect/airport/%s", iata)).request().delete();
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		return response.getStatus();
	}

	/**
	 * A liveliness check for the collection endpoint.
	 * Stated to return "1 if the endpoint is alive functioning, 0 otherwise", but obviously don't do this.
	 * Probably legacy clients adopted to this, no need to change.
	 *
	 * @return "ready" string with OK response status
	 */
	public String pingGet()
	{
		Response response = target.path("/collect/ping").request().get();
		String responseString = response.readEntity(String.class);
		response.close();

		processResponse(response.getStatus(), responseString);

		return responseString;
	}

	/**
	 * Shutdown server. Our connection breaks during attempt, so -1 is the most likely successful result.
	 * If server responds instead of quiting, this response code is returned.
	 *
	 * @return -1 or response code from the server if it responds instead of quiting
	 */
	public int exit()
	{
		try
		{
			Response response = target.path("/collect/exit").request().get();
			response.close();
			return response.getStatus();
		} catch (Exception e)
		{
			return -1;
		}
	}
}
