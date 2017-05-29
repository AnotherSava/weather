package com.crossover.trial.weather.common;

import com.crossover.trial.weather.data.*;

import java.util.Random;

/**
 * This factory is used with performance profiler client to provide random data points, airports and distances.
 */
public class RandomDataFactory
{

	private static int createTestDataPointValue(DataPointType type, int id, int shift)
	{
		long lowBound = AtmosphericInformationConstraints.getLowBound(type);
		long upperBound = AtmosphericInformationConstraints.getUpperBound(type);
		return (int) ((shift + id) % (upperBound - lowBound) + lowBound);

	}

	/**
	 * Create data point with some kind of repeatable random values.
	 * Returns same values for the same input parameters.
	 * This values lie within constraints and are the same for the same id and type.
	 *
	 * @param type data point type (for constraints)
	 * @param id   id to make return values repeatable
	 * @return data point
	 */
	public static DataPoint createDataPoint(DataPointType type, int id)
	{
		return new DataPointBuilder()
				.withMean((double) createTestDataPointValue(type, id, 111))
				.withFirst(createTestDataPointValue(type, id, 237))
				.withSecond(createTestDataPointValue(type, id, 342))
				.withThird(createTestDataPointValue(type, id, 485))
				.withCount(createTestDataPointValue(type, id, 529))
				.build();
	}

	/**
	 * Return random data point type
	 *
	 * @return date point type
	 */
	public static DataPointType createRandomDataPointType()
	{
		DataPointType[] values = DataPointType.values();
		return values[new Random().nextInt(values.length)];
	}

	/**
	 * Get random number from 0 to about 20000 (half circumference of Earth in km)
	 *
	 * @return random number from 0 to about 20000 (km)
	 */
	public static int createRandomDistance()
	{
		return (int) (new Random().nextDouble() * Math.PI * Calculations.R);
	}

	/**
	 * Create airport with random iata and coordinates
	 *
	 * @return airport with random iata and coordinates
	 */
	public static AirportData createRandomAirportData()
	{
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder(3);

		for (int i = 0; i < 3; i++)
			stringBuilder.append((char) ('A' + random.nextInt('Z' - 'A' + 1)));

		return new AirportData(stringBuilder.toString(), random.nextDouble() * 180 - 90, random.nextDouble() * 360 - 180);
	}
}