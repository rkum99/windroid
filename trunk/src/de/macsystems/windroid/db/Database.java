package de.macsystems.windroid.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author mac
 * @version $Id$
 * 
 */
public class Database extends SQLiteOpenHelper
{

	private final static String LOG_TAG = Database.class.getSimpleName();

	final static String DATABASE_NAME = "windroid.db";
	final static int VERSION = 28;

	final List<String> createScripts;

	/**
	 * 
	 * @param context
	 */
	public Database(final Context context)
	{
		super(context, DATABASE_NAME, null, VERSION);
		//
		final List<String> temp = new ArrayList<String>();

		temp.add("DROP TABLE IF EXISTS spot;");
		temp.add("DROP TABLE IF EXISTS continent;");
		temp.add("DROP TABLE IF EXISTS country;");
		temp.add("DROP TABLE IF EXISTS region;");

		//
		temp
				.add("CREATE TABLE IF NOT EXISTS spot (_id INTEGER PRIMARY KEY AUTOINCREMENT, spotid TEXT NOT NULL, continentid INTEGER, countryid INTEGER, regionid INTEGER, name TEXT NOT NULL, keyword TEXT not null, superforecast BOOLEAN, forecast BOOLEAN, statistic BOOLEAN, wavereport BOOLEAN, waveforecast BOOLEAN);");
		temp
				.add("CREATE TABLE IF NOT EXISTS continent (_id INTEGER PRIMARY KEY AUTOINCREMENT ,id INTEGER, name TEXT);");
		temp.add("CREATE TABLE IF NOT EXISTS country (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT);");
		temp.add("CREATE TABLE IF NOT EXISTS region (_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER, name TEXT);");
		//
		temp.add("CREATE INDEX spotindex ON spot (spotid);");
		temp.add("CREATE INDEX countryindex ON country (id);");
		temp.add("CREATE INDEX regionindex ON region (id);");
		temp.add("CREATE INDEX continentindex ON continent (id);");

		// //
		temp.add("CREATE INDEX conid ON continent (id);");
		temp.add("CREATE INDEX regid ON region (id);");
		temp.add("CREATE INDEX coid ON country (id);");
		createScripts = Collections.unmodifiableList(temp);
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

		for (int i = 0; i < createScripts.size(); i++)
		{
			Log.d(LOG_TAG, createScripts.get(i));
			database.execSQL(createScripts.get(i));
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
		Log.d(LOG_TAG, "onUpgrade Database");

		for (int i = 0; i < createScripts.size(); i++)
		{
			Log.d(LOG_TAG, createScripts.get(i));
			database.execSQL(createScripts.get(i));
		}
		Log.d(LOG_TAG, "onUpgrade Database finished");
	}

}
