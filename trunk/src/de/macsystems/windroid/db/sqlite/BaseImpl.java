package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.db.IDAO;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * Base Implementation for DAOs
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class BaseImpl implements IDAO
{
	private static final String LOG_TAG = BaseImpl.class.getSimpleName();
	/**
	 * 
	 */
	private final Database database;
	/**
	 * 
	 */
	private final IProgress progress;
	/**
	 * Table name which this DAO is for
	 */
	private final String tableName;

	/**
	 * 
	 * @param _database
	 * @param _tableName
	 *            default name of the table this class uses.
	 * @param _progress
	 * @throws NullPointerException
	 *             if any parameter is null.
	 */
	protected BaseImpl(final Database _database, final String _tableName, final IProgress _progress)
	{
		if (_database == null)
		{
			throw new NullPointerException("database");
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
			c = db.rawQuery("SELECT count(*) from " + tableName, null);
			if (!c.moveToFirst())
			{
				Log.d(LOG_TAG, "no entrys in " + _tabelName);
				return result;
			}
			final int index = c.getColumnIndexOrThrow("count(*)");
			result = c.getInt(index);
		}
		catch (final SQLException e)
		{
			Log.e(LOG_TAG, "failed on " + tableName, e);
		}
		finally
		{
			IOUtils.close(db);
			IOUtils.close(c);
		}
		return result;
	}

	/**
	 * Returns a Cursor which contains result of a SQL like<br>
	 * 'SELECT * from tablename WHERE column='value'. The Tablename is the
	 * default table name.
	 * 
	 * @param _columnName
	 * @param _value
	 * @return
	 */
	protected Cursor fetchBy(final String _columnName, final String _value)
	{
		if (_columnName == null || _columnName.length() == 0)
		{
			throw new IllegalArgumentException("_columnName invalid :" + _columnName);
		}
		if (_value == null || _value.length() == 0)
		{
			throw new IllegalArgumentException("_value invalid :" + _columnName);
		}

		final SQLiteDatabase db = getReadableDatabase();
		return db.query(tableName, null, _columnName + "=?", new String[]
		{ _value }, null, null, null);

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

	/**
	 * Converts an Boolean to an Integer representation
	 * 
	 * @param _boolean
	 * @return
	 */
	public static final int convertBooleanToInt(final boolean _boolean)
	{
		return _boolean == true ? 1 : 0;
	}

	/**
	 * Converts an Integer to an Boolean representation 
	 * 
	 * @param _value
	 * @return
	 */
	public static final boolean convertIntToBoolean(final long _value)
	{
		return _value == 0 ? false : true;
	}

}
