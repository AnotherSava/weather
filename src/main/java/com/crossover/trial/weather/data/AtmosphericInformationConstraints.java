package com.crossover.trial.weather.data;

/**
 * Since there are some constraints on data point mean values for different data point types,
 * it will be more convenient to separate them from the code (and move to some properties file
 * afterwards, maybe)
 */
public final class AtmosphericInformationConstraints
{

	/**
	 * Don't let anyone instantiate this class.
	 */
	private AtmosphericInformationConstraints()
	{
	}

	/**
	 * Get low bound (including) for particular data point type
	 *
	 * @param type data point type
	 * @return low bound (including)
	 */
	public static int getLowBound(DataPointType type)
	{
		switch (type)
		{
			case WIND:
				return 0;
			case TEMPERATURE:
				return -50;
			case HUMIDITY:
				return 0;
			case PRESSURE:
				return 650;
			case CLOUDCOVER:
				return 0;
			case PRECIPITATION:
				return 0;
		}
		return Integer.MIN_VALUE;
	}

	/**
	 * Get upper bound (excluding) for particular data point type
	 *
	 * @param type data point type
	 * @return upper bound (excluding)
	 */
	public static int getUpperBound(DataPointType type)
	{
		switch (type)
		{
			case WIND:
				return Integer.MAX_VALUE;
			case TEMPERATURE:
				return 100;
			case HUMIDITY:
				return 100;
			case PRESSURE:
				return 800;
			case CLOUDCOVER:
				return 100;
			case PRECIPITATION:
				return 100;
		}
		return Integer.MIN_VALUE;
	}
}
