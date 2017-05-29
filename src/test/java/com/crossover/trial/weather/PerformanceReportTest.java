package com.crossover.trial.weather;

import com.crossover.trial.weather.common.RandomDataFactory;
import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.DataPointType;
import com.crossover.trial.weather.data.PingData;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test performance monitor module and reports it provides
 */
public class PerformanceReportTest extends ConfiguredJerseyTest
{
	@Test
	public void testPerformanceReport()
	{
		AirportData jfk = new AirportData("JFK", 40.639751, -73.778925);
		AirportData dme = new AirportData("DME", 55.4086111111, 37.9061111111);
		AirportData led = new AirportData("LED", 59.8002777778, 30.2625);
		AirportData bax = new AirportData("BAX", 53.36333, 83.54167);

		AirportData[] airports = {jfk, dme, led, bax};

		for (AirportData airportData : airports)
			collectClientHelper.airportPost(airportData.getIata(), airportData.getLatitude(), airportData.getLongitude());

		PingData pingData = queryClientHelper.pingGet();
		assertEquals(0, pingData.getDatasize());
		assertEquals(1001, pingData.getRadius_freq().length);
		Map<String, Double> iataMap = pingData.getIata_freq();
		assertEquals(airports.length, iataMap.size());
		for (AirportData airportData : airports)
			assertEquals(0., iataMap.get(airportData.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);

		queryClientHelper.weatherGet(dme.getIata(), 0);
		queryClientHelper.weatherGet(led.getIata(), 0);

		pingData = queryClientHelper.pingGet();
		assertEquals(0, pingData.getDatasize());
		assertEquals(1, pingData.getRadius_freq().length);
		assertEquals(2, pingData.getRadius_freq()[0]);
		assertEquals(airports.length, pingData.getIata_freq().size());
		assertEquals(0.5, pingData.getIata_freq().get(dme.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0.5, pingData.getIata_freq().get(led.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0., pingData.getIata_freq().get(jfk.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0., pingData.getIata_freq().get(bax.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);

		collectClientHelper.weatherPost(dme.getIata(), DataPointType.CLOUDCOVER, RandomDataFactory.createDataPoint(DataPointType.CLOUDCOVER, 0));
		collectClientHelper.weatherPost(dme.getIata(), DataPointType.WIND, RandomDataFactory.createDataPoint(DataPointType.WIND, 1));
		collectClientHelper.weatherPost(jfk.getIata(), DataPointType.CLOUDCOVER, RandomDataFactory.createDataPoint(DataPointType.CLOUDCOVER, 2));

		pingData = queryClientHelper.pingGet();
		assertEquals(2, pingData.getDatasize());
		assertEquals(1, pingData.getRadius_freq().length);
		assertEquals(2, pingData.getRadius_freq()[0]);
		assertEquals(airports.length, pingData.getIata_freq().size());
		assertEquals(0.5, pingData.getIata_freq().get(dme.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0.5, pingData.getIata_freq().get(led.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0., pingData.getIata_freq().get(jfk.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0., pingData.getIata_freq().get(bax.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);

		queryClientHelper.weatherGet(dme.getIata(), 75);
		queryClientHelper.weatherGet(jfk.getIata(), 25);

		pingData = queryClientHelper.pingGet();
		assertEquals(2, pingData.getDatasize());
		assertEquals(76, pingData.getRadius_freq().length);
		for (int i = 0; i < pingData.getRadius_freq().length; i++)
			assertEquals("Radius array position " + i, i == 0 ? 2 : i == 25 || i == 75 ? 1 : 0, pingData.getRadius_freq()[i]);
		assertEquals(airports.length, pingData.getIata_freq().size());
		assertEquals(0.5, pingData.getIata_freq().get(dme.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0.25, pingData.getIata_freq().get(led.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0.25, pingData.getIata_freq().get(jfk.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0., pingData.getIata_freq().get(bax.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);

		collectClientHelper.airportDelete(jfk.getIata());

		pingData = queryClientHelper.pingGet();
		assertEquals(1, pingData.getDatasize());
		assertEquals(76, pingData.getRadius_freq().length);
		for (int i = 0; i < pingData.getRadius_freq().length; i++)
			assertEquals("Radius array position " + i, i == 0 ? 2 : i == 25 || i == 75 ? 1 : 0, pingData.getRadius_freq()[i]);
		assertEquals(airports.length - 1, pingData.getIata_freq().size());
		assertEquals(2. / 3, pingData.getIata_freq().get(dme.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(1. / 3, pingData.getIata_freq().get(led.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
		assertEquals(0., pingData.getIata_freq().get(bax.getIata()), ConfiguredJerseyTest.DELTA_FOR_COMPARE_DOUBLE);
	}
}