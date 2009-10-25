package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
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

	final static String LOG_TAG = SpotImpl.class.getSimpleName();

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

	public static final int convertBooleanToInt(final boolean _boolean)
	{
		return _boolean == true ? 1 : 0;
	}

	protected void clearAllSpots()
	{
		final SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try
		{
			Log.d(LOG_TAG, "clearAllSpots");
			db.execSQL("DELETE FROM continent;");
			db.execSQL("DELETE FROM region");
			db.execSQL("DELETE FROM country");
			db.execSQL("DELETE FROM spot");

			db.setTransactionSuccessful();
		}
		catch (final SQLException e)
		{
			Log.e(LOG_TAG, "Failed to  delete spots", e);
		}
		finally
		{
			db.endTransaction();
			IOUtils.close(db);
			Log.d(LOG_TAG, "clearAllSpots finished.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISpotDAO#insertSpots()
	 */
	public void insertSpots()
	{

		clearAllSpots();
		Log.d(LOG_TAG, "executeInsert");
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

					final Iterator<Region> regions = country.iterator();
					while (regions.hasNext())
					{
						final Region region = regions.next();
						updateRegionTable(insertRegionStatement, region, country);
						insertRegionStatement.executeInsert();

						final Iterator<Station> stations = region.iterator();
						while (stations.hasNext())
						{
							updateSpotTable(insertSpotStatement, continent, country, region, stations.next());
							insertSpotStatement.executeInsert();
							index++;
							if (index % 100 == 0)
							{
								getProgress().incrementBy(100);
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
			insertSpotStatement.close();
			insertCountryStatement.close();
			insertRegionStatement.close();
			insertContinentStatement.close();

			IOUtils.close(db);
			final long time = System.currentTimeMillis() - start;
			Log.d(LOG_TAG, "Insert finished. Time " + time + " ms");
		}

	}

	private final static void updateCountryTable(final SQLiteStatement insertStatement, final Country country,
			final Continent continent)
	{
		insertStatement.bindString(1, country.getId());
		insertStatement.bindString(2, country.getName());
		insertStatement.bindString(3, continent.getId());
	}

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
	 * @param insertStatement
	 * @param continent
	 * @param region
	 * @param station
	 * 
	 *            .add("CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY
	 *            AUTOINCREMENT, spotid TEXT NOT NULL, continentid INTEGER,
	 *            countryid INTEGER, regionid INTEGER, name TEXT NOT NULL,
	 *            keyword TEXT not null, superforecast BOOLEAN, forecast
	 *            BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast
	 *            BOOLEAN);" );
	 */
	private final static void updateSpotTable(final SQLiteStatement insertStatement, final Continent continent,
			final Country country, final Region region, final Station station)
	{

		final String spotID = station.getId();
		final String continentID = continent.getId();
		final String countryID = country.getId();
		final String regionID = region.getId();
		final String name = station.getName();
		final String keyword = station.getKeyword();

		insertStatement.bindString(1, spotID);
		insertStatement.bindString(2, continentID);
		insertStatement.bindString(3, countryID);
		insertStatement.bindString(4, regionID);
		insertStatement.bindString(5, name);
		insertStatement.bindString(6, keyword);
		insertStatement.bindLong(7, convertBooleanToInt(station.hasSuperforecast()));
		insertStatement.bindLong(8, convertBooleanToInt(station.hasForecast()));
		insertStatement.bindLong(9, convertBooleanToInt(station.hasStatistic()));
		insertStatement.bindLong(10, convertBooleanToInt(station.hasWaveReport()));
		insertStatement.bindLong(11, convertBooleanToInt(station.hasWaveforecast()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISpotDAO#hasSpots()
	 */
	@Override
	public boolean hasSpots()
	{
		final int count = getCount("spot");
		return 0 < count;
	}

	/**
	 * 
	 */
	@Override
	public Cursor fetchAll()
	{
		final SQLiteDatabase db = getReadableDatabase();
		return db.rawQuery("SELECT * FROM spot", null);
	}

	@Override
	public Cursor fetchBy(String continentid, String countryid, String regionid)
	{
		final SQLiteDatabase db = getReadableDatabase();
		// db.query("spot", columns, selection, selectionArgs, groupBy, having,
		// orderBy);
		return null;
	}
}
