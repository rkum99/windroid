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

	/**
	 * 
	 */
	public StationHandler()
	{
		super();
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

		super.startElement(uri, localName, name, attributes);
	}

	private void handleStation(final Attributes attributes)
	{
		final int indexID = attributes.getIndex("", "id");
		final int indexName = attributes.getIndex("", "name");
		final int indexKeyword = attributes.getIndex("", "keyword");
		final int indexSuperforecast = attributes.getIndex("", "superforecast");
		final int indexStatistic = attributes.getIndex("", "statistic");

		final String stationid = attributes.getValue(indexID);
		final String stationName = attributes.getValue(indexName);
		final boolean hasSuperforecast = WindUtils.toBoolean(attributes.getValue(indexSuperforecast));
		final boolean hasStatistic = WindUtils.toBoolean(attributes.getValue(indexStatistic));
		final String keyword = attributes.getValue(indexKeyword);

		final Station station = new Station(stationName, stationid, keyword, hasSuperforecast, hasStatistic);
		currentRegion.addStation(station);
	}

	private void handleRegion(final Attributes attributes)
	{
		final int indexID = attributes.getIndex("", "id");
		final int indexName = attributes.getIndex("", "name");

		currentRegion = new Region(attributes.getValue(indexID), attributes.getValue(indexName));
		currentCountry.addRegion(currentRegion);
	}

	private void handleCountry(final Attributes attributes)
	{
		final int indexID = attributes.getIndex("", "id");
		final int indexName = attributes.getIndex("", "name");

		currentCountry = new Country(attributes.getValue(indexID), attributes.getValue(indexName));
		currentContinent.addCountry(currentCountry);
	}

	private void handleContinent(final Attributes attributes)
	{
		final int indexID = attributes.getIndex("", "id");
		final int indexName = attributes.getIndex("", "name");
		currentContinent = Continent.getById(attributes.getValue(indexID));
	}

}
