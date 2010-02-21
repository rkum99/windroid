package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
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
	protected final String tableName;

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
			throws NullPointerException
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
	 * @throws NullPointerException
	 *             if any parameter is null.
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
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "no entrys in " + _tabelName);
				}
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
	public static final int asInt(final boolean _boolean)
	{
		return _boolean == true ? 1 : 0;
	}

	/**
	 * Converts an Integer to an Boolean representation
	 * 
	 * @param _value
	 * @return
	 */
	public static final boolean asBoolean(final long _value)
	{
		return _value == 0 ? false : true;
	}

	/**
	 * Returns value found in column as String
	 * 
	 * @param _c
	 * @param _columnName
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static final String getString(final Cursor _c, final String _columnName) throws IllegalArgumentException
	{
		final int index = _c.getColumnIndexOrThrow(_columnName);
		return _c.getString(index);
	}

	/**
	 * Returns value found in column as float
	 * 
	 * @param _c
	 * @param _columnName
	 * @return
	 * @throws IllegalArgumentException
	 */

	public static final float getFloat(final Cursor _c, final String _columnName) throws IllegalArgumentException
	{
		final int index = _c.getColumnIndexOrThrow(_columnName);
		return _c.getFloat(index);
	}

	/**
	 * Returns value found in column as long
	 * 
	 * @param _c
	 * @param _columnName
	 * @return
	 * @throws IllegalArgumentException
	 */

	public static final long getLong(final Cursor _c, final String _columnName) throws IllegalArgumentException
	{
		final int index = _c.getColumnIndexOrThrow(_columnName);
		return _c.getLong(index);
	}

	/**
	 * Returns value found in column as integer
	 * 
	 * @param _c
	 * @param _columnName
	 * @return
	 * @throws IllegalArgumentException
	 */

	public static final int getInt(final Cursor _c, final String _columnName) throws IllegalArgumentException
	{
		final int index = _c.getColumnIndexOrThrow(_columnName);
		return _c.getInt(index);
	}

	/**
	 * Returns value found in column as double
	 * 
	 * @param _c
	 * @param _columnName
	 * @return
	 * @throws IllegalArgumentException
	 */

	public static final double getDouble(final Cursor _c, final String _columnName) throws IllegalArgumentException
	{
		final int index = _c.getColumnIndexOrThrow(_columnName);
		return _c.getDouble(index);
	}

	/**
	 * Returns value found in column as boolean
	 * 
	 * @param _c
	 * @param _columnName
	 * @return
	 * @throws IllegalArgumentException
	 */

	public static final boolean getBoolean(final Cursor _c, final String _columnName) throws IllegalArgumentException
	{
		final int index = _c.getColumnIndexOrThrow(_columnName);
		return asBoolean(_c.getInt(index));
	}

	/**
	 * Returns value found in column as short
	 * 
	 * @param _c
	 * @param _columnName
	 * @return
	 * @throws IllegalArgumentException
	 */

	public static final short getShort(final Cursor _c, final String _columnName) throws IllegalArgumentException
	{
		final int index = _c.getColumnIndexOrThrow(_columnName);
		return _c.getShort(index);
	}

	/**
	 * Moves Cursor to first position or if cursor is empty it throws an
	 * {@link IllegalStateException}
	 * 
	 * @param _c
	 */
	public static final void moveToFirstOrThrow(final Cursor _c) throws IllegalStateException
	{
		if (!_c.moveToFirst())
		{
			throw new IllegalStateException("Cursor ist empty.");
		}

	}

	/**
	 * Returns the actual primary key of last insert on table.<br>
	 * SELECT MAX (_id) from table;<br>
	 * <br>
	 * Database will not be closed!
	 * 
	 * @param _db
	 * @return
	 */
	public final int getPrimaryKey(final SQLiteDatabase _db)
	{
		Cursor c = null;
		try
		{
			c = _db.rawQuery("SELECT MAX(" + COLUMN_ID + ") from " + tableName, null);
			moveToFirstOrThrow(c);
			return getInt(c, COLUMN_MAX_ID);
		}
		finally
		{
			IOUtils.close(c);
		}
	}
}