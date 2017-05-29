package com.crossover.trial.weather.data;

import com.google.gson.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Type;

/**
 * Use this builder to construct a {@link DataPoint} instance.
 * The following is an example shows how to use the {@code DataPointBuilder} to construct a DataPoint instance:
 * <pre>
 * DataPoint dataPoint = new DataPointBuilder()
 *      .withCount(10)
 *      .withFirst(10)
 *      .withSecond(20)
 *      .withThird(30)
 *      .withMean(22)
 *      .build();
 * </pre>
 * The order of invocation of configuration methods does not matter.
 */
final public class DataPointBuilder implements InstanceCreator<DataPoint>, JsonDeserializer<DataPoint>
{
	private DataPointImpl dataPoint;

	public DataPointBuilder()
	{
		dataPoint = new DataPointImpl();
	}

	public DataPointBuilder(DataPoint dataPoint)
	{
		this.dataPoint = new DataPointImpl(dataPoint);
	}

	public DataPointBuilder withMean(double mean)
	{
		dataPoint.mean = mean;
		return this;
	}

	public DataPointBuilder withFirst(int first)
	{
		dataPoint.first = first;
		return this;
	}

	public DataPointBuilder withSecond(int second)
	{
		dataPoint.second = second;
		return this;
	}

	public DataPointBuilder withThird(int third)
	{
		dataPoint.third = third;
		return this;
	}

	public DataPointBuilder withCount(int count)
	{
		dataPoint.count = count;
		return this;
	}

	public DataPoint build()
	{
		return new DataPointImpl(dataPoint);
	}

	@Override
	public DataPoint createInstance(Type type)
	{
		return new DataPointImpl();
	}

	@Override
	public DataPoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return context.deserialize(json, DataPointImpl.class);
	}

	/**
	 * Private class, use the builder to create this object
	 */
	private class DataPointImpl implements DataPoint
	{
		private double mean;
		private int first;
		private int second;
		private int third;
		private int count;

		private DataPointImpl()
		{
		}

		private DataPointImpl(DataPoint dataPoint)
		{
			mean = dataPoint.getMean();
			first = dataPoint.getFirst();
			second = dataPoint.getSecond();
			third = dataPoint.getThird();
			count = dataPoint.getCount();
		}

		@Override
		public double getMean()
		{
			return mean;
		}

		@Override
		public int getFirst()
		{
			return first;
		}

		@Override
		public int getSecond()
		{
			return second;
		}

		@Override
		public int getThird()
		{
			return third;
		}

		@Override
		public int getCount()
		{
			return count;
		}

		public String toString()
		{
			return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
		}

		public boolean equals(Object that)
		{
			if (!(that instanceof DataPoint))
				return false;
			DataPoint thatDataPoint = (DataPoint) that;

			return (mean == thatDataPoint.getMean()) &&
					(first == thatDataPoint.getFirst()) &&
					(second == thatDataPoint.getSecond()) &&
					(third == thatDataPoint.getThird()) &&
					(count == thatDataPoint.getCount());
		}
	}

}