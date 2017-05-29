package com.crossover.trial.weather;

import com.crossover.trial.weather.client.CollectClientHelper;
import com.crossover.trial.weather.common.Calculations;
import com.opencsv.CSVReader;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.*;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * @author code test administrator
 */
public class AirportLoader
{
	private static final String BASE_URI = "http://localhost:9090";

	/* Column indexes in comma separated input file (starting with 1, not 0)*/
	private static final int IATA_COLUMN_NUMBER = 5;
	private static final int LATITUDE_COLUMN_NUMBER = 7;
	private static final int LONGITUDE_COLUMN_NUMBER = 8;

	/* Do we skip records where iata is not three characters long or duplicate? */
	private static final boolean SKIP_INCORRECT_IATA = true;

	private Client client;
	/* High-level interface for client requests */
	private CollectClientHelper collectClientHelper;

	public AirportLoader()
	{
		client = ClientBuilder.newClient();
		collectClientHelper = new CollectClientHelper(client.target(BASE_URI));
	}

	public void upload(InputStream airportDataStream) throws IOException
	{
		/* External library for csv parsing */
		CSVReader csvReader;
		String iata, fields[];
		double latitude, longitude;
		/* For duplicate checks */
		Set<String> iataSet = new HashSet<>();
		/* Count records number and time for performance report */
		int recordNumber = 0, recordsSkipped = 0, recordsImported = 0;
		long startTime = System.currentTimeMillis();

		BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
		csvReader = new CSVReader(reader);
		while ((fields = csvReader.readNext()) != null)
		{
			recordNumber++;
			iata = fields[IATA_COLUMN_NUMBER - 1];

			/* Check for correct IATA */
			if (iata.length() != 3)
			{
				System.out.println("Warning. IATA length is not three characters: '" + iata + "', line #" + recordNumber + (SKIP_INCORRECT_IATA ? " - skipped" : ""));
				if (SKIP_INCORRECT_IATA)
				{
					recordsSkipped++;
					continue;
				}
			}

			/* Check for duplicates */
			if (iataSet.contains(iata))
			{
				System.out.println("Warning. Duplicate IATA: '" + iata + "', line #" + recordNumber + (SKIP_INCORRECT_IATA ? " - skipped" : ""));
				if (SKIP_INCORRECT_IATA)
				{
					recordsSkipped++;
					continue;
				}
			}
			else
				iataSet.add(iata);

			try
			{
				latitude = Calculations.parseDouble(fields[LATITUDE_COLUMN_NUMBER - 1]);
				longitude = Calculations.parseDouble(fields[LONGITUDE_COLUMN_NUMBER - 1]);
				collectClientHelper.airportPost(iata, latitude, longitude);
				recordsImported++;
			} catch (ParseException e)
			{
				System.out.println("Warning. Number format exception, line #" + recordNumber + ": " + e.getMessage() + " - skipped");
				recordsSkipped++;
			} catch (BadRequestException e)
			{
				System.out.println("Warning. Server rejected line #" + recordNumber + ": " + e.getMessage() + " - skipped");
				recordsSkipped++;
			}
		}
		System.out.println("\nNumber of records imported: " + recordsImported);
		System.out.println("Number of records skipped: " + recordsSkipped);
		if (recordsSkipped > 0)
			System.out.println("You can change policy and not skip records with IATA size other that three, see 'SKIP_INCORRECT_IATA' constant");

		long endTime = System.currentTimeMillis();
		System.out.println("Total time: " + (endTime - startTime) + " ms, " + recordsImported * 1000 / (endTime - startTime) + " requests per second");
	}

	public void close()
	{
		client.close();
	}

	public static void main(String args[]) throws IOException
	{
		if (args.length == 0)
		{
			System.err.println("Input parameter needed: filename");
			System.exit(1);

		}
		File airportDataFile = new File(args[0]);
		if (!airportDataFile.exists() || airportDataFile.length() == 0)
		{
			System.err.println(airportDataFile + " is not a valid input");
			System.exit(1);
		}

		AirportLoader al = new AirportLoader();
		al.upload(new FileInputStream(airportDataFile));

		al.close();
		System.exit(0);
	}
}
