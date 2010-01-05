package de.macsystems.windroid.db.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public class SelectedImpl extends BaseImpl implements ISelectedDAO
{
	/**
	 * 
	 * @param _database
	 */
	public SelectedImpl(final Database _database)
	{
		super(_database, "selected");
	}

	/**
	 * 
	 * @param _database
	 * @param _progress
	 */
	public SelectedImpl(final Database _database, final IProgress _progress)
	{
		super(_database, "selected", _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.db.ISelectedDAO#insertSpot(de.macsystems.windroid
	 * .SpotConfigurationVO)
	 */
	@Override
	public long insertSpot(final SpotConfigurationVO _vo)
	{
		if (_vo == null)
		{
			throw new NullPointerException("SpotConfigurationVO");
		}

		long newID = -1;
		// TODO : At the moment all spots are activ !!!
		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final ContentValues values = new ContentValues();
			values.put(COLUMN_SPOTID, _vo.getStation().getId());
			values.put(COLUMN_NAME, _vo.getStation().getName());
			values.put(COLUMN_ACTIV, true);
			values.put(COLUMN_USEDIRECTION, _vo.isUseWindirection());
			values.put(COLUMN_STARTING, _vo.getFromDirection().getId());
			values.put(COLUMN_TILL, _vo.getToDirection().getId());
			values.put(COLUMN_WINDMEASURE, _vo.getPreferredWindUnit().name());
			values.put(COLUMN_MINWIND, _vo.getWindspeedMin());
			values.put(COLUMN_MAXWIND, _vo.getWindspeedMax());
			//
			newID = db.insert(tableName, null, values);
		}
		finally
		{
			IOUtils.close(db);
		}

		return newID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#setActiv(long, boolean)
	 */
	public void setActiv(final long _id, final boolean isActiv)
	{
		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final ContentValues values = new ContentValues();
			values.put(COLUMN_ACTIV, isActiv);

			db.update(tableName, values, "_id=?", new String[]
			{ "" + _id });
		}
		finally
		{
			IOUtils.close(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#isActiv(long)
	 */
	@Override
	public boolean isActiv(long _id)
	{
		final SQLiteDatabase db = getReadableDatabase();
		Cursor c = null;
		boolean result = false;
		try
		{
			c = db.query(tableName, null, "_id=?", new String[]
			{ "" + _id }, null, null, null);

			if (!c.moveToFirst())
			{
				throw new IllegalArgumentException("_id not found :" + _id);
			}

			final int index = c.getColumnIndexOrThrow(COLUMN_ACTIV);
			result = convertIntToBoolean(c.getInt(index));
		}
		finally
		{
			IOUtils.close(c);
			IOUtils.close(db);
		}
		return result;
	}
}