/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.macsystems.windroid.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Country;
import de.macsystems.windroid.identifyable.Region;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.identifyable.World;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class StationHandler extends DefaultHandler
{
	private final static String LOG_TAG = StationHandler.class.getSimpleName();

	private static final String ONE = "1";

	private static final String FORECAST = "forecast";

	private static final String WAVEFORECAST = "waveforecast";

	private static final String WAVEREPORT = "wavereport";

	private static final String REPORT = "report";

	private static final String STATISTIC = "statistic";

	private static final String SUPERFORECAST = "superforecast";

	private static final String KEYWORD2 = "keyword";

	private static final String NAME = "name";

	private static final String ID = "id";

	private static final String NAMESPACE = "";

	private static final String CONTINENT = "continent";

	private static final String STATION = "station";

	private static final String REGION = "region";

	private static final String COUNTRY = "country";

	private Continent currentContinent;
	private Region currentRegion;
	private Country currentCountry;

	private long startTime;

	private volatile int nrOfStations = 0;

	private final World world;

	/**
	 *
	 */
	public StationHandler()
	{
		super();
		world = new World();
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

	/**
	 * returns the world.
	 * 
	 * @return
	 */
	public World getWorld()
	{
		return world;
	}

	@Override
	public void endDocument() throws SAXException
	{
		final long parsingTime = System.currentTimeMillis() - startTime;
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "Parsing End. Parsing time :" + parsingTime + " ms.");
		}
		super.endDocument();
	}

	@Override
	public void startDocument() throws SAXException
	{
		startTime = System.currentTimeMillis();
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "Parsing Start");
		}
		super.startDocument();
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes)
			throws SAXException
	{
		if (Thread.currentThread().isInterrupted())
		{
			throw new SAXException("Parsing Thread interrupted");
		}

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
		final int indexID = attributes.getIndex(NAMESPACE, ID);
		final int indexName = attributes.getIndex(NAMESPACE, NAME);
		final int indexKeyword = attributes.getIndex(NAMESPACE, KEYWORD2);
		final int indexSuperforecast = attributes.getIndex(NAMESPACE, SUPERFORECAST);
		final int indexStatistic = attributes.getIndex(NAMESPACE, STATISTIC);

		final int indexReport = attributes.getIndex(NAMESPACE, REPORT);
		final int indexWaveReport = attributes.getIndex(NAMESPACE, WAVEREPORT);
		final int indexWaveforecast = attributes.getIndex(NAMESPACE, WAVEFORECAST);
		final int indexForecast = attributes.getIndex(NAMESPACE, FORECAST);

		final String stationid = attributes.getValue(indexID);
		final String stationName = attributes.getValue(indexName);
		final boolean hasSuperforecast = asBoolean(attributes.getValue(indexSuperforecast));
		final boolean hasStatistic = asBoolean(attributes.getValue(indexStatistic));
		final String keyword = attributes.getValue(indexKeyword);

		final boolean hasReport = asBoolean(attributes.getValue(indexReport));
		final boolean hasWaveReport = asBoolean(attributes.getValue(indexWaveReport));
		final boolean hasWaveforecast = asBoolean(attributes.getValue(indexWaveforecast));
		final boolean hasForecast = asBoolean(attributes.getValue(indexForecast));

		final Station station = new Station(stationName, stationid, keyword, hasForecast, hasSuperforecast,
				hasStatistic, hasReport, hasWaveReport, hasWaveforecast);
		currentRegion.add(station);
		nrOfStations++;
	}

	private void handleRegion(final Attributes attributes)
	{
		final int indexID = attributes.getIndex(NAMESPACE, ID);
		final int indexName = attributes.getIndex(NAMESPACE, NAME);

		currentRegion = new Region(attributes.getValue(indexID), attributes.getValue(indexName));
		currentCountry.add(currentRegion);
	}

	private void handleCountry(final Attributes attributes)
	{
		final int indexID = attributes.getIndex(NAMESPACE, ID);
		final int indexName = attributes.getIndex(NAMESPACE, NAME);

		currentCountry = new Country(attributes.getValue(indexID), attributes.getValue(indexName));
		currentContinent.add(currentCountry);
	}

	private void handleContinent(final Attributes attributes)
	{
		final int indexID = attributes.getIndex(NAMESPACE, ID);
		final int indexName = attributes.getIndex(NAMESPACE, NAME);
		final String name = attributes.getValue(indexName);
		final String id = attributes.getValue(indexID);
		final Continent newContinent = new Continent(id, name);
		world.add(newContinent);
		currentContinent = newContinent;
	}

	/**
	 * Converts "1" into <code>true</code> else false will be returned.
	 * 
	 * @param _value
	 * @return
	 */
	private final static boolean asBoolean(final String _value)
	{
		return ONE.equals(_value);
	}
}
