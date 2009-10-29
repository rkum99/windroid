package de.macsystems.windroid.db.sqlite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creates or updates the underlying SQLite Database.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Database extends SQLiteOpenHelper
{

	private final static String LOG_TAG = Database.class.getSimpleName();

	private final static String DATABASE_NAME = "windroid.db";

	private final static int VERSION = 43;
	/**
	 * Default size for script lists
	 */
	private final static int INITIAL_CAPACITY = 64;

	/**
	 * 
	 * @param _context
	 */
	public Database(final Context _context)
	{
		super(_context, DATABASE_NAME, null, VERSION);
	}

	private List<String> createUpgradeScript()
	{
		final List<String> temp = new ArrayList<String>(INITIAL_CAPACITY);

		temp.add("DROP TABLE IF EXISTS spot;");
		temp.add("DROP TABLE IF EXISTS continent;");
		temp.add("DROP TABLE IF EXISTS country;");
		temp.add("DROP TABLE IF EXISTS region;");
		// temp.add("DROP TABLE IF EXISTS selected;");

		temp.add("create TABLE IF NOT EXISTS internal (id TEXT PRIMARY KEY, value text)");

		temp
				.add("CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid TEXT NOT NULL, countryid TEXT NOT NULL, regionid TEXT NOT NULL, name TEXT NOT NULL, keyword TEXT NOT NULL, superforecast BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN);");
		temp
				.add("CREATE TABLE IF NOT EXISTS continent (_id INTEGER PRIMARY KEY AUTOINCREMENT ,id INTEGER, name TEXT);");
		temp
				.add("CREATE TABLE IF NOT EXISTS country (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, continentid TEXT NOT NULL);");
		temp
				.add("CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, countryid TEXT NOT NULL);");
		//
		temp.add("CREATE INDEX IF NOT EXISTS spotindex ON spot (spotid);");
		temp.add("CREATE INDEX IF NOT EXISTS countryindex ON country (id);");
		temp.add("CREATE INDEX IF NOT EXISTS regionindex ON region (id);");
		temp.add("CREATE INDEX IF NOT EXISTS continentindex ON continent (id);");

		// //
		temp.add("CREATE INDEX IF NOT EXISTS conid ON continent (id);");
		temp.add("CREATE INDEX IF NOT EXISTS regid ON region (id);");
		temp.add("CREATE INDEX IF NOT EXISTS coid ON country (id);");

		// selected table
		temp
				.add("CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY AUTOINCREMENT,spotid text NOT NULL,activ BOOLEAN);");
		temp.add("CREATE INDEX IF NOT EXISTS selectedsid ON selected (spotid);");

		for (int i = 0; i < 100; i++)
		{
			temp.add("INSERT INTO selected (spotid,activ) VALUES ('nl" + i + "','" + (i % 5 == 0) + "');");
		}

		return Collections.unmodifiableList(temp);
	}

	/**
	 * @param temp
	 */
	private List<String> createNewDatabaseScript()
	{
		final List<String> temp = new ArrayList<String>(INITIAL_CAPACITY);
		temp.add("DROP TABLE IF EXISTS spot;");
		temp.add("DROP TABLE IF EXISTS continent;");
		temp.add("DROP TABLE IF EXISTS country;");
		temp.add("DROP TABLE IF EXISTS region;");
		temp.add("DROP TABLE IF EXISTS selected;");
		// Create ConfigDAO Table
		temp.add("create TABLE IF NOT EXISTS internal (id TEXT PRIMARY KEY, value text)");
		//
		temp
				.add("CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid TEXT NOT NULL, countryid TEXT NOT NULL, regionid TEXT NOT NULL, name TEXT NOT NULL, keyword TEXT NOT NULL, superforecast BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN);");
		temp
				.add("CREATE TABLE IF NOT EXISTS continent (_id INTEGER PRIMARY KEY AUTOINCREMENT ,id INTEGER, name TEXT);");
		temp
				.add("CREATE TABLE IF NOT EXISTS country (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, continentid TEXT NOT NULL);");
		temp
				.add("CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT NOT NULL, countryid TEXT NOT NULL);");
		//
		temp.add("CREATE INDEX spotindex ON spot (spotid);");
		temp.add("CREATE INDEX countryindex ON country (id);");
		temp.add("CREATE INDEX regionindex ON region (id);");
		temp.add("CREATE INDEX continentindex ON continent (id);");

		// //
		temp.add("CREATE INDEX conid ON continent (id);");
		temp.add("CREATE INDEX regid ON region (id);");
		temp.add("CREATE INDEX coid ON country (id);");
		// selected table
		temp
				.add("CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY AUTOINCREMENT,spotid text NOT NULL,activ BOOLEAN);");
		temp.add("CREATE INDEX IF NOT EXISTS selectedsid ON selected (spotid);");

		return Collections.unmodifiableList(temp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(final SQLiteDatabase database)
	{
		Log.d(LOG_TAG, "onCreate Database");

		final List<String> newDatabase = createNewDatabaseScript();

		for (int i = 0; i < newDatabase.size(); i++)
		{
			Log.d(LOG_TAG, newDatabase.get(i));
			database.execSQL(newDatabase.get(i));
		}
		Log.d(LOG_TAG, "onCreate Database finished");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion)
	{
		Log.d(LOG_TAG, "onUpgrade Database  from version " + oldVersion + " to " + newVersion);
		final List<String> upgradeDatabase = createUpgradeScript();
		for (final Iterator<String> iter = upgradeDatabase.iterator(); iter.hasNext();)
		{
			final String sql = iter.next();
			Log.d(LOG_TAG, sql);
			database.execSQL(sql);
		}
		Log.d(LOG_TAG, "onUpgrade Database finished");
	}

}
