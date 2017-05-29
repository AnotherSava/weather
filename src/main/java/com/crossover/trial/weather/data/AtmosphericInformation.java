package com.crossover.trial.weather.data;

/**
 * Sensor information for a particular location
 */
public interface AtmosphericInformation
{

	/**
	 * Get temperature in degrees celsius
	 *
	 * @return temperature in degrees celsius
	 */
	DataPoint getTemperature();

	/**
	 * Set temperature in degrees celsius
	 *
	 * @param temperature temperature in degrees celsius
	 */
	void setTemperature(DataPoint temperature);

	/**
	 * Get wind speed in km/h
	 *
	 * @return wind speed in km/h
	 */
	DataPoint getWind();

	/**
	 * Set wind speed in km/h
	 *
	 * @param wind wind speed in km/h
	 */
	void setWind(DataPoint wind);

	/**
	 * Get humidity in percent
	 *
	 * @return humidity in percent
	 */
	DataPoint getHumidity();

	/**
	 * Set humidity in percent
	 *
	 * @param humidity humidity in percent
	 */
	void setHumidity(DataPoint humidity);

	/**
	 * Get precipitation in cm
	 *
	 * @return precipitation in cm
	 */
	DataPoint getPrecipitation();

	/**
	 * Set precipitation in cm
	 *
	 * @param precipitation precipitation in cm
	 */
	void setPrecipitation(DataPoint precipitation);

	/**
	 * Get pressure in mmHg
	 *
	 * @return pressure in mmHg
	 */
	DataPoint getPressure();

	/**
	 * Set pressure in mmHg
	 *
	 * @param pressure pressure in mmHg
	 */
	void setPressure(DataPoint pressure);

	/**
	 * Get cloud cover percent
	 *
	 * @return cloud cover percent
	 */
	DataPoint getCloudCover();

	/**
	 * Set cloud cover percent
	 *
	 * @param cloudCover cloud cover percent
	 */
	void setCloudCover(DataPoint cloudCover);
}
