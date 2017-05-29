package com.crossover.trial.weather;

import com.crossover.trial.weather.common.RandomDataFactory;
import com.crossover.trial.weather.data.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test various requests
 */
public class WeatherEndpointTest extends ConfiguredJerseyTest
{
	private static final String ABSENT_AIRPORT_IATA = "XYZP";
	private DataPoint _dp;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void init()
	{
		super.init();
		createTestAirports();

		_dp = new DataPointBuilder()
				.withCount(10).withFirst(10).withSecond(20).withThird(30).withMean(22).build();
		collectClientHelper.weatherPost("BOS", DataPointType.WIND, _dp);
		queryClientHelper.weatherGet("BOS", 0);
	}

	/**
	 * Test /query/ping
	 */
	@Test
	public void testQueryPing()
	{
		PingData pingData = queryClientHelper.pingGet();
		assertEquals(1, pingData.getDatasize());
		assertEquals(5, pingData.getIata_freq().size());
	}

	/**
	 * Test /query/weather with zero radius
	 */
	@Test
	public void testQueryWeatherZero()
	{
		List<AtmosphericInformation> ais = queryClientHelper.weatherGet("BOS", 0);

		assertEquals(_dp, ais.get(0).getWind());
	}

	/**
	 * Test /query/weather with radius > 0
	 */
	@Test
	public void testQueryWeatherRadius()
	{
		// check datasize response
		collectClientHelper.weatherPost("JFK", DataPointType.WIND, _dp);
		_dp = new DataPointBuilder(_dp).withMean(40).build();
		collectClientHelper.weatherPost("EWR", DataPointType.WIND, _dp);
		_dp = new DataPointBuilder(_dp).withMean(30).build();
		collectClientHelper.weatherPost("LGA", DataPointType.WIND, _dp);

		List<AtmosphericInformation> ais = queryClientHelper.weatherGet("JFK", 200);
		assertEquals(3, ais.size());
	}

	/**
	 * Test /collect/weather
	 */
	@Test
	public void testCollectWeather()
	{

		DataPoint windDp = new DataPointBuilder().withCount(10).withFirst(10).withSecond(20).withThird(30).withMean(22).build();
		collectClientHelper.weatherPost("BOS", DataPointType.WIND, windDp);
		queryClientHelper.weatherGet("BOS", 0);

		String ping = queryClientHelper.pingGetString();
		JsonElement pingResult = new JsonParser().parse(ping);
		assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

		DataPoint cloudCoverDp = new DataPointBuilder()
				.withCount(4).withFirst(10).withSecond(60).withThird(100).withMean(50).build();
		collectClientHelper.weatherPost("BOS", DataPointType.CLOUDCOVER, cloudCoverDp);

		List<AtmosphericInformation> ais = queryClientHelper.weatherGet("BOS", 0);
		assertEquals(windDp, ais.get(0).getWind());
		assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
	}

	/**
	 * Test /collect/airport fail due to missing airport
	 */
	@Test
	public void testCollectAiportGetFail()
	{
		exception.expect(BadRequestException.class);
		exception.expectMessage(String.format("Airport not found: '%s'", ABSENT_AIRPORT_IATA));
		collectClientHelper.airportGet(ABSENT_AIRPORT_IATA);
	}

	/**
	 * Test /collect/weather fail due to non existing airport
	 */
	@Test
	public void testCollectWeatherFailAirport()
	{
		DataPointType pointType = RandomDataFactory.createRandomDataPointType();
		exception.expect(BadRequestException.class);
		exception.expectMessage(String.format("Airport not found: '%s'", ABSENT_AIRPORT_IATA));
		collectClientHelper.weatherPost(ABSENT_AIRPORT_IATA, pointType, RandomDataFactory.createDataPoint(pointType, 1));
	}

	/**
	 * Test /query/weather fail due to missing airport
	 */
	@Test
	public void testQueryWeatherFail()
	{
		exception.expect(BadRequestException.class);
		exception.expectMessage(String.format("Airport not found: '%s'", ABSENT_AIRPORT_IATA));
		queryClientHelper.weatherGet(ABSENT_AIRPORT_IATA, 0);
	}

	/**
	 * Test /collect/airport delete fail due to missing airport
	 */
	@Test
	public void testCollectAirportDeleteFail()
	{
		exception.expect(BadRequestException.class);
		exception.expectMessage(String.format("Airport not found: '%s'", ABSENT_AIRPORT_IATA));
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), collectClientHelper.airportDelete(ABSENT_AIRPORT_IATA));
	}

	/**
	 * Asserr list and array are equal (both are without duplicate elements)
	 *
	 * @param expected array without duplicate elements
	 * @param actual   list without duplicate elements
	 */
	private void assertList(String[] expected, List<String> actual)
	{
		assertEquals(expected.length, actual.size());
		for (String item : expected)
			assertTrue("Item '" + item + "' not in the list", actual.contains(item));
	}

	/**
	 * Test airport data stored and retrieved correctly
	 */
	@Test
	public void testAirportDataQuery()
	{
		AirportData airportData = RandomDataFactory.createRandomAirportData();
		collectClientHelper.airportPost(airportData.getIata(), airportData.getLatitude(), airportData.getLongitude());
		AirportData newAirportData = collectClientHelper.airportGet(airportData.getIata());
		assertEquals(airportData.getIata(), newAirportData.getIata());
		assertEquals(airportData.getLatitude(), newAirportData.getLatitude(), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(airportData.getLongitude(), newAirportData.getLongitude(), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
	}

	/**
	 * Yest /collect/airports
	 */
	@Test
	public void testCollectAirports()
	{
		assertList(new String[]{"BOS", "EWR", "JFK", "LGA", "MMU"}, collectClientHelper.airportsGet());

		collectClientHelper.airportDelete("JFK");

		assertList(new String[]{"BOS", "EWR", "LGA", "MMU"}, collectClientHelper.airportsGet());

		collectClientHelper.airportDelete("LGA");

		assertList(new String[]{"BOS", "EWR", "MMU"}, collectClientHelper.airportsGet());

		collectClientHelper.airportPost("LED", 59.8002777778, 30.2625);

		assertList(new String[]{"BOS", "LED", "EWR", "MMU"}, collectClientHelper.airportsGet());
	}

	/**
	 * Test /collect/weather fail due to incorrect data
	 */
	@Test
	public void testCollectWeatherFailData()
	{
		AirportData airportData = RandomDataFactory.createRandomAirportData();
		collectClientHelper.airportPost(airportData.getIata(), airportData.getLatitude(), airportData.getLongitude());
		DataPointType pointType = RandomDataFactory.createRandomDataPointType();
		DataPoint dataPoint = RandomDataFactory.createDataPoint(pointType, 1);

		/* Incorrect data type, can't use high-level interface for this */
		Entity<DataPoint> dataPointEntity = Entity.entity(dataPoint, MediaType.APPLICATION_JSON);
		String INCORRECT_DATA_TYPE = "NO__SUCH__TYPE";
		Response response = target().path(String.format("/collect/weather/%s/%s", airportData.getIata(), INCORRECT_DATA_TYPE)).request().post(dataPointEntity);
		String responseString = response.readEntity(String.class);
		response.close();

		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertEquals(String.format("Data point type is not recognized: '%s'", INCORRECT_DATA_TYPE), responseString);

		/* Incorrect values */
		for (DataPointType dataType : DataPointType.values())
		{
			int lowBound = AtmosphericInformationConstraints.getLowBound(dataType);
			if (lowBound > Integer.MIN_VALUE)
			{
				dataPoint = new DataPointBuilder(RandomDataFactory.createDataPoint(dataType, 1)).withMean(lowBound - 1).build();
				try
				{
					collectClientHelper.weatherPost(airportData.getIata(), dataType, dataPoint);
					assertTrue(false);
				} catch (BadRequestException e)
				{
				}
			}

			int upperBound = AtmosphericInformationConstraints.getUpperBound(dataType);
			if (upperBound < Integer.MAX_VALUE)
			{
				dataPoint = new DataPointBuilder(RandomDataFactory.createDataPoint(dataType, 1)).withMean(upperBound + 1).build();
				try
				{
					collectClientHelper.weatherPost(airportData.getIata(), dataType, dataPoint);
					assertTrue(false);
				} catch (BadRequestException e)
				{
				}
			}
		}
	}
}