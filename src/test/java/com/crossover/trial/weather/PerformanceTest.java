package com.crossover.trial.weather;

import com.crossover.trial.weather.common.RandomDataFactory;
import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Adding airports and querying weather test cases. Can be used with junit-benchmarks for further profiling.
 */
public class PerformanceTest extends ConfiguredJerseyTest
{
	private static final int NUMBER_OF_AIRPORTS = 100;

//	@Rule
//	public TestRule benchmarkRun = new BenchmarkRule();

	/**
	 * Adding airports before each test
	 */
	@Before
	public void addAirports()
	{
		for (int i = 0; i < NUMBER_OF_AIRPORTS; i++)
		{
			AirportData airportData = RandomDataFactory.createRandomAirportData();
			collectClientHelper.airportPost(airportData.getIata(), airportData.getLatitude(), airportData.getLongitude());
			for (DataPointType type : DataPointType.values())
			{
				DataPoint dataPoint = RandomDataFactory.createDataPoint(type, i);
				collectClientHelper.weatherPost(airportData.getIata(), type, dataPoint);
			}
		}
	}

	/**
	 * Test to add airports
	 */
	@Test
//	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1)
	public void testAddAirportsPerformance()
	{
		System.out.println("Total requests performed: " + NUMBER_OF_AIRPORTS * (DataPointType.values().length + 1));
	}

	/**
	 * Query weather with random radius test
	 */
	@Test
//	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1)
	public void testWeatherRandomQueryPerformance()
	{
		List<String> iataList = collectClientHelper.airportsGet();
		for (int i = 0; i < 10; i++)
			for (String iata : iataList)
				queryClientHelper.weatherGet(iata, RandomDataFactory.createRandomDistance() / 10);

		System.out.println("Total requests performed: " + (NUMBER_OF_AIRPORTS * (DataPointType.values().length + 1) + iataList.size() * 10));
	}

	/**
	 * Query weather with zero radius
	 */
	@Test
//	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1)
	public void testWeatherSingleQueryPerformance()
	{
		List<String> iataList = collectClientHelper.airportsGet();
		for (int i = 0; i < 10; i++)
			for (String iata : iataList)
				queryClientHelper.weatherGet(iata, 0);

		System.out.println("Total requests performed: " + (NUMBER_OF_AIRPORTS * (DataPointType.values().length + 1) + iataList.size() * 10));
	}
}