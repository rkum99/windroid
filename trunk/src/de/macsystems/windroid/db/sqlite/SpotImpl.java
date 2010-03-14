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
package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Country;
import de.macsystems.windroid.identifyable.Region;
import de.macsystems.windroid.identifyable.Schedule;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.identifyable.World;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public final class SpotImpl extends BaseImpl implements ISpotDAO
{

	private final static String LOG_TAG = SpotImpl.class.getSimpleName();

	private final static String INSERT_SPOT = "INSERT INTO spot "
			+ "(spotid,continentid,countryid,regionid,name,keyword,superforecast,report,forecast,statistic,wavereport,waveforecast) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?);";

	private final static String INSERT_CONTINENT = "INSERT INTO continent (id,name) values (?,?);";
	private final static String INSERT_COUNTRY = "INSERT INTO country (id,name,continentid) values (?,?,?);";
	private final static String INSERT_REGION = "INSERT INTO region (id,name,countryid) values (?,?,?);";

	/**
	 * 
	 * @param _database
	 * @throws NullPointerException
	 */
	public SpotImpl(final Database _database) throws NullPointerException
	{
		this(_database, NullProgressAdapter.INSTANCE);
	}

	/**
	 * 
	 * @param _database
	 * @param _progress
	 * @throws NullPointerException
	 */
	public SpotImpl(final Database _database, final IProgress _progress) throws NullPointerException
	{
		super(_database, "spot", _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISpotDAO#insertSpots()
	 */
	public void insertSpots(final World _world)
	{
		Log.d(LOG_TAG, "executeInsert");

		if (_world == null)
		{
			throw new NullPointerException("world");
		}

		final int PROGRESS = 100;
		final SQLiteDatabase db = getWritableDatabase();
		final SQLiteStatement insertSpotStatement = db.compileStatement(INSERT_SPOT);
		final SQLiteStatement insertContinentStatement = db.compileStatement(INSERT_CONTINENT);
		final SQLiteStatement insertCountryStatement = db.compileStatement(INSERT_COUNTRY);
		final SQLiteStatement insertRegionStatement = db.compileStatement(INSERT_REGION);

		final long start = System.currentTimeMillis();
		db.beginTransaction();
		int index = 0;
		try
		{
			for (final Continent continent : _world)
			// for (final Continent continent : Continent.values())
			{
				updateContinentTable(insertContinentStatement, continent);
				insertContinentStatement.executeInsert();

				final Iterator<Country> countrys = continent.iterator();
				while (countrys.hasNext())
				{
					final Country country = countrys.next();
					updateCountryTable(insertCountryStatement, country, continent);
					insertCountryStatement.executeInsert();

					for (final Region region : country)
					{
						updateRegionTable(insertRegionStatement, region, country);
						insertRegionStatement.executeInsert();

						for (final Station station : region)
						{
							updateSpotTable(insertSpotStatement, continent, country, region, station);
							insertSpotStatement.executeInsert();
							index++;
							if (index % PROGRESS == 0)
							{
								getProgress().incrementBy(PROGRESS);
							}
						}
					}
				}
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			IOUtils.close(insertSpotStatement);
			IOUtils.close(insertCountryStatement);
			IOUtils.close(insertRegionStatement);
			IOUtils.close(insertContinentStatement);
			IOUtils.close(db);
			final long time = System.currentTimeMillis() - start;
			Log.d(LOG_TAG, "Insert finished. Time " + time + " ms");
		}

	}

	/**
	 * 
	 * @param insertStatement
	 * @param country
	 * @param continent
	 */
	private final static void updateCountryTable(final SQLiteStatement insertStatement, final Country country,
			final Continent continent)
	{
		insertStatement.bindString(1, country.getId());
		insertStatement.bindString(2, country.getName());
		insertStatement.bindString(3, continent.getId());
	}

	/**
	 * 
	 * @param insertStatement
	 * @param region
	 * @param county
	 */
	private final static void updateRegionTable(final SQLiteStatement insertStatement, final Region region,
			final Country county)
	{
		insertStatement.bindString(1, region.getId());
		insertStatement.bindString(2, region.getName());
		insertStatement.bindString(3, county.getId());
	}

	/**
	 * @param insertStatement
	 * @param continent
	 * @param region
	 * @param station
	 */
	private final static void updateContinentTable(final SQLiteStatement insertStatement, final Continent continent)
	{
		insertStatement.bindString(1, continent.getId());
		insertStatement.bindString(2, continent.getName());
	}

	/**
	 * 
	 * @param _insertStatement
	 * @param _continent
	 * @param _country
	 * @param _region
	 * @param _station
	 */
	private final static void updateSpotTable(final SQLiteStatement _insertStatement, final Continent _continent,
			final Country _country, final Region _region, final Station _station)
	{
		final String spotID = _station.getId();
		final String continentID = _continent.getId();
		final String countryID = _country.getId();
		final String regionID = _region.getId();
		final String name = _station.getName();
		final String keyword = _station.getKeyword();

		_insertStatement.bindString(1, spotID);
		_insertStatement.bindString(2, continentID);
		_insertStatement.bindString(3, countryID);
		_insertStatement.bindString(4, regionID);
		_insertStatement.bindString(5, name);
		_insertStatement.bindString(6, keyword);
		_insertStatement.bindLong(7, asInt(_station.hasSuperforecast()));
		_insertStatement.bindLong(8, asInt(_station.hasReport()));
		_insertStatement.bindLong(9, asInt(_station.hasForecast()));
		_insertStatement.bindLong(10, asInt(_station.hasStatistic()));
		_insertStatement.bindLong(11, asInt(_station.hasWaveReport()));
		_insertStatement.bindLong(12, asInt(_station.hasWaveforecast()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISpotDAO#hasSpots()
	 */
	@Override
	public boolean hasSpots()
	{
		final int count = getSize();
		return 0 < count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISpotDAO#fetchBy(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Cursor fetchBy(final String continentid, final String countryid, final String regionid)
	{

		Log
				.d("SpotDAO", "Looking for -> Continent: " + continentid + " Country: " + countryid + " Region: "
						+ regionid);

		final SQLiteDatabase db = getReadableDatabase();

		return db.rawQuery("SELECT * from spot where continentid=? AND countryid=? AND regionid=?", new String[]
		{ continentid, countryid, regionid });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISpotDAO#fetchBy(java.lang.String)
	 */
	@Override
	public SpotConfigurationVO fetchBy(final String _stationid)
	{
		final SpotConfigurationVO vo = new SpotConfigurationVO();
		final SQLiteDatabase db = getReadableDatabase();
		final String[] colums = new String[]
		{ "spotid", "continentid", "countryid", "regionid", "name", "keyword", "superforecast", "report", "forecast",
				"statistic", "wavereport", "waveforecast" };

		Cursor cursor = null;
		try
		{
			cursor = db.query(tableName, colums, "spotid=?", new String[]
			{ _stationid }, null, null, null);
			moveToFirstOrThrow(cursor);
			/**
			 * Create an SpotConfigurationVO
			 */

			final String spotid = getString(cursor, COLUMN_SPOTID);
			// final String continentId = getString(cursor, COLUMN_CONTINENTID);
			// final String countryId = getString(cursor, COLUMN_COUNTRYID);
			// final String regionId = getString(cursor, COLUMN_REGIONID);
			final String name = getString(cursor, COLUMN_NAME);
			final String keyword = getString(cursor, COLUMN_KEYWORD);
			final boolean hasSuperforecast = getBoolean(cursor, COLUMN_SUPERFORECAST);
			final boolean hasReport = getBoolean(cursor, COLUMN_REPORT);
			final boolean hasForecast = getBoolean(cursor, COLUMN_FORECAST);
			final boolean hasStatistic = getBoolean(cursor, COLUMN_STATISTIC);
			final boolean hasWavereport = getBoolean(cursor, COLUMN_WAVEREPORT);
			final boolean hasWaveforecast = getBoolean(cursor, COLUMN_WAVEFORECAST);

			// Create a Station
			final Station station = new Station(name, spotid, keyword, hasReport, hasForecast, hasSuperforecast,
					hasStatistic, hasWavereport, hasWaveforecast);
			vo.setStation(station);

			vo.setSchedule(new Schedule());
		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
		return vo;
	}
}