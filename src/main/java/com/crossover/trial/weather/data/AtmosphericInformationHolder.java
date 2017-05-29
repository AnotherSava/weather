package com.crossover.trial.weather.data;

/**
 * Sensor information for a particular location
 */
public class AtmosphericInformationHolder implements AtmosphericInformation
{
	/* wind speed in km/h */
	private DataPoint wind;

	/* humidity in percent */
	private DataPoint humidity;

	/* temperature in degrees celsius */
	private DataPoint temperature;

	/* precipitation in cm */
	private DataPoint precipitation;

	/* pressure in mmHg */
	private DataPoint pressure;

	/* cloud cover percent */
	private DataPoint cloudCover;

	public AtmosphericInformationHolder()
	{
	}

	/**
	 * Since DataPoint is read-only object, we can copy links, not values
	 *
	 * @param atmosphericInformation object to clone
	 */
	public AtmosphericInformationHolder(AtmosphericInformation atmosphericInformation)
	{
		wind = atmosphericInformation.getWind();
		humidity = atmosphericInformation.getHumidity();
		temperature = atmosphericInformation.getTemperature();
		precipitation = atmosphericInformation.getPrecipitation();
		pressure = atmosphericInformation.getPressure();
		cloudCover = atmosphericInformation.getCloudCover();
	}

	@Override
	public DataPoint getTemperature()
	{
		return temperature;
	}

	@Override
	public void setTemperature(DataPoint temperature)
	{
		this.temperature = temperature;
	}

	@Override
	public DataPoint getWind()
	{
		return wind;
	}

	@Override
	public void setWind(DataPoint wind)
	{
		this.wind = wind;
	}

	@Override
	public DataPoint getHumidity()
	{
		return humidity;
	}

	@Override
	public void setHumidity(DataPoint humidity)
	{
		this.humidity = humidity;
	}

	@Override
	public DataPoint getPrecipitation()
	{
		return precipitation;
	}

	@Override
	public void setPrecipitation(DataPoint precipitation)
	{
		this.precipitation = precipitation;
	}

	@Override
	public DataPoint getPressure()
	{
		return pressure;
	}

	@Override
	public void setPressure(DataPoint pressure)
	{
		this.pressure = pressure;
	}

	@Override
	public DataPoint getCloudCover()
	{
		return cloudCover;
	}

	@Override
	public void setCloudCover(DataPoint cloudCover)
	{
		this.cloudCover = cloudCover;
	}
}
