package com.crossover.trial.weather.data;

import java.util.Map;

/**
 * Information provided by ping query request, aggregated in one place.
 */
public class PingData
{
	/* Number of up-to-date datapoints currently stored */
	private int datasize;
	/* Frequency of requests for each IATA code */
	private Map<String, Double> iata_freq;
	/* Frequency of requests for each radius in histogram form */
	private int[] radius_freq;

	/**
	 * Get number of up-to-date datapoints currently stored
	 *
	 * @return number of up-to-date datapoints currently stored
	 */
	public int getDatasize()
	{
		return datasize;
	}

	/**
	 * Get frequency of requests for each IATA code
	 *
	 * @return frequency of requests for each IATA code
	 */
	public Map<String, Double> getIata_freq()
	{
		return iata_freq;
	}

	/**
	 * Get frequency of requests for each integer radius from 0 km to max requested in histogram form
	 *
	 * @return frequency of requests for each radius
	 */
	public int[] getRadius_freq()
	{
		return radius_freq;
	}

	/**
	 * Set number of up-to-date datapoints currently stored
	 *
	 * @param datasize number of up-to-date datapoints currently stored
	 */
	public void setDatasize(int datasize)
	{
		this.datasize = datasize;
	}

	/**
	 * Set frequency of requests for each IATA code
	 *
	 * @param iata_freq frequency of requests for each IATA code
	 */
	public void setIata_freq(Map<String, Double> iata_freq)
	{
		this.iata_freq = iata_freq;
	}

	/**
	 * Set frequency of requests for each integer radius from 0 km to max requested in histogram form
	 *
	 * @param radius_freq frequency of requests for each integer radius from 0 km to max requested in histogram form
	 */
	public void setRadius_freq(int[] radius_freq)
	{
		this.radius_freq = radius_freq;
	}
}
