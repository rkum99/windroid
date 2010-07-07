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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IRepeatDAO;
import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author mac
 * @version $Id$
 */
public final class RepeatImpl extends BaseImpl implements IRepeatDAO
{
	private final static String LOG_TAG = RepeatImpl.class.getSimpleName();

	/**
	 * @param database
	 * @param progress
	 * @throws NullPointerException
	 */
	public RepeatImpl(final Database database, final IProgress progress) throws NullPointerException
	{
		super(database, "repeat", progress);
	}

	/**
	 * @param database
	 */
	public RepeatImpl(final Database database)
	{
		this(database, NullProgressAdapter.INSTANCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.sqlite.IRepeatDAO#getRepeat(int)
	 */
	@Override
	public Repeat getRepeat(final int _id) throws DBException
	{
		final SQLiteDatabase db = getReadableDatabase();
		final Repeat r;
		Cursor c = null;
		try
		{
			c = db.rawQuery("select * FROM " + tableName + " WHERE _id=?", new String[]
			{ Integer.toString(_id) });

			moveToFirstOrThrow(c);

			final int repeatID = getInt(c, COLUMN_ID);
			final int weekday = getInt(c, COLUMN_WEEKDAY);
			final long daytime = getLong(c, COLUMN_DAYTIME);
			final boolean activ = getBoolean(c, COLUMN_ACTIV);
			//
			r = new Repeat(repeatID);
			r.setActiv(activ);
			r.setDayOfWeek(weekday);
			r.setDayTime(daytime);
			//
		}
		finally
		{
			IOUtils.close(c);
			IOUtils.close(db);
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.db.sqlite.IRepeatDAO#update(de.macsystems.windroid
	 * .identifyable.Repeat)
	 */
	@Override
	public void update(final Repeat _repeat)
	{
		if (_repeat == null)
		{
			throw new NullPointerException("repeat");
		}
		final SQLiteDatabase db = getReadableDatabase();
		final Cursor c = null;

		try
		{
			final ContentValues value = new ContentValues();
			value.put(COLUMN_ID, _repeat.getId());
			value.put(COLUMN_DAYTIME, _repeat.getDayTime());
			value.put(COLUMN_WEEKDAY, _repeat.getDayOfWeek());
			value.put(COLUMN_ACTIV, _repeat.isActiv());
			//
			db.update(tableName, value, null, null);
			if (Logging.isEnabled())
			{
				Log.d(LOG_TAG, "updated repeat on " + tableName + " primary key is: " + _repeat.getId());
			}
		}
		finally
		{
			IOUtils.close(c);
			IOUtils.close(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.macsystems.windroid.db.IRepeatDAO#insert(de.macsystems.windroid.
	 * identifyable.Repeat)
	 */
	public void insert(final Repeat _repeat)
	{
		if (_repeat == null)
		{
			throw new NullPointerException("repeat");
		}
		final SQLiteDatabase db = getReadableDatabase();
		final Cursor c = null;

		try
		{
			final ContentValues value = new ContentValues();
			value.put(COLUMN_DAYTIME, _repeat.getDayTime());
			value.put(COLUMN_WEEKDAY, _repeat.getDayOfWeek());
			value.put(COLUMN_ACTIV, _repeat.isActiv());
			//
			final int primaryKey = (int) db.insert(tableName, null, value);
			//
			_repeat.setId(primaryKey);
			if (Logging.isEnabled())
			{
				Log.d(LOG_TAG, "Insert repeat on " + tableName + " a repeat, primaryKey is:" + primaryKey);
			}
		}
		finally
		{
			IOUtils.close(c);
			IOUtils.close(db);
		}

	}

}
