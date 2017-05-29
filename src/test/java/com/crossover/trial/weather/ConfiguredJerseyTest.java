package com.crossover.trial.weather;

import com.crossover.trial.weather.airport.AirportAtmosphericInformation;
import com.crossover.trial.weather.airport.AirportAtmosphericInformationMemory;
import com.crossover.trial.weather.airport.AirportDatabase;
import com.crossover.trial.weather.airport.AirportDatabaseMemory;
import com.crossover.trial.weather.client.CollectClientHelper;
import com.crossover.trial.weather.client.QueryClientHelper;
import com.crossover.trial.weather.common.WeatherGsonFactory;
import com.crossover.trial.weather.performance.PerformanceMonitor;
import com.crossover.trial.weather.performance.PerformanceMonitorMemory;
import com.google.gson.Gson;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;

import javax.ws.rs.core.Application;

/**
 * Provides basic configuration for Jersey tests
 */
public class ConfiguredJerseyTest extends JerseyTest
{
	public static final double DELTA_FOR_COMPARE_DOUBLE = 1e-15;

	/* Airport information provider */
	protected AirportDatabase airportDatabase;
	/* Atmospheric information provider */
	protected AirportAtmosphericInformation airportAtmosphericInformation;
	protected PerformanceMonitor performanceMonitor;
	/* High-level interfaces for client requests */
	protected QueryClientHelper queryClientHelper;
	protected CollectClientHelper collectClientHelper;

	@Before
	public void init()
	{
		queryClientHelper = new QueryClientHelper(target());
		collectClientHelper = new CollectClientHelper(target());
	}

	@Override
	protected Application configure()
	{
		final ResourceConfig resourceConfig = new ResourceConfig();

		resourceConfig.register(new AbstractBinder()
		{
			@Override
			protected void configure()
			{
				bind(airportDatabase = new AirportDatabaseMemory()).to(AirportDatabase.class);
				bind(airportAtmosphericInformation = new AirportAtmosphericInformationMemory()).to(AirportAtmosphericInformation.class);
				bind(performanceMonitor = new PerformanceMonitorMemory()).to(PerformanceMonitor.class);
				bind(WeatherGsonFactory.createGson()).to(Gson.class);
			}
		});
		resourceConfig.packages(true, "com.crossover.trial.weather");
		return resourceConfig;
	}

	protected void createTestAirports()
	{
		collectClientHelper.airportPost("BOS", 42.364347, -71.005181);
		collectClientHelper.airportPost("EWR", 40.6925, -74.168667);
		collectClientHelper.airportPost("JFK", 40.639751, -73.778925);
		collectClientHelper.airportPost("LGA", 40.777245, -73.872608);
		collectClientHelper.airportPost("MMU", 40.79935, -74.4148747);
	}
}
