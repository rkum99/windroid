package de.macsystems.windroid.db.sqlite;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.macsystems.windroid.Logging;

/**
 * Creates or updates the underlying SQLite Database.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Database extends SQLiteOpenHelper
{

	private final static String LOG_TAG = Database.class.getSimpleName();

	private final static String DATABASE_NAME = "windroid.db";

	private final static int VERSION = 87;

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
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onCreate Database");
		}

		try
		{
			final List<String> script = ScriptLoader.getCreateScript(context);
			executeScript(database, script);
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Fatal Error occured while loading database script.", e);
		}
		finally
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onCreate Database finished");
			}
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
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Upgrade Database from version " + oldVersion + " to " + newVersion);
		}
		try
		{
			final List<String> script = ScriptLoader.getUpdateScript(context);
			executeScript(database, script);
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Fatal Error occured while loading database script.", e);
		}
		finally
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Upgrade Database finished");
			}

		}
	}

	/**
	 * @param database
	 * @param _script
	 */
	private static void executeScript(final SQLiteDatabase database, final List<String> _script)
	{
		try
		{
			for (int i = 0; i < _script.size(); i++)
			{
				final String sql = _script.get(i);
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Executing :" + sql);
				}
				database.execSQL(sql);
			}
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Error occured while executing database script.", e);
		}
	}
}