package com.crossover.trial.weather;

import com.crossover.trial.weather.client.CollectClientHelper;
import com.crossover.trial.weather.client.QueryClientHelper;
import com.crossover.trial.weather.common.RandomDataFactory;
import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointType;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Client implementation for performance tests. Covers airport creation, data updating,
 * and data queries both with zero and random radius. Each operation is profiled separately.
 */
public class WeatherClientPerformanceProfiler
{
	private static final String BASE_URI = "http://localhost:9090";
	/* Number of data requests */
	private static final int QUERIES_NUMBER = 1000;
	/* Number of airports to add */
	private static final int NUMBER_OF_AIRPORTS = 1000;
	/* Length of random queries test in ms */
	private static final int RANDOM_QUERY_LENGTH = 30000;
	/* Proportion of query requests to ping requests in random test */
	private static final int RANDOM_QUERIES_PER_PING = 10;
	/* Proportion of weather request with radius 0 to requests with random radius */
	private static final int RANDOM_SINGLE_QUERIES_PER_RANDOM_QUERY = 10;

	private Client client;
	/* High-level interface for client requests */
	private QueryClientHelper queryClientHelper;
	private CollectClientHelper collectClientHelper;

	public WeatherClientPerformanceProfiler()
	{
		client = ClientBuilder.newClient();
		collectClientHelper = new CollectClientHelper(client.target(BASE_URI));
		queryClientHelper = new QueryClientHelper(client.target(BASE_URI));
	}

	/**
	 * Perform a number of add airport requests. Iata and coordinates are generated randomly
	 *
	 * @return number of requests sent (for performance measurements)
	 */
	public int addAirports()
	{

		for (int i = 0; i < NUMBER_OF_AIRPORTS; i++)
		{
			AirportData airportData = RandomDataFactory.createRandomAirportData();
			collectClientHelper.airportPost(airportData.getIata(), airportData.getLatitude(), airportData.getLongitude());
		}

		return NUMBER_OF_AIRPORTS;
	}

	/**
	 * Perform a number of update data requests: one request for each aiport.
	 * All values are generated randomly within stated constraints.
	 *
	 * @return number of requests sent (for performance measurements)
	 */
	public int addData()
	{
		System.out.println("\nAdding data");
		int id = 0;
		List<String> iataList = collectClientHelper.airportsGet();
		for (String iata : iataList)
			for (DataPointType type : DataPointType.values())
			{
				DataPoint dataPoint = RandomDataFactory.createDataPoint(type, id++);
				collectClientHelper.weatherPost(iata, type, dataPoint);
			}

		return iataList.size() * DataPointType.values().length;
	}

	/**
	 * Perform a number of data requests.
	 * Airport is chosen randomly each time, radius is also random.
	 *
	 * @return number of requests sent (for performance measurements)
	 */
	public int queryRandomWeatherData()
	{
		System.out.println("\nQuerying random data");
		Random random = new Random();
		List<String> iataList = collectClientHelper.airportsGet();

		for (int i = 0; i < QUERIES_NUMBER; i++)
			queryClientHelper.weatherGet(iataList.get(random.nextInt(iataList.size())), RandomDataFactory.createRandomDistance() / 10);

		return QUERIES_NUMBER;
	}

	/**
	 * Perform a series of random weather query requests with radius = 0.
	 * Number of requests can be adjusted by QUERIES_NUMBER constant
	 *
	 * @return number of requests sent (for performance measurements)
	 */
	public int querySingleWeatherData()
	{
		System.out.println("\nQuerying single data");
		Random random = new Random();
		List<String> iataList = collectClientHelper.airportsGet();

		for (int i = 0; i < QUERIES_NUMBER; i++)
			queryClientHelper.weatherGet(iataList.get(random.nextInt(iataList.size())), 0);

		return QUERIES_NUMBER;
	}

	/**
	 * Perform ping collect requests.
	 * Number of requests is set by QUERIES_NUMBER
	 *
	 * @return number of requests sent (for performance measurements)
	 */
	public int pingCollectRequests()
	{
		System.out.println("\nPerforming ping collect requests");

		for (int i = 0; i < QUERIES_NUMBER; i++)
			collectClientHelper.pingGet();

		return QUERIES_NUMBER;
	}

	/**
	 * Perform random query requests.
	 * Weather requests to ping requests proportion is RANDOM_QUERIES_PER_PING
	 * Proportion of weather request with radius 0 to requests with random radius is RANDOM_SINGLE_QUERIES_PER_RANDOM_QUERY
	 *
	 * @return number of requests sent (for performance measurements)
	 */
	public int randomQueries()
	{
		System.out.println("\nPerforming random requests");
		int requestNumber = 0;
		Random random = new Random();
		long endTime = System.currentTimeMillis() + RANDOM_QUERY_LENGTH;
		List<String> iataList = collectClientHelper.airportsGet();
		while (System.currentTimeMillis() < endTime)
		{
			if (random.nextInt(RANDOM_QUERIES_PER_PING) == 0)
				queryClientHelper.pingGet();
			else
			{
				String iata = iataList.get(random.nextInt(iataList.size()));
				if (random.nextInt(RANDOM_SINGLE_QUERIES_PER_RANDOM_QUERY) == 0)
					queryClientHelper.weatherGet(iata, 0);
				else
					queryClientHelper.weatherGet(iata, RandomDataFactory.createRandomDistance());
			}
			requestNumber++;
		}
		return requestNumber;
	}

	/**
	 * Measure and report performance statistics
	 *
	 * @param f method to measure performance for, should return number of requests performed during execution
	 */
	private void logPerformance(Function<WeatherClientPerformanceProfiler, Integer> f)
	{
		long start = System.currentTimeMillis();

		int requests = f.apply(this);

		long end = System.currentTimeMillis();
		System.out.println("Total time: " + (end - start) + " ms, total requests: " + requests + " = " + requests * 1000 / (end - start) + " requests per second");
	}

	/**
	 * Server shutdown request
	 */
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
		WeatherClientPerformanceProfiler wc = new WeatherClientPerformanceProfiler();

		wc.logPerformance(WeatherClientPerformanceProfiler::pingCollectRequests);

		wc.logPerformance(WeatherClientPerformanceProfiler::addAirports);

		wc.logPerformance(WeatherClientPerformanceProfiler::addData);

		wc.logPerformance(WeatherClientPerformanceProfiler::querySingleWeatherData);

		wc.logPerformance(WeatherClientPerformanceProfiler::queryRandomWeatherData);

		wc.logPerformance(WeatherClientPerformanceProfiler::randomQueries);

		wc.exit();
		wc.close();
		System.out.print("complete");
		System.exit(0);
	}
}
