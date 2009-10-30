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

	private final static int VERSION = 49;
	/**
	 * Default size for script lists
	 */
	private final static int INITIAL_CAPACITY = 64;

	private final Context context;

	/**
	 * 
	 * @param _context
	 */
	public Database(final Context _context)
	{
		super(_context, DATABASE_NAME, null, VERSION);
		context = _context;
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

		try
		{
			final List<String> script = ScriptLoader.getCreateScript(context);
			executeScript(database, script);
		}
		catch (Exception e)
		{
			Log.e(LOG_TAG, "Fatal Error occured while loading database script.", e);
		}
		finally
		{
			Log.d(LOG_TAG, "onCreate Database finished");
		}
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
		Log.d(LOG_TAG, "Upgrade Database from version " + oldVersion + " to " + newVersion);
		try
		{
			final List<String> script = ScriptLoader.getUpdateScript(context);
			executeScript(database, script);
		}
		catch (Exception e)
		{
			Log.e(LOG_TAG, "Fatal Error occured while loading database script.", e);
		}
		finally
		{
			Log.d(LOG_TAG, "Upgrade Database finished");
		}

	}

	/**
	 * @param database
	 * @param upgradeDatabase
	 */
	private void executeScript(final SQLiteDatabase database, final List<String> upgradeDatabase)
	{
		try
		{
			for (final Iterator<String> iter = upgradeDatabase.iterator(); iter.hasNext();)
			{
				final String sql = iter.next();
				Log.d(LOG_TAG, "Executing :" + sql);
				database.execSQL(sql);
			}
		}
		catch (Exception e)
		{
			Log.e(LOG_TAG, "Error occured while executing database script.", e);
		}
	}
}
