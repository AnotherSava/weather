package com.crossover.trial.weather;

import com.crossover.trial.weather.client.CollectClientHelper;
import com.crossover.trial.weather.client.QueryClientHelper;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointBuilder;
import com.crossover.trial.weather.data.DataPointType;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * A reference implementation for the weather client. Consumers of the REST API can look at WeatherClient
 * to understand API semantics. This existing client populates the REST endpoint with dummy data useful for
 * testing.
 *
 * @author code test administrator
 */
public class WeatherClient
{
	private static final String BASE_URI = "http://localhost:9090";

	private Client client;
	/* High-level interfaces for client requests */
	private QueryClientHelper queryClientHelper;
	private CollectClientHelper collectClientHelper;

	public WeatherClient()
	{
		client = ClientBuilder.newClient();
		collectClientHelper = new CollectClientHelper(client.target(BASE_URI));
		queryClientHelper = new QueryClientHelper(client.target(BASE_URI));
	}

	public void fillTestAirportData()
	{
		collectClientHelper.airportPost("BOS", 42.364347, -71.005181);
		collectClientHelper.airportPost("EWR", 40.6925, -74.168667);
		collectClientHelper.airportPost("JFK", 40.639751, -73.778925);
		collectClientHelper.airportPost("LGA", 40.777245, -73.872608);
		collectClientHelper.airportPost("MMU", 40.79935, -74.4148747);
	}

	public void pingCollect()
	{
		String collectPingResponse = collectClientHelper.pingGet();
		System.out.print("collect.ping: " + collectPingResponse + "\n");
	}

	public void query(String iata)
	{
		String queryWeatherResponse = queryClientHelper.weatherGetString(iata, 0);
		System.out.println("query." + iata + ".0: " + queryWeatherResponse);
	}

	public void pingQuery()
	{
		String queryPingResponse = queryClientHelper.pingGetString();
		System.out.println("query.ping: " + queryPingResponse);
	}

	public void populate(DataPointType pointType, int mean, int first, int second, int third, int count)
	{
		DataPoint dp = new DataPointBuilder()
				.withFirst(first).withThird(third).withMean(mean).withSecond(second).withCount(count).build();
		collectClientHelper.weatherPost("BOS", pointType, dp);
	}

	public void exit()
	{
		collectClientHelper.exit();
	}

	public void close()
	{
		client.close();
	}

	public static void main(String[] args)
	{
		WeatherClient wc = new WeatherClient();
		wc.pingCollect();

		wc.fillTestAirportData();

		wc.populate(DataPointType.WIND, 6, 0, 4, 10, 20);

		wc.query("BOS");
		wc.query("JFK");
		wc.query("EWR");
		wc.query("LGA");
		wc.query("MMU");

		wc.pingQuery();
		wc.exit();
		wc.close();
		System.out.print("complete");
		System.exit(0);
	}
}
