package com.crossover.trial.weather;

import com.crossover.trial.weather.common.Calculations;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class NumberFormatTest extends ConfiguredJerseyTest
{
	/**
	 * Test incorrect double values in latitude and longitude parameters interpretation
	 */
	@Test
	public void collectAirportNumberFormatTest()
	{
		final String TEST_AIRPORT = "BOS";
		String correctLatitude = Calculations.formatDouble(42.364347);
		String correctLongitude = Calculations.formatDouble(-71.005181);
		String incorrectLatitude = "4236'43\"";
		String incorrectLongitude = "71.005181E";
		Response response;
		Entity<Object> emptyEntity = Entity.entity(null, MediaType.APPLICATION_JSON);
		response = target(String.format("/collect/airport/%s/%s/%s", TEST_AIRPORT, correctLatitude, correctLongitude)).request().post(emptyEntity);
		response.close();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		response = target(String.format("/collect/airport/%s/%s/%s", TEST_AIRPORT, incorrectLatitude, correctLongitude)).request().post(emptyEntity);
		response.close();
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		response = target(String.format("/collect/airport/%s/%s/%s", TEST_AIRPORT, correctLatitude, incorrectLongitude)).request().post(emptyEntity);
		response.close();
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}


	/**
	 * Test incorrect double values in radius parameter interpretation
	 */
	@Test
	public void queryWeatherNumberFormatTest()
	{
		final String TEST_AIRPORT = "BOS";
		collectClientHelper.airportPost(TEST_AIRPORT, 42.364347, -71.005181);

		String correctRadius = Calculations.formatDouble(452.364347);
		String incorrectRadius = "71.005181E";
		Response response;

		response = target(String.format("query/weather/%s/%s", TEST_AIRPORT, correctRadius)).request().get();
		response.close();
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		response = target(String.format("query/weather/%s/%s", TEST_AIRPORT, incorrectRadius)).request().get();
		response.close();
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	/**
	 * Test double values in datafile format interpretation
	 */
	@Test
	public void testForceUSLocaleFormat() throws ParseException
	{
		double d = 42.364347;
		String usDouble = "42.364347";

		assertEquals(d, Calculations.parseDouble(usDouble), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);

		assertEquals(usDouble, Calculations.formatDouble(d));
	}
}