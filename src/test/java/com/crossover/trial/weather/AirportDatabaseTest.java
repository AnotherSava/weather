package com.crossover.trial.weather;

import com.crossover.trial.weather.airport.AirportDatabase;
import com.crossover.trial.weather.airport.AirportDatabaseMemory;
import com.crossover.trial.weather.common.Calculations;
import com.crossover.trial.weather.data.AirportData;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.crossover.trial.weather.ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing correct distance between airports calculation
 */
public class AirportDatabaseTest
{
	/**
	 * Test calculations library
	 */
	@Test
	public void testCalculateDistance()
	{
		AirportData jfk = new AirportData("JFK", 40.639751, -73.778925);
		AirportData dme = new AirportData("DME", 55.4086111111, 37.9061111111);
		AirportData led = new AirportData("LED", 59.8002777778, 30.2625);

		assertEquals(667., Calculations
				.calculateDistance(dme.getLatitude(), dme.getLongitude(), led.getLatitude(), led.getLongitude()), 1);
		assertEquals(7547, Calculations
				.calculateDistance(dme.getLatitude(), dme.getLongitude(), jfk.getLatitude(), jfk.getLongitude()), 1);
	}

	/**
	 * Test AirportData object creation
	 */
	@Test
	public void testAirportData()
	{
		AirportData jfk = new AirportData("JFK", 40.639751, -73.778925);

		assertEquals("JFK", jfk.getIata());
		assertEquals(40.639751, jfk.getLatitude(), DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(-73.778925, jfk.getLongitude(), DELTA_FOR_COMPARE_DOUBLE);
	}

	/**
	 * Test airport database operations
	 */
	@Test
	public void testAirportDatabaseSingle()
	{
		AirportDatabase airportDatabase = new AirportDatabaseMemory();

		AirportData jfk = new AirportData("JFK", 40.639751, -73.778925);
		airportDatabase.addAirport(jfk);

		assertEquals(jfk, airportDatabase.getAirportData(jfk.getIata()));
		assertEquals(jfk.getLatitude(), airportDatabase.getAirportData(jfk.getIata()).getLatitude(), DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(jfk.getLongitude(), airportDatabase.getAirportData(jfk.getIata()).getLongitude(), DELTA_FOR_COMPARE_DOUBLE);
	}

	/**
	 * Test airport database requests for multiple objects
	 */
	@Test
	public void testAirportDatabase()
	{
		AirportDatabase airportDatabase = new AirportDatabaseMemory();

		AirportData jfk = new AirportData("JFK", 40.639751, -73.778925);
		AirportData dme = new AirportData("DME", 55.4086111111, 37.9061111111);
		AirportData led = new AirportData("LED", 59.8002777778, 30.2625);
		AirportData bax = new AirportData("BAX", 53.36333, 83.54167);
		AirportData vvo = new AirportData("VVO", 43.39917, 132.15139);
		AirportData vog = new AirportData("VOG", 48.78167, 44.34667);
		AirportData klf = new AirportData("KLF", 54.5466666667, 36.3688888889);
		AirportData nbc = new AirportData("NBC", 55.56333, 52.095);
		AirportData goj = new AirportData("GOJ", 56.23, 43.78667);
		AirportData rov = new AirportData("ROV", 47.25833, 39.81833);
		AirportData aer = new AirportData("AER", 43.45, 39.95667);
		AirportData iar = new AirportData("IAR", 57.61888888, 39.84138888);

		airportDatabase.addAirport(jfk);
		airportDatabase.addAirport(dme);
		airportDatabase.addAirport(led);
		airportDatabase.addAirport(bax);
		airportDatabase.addAirport(vvo);
		airportDatabase.addAirport(vog);
		airportDatabase.addAirport(klf);
		airportDatabase.addAirport(nbc);
		airportDatabase.addAirport(goj);
		airportDatabase.addAirport(rov);
		airportDatabase.addAirport(aer);
		airportDatabase.addAirport(iar);

		Map<Integer, AirportData[]> testAirportMap = new HashMap<>();
		testAirportMap.put(0, new AirportData[]{dme});
		testAirportMap.put(400, new AirportData[]{dme, klf, iar, goj});
		testAirportMap.put(800, new AirportData[]{dme, klf, iar, goj, led});
		testAirportMap.put(1200, new AirportData[]{dme, klf, iar, goj, led, nbc, vog, rov});
		testAirportMap.put(1600, new AirportData[]{dme, klf, iar, goj, led, nbc, vog, rov, aer});
		testAirportMap.put(8000, new AirportData[]{dme, klf, iar, goj, led, nbc, vog, rov, aer, jfk, vvo, bax});

		for (Map.Entry<Integer, AirportData[]> entry : testAirportMap.entrySet())
		{
			List<AirportData> airportsAround = airportDatabase.getAirportsAround(dme, entry.getKey());
			assertEquals(entry.getValue().length, airportsAround.size());
			for (AirportData ap : entry.getValue())
				assertTrue("Airport '" + ap.getIata() + "' should be in " + entry.getKey() + " km distance from DME",
						airportsAround.contains(ap));
		}
	}
}