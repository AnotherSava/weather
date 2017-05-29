package com.crossover.trial.weather;

import com.crossover.trial.weather.common.RandomDataFactory;
import com.crossover.trial.weather.data.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * After object is requested from storage class and modified, this should not effect storage.
 * And after new object is put into storage and modified afterwards, storage should not be effected.
 * <p>
 * AirportData and DataPoint are read-only objects, so the only storage need to be tested at the moment is
 * AirportAtmosphericInformation for the AtmosphericInformation objects;
 */
public class ImmutableStorageTest extends ConfiguredJerseyTest
{
	/**
	 * After object is requested from storage class and modified, this should not effect storage.
	 */
	@Test
	public void testGetThenModify()
	{
		AirportData airportData = RandomDataFactory.createRandomAirportData();
		DataPoint dataPointInitial = RandomDataFactory.createDataPoint(DataPointType.CLOUDCOVER, 1);
		AtmosphericInformation atmosphericInformation = new AtmosphericInformationHolder();
		atmosphericInformation.setCloudCover(dataPointInitial);
		/* Set initial value to storage */
		airportAtmosphericInformation.updateAtmosphericInformation(airportData, atmosphericInformation);

		/* Get it and check equal */
		AtmosphericInformation atmosphericInformationFromDB = airportAtmosphericInformation.getAtmosphericInformation(airportData);
		assertEquals(atmosphericInformation.getCloudCover(), atmosphericInformationFromDB.getCloudCover());

		/* Create another dataPoint value */
		DataPoint dataPointNew = RandomDataFactory.createDataPoint(DataPointType.CLOUDCOVER, 2);
		assertNotEquals(dataPointInitial, dataPointNew);

		/* Change local atmosphericInformation */
		atmosphericInformation.setCloudCover(dataPointNew);
		assertNotEquals(atmosphericInformation.getCloudCover(), atmosphericInformationFromDB.getCloudCover());

		/* Should be unchanged in storage */
		atmosphericInformationFromDB = airportAtmosphericInformation.getAtmosphericInformation(airportData);
		assertEquals(dataPointInitial, atmosphericInformationFromDB.getCloudCover());

		/* Now we change received atmosphericInformation and this should not effect storage too */
		atmosphericInformationFromDB.setCloudCover(dataPointNew);
		AtmosphericInformation atmosphericInformationFromDBNew = airportAtmosphericInformation.getAtmosphericInformation(airportData);
		assertEquals(dataPointInitial, atmosphericInformationFromDBNew.getCloudCover());
	}
}
