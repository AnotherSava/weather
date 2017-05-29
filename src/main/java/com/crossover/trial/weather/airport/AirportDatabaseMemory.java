package com.crossover.trial.weather.airport;

import com.crossover.trial.weather.common.Calculations;
import com.crossover.trial.weather.data.AirportData;
import org.jvnet.hk2.annotations.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation. Good for performance, bad for scalability beyond particular size.
 */
@Service
public class AirportDatabaseMemory implements AirportDatabase
{

	/* Map iata -> airport */
	protected ConcurrentMap<String, AirportData> airports;

	public AirportDatabaseMemory()
	{
		airports = new ConcurrentHashMap<>();
	}

	@Override
	public void addAirport(AirportData airportData)
	{
		airports.put(airportData.getIata(), airportData);
	}

	@Override
	public void removeAirport(AirportData airport)
	{
		airports.remove(airport.getIata());
	}

	@Override
	public AirportData getAirportData(String iata)
	{
		return airports.get(iata);
	}

	@Override
	public List<AirportData> getAirportsAround(AirportData airport, double distance)
	{
		return airports.values().stream().filter(ap -> getDistance(airport, ap) <= distance).collect(Collectors.toList());
	}

	protected double getDistance(AirportData from, AirportData to)
	{
		return Calculations.calculateDistance(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
	}

	@Override
	public Collection<String> getAllAirportIata()
	{
		return airports.keySet();
	}
}
