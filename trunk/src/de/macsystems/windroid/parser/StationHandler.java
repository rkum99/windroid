package de.macsystems.windroid.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Country;
import de.macsystems.windroid.identifyable.Region;
import de.macsystems.windroid.identifyable.Station;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class StationHandler extends DefaultHandler
{

	private static final String NAMESPACE = "";

	private final static String LOG_TAG = StationHandler.class.getSimpleName();

	private static final String CONTINENT = "continent";

	private static final String STATION = "station";

	private static final String REGION = "region";

	protected static final String COUNTRY = "country";

	private Continent currentContinent;
	private Region currentRegion;
	private Country currentCountry;

	private long startTime;
	private long endTime;

	private volatile int nrOfStations = 0;

	/**
	 * 
	 */
	public StationHandler()
	{
		super();
	}

	/**
	 * returns the nr of stations found in parsed XML.
	 * 
	 * @return
	 */
	public int getNrOfStations()
	{
		return nrOfStations;
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException
	{
		super.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException
	{
		endTime = System.currentTimeMillis();
		final long parsingTime = endTime - startTime;
		Log.d(LOG_TAG, "Parsing End. Time took:" + parsingTime + " ms.");
		Continent.setParsed();
		super.endDocument();
	}

	@Override
	public void startDocument() throws SAXException
	{
		startTime = System.currentTimeMillis();
		Log.i(LOG_TAG, "Start");
		super.startDocument();
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
			throws SAXException
	{
		if (CONTINENT.equals(localName))
		{
			handleContinent(attributes);
		}
		else if (COUNTRY.equals(localName))
		{
			handleCountry(attributes);
		}
		else if (REGION.equals(localName))
		{
			handleRegion(attributes);
		}
		else if (STATION.equals(localName))
		{
			handleStation(attributes);
		}
	}

	private void handleStation(final Attributes attributes)
	{
		final int indexID = attributes.getIndex(NAMESPACE, "id");
		final int indexName = attributes.getIndex(NAMESPACE, "name");
		final int indexKeyword = attributes.getIndex(NAMESPACE, "keyword");
		final int indexSuperforecast = attributes.getIndex(NAMESPACE, "superforecast");
		final int indexStatistic = attributes.getIndex(NAMESPACE, "statistic");

		final int indexReport = attributes.getIndex(NAMESPACE, "report");
		final int indexWaveReport = attributes.getIndex(NAMESPACE, "wavereport");
		final int indexWaveforecast = attributes.getIndex(NAMESPACE, "waveforecast");
		final int indexForecast = attributes.getIndex(NAMESPACE, "forecast");

		final String stationid = attributes.getValue(indexID);
		final String stationName = attributes.getValue(indexName);
		final boolean hasSuperforecast = WindUtils.toBoolean(attributes.getValue(indexSuperforecast));
		final boolean hasStatistic = WindUtils.toBoolean(attributes.getValue(indexStatistic));
		final String keyword = attributes.getValue(indexKeyword);

		final boolean hasReport = WindUtils.toBoolean(attributes.getValue(indexReport));
		final boolean hasWaveReport = WindUtils.toBoolean(attributes.getValue(indexWaveReport));
		final boolean hasWaveforecast = WindUtils.toBoolean(attributes.getValue(indexWaveforecast));
		final boolean hasForecast = WindUtils.toBoolean(attributes.getValue(indexForecast));

		final Station station = new Station(stationName, stationid, keyword, hasForecast, hasSuperforecast,
				hasStatistic, hasReport, hasWaveReport, hasWaveforecast);
		currentRegion.addStation(station);
		nrOfStations++;
	}

	private void handleRegion(final Attributes attributes)
	{
		final int indexID = attributes.getIndex(NAMESPACE, "id");
		final int indexName = attributes.getIndex(NAMESPACE, "name");

		currentRegion = new Region(attributes.getValue(indexID), attributes.getValue(indexName));
		currentCountry.addRegion(currentRegion);

		// Log.d(LOG_TAG, "Region id=" + currentRegion.getId() + " name=" +
		// currentRegion.getName());

	}

	private void handleCountry(final Attributes attributes)
	{
		final int indexID = attributes.getIndex(NAMESPACE, "id");
		final int indexName = attributes.getIndex(NAMESPACE, "name");

		currentCountry = new Country(attributes.getValue(indexID), attributes.getValue(indexName));
		currentContinent.addCountry(currentCountry);
	}

	private void handleContinent(final Attributes attributes)
	{
		final int indexID = attributes.getIndex(NAMESPACE, "id");
		// final int indexName = attributes.getIndex("", "name");
		currentContinent = Continent.getById(attributes.getValue(indexID));
	}

}
