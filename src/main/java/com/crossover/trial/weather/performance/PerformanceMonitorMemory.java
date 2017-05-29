package com.crossover.trial.weather.performance;

import com.crossover.trial.weather.data.AirportData;
import org.jvnet.hk2.annotations.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory implementation of performance monitor
 */
@Service
public class PerformanceMonitorMemory implements PerformanceMonitor
{
	/* Don't know why, but empty radius statistics should return that amount of numbers (+1) */
	private static final double EMPTY_RADIUS_REPORT_SIZE = 1000.0;

	/* Number of times each airport was requested */
	private ConcurrentMap<AirportData, Integer> airportFrequency;

	/* Number of times each radius was used in a request. Probably Integer would be more solid. */
	private ConcurrentMap<Double, Integer> radiusFrequency;

	public PerformanceMonitorMemory()
	{
		airportFrequency = new ConcurrentHashMap<>();
		radiusFrequency = new ConcurrentHashMap<>();
	}

	@Override
	public void recordRadiusRequest(double radius)
	{
		Double key = radius;
		/* Not too atomic in nature, but loosing a bit of request logging now and then don't seem too harmful */
		if (!radiusFrequency.containsKey(key))
			radiusFrequency.put(key, 1);
		else
			radiusFrequency.put(key, radiusFrequency.get(key) + 1);
	}

	@Override
	public void recordAirportRequest(AirportData airportData)
	{
		/* Not too atomic in nature, but loosing a bit of request logging now and then don't seem too harmful */
		if (!airportFrequency.containsKey(airportData))
			airportFrequency.put(airportData, 1);
		else
			airportFrequency.put(airportData, airportFrequency.get(airportData) + 1);
	}

	@Override
	public void clearPerformanceLog(AirportData airportData)
	{
		airportFrequency.remove(airportData);
	}

	@Override
	public int[] getRadiusHistogram()
	{
		int m = radiusFrequency.keySet().stream()
				.max(Double::compare)
				.orElse(EMPTY_RADIUS_REPORT_SIZE).intValue() + 1;

		int[] hist = new int[m];
		for (Map.Entry<Double, Integer> e : radiusFrequency.entrySet())
		{
			int i = e.getKey().intValue(); // was % 10 before, but I can't see why
			if (i < m) // could get extra values due to concurrency, but that's not large enough issue to sync - that's only reports
				hist[i] += e.getValue();
		}
		return hist;
	}

	@Override
	public Map<AirportData, Double> getAirportFrequencyStats()
	{
		Map<AirportData, Double> airportFrequencyStats = new HashMap<>();

		int requestsTotal = airportFrequency.values().stream().mapToInt(Integer::intValue).sum();

		if (requestsTotal > 0)
			for (Map.Entry<AirportData, Integer> entry : airportFrequency.entrySet())
				airportFrequencyStats.put(entry.getKey(), ((double) entry.getValue()) / requestsTotal);

		return airportFrequencyStats;
	}
}
