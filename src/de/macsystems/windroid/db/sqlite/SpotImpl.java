package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Country;
import de.macsystems.windroid.identifyable.Region;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class SpotImpl extends BaseImpl implements ISpotDAO
{

	private final static String LOG_TAG = SpotImpl.class.getSimpleName();

	private final static String INSERT_SPOT = "INSERT INTO spot "
			+ "(spotid,continentid,countryid,regionid,name,keyword,superforecast,forecast,statistic,wavereport,waveforecast) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?);";

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
		super(_database, "spot");
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
	public void insertSpots()
	{
		Log.d(LOG_TAG, "executeInsert");
		final int PROGRESS = 100;
		final SQLiteDatabase db = getWritableDatabase();
		final SQLiteStatement insertSpotStatement = db.compileStatement(INSERT_SPOT);
		final SQLiteStatement insertContinentStatement = db.compileStatement(INSERT_CONTINENT);
		final SQLiteStatement insertCountryStatement = db.compileStatement(INSERT_COUNTRY);
		final SQLiteStatement insertRegionStatement = db.compileStatement(INSERT_REGION);

		if (!Continent.isParsed())
		{
			throw new IllegalStateException("Please parse Continents first.");
		}

		final long start = System.currentTimeMillis();
		db.beginTransaction();
		int index = 0;
		try
		{
			for (final Continent continent : Continent.values())
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
		insertStatement.bindString(2, continent.name());
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
		_insertStatement.bindLong(7, convertBooleanToInt(_station.hasSuperforecast()));
		_insertStatement.bindLong(8, convertBooleanToInt(_station.hasForecast()));
		_insertStatement.bindLong(9, convertBooleanToInt(_station.hasStatistic()));
		_insertStatement.bindLong(10, convertBooleanToInt(_station.hasWaveReport()));
		_insertStatement.bindLong(11, convertBooleanToInt(_station.hasWaveforecast()));
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
	public Cursor fetchBy(String continentid, String countryid, String regionid)
	{
		final SQLiteDatabase db = getReadableDatabase();
		// db.query("spot", columns, selection, selectionArgs, groupBy, having,
		// orderBy);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISpotDAO#fetchBy(java.lang.String)
	 */
	@Override
	public SpotConfigurationVO fetchBy(String stationid)
	{
		final SpotConfigurationVO vo = new SpotConfigurationVO();
		final SQLiteDatabase db = getReadableDatabase();
		//
		// select
		// _id,spotid,continentid,countryid,regionid,name,keyword,superforecast,forecast,statistic,wavereport,waveforecast
		// from Spot where spotid='eg36';
		final String[] colums = new String[]
		{ "spotid", "continentid", "countryid", "regionid", "name", "keyword", "superforecast", "forecast",
				"statistic", "wavereport", "waveforecast" };

		Cursor cursor = null;
		try
		{
			cursor = db.query("spot", colums, "spotid=?", new String[]
			{ stationid }, null, null, null);
			if (!cursor.moveToFirst())
			{
				throw new IllegalStateException("Empty Result");
			}
			/**
			 * Create an SpotConfigurationVO
			 */
			final int spotidIndex = cursor.getColumnIndexOrThrow(colums[0]);
			final int continentIdIndex = cursor.getColumnIndexOrThrow(colums[1]);
			final int countryidIndex = cursor.getColumnIndexOrThrow(colums[2]);
			final int regionIdIndex = cursor.getColumnIndexOrThrow(colums[3]);
			final int nameIndex = cursor.getColumnIndexOrThrow(colums[4]);
			final int keywordIndex = cursor.getColumnIndexOrThrow(colums[5]);
			final int superforecastIndex = cursor.getColumnIndexOrThrow(colums[6]);
			final int forecastIndex = cursor.getColumnIndexOrThrow(colums[7]);
			final int statisticIndex = cursor.getColumnIndexOrThrow(colums[8]);
			final int wavereportIndex = cursor.getColumnIndexOrThrow(colums[9]);
			final int waveforecastIndex = cursor.getColumnIndexOrThrow(colums[10]);

			final String spotid = cursor.getString(spotidIndex);
			final String continentId = cursor.getString(continentIdIndex);
			final String countryId = cursor.getString(countryidIndex);
			final String regionId = cursor.getString(regionIdIndex);
			final String name = cursor.getString(nameIndex);
			final String keyword = cursor.getString(keywordIndex);
			final boolean hasSuperforecast = convertIntToBoolean(cursor.getLong(superforecastIndex));
			final boolean hasForecast = convertIntToBoolean(cursor.getLong(forecastIndex));
			final boolean hasStatistic = convertIntToBoolean(cursor.getLong(statisticIndex));
			final boolean hasWavereport = convertIntToBoolean(cursor.getLong(wavereportIndex));
			final boolean hasWaveforecast = convertIntToBoolean(cursor.getLong(waveforecastIndex));

			// TODO: Forgot hasReport there, set it to false at the moment
			final Station station = new Station(name, spotid, keyword, false, hasForecast, hasSuperforecast,
					hasStatistic, hasWavereport, hasWaveforecast);
			vo.setStation(station);

			// vo
		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
		return vo;
	}
}
