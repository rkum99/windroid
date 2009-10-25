package de.macsystems.windroid.db.sqlite;

import java.util.Arrays;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.db.IDAO;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public class BaseImpl implements IDAO
{
	private static final String LOG_TAG = BaseImpl.class.getSimpleName();

	private final Database database;

	private final IProgress progress;

	private final String tableName;

	/**
	 * 
	 * @param _database
	 * @param _tableName
	 * @param _progress
	 */
	protected BaseImpl(final Database _database, final String _tableName, final IProgress _progress)
	{
		if (_database == null)
		{
			throw new NullPointerException("Database");
		}
		if (_tableName == null)
		{
			throw new NullPointerException("tablename");
		}

		if (_progress == null)
		{
			throw new NullPointerException("progress");
		}

		database = _database;
		tableName = _tableName;
		progress = _progress;

	}

	/**
	 * A Base Implementation with a {@link NullProgressAdapter}
	 * 
	 * @param _database
	 * @param _tableName
	 * @see NullProgressAdapter
	 */
	protected BaseImpl(final Database _database, final String _tableName)
	{
		this(_database, _tableName, NullProgressAdapter.INSTANCE);
	}

	/**
	 * 
	 * @return
	 */
	protected IProgress getProgress()
	{
		return progress;
	}

	/**
	 * returns a readable {@link SQLiteDatabase}
	 * 
	 * @return
	 */
	protected SQLiteDatabase getReadableDatabase()
	{
		return getDatabase().getReadableDatabase();
	}

	/**
	 * returns a writable {@link SQLiteDatabase}
	 * 
	 * @return
	 */
	protected SQLiteDatabase getWritableDatabase()
	{
		return getDatabase().getWritableDatabase();
	}

	/**
	 * Returns the Database
	 * 
	 * @return
	 */
	protected Database getDatabase()
	{
		return database;
	}

	/**
	 * Returns the count(*) of given table name like a 'select count(*) from
	 * table'
	 * 
	 * @param _tabelName
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected int getCount(final String _tabelName) throws IllegalArgumentException
	{
		if (_tabelName == null)
		{
			throw new IllegalArgumentException("invalid table name");
		}
		int result = 0;
		final SQLiteDatabase db = getDatabase().getReadableDatabase();
		Cursor c = null;
		try
		{
			c = db.rawQuery("SELECT count(*) from " + _tabelName, null);
			if (!c.moveToFirst())
			{
				Log.d(LOG_TAG, "no entrys.");
				return result;
			}
			Log.d(LOG_TAG, "Colums " + Arrays.toString(c.getColumnNames()));
			Log.d(LOG_TAG, "count " + c.getCount());
			Log.d(LOG_TAG, "Colum index " + c.getColumnIndexOrThrow("count(*)"));
			Log.d(LOG_TAG, "return value " + c.getInt(0));

			final int index = c.getColumnIndexOrThrow("count(*)");
			result = c.getInt(index);
		}
		catch (final SQLException e)
		{
			Log.e(LOG_TAG, "getCount", e);
		}
		finally
		{
			IOUtils.close(db);
			IOUtils.close(c);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IDAO#fetchAll()
	 */
	@Override
	public Cursor fetchAll()
	{
		return getReadableDatabase().query(tableName, null, null, null, null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IDAO#getSize()
	 */
	@Override
	public int getSize()
	{
		return getCount(tableName);
	}
}
