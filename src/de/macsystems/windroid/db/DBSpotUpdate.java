package de.macsystems.windroid.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Country;
import de.macsystems.windroid.identifyable.Region;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.io.IOUtils;

public class DBSpotUpdate implements ISpot
{
	final Database database;

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
	 * @throws SQLException
	 */
	public DBSpotUpdate(final Database _database) throws NullPointerException, SQLException
	{
		if (_database == null)
		{
			throw new NullPointerException();
		}
		database = _database;
		// insertStatement =
		// database.getWritableDatabase().compileStatement(INSERT_SPOT);
		// insertTest =
		// database.getWritableDatabase().compileStatement(INSERT_TEST);
	}

	public static int convertBooleanToInt(boolean _boolean)
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
			db.execSQL("DELETE FROM test");
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
		// db.beginTransaction();

		Log.d(LOG_TAG, "executeInsert");

		final long start = System.currentTimeMillis();
		try
		{
			for (final Continent continent : Continent.values())
			{
				updateContinentTable(insertContinentStatement, continent);
				insertContinentStatement.execute();

				final Country[] countrys = continent.getCoutrys();
				for (final Country country : countrys)
				{
					final Region[] regions = country.getRegions();
					for (final Region region : regions)
					{
						final Station[] stations = region.getStations();
						for (final Station station : stations)
						{
							/**
							 * "INSERT_SPOT INTO spot (spotid,continentid,regionid,name,keyword,superforecast,forecast,statistic,wavereport,waveforecast)  "
							 * values (?,?,?,?,?,?,?,?,?,?);";
							 */
							updateSpotTable(insertSpotStatement, continent, region, station);
							insertSpotStatement.execute();

						}
					}
				}
			}
		}
		finally
		{
			// db.endTransaction();
			insertSpotStatement.close();
			IOUtils.close(db);
			final long time = System.currentTimeMillis() - start;
			Log.d(LOG_TAG, "Insert took " + time + " ms");
		}

	}

	/**
	 * @param insertStatement
	 * @param continent
	 * @param region
	 * @param station
	 */
	private void updateContinentTable(final SQLiteStatement insertStatement, final Continent continent)
	{
		insertStatement.bindString(1, continent.getId());
		insertStatement.bindString(2, continent.name());
	}

	/**
	 * @param insertStatement
	 * @param continent
	 * @param region
	 * @param station
	 */
	private void updateSpotTable(final SQLiteStatement insertStatement, final Continent continent, final Region region,
			final Station station)
	{
		insertStatement.bindString(1, station.getId());
		insertStatement.bindString(2, continent.getId());
		insertStatement.bindString(3, region.getId());
		insertStatement.bindString(4, station.getName());
		insertStatement.bindString(5, station.getKeyword());
		insertStatement.bindLong(6, convertBooleanToInt(station.hasSuperforecast()));
		insertStatement.bindLong(7, 0);
		insertStatement.bindLong(8, 0);
		insertStatement.bindLong(9, 0);
	}
}
