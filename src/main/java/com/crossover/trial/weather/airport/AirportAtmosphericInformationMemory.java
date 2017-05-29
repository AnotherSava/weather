package com.crossover.trial.weather.airport;

import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.data.AtmosphericInformationHolder;
import org.jvnet.hk2.annotations.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory implementation of AirportAtmosphericInformation.
 * AtmosphericInformation is cloned to prevent direct data manipulation.
 * Other data types are read-only
 */
@Service
public class AirportAtmosphericInformationMemory implements AirportAtmosphericInformation
{
	private ConcurrentMap<AirportData, AtmosphericInformation> atmosphericInformationMap;
	private ConcurrentMap<AirportData, Long> atmosphericInformationUpdateTime;

	public AirportAtmosphericInformationMemory()
	{
		atmosphericInformationMap = new ConcurrentHashMap<>();
		atmosphericInformationUpdateTime = new ConcurrentHashMap<>();
	}

	@Override
	public AtmosphericInformation getAtmosphericInformation(AirportData airportData)
	{
		AtmosphericInformation atmosphericInformation = atmosphericInformationMap.get(airportData);
		return atmosphericInformation == null ? null : new AtmosphericInformationHolder(atmosphericInformation);
	}

	@Override
	public void updateAtmosphericInformation(AirportData airportData, AtmosphericInformation atmosphericInformation)
	{
		atmosphericInformationMap.put(airportData, new AtmosphericInformationHolder(atmosphericInformation));
		atmosphericInformationUpdateTime.put(airportData, System.currentTimeMillis());
	}

	@Override
	public void clearAtmosphericInformation(AirportData airportData)
	{
		atmosphericInformationMap.remove(airportData);
		atmosphericInformationUpdateTime.remove(airportData);
	}

	@Override
	public int size(long validTimePeriod)
	{
		long updatedAfter = System.currentTimeMillis() - validTimePeriod;
		return (int) atmosphericInformationUpdateTime.values().stream().filter(x -> x > updatedAfter).count();
	}
}
