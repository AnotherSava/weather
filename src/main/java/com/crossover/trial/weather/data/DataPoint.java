package com.crossover.trial.weather.data;

/**
 * A collected information from a single point, including some information about the range of collected values
 *
 * @author code test administrator
 */
public interface DataPoint
{
	/**
	 * Get mean of the observations
	 *
	 * @return mean of the observations
	 */
	double getMean();

	/**
	 * Get 1st quartile -- useful as a lower bound
	 *
	 * @return 1st quartile
	 */
	int getFirst();

	/**
	 * Get 2nd quartile -- median value
	 *
	 * @return 2nd quartile
	 */
	int getSecond();

	/**
	 * Get 3rd quartile value -- less noisy upper value
	 *
	 * @return 3rd quartile value
	 */
	int getThird();

	/**
	 * Get total number of measurements
	 *
	 * @return the total number of measurements
	 */
	int getCount();
}