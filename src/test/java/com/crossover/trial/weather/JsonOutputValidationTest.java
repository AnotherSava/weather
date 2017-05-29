package com.crossover.trial.weather;

import com.crossover.trial.weather.common.Calculations;
import com.crossover.trial.weather.common.RandomDataFactory;
import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Testing JSON output to be sure not to change public interface while refactoring implementation
 */
public class JsonOutputValidationTest extends ConfiguredJerseyTest
{
	private final static Logger LOGGER = Logger.getLogger(JsonOutputValidationTest.class.getName());


	/**
	 * Validating JSON output for weather query, both field names and values.
	 * Schema validation can be done with 3rd party tools - subject for further refactoring.
	 */
	@Test
	public void testQueryWeather()
	{
		final String TEST_AIRPORT = "BOS";
		final int TEST_RADIUS = 0;

		int id = 0;
		createTestAirports();

		for (DataPointType type : DataPointType.values())
			collectClientHelper.weatherPost(TEST_AIRPORT, type, RandomDataFactory.createDataPoint(type, id++));

		Response response = target(String.format("query/weather/%s/%s", TEST_AIRPORT, Calculations.formatDouble((double) TEST_RADIUS))).request().get();
		String responseString = response.readEntity(String.class);
		response.close();

		JsonElement jsonResponse = new JsonParser().parse(responseString);
		assertTrue(jsonResponse.isJsonArray());
		JsonArray atmosphericInformationArray = jsonResponse.getAsJsonArray();
		assertEquals(1, atmosphericInformationArray.size());
		JsonElement atmosphericInformation = atmosphericInformationArray.get(0);
		assertTrue(atmosphericInformation.isJsonObject());

		id = 0;
		for (DataPointType type : DataPointType.values())
		{
			String typeName = getDataPointTypeName(type);
			assertTrue(typeName + " field not found", atmosphericInformation.getAsJsonObject().has(typeName));
			JsonElement dataPoint = atmosphericInformation.getAsJsonObject().get(typeName);
			assertTrue(typeName + " field is not an object", dataPoint.isJsonObject());
			validateDataPoint(dataPoint.getAsJsonObject(), type, id++);
		}

		assertFalse("lastUpdateTime fireld became visible",
				atmosphericInformation.getAsJsonObject().has("lastUpdateTime"));
	}

	/**
	 * Get proper field name for validation
	 *
	 * @param type data type
	 * @return data type field name
	 */
	private String getDataPointTypeName(DataPointType type)
	{
		if (DataPointType.CLOUDCOVER.equals(type))
			return "cloudCover";

		return type.name().toLowerCase();
	}

	/**
	 * Validate DataPoint JSON field names, assert values
	 *
	 * @param dataPoint Json DataPoint object
	 * @param id        data point id for testDataProvider
	 */
	private void validateDataPoint(JsonObject dataPoint, DataPointType type, int id)
	{
		LOGGER.info("Validating point: " + type);
		DataPoint expectedDataPoint = RandomDataFactory.createDataPoint(type, id);
		assertTrue(dataPoint.has("mean"));
		assertEquals(expectedDataPoint.getMean(), dataPoint.get("mean").getAsDouble(), DELTA_FOR_COMPARE_DOUBLE);
		assertTrue(dataPoint.has("first"));
		assertEquals(expectedDataPoint.getFirst(), dataPoint.get("first").getAsInt());
		assertTrue(dataPoint.has("second"));
		assertEquals(expectedDataPoint.getSecond(), dataPoint.get("second").getAsInt());
		assertTrue(dataPoint.has("third"));
		assertEquals(expectedDataPoint.getThird(), dataPoint.get("third").getAsInt());
		assertTrue(dataPoint.has("count"));
		assertEquals(expectedDataPoint.getCount(), dataPoint.get("count").getAsInt());
	}

	/**
	 * Validate ping query output field names
	 */
	@Test
	public void testPing()
	{
		AirportData airportData = RandomDataFactory.createRandomAirportData();
		collectClientHelper.airportPost(airportData.getIata(), airportData.getLatitude(), airportData.getLongitude());
		collectClientHelper.weatherPost(airportData.getIata(), DataPointType.WIND, RandomDataFactory.createDataPoint(DataPointType.WIND, 1));
		queryClientHelper.weatherGet(airportData.getIata(), 0);

		String ping = queryClientHelper.pingGetString();
		JsonElement pingResult = new JsonParser().parse(ping);
		assertTrue(pingResult.getAsJsonObject().has("datasize"));
		assertTrue(pingResult.getAsJsonObject().has("iata_freq"));
		assertTrue(pingResult.getAsJsonObject().has("radius_freq"));
	}

	/**
	 * This request is said to return "1 if the endpoint is alive functioning, 0 otherwise", but obviously don't do this.
	 * Probably legacy clients adopted to this, so we maintain consistence.
	 */
	@Test
	public void testCollectPing()
	{
		assertEquals("ready", collectClientHelper.pingGet());
	}
}