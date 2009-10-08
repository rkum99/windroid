package de.macsystems.windroid.db;

import java.util.Iterator;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Country;
import de.macsystems.windroid.identifyable.Region;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * 
 * @author mac
 * @version $Id$
 */
public class DBSpotUpdate implements ISpot
{
	final Database database;
	final IProgress progress;

	// final SQLiteStatement insertStatement;
	// final SQLiteStatement insertTest;

	final static String LOG_TAG = DBSpotUpdate.class.getSimpleName();

	// ("CREATE TABLE IF NOT EXISTS spot (id INTEGER PRIMARY KEY AUTOINCREMENT,
	// spotid TEXT NOT NULL, continentid INTEGER, countryid INTEGER,
	// regionid INTEGER, name TEXT NOT NULL, keyword TEXT not null,
	// superforecast BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport
	// BOOLEAN, waveforecast BOOLEAN)");

	private final static String INSERT_SPOT = "INSERT INTO spot "
			+ "(spotid,continentid,regionid,name,keyword,superforecast,forecast,statistic,wavereport,waveforecast) "
			+ "values (?,?,?,?,?,?,?,?,?,?);";

	private final static String INSERT_CONTINENT = "INSERT INTO continent (id,name) values (?,?);";
	private final static String INSERT_REGION = "INSERT INTO region (id,name) values (?,?);";
	private final static String INSERT_COUNTRY = "INSERT INTO country (id,name) values (?,?);";

	// private final static String INSERT_SPOT = "INSERT_SPOT INTO spot "
	// +
	// "(spotid,continentid,regionid,name,keyword,superforecast,forecast,statistic,wavereport,waveforecast) "
	// + "values (?,?,?,?,?,?,?,?,?,?);";

	// private final static String INSERT_TEST =
	// "INSERT_SPOT INTO test (id,name)  values (?,?);";

	/**
	 * 
	 * @param _database
	 * @throws NullPointerException
	 */
	public DBSpotUpdate(final Database _database, final IProgress _progress) throws NullPointerException
	{
		if (_database == null)
		{
			throw new NullPointerException();
		}
		if (_progress == null)
		{
			throw new NullPointerException();
		}

		database = _database;
		progress = _progress;
		// insertStatement =
		// database.getWritableDatabase().compileStatement(INSERT_SPOT);
		// insertTest =
		// database.getWritableDatabase().compileStatement(INSERT_TEST);
	}

	public static final int convertBooleanToInt(final boolean _boolean)
	{
		return _boolean == true ? 1 : 0;
	}

	protected void clearAllSpots()
	{
		final SQLiteDatabase db = database.getWritableDatabase();
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
		}

	}

	public void update()
	{
		clearAllSpots();
		final SQLiteDatabase db = database.getWritableDatabase();
		final SQLiteStatement insertSpotStatement = db.compileStatement(INSERT_SPOT);
		final SQLiteStatement insertContinentStatement = db.compileStatement(INSERT_CONTINENT);
		final SQLiteStatement insertCountryStatement = db.compileStatement(INSERT_COUNTRY);
		final SQLiteStatement insertRegionStatement = db.compileStatement(INSERT_REGION);

		
		db.beginTransaction();

		if (!Continent.isParsed())
		{
			throw new IllegalStateException("Please parse Continents");
		}

		Log.d(LOG_TAG, "executeInsert");

		final long start = System.currentTimeMillis();
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
					updateCountryTable(insertCountryStatement, country);
					insertCountryStatement.executeInsert();

					final Iterator<Region> regions = country.iterator();
					while (regions.hasNext())
					{
						final Region region = regions.next();
						updateRegionTable(insertRegionStatement, region);
						insertRegionStatement.executeInsert();

						final Iterator<Station> stations = region.iterator();
						while (stations.hasNext())
						{
							updateSpotTable(insertSpotStatement, continent, country, region, stations.next());
							index++;
							if (index % 100 == 0)
							{
								progress.incrementBy(100);
							}
						}
					}
				}
			}
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
			Log.d(LOG_TAG, "Insert took " + time + " ms");
		}

	}

	private final static void updateCountryTable(final SQLiteStatement insertStatement, final Country country)
	{
		insertStatement.bindString(1, country.getId());
		insertStatement.bindString(2, country.getName());
	}

	private final static void updateRegionTable(final SQLiteStatement insertStatement, final Region region)
	{
		insertStatement.bindString(1, region.getId());
		insertStatement.bindString(2, region.getName());
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
		insertStatement.bindString(1, station.getId());
		insertStatement.bindString(2, continent.getId());
		insertStatement.bindString(3, country.getId());
		insertStatement.bindString(4, region.getId());
		insertStatement.bindString(5, station.getName());
		insertStatement.bindString(6, station.getKeyword());
		insertStatement.bindLong(7, convertBooleanToInt(station.hasSuperforecast()));
		insertStatement.bindLong(8, 0);
		insertStatement.bindLong(9, 0);
		insertStatement.bindLong(10, 0);
	}
}
