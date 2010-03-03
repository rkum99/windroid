package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.db.IContinentDAO;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * DAO for Table Continent
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public final class ContinentImpl extends BaseImpl implements IContinentDAO
{
	/**
	 * 
	 * @param database
	 */
	public ContinentImpl(final Database database)
	{
		this(database, NullProgressAdapter.INSTANCE);
	}

	/**
	 * 
	 * @param database
	 * @param _progress
	 */
	public ContinentImpl(final Database database, final IProgress _progress)
	{
		super(database, "continent", _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IContinentDAO#get(int)
	 */
	@Override
	public Continent get(final int id)
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.db.IContinentDAO#getIndexByID(java.lang.String)
	 */
	@Override
	public int getIndexByID(final String _id)
	{
		int result = -1;
		final SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query("continent", null, "id=?", new String[]
			{ _id }, null, null, null);
			moveToFirstOrThrow(cursor);
			final int index = cursor.getColumnIndexOrThrow("_id");
			result = cursor.getInt(index);
			return result;
		}
		finally
		{
			cursor.close();
		}
	}
}