package com.crossover.trial.weather.common;

import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.data.AtmosphericInformationHolder;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;

/**
 * Use this factory to construct a {@link Gson} configured for proper handling project data types.
 */
public class WeatherGsonFactory
{
	public static Gson createGson()
	{
		return createGson(new GsonBuilder());
	}

	public static Gson createGson(GsonBuilder gsonBuilder)
	{
		gsonBuilder.registerTypeAdapter(DataPoint.class, new DataPointBuilder());

		gsonBuilder.registerTypeAdapter(AtmosphericInformation.class,
				(InstanceCreator<AtmosphericInformation>) type -> new AtmosphericInformationHolder());
		gsonBuilder.registerTypeAdapter(AtmosphericInformation.class,
				(JsonDeserializer<AtmosphericInformation>) (json, typeOfT, context) ->
						context.deserialize(json, AtmosphericInformationHolder.class));

		return gsonBuilder.create();
	}
}
