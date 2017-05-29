package com.crossover.trial.weather;

import com.crossover.trial.weather.airport.AirportAtmosphericInformation;
import com.crossover.trial.weather.airport.AirportAtmosphericInformationMemory;
import com.crossover.trial.weather.airport.AirportDatabase;
import com.crossover.trial.weather.airport.AirportDatabaseMemory;
import com.crossover.trial.weather.common.WeatherGsonFactory;
import com.crossover.trial.weather.performance.PerformanceMonitor;
import com.crossover.trial.weather.performance.PerformanceMonitorMemory;
import com.google.gson.Gson;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;


/**
 * This main method will be use by the automated functional grader. You shouldn't move this class or removeAirport the
 * main method. You may change the implementation, but we encourage caution.
 *
 * @author code test administrator
 */
public class WeatherServer
{
	private static final String BASE_URL = "http://localhost:9090/";

	public static void main(String[] args)
	{
		try
		{
			System.out.println("Starting Weather App local testing server: " + BASE_URL);

			final AirportDatabase airportDatabase = new AirportDatabaseMemory();
			final AirportAtmosphericInformation airportAtmosphericInformation = new AirportAtmosphericInformationMemory();
			final PerformanceMonitor performanceMonitor = new PerformanceMonitorMemory();

			final ResourceConfig resourceConfig = new ResourceConfig();
			resourceConfig.register(new AbstractBinder()
			{
				@Override
				protected void configure()
				{
					bind(airportDatabase).to(AirportDatabase.class);
					bind(airportAtmosphericInformation).to(AirportAtmosphericInformation.class);
					bind(performanceMonitor).to(PerformanceMonitor.class);
					bind(WeatherGsonFactory.createGson()).to(Gson.class);
				}
			});
			resourceConfig.packages(true, "com.crossover.trial.weather");

			HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), resourceConfig, false);
			Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

			HttpServerProbe probe = new HttpServerProbe.Adapter()
			{
				public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request)
				{
					System.out.println(request.getRequestURI());
				}
			};
			server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);

			// the autograder waits for this output before running automated tests, please don't remove it
			server.start();
			System.out.println(format("Weather Server started.\n url=%s\n", BASE_URL));

			// blocks until the process is terminated
			Thread.currentThread().join();
			server.shutdown();
		} catch (IOException | InterruptedException ex)
		{
			Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
