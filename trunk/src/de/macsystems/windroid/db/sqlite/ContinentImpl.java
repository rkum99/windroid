/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.db.DBException;
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
	throws DBException
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